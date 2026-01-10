package com.affiliate;

import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.affiliate.affiliate.R;
import com.affiliate.affiliate.models.NotificationModel;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.List;

public class NotificationActivity extends AppCompatActivity {

    TextView toolbarTextView;
    RecyclerView notifRecyclerView;
    ImageView no_notif;
    TextView notif_desc, dateTime;
    DatabaseReference databaseReference;
    NotificationAdaper notificationAdaper;

    List<NotificationModel> notificationModelList;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_notification);
        Toolbar toolbar = findViewById(R.id.toolbar);
        toolbarTextView = findViewById(R.id.toolbarTextView);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowHomeEnabled(true);
        getSupportActionBar().setDisplayShowTitleEnabled(false);
        toolbarTextView.setText("Notifications");
        notifRecyclerView = findViewById(R.id.notifRecyclerView);
        no_notif = findViewById(R.id.no_notif);
        notif_desc = (TextView) findViewById(R.id.notif_desc);
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
        databaseReference = FirebaseDatabase.getInstance().getReference("Notifications");
        databaseReference.keepSynced(true);

        LinearLayoutManager layoutManager = new LinearLayoutManager(NotificationActivity.this);
        notifRecyclerView.setLayoutManager(layoutManager);


        databaseReference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                if (dataSnapshot.exists()) {

                    for (DataSnapshot dataSnapshot1 : dataSnapshot.getChildren()) {

                        NotificationModel notificationModel = dataSnapshot1.getValue(NotificationModel.class);
                        notificationModelList.add(notificationModel);
                    }

                    notificationAdaper = new NotificationAdaper(notificationModelList, NotificationActivity.this);

                    checkEmpty();
                    notifRecyclerView.setAdapter(notificationAdaper);

                } else {
                    Toast.makeText(NotificationActivity.this, "No data available !", Toast.LENGTH_SHORT).show();


                }

            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                Toast.makeText(NotificationActivity.this, "Error" + databaseError.getMessage(), Toast.LENGTH_SHORT).show();

            }
        });
    }

    private void checkEmpty() {
        if (notificationAdaper.getItemCount() == 0) {
            Log.e("Item count", String.valueOf(notificationAdaper.getItemCount()));
            no_notif.setVisibility(View.VISIBLE);
        } else {
            no_notif.setVisibility(View.GONE);
        }
    }

}