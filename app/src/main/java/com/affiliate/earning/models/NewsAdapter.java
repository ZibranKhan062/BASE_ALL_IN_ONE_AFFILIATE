package com.affiliate.affiliate.models;

import android.content.Context;
import android.content.Intent;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.affiliate.affiliate.LoginActivityNew;
import com.affiliate.affiliate.R;
import com.google.android.material.dialog.MaterialAlertDialogBuilder;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.MutableData;
import com.google.firebase.database.Transaction;
import com.google.firebase.database.ValueEventListener;

import java.util.List;

public class NewsAdapter extends RecyclerView.Adapter<NewsAdapter.NewsViewHolder> {
    private List<NewsItem> newsList;
    private OnNewsItemClickListener listener;
    private FirebaseAuth auth;
    private DatabaseReference likesRef;
    private DatabaseReference bookmarksRef;
    private DatabaseReference newsRef;

    public interface OnNewsItemClickListener {
        void onNewsItemClick(NewsItem newsItem);
    }

    public NewsAdapter(List<NewsItem> newsList, OnNewsItemClickListener listener) {
        this.newsList = newsList;
        this.listener = listener;
        this.auth = FirebaseAuth.getInstance();
        FirebaseDatabase database = FirebaseDatabase.getInstance();
        this.likesRef = database.getReference("likes");
        this.bookmarksRef = database.getReference("bookmarks");
        this.newsRef = database.getReference("news");
    }

    @NonNull
    @Override
    public NewsViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.layout_news_item, parent, false);
        return new NewsViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull NewsViewHolder holder, int position) {
        NewsItem newsItem = newsList.get(position);
        holder.bind(newsItem);

    }

    @Override
    public int getItemCount() {
        return newsList.size();
    }

    public void updateNews(List<NewsItem> newsList) {
        this.newsList = newsList;
        notifyDataSetChanged();
    }

    class NewsViewHolder extends RecyclerView.ViewHolder {
        private ImageView ivNewsImage;
        private TextView tvCategory,countLike;
        private TextView tvTitle;
        private TextView tvDescription;
        private ImageButton btnLike;
        private ImageButton btnComment;
        private ImageButton btnShare;
        private ImageButton btnBookmark;

        public NewsViewHolder(@NonNull View itemView) {
            super(itemView);
            ivNewsImage = itemView.findViewById(R.id.ivNewsImage);
            tvCategory = itemView.findViewById(R.id.tvCategory);
            tvTitle = itemView.findViewById(R.id.tvTitle);
            tvDescription = itemView.findViewById(R.id.tvDescription);
            btnLike = itemView.findViewById(R.id.btnLike);
            btnComment = itemView.findViewById(R.id.btnComment);
            btnShare = itemView.findViewById(R.id.btnShare);
            btnBookmark = itemView.findViewById(R.id.btnBookmark);
            countLike = itemView.findViewById(R.id.countLike);
        }

        public void bind(NewsItem newsItem) {
            tvCategory.setText(newsItem.getCategory());
            tvTitle.setText(newsItem.getTitle());
            tvDescription.setText(newsItem.getDescription());

            // Load image using Glide
            Glide.with(itemView.getContext())
                    .load(newsItem.getImageUrl())
                    .placeholder(R.drawable.placeholder)
                    .error(R.drawable.placeholder)
                    .into(ivNewsImage);

            // Set click listeners
            itemView.setOnClickListener(v -> listener.onNewsItemClick(newsItem));

            btnLike.setOnClickListener(v -> handleLikeClick(newsItem));
//            btnComment.setOnClickListener(v -> handleCommentClick(newsItem));
            btnShare.setOnClickListener(v -> handleShareClick(newsItem));
            btnBookmark.setOnClickListener(v -> handleBookmarkClick(newsItem));

            // Update UI states
            updateLikeState(newsItem);
            updateBookmarkState(newsItem);
        }

        private void handleLikeClick(NewsItem newsItem) {
            if (auth.getCurrentUser() == null) {
                showLoginPrompt(itemView.getContext());
                return;
            }

            String userId = auth.getCurrentUser().getUid();
            String newsId = newsItem.getId();
            DatabaseReference userLikeRef = likesRef.child(newsId).child(userId);

            userLikeRef.addListenerForSingleValueEvent(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot snapshot) {
                    Log.d("NewsFragment", "Handling like click. Current exists: " + snapshot.exists());
                    if (snapshot.exists()) {
                        userLikeRef.removeValue();
                        newsItem.setLiked(false);
                        updateLikeCount(newsItem, false);
                    } else {
                        userLikeRef.setValue(true);
                        newsItem.setLiked(true);
                        updateLikeCount(newsItem, true);
                    }
                    updateLikeState(newsItem);
                }

                @Override
                public void onCancelled(@NonNull DatabaseError error) {
                    Log.e("NewsFragment", ""+ error);
                    Toast.makeText(itemView.getContext(),
                            "Failed to update like", Toast.LENGTH_SHORT).show();
                }
            });
        }


        private void handleBookmarkClick(NewsItem newsItem) {
            if (auth.getCurrentUser() == null) {
                showLoginPrompt(itemView.getContext());
                return;
            }

            String userId = auth.getCurrentUser().getUid();
            String newsId = newsItem.getId();
            DatabaseReference userBookmarkRef = bookmarksRef.child(userId).child(newsId);

            userBookmarkRef.addListenerForSingleValueEvent(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot snapshot) {
                    if (snapshot.exists()) {
                        userBookmarkRef.removeValue();
                        newsItem.setBookmarked(false);
                    } else {
                        userBookmarkRef.setValue(true);
                        newsItem.setBookmarked(true);
                    }
                    updateBookmarkState(newsItem);
                }

                @Override
                public void onCancelled(@NonNull DatabaseError error) {
                    Toast.makeText(itemView.getContext(),
                            "Failed to update bookmark", Toast.LENGTH_SHORT).show();
                }
            });
        }

        private void updateLikeCount(NewsItem newsItem, boolean increment) {
            DatabaseReference likeCountRef = newsRef.child(newsItem.getId()).child("likeCount");
            likeCountRef.runTransaction(new Transaction.Handler() {
                @NonNull
                @Override
                public Transaction.Result doTransaction(@NonNull MutableData mutableData) {
                    Integer currentValue = mutableData.getValue(Integer.class);
                    if (currentValue == null) {
                        mutableData.setValue(increment ? 1 : 0);
                    } else {
                        mutableData.setValue(increment ? currentValue + 1 : currentValue - 1);
                    }
                    return Transaction.success(mutableData);
                }

                @Override
                public void onComplete(DatabaseError error, boolean committed, DataSnapshot snapshot) {
                    if (error != null) {
                        Toast.makeText(itemView.getContext(),
                                "Error updating like count", Toast.LENGTH_SHORT).show();
                    }
                }
            });
        }

        private void showLoginPrompt(Context context) {
            new MaterialAlertDialogBuilder(context)
                    .setTitle("Login Required")
                    .setMessage("Please login to access this feature")
                    .setPositiveButton("Login", (dialog, which) -> {
                        context.startActivity(new Intent(context, LoginActivityNew.class));
                    })
                    .setNegativeButton("Cancel", null)
                    .show();
        }


        private void handleShareClick(NewsItem newsItem) {
            Intent shareIntent = new Intent(Intent.ACTION_SEND);
            shareIntent.setType("text/plain");
            shareIntent.putExtra(Intent.EXTRA_SUBJECT, newsItem.getTitle());
            shareIntent.putExtra(Intent.EXTRA_TEXT,
                    newsItem.getTitle() + "\n\nRead more at: [Your App URL]");
            itemView.getContext().startActivity(
                    Intent.createChooser(shareIntent, "Share via"));
        }




        private void updateLikeState(NewsItem newsItem) {
            // Update like button state based on user interaction
            btnLike.setImageResource(newsItem.isLiked() ?
                    R.drawable.fav : R.drawable.baseline_favorite_border_24_new);

            // Update like count
            countLike.setText(String.valueOf(newsItem.getLikeCount()));
        }

        private void updateBookmarkState(NewsItem newsItem) {
            // Update bookmark button state based on user interaction
            btnBookmark.setImageResource(newsItem.isBookmarked() ?
                    R.drawable.ic_bookmark_filled : R.drawable.ic_bookmark);
        }
    }
}