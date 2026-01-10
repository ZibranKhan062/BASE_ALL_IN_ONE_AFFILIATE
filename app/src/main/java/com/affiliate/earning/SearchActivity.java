package com.affiliate.earning;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.widget.EditText;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.affiliate.earning.homeelements.HomeAdapter;
import com.affiliate.earning.homeelements.HomeModel;
import com.firebase.ui.database.FirebaseRecyclerOptions;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

public class SearchActivity extends AppCompatActivity {


    EditText search;
    FirebaseRecyclerOptions<HomeModel> options;

    RecyclerView recyclerView;
    HomeAdapter homeAdapter;
    String ref = "HomeItems";
    public String TAG = SearchActivity.class.getSimpleName();

    TextView toolbarTextView;
    Intent intent;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_search);
        search = findViewById(R.id.search);
        recyclerView = findViewById(R.id.recyclerView);
        toolbarTextView = findViewById(R.id.toolbarTextView);

        Toolbar toolbar = findViewById(R.id.toolbar);
        toolbarTextView = findViewById(R.id.toolbarTextView);
        toolbarTextView.setText("Select a Category to search");
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowHomeEnabled(true);
        getSupportActionBar().setDisplayShowTitleEnabled(false);

        intent = getIntent();
        intent.getStringExtra("HomeSearch");
        Log.e("SearchAct", String.valueOf(intent.getStringExtra("HomeSearch")));
        loadResources();
        homeAdapter = new HomeAdapter(options, SearchActivity.this);
//        search.addTextChangedListener(new TextWatcher() {
//            @Override
//            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {
//                /* text methods */
//            }
//
//            @Override
//            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
//                /* text methods */
//
//            }
//
//            @Override
//            public void afterTextChanged(Editable editable) {
//
//
//                if (TextUtils.isEmpty(search.getText().toString())) {
//                    loadResources();
//                } else {
//                    DatabaseReference databaseReference = FirebaseDatabase.getInstance().getReference(ref);
//                    databaseReference.keepSynced(true);
//
//
//
//                    options = new FirebaseRecyclerOptions.Builder<HomeModel>()
//                            .setQuery(FirebaseDatabase.getInstance().getReference(ref).orderByChild("name").startAt(String.valueOf(editable)).endAt(editable + "\uf8ff"), HomeModel.class)
//                            .build();
//
//                    Log.e("afterTextChanged: ", String.valueOf(options.getSnapshots()));
//
//                    recyclerView.setLayoutManager(new GridLayoutManager(SearchActivity.this, 3));
//                    homeAdapter = new HomeAdapter(options, SearchActivity.this);
//                    homeAdapter.startListening();
//
//                    recyclerView.setAdapter(homeAdapter);
//                }
//
//            }
//
//        });
    }

    public void loadResources() {


        DatabaseReference databaseReference = FirebaseDatabase.getInstance().getReference("HomeItems");
        databaseReference.keepSynced(true);
        options = new FirebaseRecyclerOptions.Builder<HomeModel>()
                .setQuery(FirebaseDatabase.getInstance().getReference("HomeItems"), HomeModel.class)
                .build();

        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        homeAdapter = new HomeAdapter(options, this);
        homeAdapter.startListening();
        recyclerView.setAdapter(homeAdapter);
    }

    @Override
    protected void onStart() {
        super.onStart();
        homeAdapter.startListening();
    }

    @Override
    protected void onStop() {
        super.onStop();
        homeAdapter.startListening();
    }

    @Override
    public boolean onSupportNavigateUp() {
        onBackPressed();
        return true;
    }
}