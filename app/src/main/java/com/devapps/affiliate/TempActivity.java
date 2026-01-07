package com.devapps.affiliate;

import android.os.Bundle;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.devapps.affiliate.models.TempModel;
import com.firebase.ui.database.FirebaseRecyclerOptions;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

public class TempActivity extends AppCompatActivity {
    DatabaseReference databaseReference;

    RecyclerView recyclerView;
    FirebaseRecyclerOptions<TempModel> options;
    TempAdapter tempAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_temp);
        recyclerView = findViewById(R.id.recyclerView);

        loadResources();
    }

    public void loadResources() {


        databaseReference = FirebaseDatabase.getInstance().getReference("HomeItems");
        databaseReference.keepSynced(true);


        options = new FirebaseRecyclerOptions.Builder<TempModel>()
                .setQuery(FirebaseDatabase.getInstance().getReference("HomeItems"), TempModel.class)
                .build();

        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        tempAdapter = new TempAdapter(options, TempActivity.this);
        tempAdapter.startListening();
        recyclerView.setAdapter(tempAdapter);
    }

    @Override
    public void onStart() {
        super.onStart();
        tempAdapter.startListening();
    }

    @Override
    public void onStop() {
        super.onStop();
        tempAdapter.stopListening();
    }

}