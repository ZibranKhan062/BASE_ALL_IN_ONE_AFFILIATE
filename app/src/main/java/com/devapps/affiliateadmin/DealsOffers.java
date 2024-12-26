package com.devapps.affiliateadmin;

import android.os.Bundle;
import android.text.TextUtils;
import android.view.Gravity;
import android.view.View;
import android.widget.Button;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.SwitchCompat;
import androidx.appcompat.widget.Toolbar;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.devapps.affiliateadmin.adapters.DealsAdapter;
import com.devapps.affiliateadmin.models.DealsModel;
import com.firebase.ui.database.FirebaseRecyclerOptions;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.orhanobut.dialogplus.DialogPlus;
import com.orhanobut.dialogplus.ViewHolder;

public class DealsOffers extends AppCompatActivity {

    RecyclerView dealsRecyclerView;
    FloatingActionButton addHomeItem;
    DealsAdapter dealAdapter;
    FirebaseRecyclerOptions<DealsModel> options;
    boolean switch_position;

    @Override
    public boolean onSupportNavigateUp() {
        onBackPressed();
        return true;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_deals_offers);
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowHomeEnabled(true);
        getSupportActionBar().setDisplayShowTitleEnabled(false);
        addHomeItem = findViewById(R.id.addHomeItem);
        dealsRecyclerView = findViewById(R.id.dealsRecyclerView);
        addHomeItem.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                addItem(FirebaseDatabase.getInstance().getReference("Deals"));
            }
        });


        loadResources();

    }

    public void addItem(final DatabaseReference databaseReference1) {
        final DialogPlus dialog = DialogPlus.newDialog(DealsOffers.this)
                .setGravity(Gravity.CENTER)
                .setMargin(50, 0, 50, 0)
                .setContentHolder(new ViewHolder(R.layout.add_deals_items))
                .setExpanded(false)  // This will enable the expand feature, (similar to android L share dialog)
                .create();

        View holderView = dialog.getHolderView();
        dialog.show();

        final EditText prodName = holderView.findViewById(R.id.prodName);
        final EditText prodImage = holderView.findViewById(R.id.prodImage);
        final EditText sellingPrice = holderView.findViewById(R.id.sellingPrice);
        final EditText discountedPrice = holderView.findViewById(R.id.discountedPrice);
        final EditText percentOff = holderView.findViewById(R.id.percentOff);
        final EditText prodDescription = holderView.findViewById(R.id.prodDescription);
        final EditText prodLink = holderView.findViewById(R.id.prodLink);
        final EditText videoLink = holderView.findViewById(R.id.videoLink);
        final ImageView vidInfo = holderView.findViewById(R.id.vidInfo);
        final SwitchCompat switch_vidLink = holderView.findViewById(R.id.switch_vidLink);
        Button add_deal_item = holderView.findViewById(R.id.add_deal_item);
        final ImageView info = holderView.findViewById(R.id.info);

        info.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                new AlertDialog.Builder(DealsOffers.this)
                        .setTitle("Maximize use of Free Database")
                        .setMessage("Upload your Hi res images to Free websites like postimages.org/imgbb.com and just paste the Image Link here. 512x512 px recommended")
                        .setNegativeButton("OK", null)
                        .setIcon(android.R.drawable.ic_dialog_info)
                        .show();
            }
        });

        vidInfo.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                new AlertDialog.Builder(DealsOffers.this)
                        .setMessage("If the Video URL is https://www.youtube.com/watch?v=HwgpmIUHQOo then Video ID is HwgpmIUHQOo")
                        .setNegativeButton("OK", null)
                        .setIcon(android.R.drawable.ic_dialog_info)
                        .show();
            }
        });

        videoLink.setVisibility(View.GONE);
        switch_vidLink.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton compoundButton, boolean b) {

                if (b) {
                    videoLink.setVisibility(View.VISIBLE);
                    switch_position = true;

                } else {
                    videoLink.setVisibility(View.GONE);
                    switch_position = false;

                }
            }
        });


        add_deal_item.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String getprodName = prodName.getText().toString().trim();
                String getprodImage = prodImage.getText().toString().trim();
                String getsellingPrice = sellingPrice.getText().toString().trim();
                String getdiscountedPrice = discountedPrice.getText().toString().trim();
                String getpercentOff = percentOff.getText().toString().trim();
                String getprodDescription = prodDescription.getText().toString().trim();
                String getprodLink = prodLink.getText().toString().trim();
                String getVidLink = videoLink.getText().toString().trim();


                if (Config.isdemoEnabled) {
                    Toast.makeText(DealsOffers.this, "Operation not allowed in Demo App", Toast.LENGTH_LONG).show();
                    return;
                }

                if (switch_position == true) {
                    if (TextUtils.isEmpty(getprodName) || TextUtils.isEmpty(getprodImage)
                            || TextUtils.isEmpty(getsellingPrice)
                            || TextUtils.isEmpty(getdiscountedPrice)
                            || TextUtils.isEmpty(getpercentOff)
                            || TextUtils.isEmpty(getprodDescription) || TextUtils.isEmpty(getprodLink)
                            || TextUtils.isEmpty(getVidLink)

                    ) {
                        Toast.makeText(DealsOffers.this, "Please fill all fields", Toast.LENGTH_SHORT).show();
                        return;
                    }

                    pushData(getdiscountedPrice,
                            getpercentOff,
                            getprodImage,
                            getprodName,
                            getsellingPrice,
                            getprodDescription,
                            getprodLink,
                            getVidLink);
                    dialog.dismiss();
                } else {

                    if (TextUtils.isEmpty(getprodName) || TextUtils.isEmpty(getprodImage)
                            || TextUtils.isEmpty(getsellingPrice)
                            || TextUtils.isEmpty(getdiscountedPrice)
                            || TextUtils.isEmpty(getpercentOff)
                            || TextUtils.isEmpty(getprodDescription) || TextUtils.isEmpty(getprodLink)

                    ) {
                        Toast.makeText(DealsOffers.this, "Please fill all fields", Toast.LENGTH_SHORT).show();
                        return;
                    }
                    pushData(getdiscountedPrice,
                            getpercentOff,
                            getprodImage,
                            getprodName,
                            getsellingPrice,
                            getprodDescription,
                            getprodLink,
                            "n/a");
                    dialog.dismiss();
                }


            }
        });


    }

    public void loadResources() {

        options = new FirebaseRecyclerOptions.Builder<DealsModel>()
                .setQuery(FirebaseDatabase.getInstance().getReference("Deals"), DealsModel.class)
                .build();

        dealsRecyclerView.setLayoutManager(new GridLayoutManager(getApplicationContext(), 2));
        dealAdapter = new DealsAdapter(options, DealsOffers.this);
        dealsRecyclerView.setAdapter(dealAdapter);

    }

    public void pushData(String getdiscountedPrice, String getpercentOff, String getprodImage, String getprodName,
                         String getsellingPrice, String getprodDescription,
                         String getprodLink, String getVidLink) {


        DealsModel dealsModel = new DealsModel(getdiscountedPrice, getpercentOff, getprodImage,
                getprodName, getsellingPrice, getprodDescription, getprodLink, getVidLink);

        DatabaseReference databaseReference = FirebaseDatabase.getInstance().getReference("Deals");
        String getKey = databaseReference.push().getKey();
        databaseReference.child(getKey).setValue(dealsModel).addOnCompleteListener(new OnCompleteListener<Void>() {
            @Override
            public void onComplete(@NonNull Task<Void> task) {
                if (task.isSuccessful()) {
                    Toast.makeText(DealsOffers.this, "Task Completed Successfully", Toast.LENGTH_SHORT).show();
                } else {
                    Toast.makeText(DealsOffers.this, "Some Error Occurred", Toast.LENGTH_SHORT).show();
                }


            }


        });
    }

    @Override
    protected void onStart() {
        super.onStart();
        dealAdapter.startListening();
    }

    @Override
    protected void onStop() {
        super.onStop();
        dealAdapter.stopListening();
    }


}