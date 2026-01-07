package com.devapps.affiliate;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import java.util.ArrayList;
import java.util.List;

public class MyRewards extends AppCompatActivity {

    TextView tvTotalPoints;
    RecyclerView rewardRecyclerView;
    List<ReferralHistoryItem> historyList;
    HistoryAdapter adapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_my_rewards);

        tvTotalPoints = findViewById(R.id.textView_total_reward_point_fragment);
        rewardRecyclerView = findViewById(R.id.rewardRecyclerView);
        rewardRecyclerView.setLayoutManager(new LinearLayoutManager(this));

        historyList = new ArrayList<>();
        adapter = new HistoryAdapter(historyList);
        rewardRecyclerView.setAdapter(adapter);

        String uid = FirebaseAuth.getInstance().getUid();
        if (uid != null) {
            loadPointsAndHistory(uid);
        }
    }

    private void loadPointsAndHistory(String uid) {
        DatabaseReference dbRef = FirebaseDatabase.getInstance().getReference();

        // 1. Load Point Balance
        dbRef.child("Users").child(uid).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if (snapshot.hasChild("points")) {
                    tvTotalPoints.setText(String.valueOf(snapshot.child("points").getValue(Long.class)));
                }
            }
            @Override public void onCancelled(@NonNull DatabaseError error) {}
        });

        // 2. Load History List
        dbRef.child("ReferralHistory").child(uid).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                historyList.clear();
                for (DataSnapshot snap : snapshot.getChildren()) {
                    ReferralHistoryItem item = new ReferralHistoryItem(
                            snap.child("name").getValue(String.class),
                            snap.child("pointsEarned").getValue(Long.class)
                    );
                    historyList.add(item);
                }
                adapter.notifyDataSetChanged();
            }
            @Override public void onCancelled(@NonNull DatabaseError error) {}
        });
    }

    // Standard Adapter for History
    private class HistoryAdapter extends RecyclerView.Adapter<HistoryAdapter.ViewHolder> {
        List<ReferralHistoryItem> items;
        HistoryAdapter(List<ReferralHistoryItem> items) { this.items = items; }

        @NonNull
        @Override
        public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
            View v = LayoutInflater.from(parent.getContext()).inflate(android.R.layout.simple_list_item_2, parent, false);
            return new ViewHolder(v);
        }

        @Override
        public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
            ReferralHistoryItem item = items.get(position);
            holder.text1.setText(item.name + " joined using your code");
            holder.text2.setText("+ " + item.points + " Points");
        }

        @Override public int getItemCount() { return items.size(); }

        class ViewHolder extends RecyclerView.ViewHolder {
            TextView text1, text2;
            ViewHolder(View v) {
                super(v);
                text1 = v.findViewById(android.R.id.text1);
                text2 = v.findViewById(android.R.id.text2);
            }
        }
    }

    // Simple Data Model for History
    private static class ReferralHistoryItem {
        String name; long points;
        ReferralHistoryItem(String n, long p) { this.name = n; this.points = p; }
    }
}