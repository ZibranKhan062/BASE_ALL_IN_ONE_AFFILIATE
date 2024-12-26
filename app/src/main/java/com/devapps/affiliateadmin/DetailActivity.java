package com.devapps.affiliateadmin;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.Gravity;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.devapps.affiliateadmin.adapters.DetailAdapter;
import com.devapps.affiliateadmin.models.DetailModel;
import com.firebase.ui.database.FirebaseRecyclerOptions;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.orhanobut.dialogplus.DialogPlus;
import com.orhanobut.dialogplus.ViewHolder;

import java.util.List;

public class DetailActivity extends AppCompatActivity {
    RecyclerView recyclerviewDetail;
    List<DetailModel> detailModelList;
    DetailAdapter detailAdapter;
    TextView headerText;
    FirebaseRecyclerOptions<DetailModel> options;
    FloatingActionButton adddItem;
    DatabaseReference databaseReference;
    Intent intent1;
    String updatedPos;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_detail);
        recyclerviewDetail = findViewById(R.id.recyclerviewDetail);
        adddItem = findViewById(R.id.adddItem);


        Intent intent = getIntent();
        intent1 = getIntent();
        final String rec_data =intent.getStringExtra("Data");

        updatedPos = intent1.getStringExtra("CurrentPosition");
        Log.e("Updated Position", updatedPos);

        updateNewData(FirebaseDatabase.getInstance().getReference("HomeItems").child(updatedPos).child("items"));
        recyclerviewDetail.setLayoutManager(new LinearLayoutManager(this));
        detailAdapter = new DetailAdapter(options, this);

        recyclerviewDetail.setAdapter(detailAdapter);

        adddItem.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                addItem(FirebaseDatabase.getInstance().getReference("HomeItems").child(updatedPos).child("items"));

            }
        });

    }


    @Override
    protected void onStart() {
        super.onStart();
        detailAdapter.startListening();
    }

    @Override
    protected void onStop() {
        super.onStop();
        detailAdapter.stopListening();
    }

    public void updateNewData(DatabaseReference databaseReferenceNode) {
        options = new FirebaseRecyclerOptions.Builder<DetailModel>()
                .setQuery(databaseReferenceNode, DetailModel.class)
                .build();
        detailAdapter = new DetailAdapter(options, this);
        recyclerviewDetail.setAdapter(detailAdapter);

    }

    public void addItem(final DatabaseReference databaseReference1) {
        final DialogPlus dialog = DialogPlus.newDialog(DetailActivity.this)
                .setGravity(Gravity.CENTER)
                .setMargin(50, 0, 50, 0)
                .setContentHolder(new ViewHolder(R.layout.add_item_content))
                .setExpanded(false)  // This will enable the expand feature, (similar to android L share dialog)
                .create();

        View holderView = dialog.getHolderView();
        dialog.show();
        final EditText title = holderView.findViewById(R.id.add_title);
        final EditText link = holderView.findViewById(R.id.add_link);
        final EditText imageLink = holderView.findViewById(R.id.add_image_link);
        Button addItem = holderView.findViewById(R.id.add_item);
        final ImageView info = holderView.findViewById(R.id.info);

        info.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                new AlertDialog.Builder(DetailActivity.this)
                        .setTitle("Maximize use of Free Database")
                        .setMessage("Upload your Hi res images to Free websites like postimages.org/imgbb.com and just paste the Image Link here. 512x512 px recommended")
                        .setNegativeButton("OK", null)
                        .setIcon(android.R.drawable.ic_dialog_info)
                        .show();
            }
        });


        addItem.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String getTitle = title.getText().toString().trim();
                String getLink = link.getText().toString().trim();
                String getImageLink = imageLink.getText().toString().trim();

                DetailModel detailModel = new DetailModel(getTitle, getImageLink, getLink);
                databaseReference = databaseReference1;

                String getKey = databaseReference1.push().getKey();
                databaseReference1.child(getKey).setValue(detailModel).addOnCompleteListener(new OnCompleteListener<Void>() {
                    @Override
                    public void onComplete(@NonNull Task<Void> task) {
                        if (task.isSuccessful()) {
                            Toast.makeText(DetailActivity.this, "Item Added Successfully", Toast.LENGTH_SHORT).show();
                        } else {
                            Toast.makeText(DetailActivity.this, "Some Error Occurred", Toast.LENGTH_SHORT).show();
                        }

                        dialog.dismiss();
                    }


                });
            }
        });


    }
}