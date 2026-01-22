package com.devapps.affiliate;

import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
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
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;
import java.util.ArrayList;
import java.util.List;

public class MyRewards extends AppCompatActivity {

    TextView tvTotalPoints, tvInrValue;
    RecyclerView rewardRecyclerView;
    Button claim_reward;
    List<WithdrawalItem> withdrawList;
    WithdrawAdapter adapter;

    long currentPoints = 0;
    long pointsPerINR = 100; // Default fallback

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_my_rewards);

        tvTotalPoints = findViewById(R.id.textView_total_reward_point_fragment);
        tvInrValue = findViewById(R.id.tvInrValue); // New ID for conversion display
        rewardRecyclerView = findViewById(R.id.rewardRecyclerView);
        claim_reward = findViewById(R.id.claim_reward);

        rewardRecyclerView.setLayoutManager(new LinearLayoutManager(this));
        withdrawList = new ArrayList<>();
        adapter = new WithdrawAdapter(withdrawList);
        rewardRecyclerView.setAdapter(adapter);

        claim_reward.setOnClickListener(v -> {
            startActivity(new Intent(MyRewards.this, RedeemActivity.class));
        });

        String uid = FirebaseAuth.getInstance().getUid();
        if (uid != null) {
            loadConfigAndPoints(uid);
            loadWithdrawalHistory(uid);
        }
    }

    private void loadConfigAndPoints(String uid) {
        DatabaseReference rootRef = FirebaseDatabase.getInstance().getReference();

        // 1. Fetch Conversion Rate from AppConfig
        rootRef.child("AppConfig").child("pointsPerINR").addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if (snapshot.exists()) {
                    pointsPerINR = snapshot.getValue(Long.class);
                    updateInrDisplay();
                }
            }
            @Override public void onCancelled(@NonNull DatabaseError error) {}
        });

        // 2. Fetch User Points
        rootRef.child("Users").child(uid).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if (snapshot.exists() && snapshot.hasChild("points")) {
                    currentPoints = snapshot.child("points").getValue(Long.class);
                    tvTotalPoints.setText(String.valueOf(currentPoints));
                    updateInrDisplay();
                }
            }
            @Override public void onCancelled(@NonNull DatabaseError error) {}
        });
    }

    private void updateInrDisplay() {
        if (pointsPerINR > 0) {
            long inrValue = currentPoints / pointsPerINR;
            tvInrValue.setText("Approx. Value: ₹" + inrValue + " INR");
        }
    }

    private void loadWithdrawalHistory(String uid) {
        Query query = FirebaseDatabase.getInstance().getReference("RedemptionRequests")
                .orderByChild("uid").equalTo(uid);

        query.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                withdrawList.clear();
                for (DataSnapshot snap : snapshot.getChildren()) {
                    WithdrawalItem item = new WithdrawalItem(
                            snap.child("pointsDeducted").exists() ? snap.child("pointsDeducted").getValue(Long.class) : 0,
                            snap.child("status").getValue(String.class),
                            snap.child("paymentDetails").getValue(String.class)
                    );
                    withdrawList.add(item);
                }
                adapter.notifyDataSetChanged();
            }
            @Override public void onCancelled(@NonNull DatabaseError error) {}
        });
    }

    private class WithdrawAdapter extends RecyclerView.Adapter<WithdrawAdapter.VH> {
        List<WithdrawalItem> list;
        WithdrawAdapter(List<WithdrawalItem> list) { this.list = list; }

        @NonNull
        @Override
        public VH onCreateViewHolder(@NonNull ViewGroup p, int vt) {
            View v = LayoutInflater.from(p.getContext()).inflate(android.R.layout.simple_list_item_2, p, false);
            return new VH(v);
        }

        @Override
        public void onBindViewHolder(@NonNull VH h, int pos) {
            WithdrawalItem item = list.get(pos);
            h.t1.setText("Redeemed: " + item.points + " Points");
            h.t2.setText("Status: " + item.status + " (" + item.details + ")");

            if ("Completed".equalsIgnoreCase(item.status)) {
                h.t2.setTextColor(getResources().getColor(android.R.color.holo_green_dark));
            } else {
                h.t2.setTextColor(getResources().getColor(android.R.color.holo_orange_dark));
            }
        }

        @Override public int getItemCount() { return list.size(); }
        class VH extends RecyclerView.ViewHolder {
            TextView t1, t2;
            VH(View v) { super(v); t1 = v.findViewById(android.R.id.text1); t2 = v.findViewById(android.R.id.text2); }
        }
    }

    private static class WithdrawalItem {
        long points; String status, details;
        WithdrawalItem(long p, String s, String d) { this.points = p; this.status = s; this.details = d; }
    }
}