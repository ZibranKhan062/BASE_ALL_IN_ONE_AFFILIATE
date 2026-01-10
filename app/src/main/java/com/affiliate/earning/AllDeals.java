package com.affiliate.affiliate;

import android.os.Bundle;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.affiliate.affiliate.models.DealsModel;
import com.firebase.ui.database.FirebaseRecyclerOptions;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

public class AllDeals extends AppCompatActivity {

    RecyclerView dealsRecyclerView;
    FirebaseRecyclerOptions<DealsModel> dealsOptions;
    DealsAdapter dealsAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_all_deals);
        dealsRecyclerView = findViewById(R.id.dealsRecyclerView);

        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowHomeEnabled(true);
        getSupportActionBar().setDisplayShowTitleEnabled(false);
        loadDealsOffers();

    }

    @Override
    public boolean onSupportNavigateUp() {
        onBackPressed();
        return true;
    }

    public void loadDealsOffers() {
        DatabaseReference dealsReference = FirebaseDatabase.getInstance().getReference("Deals");
        dealsReference.keepSynced(true);

        dealsOptions = new FirebaseRecyclerOptions.Builder<DealsModel>()
                .setQuery(FirebaseDatabase.getInstance().getReference("Deals"), DealsModel.class)
                .build();


        dealsRecyclerView.setLayoutManager(new GridLayoutManager(AllDeals.this, 2));
//        dealsRecyclerView.scrollToPosition(0);
        dealsAdapter = new DealsAdapter(dealsOptions, AllDeals.this);
        dealsRecyclerView.setAdapter(dealsAdapter);
    }

    @Override
    protected void onStart() {
        super.onStart();
        dealsAdapter.startListening();
    }

    @Override
    protected void onStop() {
        super.onStop();
        dealsAdapter.stopListening();
    }
}