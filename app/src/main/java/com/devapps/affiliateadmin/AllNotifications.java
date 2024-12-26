package com.devapps.affiliateadmin;

import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.devapps.affiliateadmin.models.NotificationModel;
import com.firebase.ui.database.FirebaseRecyclerOptions;
import com.google.firebase.database.FirebaseDatabase;

import java.util.ArrayList;
import java.util.List;

public class AllNotifications extends AppCompatActivity {

    TextView toolbarTextView;
    RecyclerView notifRecyclerView;
    ImageView no_notif;
    TextView notif_desc, dateTime;

    NotificationAdaper notificationAdaper;

    List<NotificationModel> notificationModelList;
    FirebaseRecyclerOptions<NotificationModel> options;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_all_notifications);

        Toolbar toolbar = findViewById(R.id.toolbar);
        toolbarTextView = findViewById(R.id.toolbarTextView);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowHomeEnabled(true);
        getSupportActionBar().setDisplayShowTitleEnabled(false);
        toolbarTextView.setText("Notifications");
        notifRecyclerView = findViewById(R.id.notifRecyclerView);
        no_notif = findViewById(R.id.no_notif);
        dateTime = findViewById(R.id.dateTime);
//        Linkify.addLinks(notif_desc, Linkify.ALL);

        loadResources();

    }

    @Override
    public boolean onSupportNavigateUp() {
        onBackPressed();
        return true;
    }

    public void loadResources() {

        notificationModelList = new ArrayList<>();


        LinearLayoutManager layoutManager = new LinearLayoutManager(AllNotifications.this);
        notifRecyclerView.setLayoutManager(layoutManager);


        options = new FirebaseRecyclerOptions.Builder<NotificationModel>()
                .setQuery(FirebaseDatabase.getInstance().getReference("Notifications"), NotificationModel.class)
                .build();

        notifRecyclerView.setLayoutManager(new LinearLayoutManager(this));
        notificationAdaper = new NotificationAdaper(options, this);
        notifRecyclerView.setAdapter(notificationAdaper);
//        checkEmpty();

//        databaseReference.addValueEventListener(new ValueEventListener() {
//            @Override
//            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
//                if (dataSnapshot.exists()) {
//
//                    for (DataSnapshot dataSnapshot1 : dataSnapshot.getChildren()) {
//
//                        NotificationModel notificationModel = dataSnapshot1.getValue(NotificationModel.class);
//                        notificationModelList.add(notificationModel);
//                    }
//
//                    notificationAdaper = new NotificationAdaper(notificationModelList, AllNotifications.this);
//
//                    checkEmpty();
//                    notifRecyclerView.setAdapter(notificationAdaper);
//
//                } else {
//                    Toast.makeText(AllNotifications.this, "No data available !", Toast.LENGTH_SHORT).show();
//
//
//                }
//
//            }
//
//            @Override
//            public void onCancelled(@NonNull DatabaseError databaseError) {
//                Toast.makeText(AllNotifications.this, "Error" + databaseError.getMessage(), Toast.LENGTH_SHORT).show();
//
//            }
//        });
    }

    private void checkEmpty() {
        if (notificationAdaper.getItemCount() == 0) {
            Log.e("Item count", String.valueOf(notificationAdaper.getItemCount()));
            no_notif.setVisibility(View.VISIBLE);
        } else {
            no_notif.setVisibility(View.GONE);
        }
    }

    @Override
    protected void onStart() {
        super.onStart();
        notificationAdaper.startListening();
    }

    @Override
    protected void onStop() {
        super.onStop();
        notificationAdaper.stopListening();
    }
}
