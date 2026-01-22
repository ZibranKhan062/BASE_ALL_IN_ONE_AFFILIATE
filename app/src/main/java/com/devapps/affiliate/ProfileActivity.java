package com.devapps.affiliate;

import android.content.ClipData;
import android.content.ClipboardManager;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import com.devapps.affiliate.LoginSignup.LoginActivity;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

public class ProfileActivity extends AppCompatActivity {

    private TextView tvName, tvEmail, tvPoints, tvCode;
    private ImageView backBtn, copyBtn;
    private Button logoutBtn;
    private DatabaseReference userRef;
    private FirebaseAuth mAuth;
    private long lastKnownPoints = -1;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_profile);

        mAuth = FirebaseAuth.getInstance();

        tvName = findViewById(R.id.profile_name);
        tvEmail = findViewById(R.id.profile_email);
        tvPoints = findViewById(R.id.profile_points);
        tvCode = findViewById(R.id.profile_refer_code);
        backBtn = findViewById(R.id.back_btn);
        copyBtn = findViewById(R.id.copy_code_btn);
        logoutBtn = findViewById(R.id.logout_btn);

        if (mAuth.getUid() != null) {
            userRef = FirebaseDatabase.getInstance().getReference("Users").child(mAuth.getUid());
            loadUserData();
        } else {
            redirectToLogin();
        }

        backBtn.setOnClickListener(v -> finish());

        // Copy Referral Code to Clipboard
        copyBtn.setOnClickListener(v -> {
            String code = tvCode.getText().toString();
            if (!code.equals("------")) {
                ClipboardManager clipboard = (ClipboardManager) getSystemService(Context.CLIPBOARD_SERVICE);
                ClipData clip = ClipData.newPlainText("Referral Code", code);
                clipboard.setPrimaryClip(clip);
                Toast.makeText(this, "Code Copied!", Toast.LENGTH_SHORT).show();
            }
        });

        // Logout with Confirmation
        logoutBtn.setOnClickListener(v -> showLogoutConfirmation());
    }

    private void showLogoutConfirmation() {
        new AlertDialog.Builder(this)
                .setTitle("Logout")
                .setMessage("Are you sure you want to log out?")
                .setPositiveButton("Logout", (dialog, which) -> {
                    mAuth.signOut();
                    Toast.makeText(ProfileActivity.this, "Logged out successfully", Toast.LENGTH_SHORT).show();
                    redirectToLogin();
                })
                .setNegativeButton("Cancel", null)
                .show();
    }

    private void loadUserData() {
        userRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if (snapshot.exists()) {
                    String name = snapshot.child("name").getValue(String.class);
                    String email = snapshot.child("email").getValue(String.class);
                    String code = snapshot.child("referralCode").getValue(String.class);
                    long points = snapshot.child("points").exists() ? snapshot.child("points").getValue(Long.class) : 0;

                    tvName.setText(name);
                    tvEmail.setText(email);
                    tvCode.setText(code != null ? code.toUpperCase() : "N/A");
                    tvPoints.setText(points + " pts");

                    // Notify user if points increased
                    if (lastKnownPoints != -1 && points > lastKnownPoints) {
                        Toast.makeText(ProfileActivity.this, "Yay! You earned points!", Toast.LENGTH_LONG).show();
                    }
                    lastKnownPoints = points;
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                Toast.makeText(ProfileActivity.this, "Error: " + error.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void redirectToLogin() {
        Intent intent = new Intent(ProfileActivity.this, LoginActivity.class);
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
        startActivity(intent);
        finish();
    }
}