package com.devapps.affiliateadmin;

import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.devapps.affiliateadmin.adapters.CategoryAdapter;
import com.devapps.affiliateadmin.models.Category;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class CategoriesFragment extends Fragment implements SearchableFragment, RefreshableFragment {
    private static final String TAG = "CategoriesFragment";
    private RecyclerView recyclerView;
    private CategoryAdapter adapter;
    private DatabaseReference categoriesRef;
    private List<Category> categories;
//    private SwipeRefreshLayout swipeRefreshLayout;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_categories, container, false);
        initViews(view);
        setupRecyclerView();
        initFirebase();
        loadCategories();
        return view;
    }

    private void initViews(View view) {
        recyclerView = view.findViewById(R.id.recyclerView);
//        swipeRefreshLayout = view.findViewById(R.id.swipeRefreshLayout);
//        swipeRefreshLayout.setOnRefreshListener(this::onRefresh);
        FloatingActionButton fabAddCategory = view.findViewById(R.id.fabAddCategory);
        fabAddCategory.setOnClickListener(v -> showAddDialog());

    }

    private void showAddDialog() {
        View dialogView = getLayoutInflater().inflate(R.layout.dialog_add_category, null);
        EditText etName = dialogView.findViewById(R.id.etCategoryName);
        EditText etDescription = dialogView.findViewById(R.id.etCategoryDescription);
        EditText etImgLink = dialogView.findViewById(R.id.etImgLink);

        AlertDialog.Builder builder = new AlertDialog.Builder(requireContext());
        builder.setTitle("Add New Category")
                .setView(dialogView)
                .setPositiveButton("Add", (dialog, which) -> {

                    if (Config.isdemoEnabled) {
                        Toast.makeText(getActivity(), "This feature is not available in demo mode.", Toast.LENGTH_SHORT).show();
                        return;
                    }
                    String name = etName.getText().toString().trim();
                    String description = etDescription.getText().toString().trim();
                    String imageUrl = etImgLink.getText().toString().trim();

                    if (TextUtils.isEmpty(name)) {
                        Toast.makeText(getContext(), "Name is required", Toast.LENGTH_SHORT).show();
                        return;
                    }

                    // Create new category with imageUrl
                    String categoryId = categoriesRef.push().getKey();
                    if (categoryId != null) {
                        // Create a HashMap to ensure all fields are properly set
                        Map<String, Object> categoryData = new HashMap<>();
                        categoryData.put("id", categoryId);
                        categoryData.put("name", name);
                        categoryData.put("description", description);
                        categoryData.put("imageUrl", imageUrl); // Explicitly set imageUrl

                        // Log the data being saved
                        Log.d(TAG, "Saving category with imageUrl: " + imageUrl);

                        categoriesRef.child(categoryId).setValue(categoryData)
                                .addOnSuccessListener(aVoid -> {
                                    Log.d(TAG, "Category saved successfully with imageUrl");
                                    Toast.makeText(getContext(), "Category added successfully", Toast.LENGTH_SHORT).show();
                                    notifyAdapterDataChanged();
                                })
                                .addOnFailureListener(e -> {
                                    Log.e(TAG, "Error adding category: ", e);
                                    Toast.makeText(getContext(), "Failed to add category", Toast.LENGTH_SHORT).show();
                                });
                    }
                })
                .setNegativeButton("Cancel", null);

        AlertDialog dialog = builder.create();
        dialog.show();
    }

    private void setupRecyclerView() {
        categories = new ArrayList<>();
        adapter = new CategoryAdapter(categories, this::onCategoryClick);
        recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
        recyclerView.setAdapter(adapter);
    }

    private void onCategoryClick(Category category) {
        showEditDialog(category);
    }

    private void initFirebase() {
        FirebaseDatabase database = FirebaseDatabase.getInstance();
//        database.setPersistenceEnabled(true);
        categoriesRef = database.getReference().child("categories");
        categoriesRef.keepSynced(true);
    }

    private void loadCategories() {
//        swipeRefreshLayout.setRefreshing(true);

        categoriesRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                Log.d(TAG, "Loading categories. Snapshot exists: " + snapshot.exists());
                categories.clear();

                for (DataSnapshot ds : snapshot.getChildren()) {
                    try {
                        Category category = ds.getValue(Category.class);
                        if (category != null) {
                            category.setId(ds.getKey());
                            categories.add(category);
                            Log.d(TAG, "Added category: " + category.getName());
                        }
                    } catch (Exception e) {
                        Log.e(TAG, "Error parsing category: ", e);
                    }
                }

                Log.d(TAG, "Loaded " + categories.size() + " categories");
                notifyAdapterDataChanged();
//                swipeRefreshLayout.setRefreshing(false);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                Log.e(TAG, "Error loading categories: " + error.getMessage());
                Toast.makeText(getContext(), "Failed to load categories: " + error.getMessage(),
                        Toast.LENGTH_SHORT).show();
//                swipeRefreshLayout.setRefreshing(false);
            }
        });
    }

    private void notifyAdapterDataChanged() {
        if (adapter != null) {
            adapter.notifyDataSetChanged();
            adapter.getFilter().filter(""); // Reset filter to show all items
        }
    }

    private void showEditDialog(Category category) {
        View dialogView = getLayoutInflater().inflate(R.layout.dialog_add_category, null);
        EditText etName = dialogView.findViewById(R.id.etCategoryName);
        EditText etDescription = dialogView.findViewById(R.id.etCategoryDescription);
        EditText etImgLink = dialogView.findViewById(R.id.etImgLink);

        etName.setText(category.getName());
        etDescription.setText(category.getDescription());
        etImgLink.setText(category.getImageUrl());

        AlertDialog.Builder builder = new AlertDialog.Builder(requireContext());
        builder.setTitle("Edit Category")
                .setView(dialogView)
                .setPositiveButton("Save", (dialog, which) -> {


                    if (Config.isdemoEnabled) {
                        Toast.makeText(getActivity(), "This feature is not available in demo mode.", Toast.LENGTH_SHORT).show();
                        return;
                    }

                    String name = etName.getText().toString().trim();
                    String description = etDescription.getText().toString().trim();
                    String imageUrl = etImgLink.getText().toString().trim();

                    if (!TextUtils.isEmpty(name)) {
                        category.setName(name);
                        category.setDescription(description);
                        category.setImageUrl(imageUrl);
                        updateCategory(category);
                    } else {
                        Toast.makeText(getContext(), "Name is required", Toast.LENGTH_SHORT).show();
                    }
                })
                .setNegativeButton("Cancel", null)
                .setNeutralButton("Delete", (dialog, which) -> {

                    if (Config.isdemoEnabled) {
                        Toast.makeText(getActivity(), "This feature is not available in demo mode.", Toast.LENGTH_SHORT).show();
                        return;
                    }
                    showDeleteConfirmation(category);
                });

        builder.create().show();
    }

    private void showDeleteConfirmation(Category category) {
        new AlertDialog.Builder(requireContext())
                .setTitle("Delete Category")
                .setMessage("Are you sure you want to delete this category?")
                .setPositiveButton("Delete", (dialog, which) -> deleteCategory(category))
                .setNegativeButton("Cancel", null)
                .show();
    }

    private void updateCategory(Category category) {
        categoriesRef.child(category.getId()).setValue(category)
                .addOnSuccessListener(aVoid -> {
                    Toast.makeText(getContext(), "Category updated", Toast.LENGTH_SHORT).show();
                    notifyAdapterDataChanged();
                })
                .addOnFailureListener(e -> {
                    Log.e(TAG, "Error updating category: ", e);
                    Toast.makeText(getContext(), "Update failed", Toast.LENGTH_SHORT).show();
                });
    }

    private void deleteCategory(Category category) {
        categoriesRef.child(category.getId()).removeValue()
                .addOnSuccessListener(aVoid -> {
                    Toast.makeText(getContext(), "Category deleted", Toast.LENGTH_SHORT).show();
                    notifyAdapterDataChanged();
                })
                .addOnFailureListener(e -> {
                    Log.e(TAG, "Error deleting category: ", e);
                    Toast.makeText(getContext(), "Delete failed", Toast.LENGTH_SHORT).show();
                });
    }

    @Override
    public void onSearch(String query) {
        if (adapter != null) {
            adapter.getFilter().filter(query);
        }
    }

    @Override
    public void onRefresh() {
        loadCategories();
    }
}