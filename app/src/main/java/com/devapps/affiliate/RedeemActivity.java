package com.devapps.affiliate;

import android.os.Bundle;
import android.text.TextUtils;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import java.util.HashMap;

public class RedeemActivity extends AppCompatActivity {

    private EditText etPaymentDetails;
    private Button btnSubmitRequest;
    private TextView tvAvailablePoints, tvMinLimit;
    private DatabaseReference userRef, requestRef, configRef;
    private String uid;
    private long currentPoints = 0;
    private long minPointsRequired = 1000; // Default fallback

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_redeem);

        etPaymentDetails = findViewById(R.id.etPaymentDetails);
        btnSubmitRequest = findViewById(R.id.btnSubmitRequest);
        tvAvailablePoints = findViewById(R.id.tvAvailablePoints);
        tvMinLimit = findViewById(R.id.tvMinLimit); // Add this TextView in XML

        uid = FirebaseAuth.getInstance().getUid();
        userRef = FirebaseDatabase.getInstance().getReference("Users").child(uid);
        requestRef = FirebaseDatabase.getInstance().getReference("RedemptionRequests");
        configRef = FirebaseDatabase.getInstance().getReference("AppConfig");

        // 1. Fetch Dynamic Minimum Limit from DB
        configRef.child("minWithdrawPoints").addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if (snapshot.exists()) {
                    minPointsRequired = snapshot.getValue(Long.class);
                    tvMinLimit.setText("Minimum required: " + minPointsRequired + " Points");
                }
            }
            @Override
            public void onCancelled(@NonNull DatabaseError error) {}
        });

        // 2. Load current points
        userRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if (snapshot.exists() && snapshot.hasChild("points")) {
                    currentPoints = snapshot.child("points").getValue(Long.class);
                    tvAvailablePoints.setText("Available: " + currentPoints + " Points");
                }
            }
            @Override
            public void onCancelled(@NonNull DatabaseError error) {}
        });

        btnSubmitRequest.setOnClickListener(v -> {
            String details = etPaymentDetails.getText().toString().trim();
            if (currentPoints < minPointsRequired) {
                Toast.makeText(this, "You need at least " + minPointsRequired + " points!", Toast.LENGTH_SHORT).show();
            } else if (TextUtils.isEmpty(details)) {
                Toast.makeText(this, "Enter Payment Details", Toast.LENGTH_SHORT).show();
            } else {
                submitRequest(details);
            }
        });
    }

    private void submitRequest(String details) {
        String requestId = requestRef.push().getKey();
        HashMap<String, Object> requestMap = new HashMap<>();
        requestMap.put("requestId", requestId);
        requestMap.put("uid", uid);
        requestMap.put("paymentDetails", details);
        requestMap.put("pointsDeducted", currentPoints);
        requestMap.put("status", "Pending"); // Your manual change to "Completed" happens here later
        requestMap.put("timestamp", System.currentTimeMillis());

        if (requestId != null) {
            requestRef.child(requestId).setValue(requestMap).addOnCompleteListener(task -> {
                if (task.isSuccessful()) {
                    userRef.child("points").setValue(0);
                    Toast.makeText(this, "Request Sent!", Toast.LENGTH_SHORT).show();
                    finish();
                }
            });
        }
    }
}