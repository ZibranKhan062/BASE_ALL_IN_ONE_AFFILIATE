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

import com.devapps.affiliateadmin.adapters.BannerImagesAppsAdapter;
import com.devapps.affiliateadmin.models.BannerModel;
import com.firebase.ui.database.FirebaseRecyclerOptions;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.orhanobut.dialogplus.DialogPlus;
import com.orhanobut.dialogplus.ViewHolder;

public class BannerImages extends AppCompatActivity {
    RecyclerView recyclerviewDetail;
    DatabaseReference databaseReference;
    FirebaseRecyclerOptions<BannerModel> options;
    FloatingActionButton adddItem;
    BannerImagesAppsAdapter bannerImagesAppsAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_banner_images);
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


        options = new FirebaseRecyclerOptions.Builder<BannerModel>()
                .setQuery(FirebaseDatabase.getInstance().getReference("SlidingImages"), BannerModel.class)
                .build();

        recyclerviewDetail.setLayoutManager(new LinearLayoutManager(this));
        bannerImagesAppsAdapter = new BannerImagesAppsAdapter(options, this);
        recyclerviewDetail.setAdapter(bannerImagesAppsAdapter);

    }

    @Override
    protected void onStart() {
        super.onStart();
        bannerImagesAppsAdapter.startListening();
    }

    @Override
    protected void onStop() {
        super.onStop();
        bannerImagesAppsAdapter.stopListening();
    }

    public void addItem() {
        final DialogPlus dialog = DialogPlus.newDialog(BannerImages.this)
                .setGravity(Gravity.CENTER)
                .setMargin(50, 0, 50, 0)
                .setContentHolder(new ViewHolder(R.layout.add_banner))
                .setExpanded(false)  // This will enable the expand feature, (similar to android L share dialog)
                .create();

        View holderView = dialog.getHolderView();
        dialog.show();
        final EditText imgLink = holderView.findViewById(R.id.imgLink);
        final EditText url = holderView.findViewById(R.id.url);

        Button addItem = holderView.findViewById(R.id.addItem);
        addItem.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String getImgLink = imgLink.getText().toString().trim();
                String getURL = url.getText().toString().trim();
                if (Config.isdemoEnabled) {
                    Toast.makeText(BannerImages.this, "Operation not allowed in Demo App", Toast.LENGTH_LONG).show();
                    return;
                }
                BannerModel bannerModel = new BannerModel(getURL, getImgLink);
                databaseReference = FirebaseDatabase.getInstance().getReference("SlidingImages");
                String getKey = databaseReference.push().getKey();
                databaseReference.child(getKey).setValue(bannerModel).addOnCompleteListener(new OnCompleteListener<Void>() {
                    @Override
                    public void onComplete(@NonNull Task<Void> task) {
                        if (task.isSuccessful()) {
                            Toast.makeText(BannerImages.this, "Item Added Successfully", Toast.LENGTH_SHORT).show();
                        } else {
                            Toast.makeText(BannerImages.this, "Some Error Occurred", Toast.LENGTH_SHORT).show();
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