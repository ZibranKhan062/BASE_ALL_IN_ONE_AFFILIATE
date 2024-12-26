package com.devapps.affiliateadmin;

import android.os.Bundle;
import android.text.TextUtils;
import android.view.Gravity;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.devapps.affiliateadmin.adapters.HomeAdapter;
import com.devapps.affiliateadmin.models.HomeModel;
import com.firebase.ui.database.FirebaseRecyclerOptions;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.orhanobut.dialogplus.DialogPlus;
import com.orhanobut.dialogplus.ViewHolder;

public class SiteCategories extends AppCompatActivity {

    RecyclerView recyclerView;
    DatabaseReference databaseReference;
    HomeAdapter homeAdapter;
    FloatingActionButton addHomeItem;
    FirebaseRecyclerOptions<HomeModel> options;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_site_categories);
        recyclerView = findViewById(R.id.recyclerView);
        addHomeItem = findViewById(R.id.addHomeItem);

        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowHomeEnabled(true);
        getSupportActionBar().setDisplayShowTitleEnabled(false);

        addHomeItem.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                addItem(FirebaseDatabase.getInstance().getReference("HomeItems"));
            }
        });
        loadResources();
    }

    public void addItem(final DatabaseReference databaseReference1) {
        final DialogPlus dialog = DialogPlus.newDialog(SiteCategories.this)
                .setGravity(Gravity.CENTER)
                .setMargin(50, 0, 50, 0)
                .setContentHolder(new ViewHolder(R.layout.add_home_items))
                .setExpanded(false)  // This will enable the expand feature, (similar to android L share dialog)
                .create();

        View holderView = dialog.getHolderView();
        dialog.show();

        final EditText Name = holderView.findViewById(R.id.name);
        final EditText Image = holderView.findViewById(R.id.imgLink);
        Button addItem = holderView.findViewById(R.id.add_home_item);
        final ImageView info = holderView.findViewById(R.id.info);

        info.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                new AlertDialog.Builder(SiteCategories.this)
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
                String getName = Name.getText().toString().trim();
                String getImage = Image.getText().toString().trim();


                if (TextUtils.isEmpty(getName) || TextUtils.isEmpty(getImage)

                ) {
                    Toast.makeText(SiteCategories.this, "Please fill all fields", Toast.LENGTH_SHORT).show();
                    return;
                }

                if (Config.isdemoEnabled) {
                    Toast.makeText(SiteCategories.this, "Operation not allowed in Demo App", Toast.LENGTH_LONG).show();
                    return;
                }

                AddHomeModel addHomeModel = new AddHomeModel(getName, getImage);
                databaseReference = databaseReference1;

                String getKey = databaseReference1.push().getKey();
                databaseReference1.child(getKey).setValue(addHomeModel).addOnCompleteListener(new OnCompleteListener<Void>() {
                    @Override
                    public void onComplete(@NonNull Task<Void> task) {
                        if (task.isSuccessful()) {
                            Toast.makeText(SiteCategories.this, "Task Completed Successfully", Toast.LENGTH_SHORT).show();
                        } else {
                            Toast.makeText(SiteCategories.this, "Some Error Occurred", Toast.LENGTH_SHORT).show();
                        }

                        dialog.dismiss();
                    }


                });


            }
        });


    }

    public void loadResources() {

        options = new FirebaseRecyclerOptions.Builder<HomeModel>()
                .setQuery(FirebaseDatabase.getInstance().getReference("HomeItems"), HomeModel.class)
                .build();

        recyclerView.setLayoutManager(new GridLayoutManager(getApplicationContext(), 2));
        homeAdapter = new HomeAdapter(options, SiteCategories.this);
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
        homeAdapter.stopListening();
    }

    @Override
    public boolean onSupportNavigateUp() {
        onBackPressed();
        return true;
    }

}