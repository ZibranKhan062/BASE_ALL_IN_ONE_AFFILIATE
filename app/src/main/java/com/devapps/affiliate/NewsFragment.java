package com.devapps.affiliate;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.widget.SearchView;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import com.devapps.affiliate.models.Category;
import com.devapps.affiliate.models.NewsAdapter;
import com.devapps.affiliate.models.NewsItem;
import com.google.android.material.appbar.MaterialToolbar;
import com.google.android.material.chip.Chip;
import com.google.android.material.chip.ChipGroup;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.atomic.AtomicInteger;

public class NewsFragment extends Fragment {

    private RecyclerView rvNews;
    private SwipeRefreshLayout swipeRefresh;
    private NewsAdapter newsAdapter;
    private ChipGroup categoryChipGroup;
    private DatabaseReference categoriesRef;
    private DatabaseReference newsRef;
    private DatabaseReference likesRef;
    private DatabaseReference bookmarksRef;
    private List<Category> categories;
    private List<NewsItem> newsList;
    private FirebaseAuth auth;

    private ValueEventListener newsListener;
    private ValueEventListener categoriesListener;
    private boolean isDataLoaded = false;
    private boolean isFirstLoad = true;

    private MaterialToolbar toolbar;
    private SearchView searchView;
    private String currentQuery = "";
    private List<NewsItem> originalNewsList;
    private boolean isReturningFromDetail = false;

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        FirebaseDatabase database = FirebaseDatabase.getInstance();
        categoriesRef = database.getReference("categories");
        newsRef = database.getReference("news");
        likesRef = database.getReference("likes");
        bookmarksRef = database.getReference("bookmarks");
        auth = FirebaseAuth.getInstance();
        categories = new ArrayList<>();
        newsList = new ArrayList<>();
    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_news, container, false);
        initViews(view);

        return view;
    }

    private void setupToolbar() {
        toolbar = requireView().findViewById(R.id.toolbar);
        toolbar.inflateMenu(R.menu.menu_search);

        MenuItem searchItem = toolbar.getMenu().findItem(R.id.action_search);
        searchView = (SearchView) searchItem.getActionView();

        searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String query) {
                filterNews(query);
                return true;
            }

            @Override
            public boolean onQueryTextChange(String newText) {
                filterNews(newText);
                return true;
            }
        });

        searchItem.setOnActionExpandListener(new MenuItem.OnActionExpandListener() {
            @Override
            public boolean onMenuItemActionExpand(MenuItem item) {
                // Save the current list when search is expanded
                originalNewsList = new ArrayList<>(newsList);
                return true;
            }

            @Override
            public boolean onMenuItemActionCollapse(MenuItem item) {
                // Restore the original list when search is collapsed
                if (originalNewsList != null) {
                    newsList.clear();
                    newsList.addAll(originalNewsList);
                    newsAdapter.updateNews(newsList);
                }
                return true;
            }
        });
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        Log.d("TAG", "onViewCreated - isDataLoaded: " + isDataLoaded);
        setupToolbar();

        if (!isDataLoaded) {
            Log.d("TAG", "Loading initial data");
            loadCategories();
            loadAllNews();
        } else {
            Log.d("TAG", "Using cached data, list size: " + newsList.size());
            // Check if we're returning from detail
            if (isReturningFromDetail) {
                Log.d("TAG", "Returning from detail - updating only status");
                updateNewsStatus();
            } else {
                newsAdapter.updateNews(newsList);
            }
        }
    }

    @Override
    public void onResume() {
        super.onResume();
        Log.d("TAG", "onResume called");
        isReturningFromDetail = true;
    }

    @Override
    public void onPause() {
        super.onPause();
        Log.d("TAG", "onPause called");
    }

    private void updateNewsStatus() {
        Log.d("TAG", "Updating news status for " + newsList.size() + " items");
        if (auth.getCurrentUser() != null) {
            int totalItems = newsList.size();
            AtomicInteger processedItems = new AtomicInteger(0);

            for (NewsItem newsItem : newsList) {
                updateSingleNewsStatus(newsItem, () -> {
                    if (processedItems.incrementAndGet() == totalItems) {
                        Log.d("TAG", "All items status updated");
                        if (isAdded()) {
                            newsAdapter.notifyDataSetChanged();
                        }
                    }
                });
            }
        } else {
            newsAdapter.notifyDataSetChanged();
        }
    }

    private void updateSingleNewsStatus(NewsItem newsItem, Runnable onComplete) {
        if (!isAdded() || auth.getCurrentUser() == null) {
            onComplete.run();
            return;
        }

        String userId = auth.getCurrentUser().getUid();
        CountDownLatch latch = new CountDownLatch(2);

        // Check like status
        likesRef.child(newsItem.getId()).child(userId)
                .addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot snapshot) {
                        if (isAdded()) {
                            newsItem.setLiked(snapshot.exists());
                            latch.countDown();
                            if (latch.getCount() == 0) {
                                onComplete.run();
                            }
                        }
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError error) {
                        latch.countDown();
                        if (latch.getCount() == 0) {
                            onComplete.run();
                        }
                    }
                });

        // Check bookmark status
        bookmarksRef.child(userId).child(newsItem.getId())
                .addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot snapshot) {
                        if (isAdded()) {
                            newsItem.setBookmarked(snapshot.exists());
                            latch.countDown();
                            if (latch.getCount() == 0) {
                                onComplete.run();
                            }
                        }
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError error) {
                        latch.countDown();
                        if (latch.getCount() == 0) {
                            onComplete.run();
                        }
                    }
                });
    }

    private void filterNews(String query) {
        currentQuery = query.toLowerCase().trim();

        if (currentQuery.isEmpty() && originalNewsList != null) {
            // If query is empty, restore original list
            newsList.clear();
            newsList.addAll(originalNewsList);
        } else {
            // Filter the original list based on query
            List<NewsItem> filteredList = new ArrayList<>();
            List<NewsItem> listToFilter = originalNewsList != null ? originalNewsList : newsList;

            for (NewsItem newsItem : listToFilter) {
                if (newsItem.getTitle().toLowerCase().contains(currentQuery) ||
                        newsItem.getDescription().toLowerCase().contains(currentQuery)) {
                    filteredList.add(newsItem);
                }
            }

            newsList.clear();
            newsList.addAll(filteredList);
        }

        newsAdapter.updateNews(newsList);

        // Show message if no results found
        if (newsList.isEmpty() && !currentQuery.isEmpty()) {
            Toast.makeText(requireContext(), "No results found for: " + currentQuery,
                    Toast.LENGTH_SHORT).show();
        }
    }

    private void initViews(View view) {
        toolbar = view.findViewById(R.id.toolbar);
        rvNews = view.findViewById(R.id.rvNews);
        swipeRefresh = view.findViewById(R.id.swipeRefresh);
        categoryChipGroup = view.findViewById(R.id.categoryChipGroup);

        newsAdapter = new NewsAdapter(newsList, this::onNewsItemClick);
        rvNews.setLayoutManager(new LinearLayoutManager(getContext()));
        rvNews.setAdapter(newsAdapter);

        swipeRefresh.setOnRefreshListener(this::loadAllNews);

    }

    private void loadCategories() {
        if (!isAdded()) return;

        categoriesListener = categoriesRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if (!isAdded()) return;

                categories.clear();
                categoryChipGroup.removeAllViews();
                addCategoryChip("all", "All");

                for (DataSnapshot categorySnapshot : snapshot.getChildren()) {
                    Category category = categorySnapshot.getValue(Category.class);
                    if (category != null) {
                        categories.add(category);
                        addCategoryChip(category.getId(), category.getName());
                    }
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                if (!isAdded()) return;
                Toast.makeText(requireContext(), "Failed to load categories: " + error.getMessage(),
                        Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void addCategoryChip(String id, String name) {
        if (!isAdded()) return;

        Context context = requireContext();
        Chip chip = new Chip(context);
        chip.setText(name);
        chip.setCheckable(true);
        chip.setTag(id);

        chip.setOnCheckedChangeListener((buttonView, isChecked) -> {
            if (isChecked && isAdded()) {
                if ("all".equals(id)) {
                    loadAllNews();
                } else {
                    loadNewsByCategory(id);
                }
            }
        });

        categoryChipGroup.addView(chip);
        if ("all".equals(id)) {
            chip.setChecked(true);
        }
    }

    private void loadAllNews() {
        Log.d("TAG", "loadAllNews called");
        if (!isAdded()) return;

        if (!isDataLoaded || swipeRefresh.isRefreshing()) {
            swipeRefresh.setRefreshing(true);
        }

        if (newsListener != null) {
            newsRef.removeEventListener(newsListener);
        }

        newsListener = newsRef.orderByChild("timestamp").addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                Log.d("TAG", "loadAllNews - onDataChange");
                if (!isAdded()) return;

                try {
                    List<NewsItem> tempNewsList = new ArrayList<>();
                    for (DataSnapshot newsSnapshot : snapshot.getChildren()) {
                        NewsItem newsItem = newsSnapshot.getValue(NewsItem.class);
                        if (newsItem != null) {
                            newsItem.setId(newsSnapshot.getKey());
                            // Fetch counts for each news item
                            fetchCounts(newsItem, newsSnapshot.getKey());
                            tempNewsList.add(0, newsItem);
                        }
                    }

                    originalNewsList = new ArrayList<>(tempNewsList);
                    newsList.clear();
                    newsList.addAll(tempNewsList);

                    if (!currentQuery.isEmpty()) {
                        filterNews(currentQuery);
                    } else {
                        newsAdapter.updateNews(newsList);
                    }

                    isDataLoaded = true;
                    swipeRefresh.setRefreshing(false);

                    // Update statuses after loading
                    if (auth.getCurrentUser() != null) {
                        updateNewsStatus();
                    }

                } catch (Exception e) {
                    Log.e("TAG", "Error loading news", e);
                    if (isAdded()) {
                        Toast.makeText(requireContext(),
                                "Error loading news: " + e.getMessage(),
                                Toast.LENGTH_SHORT).show();
                    }
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                if (!isAdded()) return;

                Log.e("NewsFragment", "Database error: " + error.getMessage());

                // Update states
                isDataLoaded = false;
                swipeRefresh.setRefreshing(false);

                // Show error message
                Toast.makeText(requireContext(),
                        "Failed to load news: " + error.getMessage(),
                        Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void checkLikeStatus(NewsItem newsItem) {
        if (!isAdded() || auth.getCurrentUser() == null) return;

        String userId = auth.getCurrentUser().getUid();
        likesRef.child(newsItem.getId()).child(userId)
                .addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot snapshot) {
                        if (!isAdded()) return;
                        newsItem.setLiked(snapshot.exists());
                        newsAdapter.notifyDataSetChanged();
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError error) {
                        // Handle error
                    }
                });
    }

    private void checkBookmarkStatus(NewsItem newsItem) {
        if (!isAdded() || auth.getCurrentUser() == null) return;

        String userId = auth.getCurrentUser().getUid();
        bookmarksRef.child(userId).child(newsItem.getId())
                .addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot snapshot) {
                        if (!isAdded()) return;
                        newsItem.setBookmarked(snapshot.exists());
                        newsAdapter.notifyDataSetChanged();
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError error) {
                        // Handle error
                    }
                });
    }

    private void loadNewsByCategory(String categoryId) {
        swipeRefresh.setRefreshing(true);
        newsRef.orderByChild("category").equalTo(categoryId)
                .addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot snapshot) {
                        newsList.clear();
                        for (DataSnapshot newsSnapshot : snapshot.getChildren()) {
                            NewsItem newsItem = newsSnapshot.getValue(NewsItem.class);
                            if (newsItem != null) {
                                newsList.add(0, newsItem);
                            }
                        }
                        newsAdapter.updateNews(newsList);
                        swipeRefresh.setRefreshing(false);
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError error) {
                        swipeRefresh.setRefreshing(false);
                        Toast.makeText(getContext(), "Failed to load category news: " +
                                error.getMessage(), Toast.LENGTH_SHORT).show();
                    }
                });
    }

    private void onNewsItemClick(NewsItem newsItem) {
        if (!isAdded()) return;
        Log.d("NewsFragment", "Clicked news item: " + newsItem.getId());
        Log.d("NewsFragment", "Like count before passing: " + newsItem.getLikeCount());
        Log.d("NewsFragment", "Is liked status: " + newsItem.isLiked());
        Intent intent = new Intent(requireContext(), NewsDetailActivity.class);
        intent.putExtra("news_item", newsItem);
        startActivity(intent);
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        if (newsListener != null) {
            newsRef.removeEventListener(newsListener);
        }
        if (categoriesListener != null) {
            categoriesRef.removeEventListener(categoriesListener);
        }
    }

    @Override
    public void onStop() {
        super.onStop();
        isReturningFromDetail = false;
        Log.d("TAG", "onStop called");
    }

    private void fetchCounts(NewsItem newsItem, String newsId) {
        Log.d("NewsFragment", "Fetching counts for news: " + newsId);
        // Fetch like count
        likesRef.child(newsId).get().addOnSuccessListener(snapshot -> {
            if (isAdded()) {
                long likeCount = snapshot.getChildrenCount();
                Log.d("NewsFragment", "Fetched like count: " + likeCount);
                newsItem.setLikeCount(likeCount);
                newsAdapter.notifyDataSetChanged();
            }
        }).addOnFailureListener(e -> {
            Log.e("NewsFragment", "Error fetching like count", e);
        });
    }


}