package com.devapps.affiliateadmin;

import android.os.Bundle;
import android.view.Gravity;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.devapps.affiliateadmin.adapters.FeaturedAppsAdapter;
import com.devapps.affiliateadmin.models.FeaturedAppsModel;
import com.firebase.ui.database.FirebaseRecyclerOptions;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.orhanobut.dialogplus.DialogPlus;
import com.orhanobut.dialogplus.ViewHolder;

public class AppListings extends AppCompatActivity {
    RecyclerView recyclerviewDetail;
    DatabaseReference databaseReference;
    FirebaseRecyclerOptions<FeaturedAppsModel> options;
    FloatingActionButton adddItem;
    FeaturedAppsAdapter featuredAppsAdapter;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_app_listings);
        recyclerviewDetail = findViewById(R.id.recyclerviewDetail);
        adddItem = findViewById(R.id.adddItem);
        adddItem.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                addItem();
            }
        });
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowHomeEnabled(true);
        getSupportActionBar().setDisplayShowTitleEnabled(false);

        options = new FirebaseRecyclerOptions.Builder<FeaturedAppsModel>()
                .setQuery(FirebaseDatabase.getInstance().getReference("FeaturedApps"), FeaturedAppsModel.class)
                .build();

        recyclerviewDetail.setLayoutManager(new LinearLayoutManager(this));
        featuredAppsAdapter = new FeaturedAppsAdapter(options, this);
        recyclerviewDetail.setAdapter(featuredAppsAdapter);

    }

    @Override
    protected void onStart() {
        super.onStart();
        featuredAppsAdapter.startListening();
    }

    @Override
    protected void onStop() {
        super.onStop();
        featuredAppsAdapter.stopListening();
    }

    public void addItem() {
        final DialogPlus dialog = DialogPlus.newDialog(AppListings.this)
                .setGravity(Gravity.CENTER)
                .setMargin(50, 0, 50, 0)
                .setContentHolder(new ViewHolder(R.layout.add_app_listing_content))
                .setExpanded(false)  // This will enable the expand feature, (similar to android L share dialog)
                .create();

        View holderView = dialog.getHolderView();
        dialog.show();
        final EditText name = holderView.findViewById(R.id.name);
        final EditText imgLink = holderView.findViewById(R.id.imgLink);
        final EditText link = holderView.findViewById(R.id.link);
        final EditText ratings = holderView.findViewById(R.id.ratings);

        Button addItem = holderView.findViewById(R.id.add_item);
        addItem.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String getName = name.getText().toString().trim();
                String getImgLink = imgLink.getText().toString().trim();
                String getLink = link.getText().toString().trim();

                float getRatings = Float.parseFloat(ratings.getText().toString().trim());

                FeaturedAppsModel featuredAppsModel = new FeaturedAppsModel(getImgLink, getName, getLink, getRatings);
                databaseReference = FirebaseDatabase.getInstance().getReference("FeaturedApps");
                String getKey = databaseReference.push().getKey();
                databaseReference.child(getKey).setValue(featuredAppsModel).addOnCompleteListener(new OnCompleteListener<Void>() {
                    @Override
                    public void onComplete(@NonNull Task<Void> task) {
                        if (task.isSuccessful()) {
                            Toast.makeText(AppListings.this, "Item Added Successfully", Toast.LENGTH_SHORT).show();
                        } else {
                            Toast.makeText(AppListings.this, "Some Error Occurred", Toast.LENGTH_SHORT).show();
                        }

                        dialog.dismiss();
                    }


                });
            }
        });


    }

    @Override
    public boolean onSupportNavigateUp() {
        onBackPressed();
        return true;
    }
}