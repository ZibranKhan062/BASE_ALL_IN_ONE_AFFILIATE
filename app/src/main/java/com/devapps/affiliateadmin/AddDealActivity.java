package com.devapps.affiliateadmin;

import android.os.Bundle;
import android.view.MenuItem;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.google.android.material.button.MaterialButton;
import com.google.android.material.textfield.TextInputEditText;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ServerValue;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class AddDealActivity extends AppCompatActivity {
    private DatabaseReference categoriesRef, dealsRef;
    private List<Category> categories = new ArrayList<>();
    private TextInputEditText titleInput, descriptionInput, imageUrlInput;
    private AutoCompleteTextView categoryInput;
    private MaterialButton submitButton;
    private String selectedCategoryId;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_deal);

        // Initialize Firebase
        FirebaseDatabase database = FirebaseDatabase.getInstance();
        categoriesRef = database.getReference("categories");
        dealsRef = database.getReference("news");

        // Initialize views
        titleInput = findViewById(R.id.titleInput);
        descriptionInput = findViewById(R.id.descriptionInput);
        imageUrlInput = findViewById(R.id.imageUrlInput);
        categoryInput = findViewById(R.id.categorySpinner);
        submitButton = findViewById(R.id.submitButton);

        // Load categories and setup submit button
        loadCategories();
        setupSubmitButton();
    }

    private void loadCategories() {
        categoriesRef.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                categories.clear();
                for (DataSnapshot categorySnapshot : snapshot.getChildren()) {
                    String categoryId = categorySnapshot.getKey();
                    String categoryName = categorySnapshot.child("name").getValue(String.class);
                    if (categoryName != null) {
                        Category category = new Category();
                        category.setId(categoryId);
                        category.setName(categoryName);
                        categories.add(category);
                    }
                }
                setupCategorySpinner();
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                Toast.makeText(AddDealActivity.this,
                        "Error loading categories: " + error.getMessage(),
                        Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void setupCategorySpinner() {
        // Create list of category names
        List<String> categoryNames = new ArrayList<>();
        for (Category category : categories) {
            categoryNames.add(category.getName());
        }

        // Create and set the adapter
        ArrayAdapter<String> adapter = new ArrayAdapter<>(
                this,
                R.layout.dropdown_item,  // Create this layout
                categoryNames
        );

        categoryInput.setAdapter(adapter);

        // Show dropdown when clicked
        categoryInput.setOnClickListener(v -> categoryInput.showDropDown());

        // Handle item selection
        categoryInput.setOnItemClickListener((parent, view, position, id) -> {
            selectedCategoryId = categories.get(position).getId();
        });

        // Set initial selection
        if (!categories.isEmpty()) {
            categoryInput.setText(categoryNames.get(0), false);
            selectedCategoryId = categories.get(0).getId();
        }
    }

    private void setupSubmitButton() {
        submitButton.setOnClickListener(v -> {
            if (validateForm()) {
                submitDeal();
            }
        });
    }

    private boolean validateForm() {
        boolean isValid = true;

        String title = titleInput.getText().toString().trim();
        String description = descriptionInput.getText().toString().trim();
        String imageUrl = imageUrlInput.getText().toString().trim();

        if (title.isEmpty()) {
            titleInput.setError("Title is required");
            isValid = false;
        }

        if (description.isEmpty()) {
            descriptionInput.setError("Description is required");
            isValid = false;
        }

        if (imageUrl.isEmpty()) {
            imageUrlInput.setError("Image URL is required");
            isValid = false;
        }

        if (selectedCategoryId == null) {
            Toast.makeText(this, "Please select a category", Toast.LENGTH_SHORT).show();
            isValid = false;
        }

        return isValid;
    }

    private void submitDeal() {
        // Disable submit button to prevent double submission
        submitButton.setEnabled(false);

        // Create new deal object
        String dealId = dealsRef.push().getKey();
        if (dealId == null) {
            Toast.makeText(this, "Error creating deal ID", Toast.LENGTH_SHORT).show();
            submitButton.setEnabled(true);
            return;
        }

        Map<String, Object> dealData = new HashMap<>();
        dealData.put("id", dealId);
        dealData.put("title", titleInput.getText().toString().trim());
        dealData.put("description", descriptionInput.getText().toString().trim());
        dealData.put("imageUrl", imageUrlInput.getText().toString().trim());
        dealData.put("category", selectedCategoryId);
        dealData.put("timestamp", ServerValue.TIMESTAMP);
        dealData.put("likes", 0);
        dealData.put("isSponsored", false);

        dealsRef.child(dealId).setValue(dealData)
                .addOnSuccessListener(aVoid -> {
                    Toast.makeText(AddDealActivity.this,
                            "Deal added successfully",
                            Toast.LENGTH_SHORT).show();
                    finish();
                })
                .addOnFailureListener(e -> {
                    Toast.makeText(AddDealActivity.this,
                            "Error adding deal: " + e.getMessage(),
                            Toast.LENGTH_SHORT).show();
                    submitButton.setEnabled(true);
                });
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == android.R.id.home) {
            onBackPressed();
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    // Category model class
    public static class Category {
        private String id;
        private String name;
        private String description;
        private String imageUrl;

        public Category() {
        }

        public String getId() {
            return id;
        }

        public void setId(String id) {
            this.id = id;
        }

        public String getName() {
            return name;
        }

        public void setName(String name) {
            this.name = name;
        }

        public String getDescription() {
            return description;
        }

        public void setDescription(String description) {
            this.description = description;
        }

        public String getImageUrl() {
            return imageUrl;
        }

        public void setImageUrl(String imageUrl) {
            this.imageUrl = imageUrl;
        }
    }
}