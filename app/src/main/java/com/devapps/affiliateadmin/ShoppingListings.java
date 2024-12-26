package com.devapps.affiliateadmin;

import android.os.Bundle;
import android.view.Gravity;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.devapps.affiliateadmin.adapters.ShopAdapter;
import com.devapps.affiliateadmin.models.AddNewCatModel;
import com.devapps.affiliateadmin.models.CategoryModel;
import com.firebase.ui.database.FirebaseRecyclerOptions;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.orhanobut.dialogplus.DialogPlus;
import com.orhanobut.dialogplus.ViewHolder;

public class ShoppingListings extends AppCompatActivity {

    RecyclerView recyclerviewShop;
    ProgressBar progressbar;
    ShopAdapter shopAdapter;
    DatabaseReference databaseReference;
    FirebaseRecyclerOptions<CategoryModel> options;
    FloatingActionButton addItem;

    @Override
    public boolean onSupportNavigateUp() {
        onBackPressed();
        return true;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_shopping_listings);

        // Initialize views
        recyclerviewShop = findViewById(R.id.recyclerviewShop);
        progressbar = findViewById(R.id.progressbar);
        addItem = findViewById(R.id.addItem);

        // Setup toolbar
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowHomeEnabled(true);
        getSupportActionBar().setDisplayShowTitleEnabled(false);

        // Setup RecyclerView
        GridLayoutManager layoutManager = new GridLayoutManager(this, 3);
//        layoutManager.setSupportsPredictiveItemAnimations(false);
        recyclerviewShop.setLayoutManager(layoutManager);
        recyclerviewShop.setHasFixedSize(true);
        recyclerviewShop.setItemViewCacheSize(20);
        recyclerviewShop.setDrawingCacheEnabled(true);
        recyclerviewShop.setDrawingCacheQuality(View.DRAWING_CACHE_QUALITY_HIGH);

        // Initialize Firebase
        databaseReference = FirebaseDatabase.getInstance().getReference("ShopCategories");
        options = new FirebaseRecyclerOptions.Builder<CategoryModel>()
                .setQuery(databaseReference, CategoryModel.class)
                .setLifecycleOwner(this)
                .build();

        shopAdapter = new ShopAdapter(options, ShoppingListings.this);
        recyclerviewShop.setAdapter(shopAdapter);
        progressbar.setVisibility(View.GONE);

        addItem.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                final DialogPlus dialog = DialogPlus.newDialog(ShoppingListings.this)
                        .setGravity(Gravity.CENTER)
                        .setMargin(50, 0, 50, 20)
                        .setContentHolder(new ViewHolder(R.layout.add_shopping_item_content))
                        .setExpanded(false)
                        .create();

                View holderView = dialog.getHolderView();
                dialog.show();

                final EditText category_name = holderView.findViewById(R.id.category_name);
                final EditText category_image = holderView.findViewById(R.id.category_image);
                final ImageView info = holderView.findViewById(R.id.info);

                info.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        new AlertDialog.Builder(ShoppingListings.this)
                                .setTitle("Maximize use of Free Database")
                                .setMessage("Upload your Hi res images to Free websites like postimages.org/imgbb.com and just paste the Image Link here. 512x512 px recommended")
                                .setNegativeButton("OK", null)
                                .setIcon(android.R.drawable.ic_dialog_info)
                                .show();
                    }
                });

                Button add_category = holderView.findViewById(R.id.add_category);
                add_category.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        if (Config.isdemoEnabled) {
                            Toast.makeText(ShoppingListings.this, "Operation not allowed in Demo App", Toast.LENGTH_SHORT).show();
                            return;
                        }

                        final String category_name_text = category_name.getText().toString().trim();
                        final String category_image_text = category_image.getText().toString().trim();

                        if (category_name_text.isEmpty() || category_image_text.isEmpty()) {
                            Toast.makeText(ShoppingListings.this, "Please fill all fields", Toast.LENGTH_SHORT).show();
                            return;
                        }

                        AddNewCatModel addNewCatModel = new AddNewCatModel(category_name_text, category_image_text);
                        String getKey = databaseReference.push().getKey();
                        databaseReference.child(getKey).setValue(addNewCatModel)
                                .addOnCompleteListener(new OnCompleteListener<Void>() {
                                    @Override
                                    public void onComplete(@NonNull Task<Void> task) {
                                        if (task.isSuccessful()) {
                                            Toast.makeText(ShoppingListings.this, "Category Added Successfully", Toast.LENGTH_SHORT).show();
                                        } else {
                                            Toast.makeText(ShoppingListings.this, "Some Error Occurred", Toast.LENGTH_SHORT).show();
                                        }
                                        dialog.dismiss();
                                    }
                                });
                    }
                });
            }
        });
    }

    @Override
    protected void onStart() {
        super.onStart();
        if (shopAdapter != null) {
            shopAdapter.startListening();
        }
    }

    @Override
    protected void onStop() {
        super.onStop();
        if (shopAdapter != null) {
            shopAdapter.stopListening();
        }
    }
}