package com.devapps.affiliateadmin;

import android.app.ProgressDialog;
import android.os.Bundle;
import android.view.Gravity;
import android.view.View;
import android.widget.Button;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.SwitchCompat;
import androidx.appcompat.widget.Toolbar;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.devapps.affiliateadmin.adapters.VideoAdapter;
import com.devapps.affiliateadmin.models.VideoModel;
import com.firebase.ui.database.FirebaseRecyclerOptions;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.orhanobut.dialogplus.DialogPlus;
import com.orhanobut.dialogplus.ViewHolder;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

public class VideoActivity extends AppCompatActivity {

    RecyclerView recyclerViewVideos;
    FloatingActionButton addVideos;

    ProgressDialog pd;
    VideoAdapter videoAdapter;
    FirebaseRecyclerOptions<VideoModel> options;
    DatabaseReference databaseReference;
    boolean switch_position;

    @Override
    public boolean onSupportNavigateUp() {
        onBackPressed();
        return true;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_video);
        recyclerViewVideos = findViewById(R.id.recyclerViewVideos);
        addVideos = findViewById(R.id.addVideos);
        addVideos.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                addVideos();
            }
        });

        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowHomeEnabled(true);
        getSupportActionBar().setDisplayShowTitleEnabled(false);


        options = new FirebaseRecyclerOptions.Builder<VideoModel>()
                .setQuery(FirebaseDatabase.getInstance().getReference("Videos"), VideoModel.class)
                .build();

        recyclerViewVideos.setLayoutManager(new LinearLayoutManager(this));
        videoAdapter = new VideoAdapter(options, this, VideoActivity.this);
        recyclerViewVideos.setAdapter(videoAdapter);
    }


    public void addVideos() {

        final DialogPlus dialog = DialogPlus.newDialog(VideoActivity.this)
                .setGravity(Gravity.CENTER)
                .setMargin(50, 0, 50, 0)
                .setContentHolder(new ViewHolder(R.layout.add_video_popup))
                .setExpanded(false)  // This will enable the expand feature, (similar to android L share dialog)
                .create();

        View holderView = dialog.getHolderView();
        dialog.show();
        final EditText vidTitle = holderView.findViewById(R.id.vidTitle);
        final EditText vidID = holderView.findViewById(R.id.vidID);
        final EditText vidDesc = holderView.findViewById(R.id.vidDesc);
        final EditText buy_link = holderView.findViewById(R.id.buy_link);
        final TextView buyLabel = holderView.findViewById(R.id.buyLabel);
        final SwitchCompat switch_buy = holderView.findViewById(R.id.switch_buy);
        ImageView info = holderView.findViewById(R.id.info);
        info.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                new AlertDialog.Builder(VideoActivity.this)
                        .setMessage("If the Video URL is https://www.youtube.com/watch?v=HwgpmIUHQOo then Video ID is HwgpmIUHQOo")
                        .setNegativeButton("OK", null)
                        .setIcon(android.R.drawable.ic_dialog_info)
                        .show();
            }
        });


        buy_link.setVisibility(View.GONE);
        switch_buy.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton compoundButton, boolean b) {

                if (b) {
                    buy_link.setVisibility(View.VISIBLE);
                    switch_position = true;

                } else {
                    buy_link.setVisibility(View.GONE);
                    switch_position = false;

                }
            }
        });

        Button addItem = holderView.findViewById(R.id.add_item);
        addItem.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (Config.isdemoEnabled) {
                    Toast.makeText(VideoActivity.this, "Operation not allowed in Demo App", Toast.LENGTH_LONG).show();
                    return;
                }


                if (switch_position == true) {


                    String getTitle = vidTitle.getText().toString().trim();
                    String getVidID = vidID.getText().toString().trim();
                    String getVidDesc = vidDesc.getText().toString().trim();
                    String getBuyLink = buy_link.getText().toString().trim();
                    String date = new SimpleDateFormat("yyyy-MM-dd", Locale.getDefault()).format(new Date());

                    VideoModel videoModel = new VideoModel(getVidID, getTitle, getVidDesc, getBuyLink, date);
                    databaseReference = FirebaseDatabase.getInstance().getReference("Videos");
                    String getKey = databaseReference.push().getKey();
                    databaseReference.child(getKey).setValue(videoModel).addOnCompleteListener(new OnCompleteListener<Void>() {
                        @Override
                        public void onComplete(@NonNull Task<Void> task) {
                            if (task.isSuccessful()) {
                                Toast.makeText(VideoActivity.this, "Video Added Successfully", Toast.LENGTH_LONG).show();
                            } else {
                                Toast.makeText(VideoActivity.this, "Some Error Occurred", Toast.LENGTH_LONG).show();
                            }

                            dialog.dismiss();
                        }


                    });
                } else {

                    String getTitle = vidTitle.getText().toString().trim();
                    String getVidID = vidID.getText().toString().trim();
                    String getVidDesc = vidDesc.getText().toString().trim();
                    String getBuyLink = buy_link.getText().toString().trim();
                    String date = new SimpleDateFormat("yyyy-MM-dd", Locale.getDefault()).format(new Date());

                    VideoModel videoModel = new VideoModel(getVidID, getTitle, getVidDesc, "null", date);
                    databaseReference = FirebaseDatabase.getInstance().getReference("Videos");
                    String getKey = databaseReference.push().getKey();
                    databaseReference.child(getKey).setValue(videoModel).addOnCompleteListener(new OnCompleteListener<Void>() {
                        @Override
                        public void onComplete(@NonNull Task<Void> task) {
                            if (task.isSuccessful()) {
                                Toast.makeText(VideoActivity.this, "Video Added Successfully", Toast.LENGTH_LONG).show();
                            } else {
                                Toast.makeText(VideoActivity.this, "Some Error Occurred", Toast.LENGTH_LONG).show();
                            }

                            dialog.dismiss();
                        }


                    });

                }
            }

        });


    }

    @Override
    protected void onStart() {
        super.onStart();
        videoAdapter.startListening();
    }

    @Override
    protected void onStop() {
        super.onStop();
        videoAdapter.stopListening();
    }

}