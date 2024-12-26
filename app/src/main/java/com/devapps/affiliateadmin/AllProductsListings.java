package com.devapps.affiliateadmin;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.Gravity;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import com.devapps.affiliateadmin.adapters.AllProductsListingsAdapter;
import com.devapps.affiliateadmin.models.AddNewProductModel;
import com.devapps.affiliateadmin.models.AllProductsListingsModel;
import com.firebase.ui.database.FirebaseRecyclerOptions;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.orhanobut.dialogplus.DialogPlus;
import com.orhanobut.dialogplus.ViewHolder;

public class AllProductsListings extends AppCompatActivity {

    private static final String TAG = AllProductsListings.class.getSimpleName();
    Intent intent;
    DatabaseReference databaseReference;
    DatabaseReference databaseReference1;
    SwipeRefreshLayout mSwipeRefreshLayout;
    ProgressBar progressbar;
    RecyclerView recyclerView;
    FirebaseRecyclerOptions<AllProductsListingsModel> options;
    AllProductsListingsAdapter allProductsListingsAdapter;
    FloatingActionButton addItem;
    TextView trendingText;
    String itemName;

    String receivedKey;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_all_products_listings);
        intent = getIntent();
        receivedKey = intent.getStringExtra("CurrentPosition");
        itemName = intent.getStringExtra("itemName");
        Log.e(TAG, receivedKey);
        recyclerView = findViewById(R.id.recyclerView);
        trendingText = findViewById(R.id.trendingText);

        mSwipeRefreshLayout = findViewById(R.id.swiperefresh);
        progressbar = findViewById(R.id.progressbar);
        addItem = findViewById(R.id.addItem);
        progressbar.setVisibility(View.VISIBLE);

        trendingText.setText(itemName.trim());
        databaseReference1 = FirebaseDatabase.getInstance().getReference("ShopCategories").child(receivedKey).child("items");
        options = new FirebaseRecyclerOptions.Builder<AllProductsListingsModel>()
                .setQuery(databaseReference1, AllProductsListingsModel.class)
                .build();

        recyclerView.setLayoutManager(new GridLayoutManager(AllProductsListings.this, 2));
        allProductsListingsAdapter = new AllProductsListingsAdapter(options, AllProductsListings.this);
        recyclerView.setAdapter(allProductsListingsAdapter);
        progressbar.setVisibility(View.GONE);

        mSwipeRefreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                allProductsListingsAdapter.notifyDataSetChanged();
                mSwipeRefreshLayout.setRefreshing(false);
            }
        });


        addItem.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                addItem();
            }
        });

    }

    public void addItem() {
        final DialogPlus dialog = DialogPlus.newDialog(AllProductsListings.this)
                .setGravity(Gravity.CENTER)
                .setMargin(50, 0, 50, 20)
                .setContentHolder(new ViewHolder(R.layout.add_product_content))
                .setExpanded(false)  // This will enable the expand feature, (similar to android L share dialog)
                .create();

        View holderView = dialog.getHolderView();
        dialog.show();
        final EditText product_name = holderView.findViewById(R.id.product_name);
        final EditText product_pricing = holderView.findViewById(R.id.product_pricing);
        final EditText product_link = holderView.findViewById(R.id.product_link);
        final EditText product_image_link = holderView.findViewById(R.id.product_image_link);
        final EditText product_rating = holderView.findViewById(R.id.product_rating);
        final EditText total_ratings = holderView.findViewById(R.id.total_ratings);

        Button save_changes_btn = holderView.findViewById(R.id.save_changes_btn);
        save_changes_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String getName = product_name.getText().toString().trim();
                String getPricing = product_pricing.getText().toString().trim();
                String getLink = product_link.getText().toString().trim();
                String getImageLink = product_image_link.getText().toString().trim();
                float getRating = Float.parseFloat( product_rating.getText().toString().trim());
                String getTotalRating = total_ratings.getText().toString().trim();

                if (Config.isdemoEnabled) {
                    Toast.makeText(AllProductsListings.this, "Operation not allowed in Demo App", Toast.LENGTH_LONG).show();
                    return;
                }

                AddNewProductModel addNewProductModel = new AddNewProductModel(getImageLink, getLink, getTotalRating, getPricing, getRating, getName);
                databaseReference = FirebaseDatabase.getInstance().getReference("ShopCategories").child(receivedKey).child("items");
                String getKey = databaseReference.push().getKey();
                databaseReference.child(getKey).setValue(addNewProductModel).addOnCompleteListener(new OnCompleteListener<Void>() {
                    @Override
                    public void onComplete(@NonNull Task<Void> task) {
                        if (task.isSuccessful()) {
                            Toast.makeText(AllProductsListings.this, "Coupon Added Successfully", Toast.LENGTH_LONG).show();
                        } else {
                            Toast.makeText(AllProductsListings.this, "Some Error Occurred", Toast.LENGTH_SHORT).show();
                        }

                        dialog.dismiss();
                    }


                });
            }
        });


    }

    @Override
    protected void onStart() {
        super.onStart();
        allProductsListingsAdapter.startListening();
    }

    @Override
    protected void onStop() {
        super.onStop();
        allProductsListingsAdapter.stopListening();
    }

}