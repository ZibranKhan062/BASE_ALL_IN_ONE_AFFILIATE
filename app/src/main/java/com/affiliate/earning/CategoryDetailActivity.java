package com.affiliate.affiliate;

import android.content.Context;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.affiliate.affiliate.trendingfiles.TrendingAdapter;
import com.affiliate.affiliate.trendingfiles.TrendingModel;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.List;

public class CategoryDetailActivity extends AppCompatActivity {
    RecyclerView recyclerView;
    ProgressBar progressbar;
    TextView catText;
    List<TrendingModel> trendingModelList;
    DatabaseReference databaseReference;
    Intent intent;
    String recPosition, recCatText;
    TrendingAdapter trendingAdapter;
    RelativeLayout no_notif;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_category_detail);

        recyclerView = findViewById(R.id.recyclerView);
        progressbar = findViewById(R.id.progressbar);
        catText = findViewById(R.id.catText);
        no_notif = findViewById(R.id.no_notif);
        intent = getIntent();
        recPosition = intent.getStringExtra("CurrentPosition");
        recCatText = intent.getStringExtra("itemName");
        catText.setText(recCatText.trim());
        trendingAdapter = new TrendingAdapter(trendingModelList,CategoryDetailActivity.this);

        checkInternetConnection();
        loadData();


    }

    private void checkEmptyDataSet() {
        if (trendingAdapter.getItemCount() == 0) {
            Log.e("Item count", String.valueOf(trendingAdapter.getItemCount()));
            no_notif.setVisibility(View.VISIBLE);
        } else {
            no_notif.setVisibility(View.GONE);
        }
    }

    private void loadData() {
        databaseReference =   FirebaseDatabase.getInstance().getReference("ShopCategories").child(recPosition).child("items");
        trendingModelList = new ArrayList<>();
        databaseReference.keepSynced(true);
        recyclerView.setLayoutManager(new GridLayoutManager(CategoryDetailActivity.this, 2));

        databaseReference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                if (dataSnapshot.exists()) {
                    trendingModelList.clear(); //change here
                    for (DataSnapshot dataSnapshot1 : dataSnapshot.getChildren()) {

                        TrendingModel trendingModel = dataSnapshot1.getValue(TrendingModel.class);
                        trendingModelList.add(trendingModel);
                    }

                    progressbar.setVisibility(View.GONE);
                    trendingAdapter = new TrendingAdapter(trendingModelList, CategoryDetailActivity.this);
                    checkEmptyDataSet();
                    recyclerView.setAdapter(trendingAdapter);
                    progressbar.setVisibility(View.GONE);

                } else {
                    Toast.makeText(CategoryDetailActivity.this, "No data available !", Toast.LENGTH_SHORT).show();
                    progressbar.setVisibility(View.GONE);

                }

            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                Toast.makeText(CategoryDetailActivity.this, "Error" + databaseError.getMessage(), Toast.LENGTH_SHORT).show();

            }
        });
    }

    private void checkInternetConnection() {
        if (!isNetworkAvailable()) {
            Toast.makeText(CategoryDetailActivity.this, "No Internet Connection Available", Toast.LENGTH_LONG).show();

        }

    }

    public boolean isNetworkAvailable() {
        ConnectivityManager connectivityManager
                = (ConnectivityManager) CategoryDetailActivity.this.getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo activeNetworkInfo = connectivityManager.getActiveNetworkInfo();
        return activeNetworkInfo != null && activeNetworkInfo.isConnected();
    }
}