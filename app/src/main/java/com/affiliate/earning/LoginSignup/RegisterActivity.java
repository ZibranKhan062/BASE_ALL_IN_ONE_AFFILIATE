package com.affiliate.earning.LoginSignup;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.WindowManager;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import com.affiliate.earning.R;
import com.affiliate.earning.models.User;
import com.google.android.material.textfield.TextInputEditText;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;
import java.security.SecureRandom;
import java.util.Random;

public class RegisterActivity extends AppCompatActivity {

    TextInputEditText uName, userEmail, userPass, user_refer;
    RelativeLayout loginBtn;
    TextView goToSignIn;
    ProgressDialog progressDialog;
    FirebaseAuth firebaseAuth;
    DatabaseReference usersRef;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);
        setContentView(R.layout.activity_register);

        firebaseAuth = FirebaseAuth.getInstance();
        usersRef = FirebaseDatabase.getInstance().getReference("Users");

        progressDialog = new ProgressDialog(this);
        uName = findViewById(R.id.u_name);
        userEmail = findViewById(R.id.user_email);
        userPass = findViewById(R.id.user_pass);
        user_refer = findViewById(R.id.user_refer);
        loginBtn = findViewById(R.id.login_btn);
        goToSignIn = findViewById(R.id.goToSignIn);

        loginBtn.setOnClickListener(v -> registerUser());
        goToSignIn.setOnClickListener(v -> {
            startActivity(new Intent(RegisterActivity.this, LoginActivity.class));
            finish();
        });
    }

    private void registerUser() {
        String name = uName.getText().toString().trim();
        String email = userEmail.getText().toString().trim();
        String password = userPass.getText().toString().trim();
        String enteredRefCode = user_refer.getText().toString().trim();

        if (TextUtils.isEmpty(name) || TextUtils.isEmpty(email) || password.length() < 6) {
            Toast.makeText(this, "Please fill all details (Password min 6 chars)", Toast.LENGTH_SHORT).show();
            return;
        }

        progressDialog.setMessage("Creating Account...");
        progressDialog.show();

        firebaseAuth.createUserWithEmailAndPassword(email, password).addOnCompleteListener(task -> {
            if (task.isSuccessful()) {
                String uid = firebaseAuth.getCurrentUser().getUid();
                User newUser = new User(uid, name, email, null);

                // Assign a unique code to this new user
                newUser.setReferralCode(generateRandomCode(8));

                if (!TextUtils.isEmpty(enteredRefCode)) {
                    rewardReferrer(enteredRefCode, newUser);
                } else {
                    saveUserToDatabase(newUser);
                }
            } else {
                progressDialog.dismiss();
                Toast.makeText(this, "Error: " + task.getException().getMessage(), Toast.LENGTH_SHORT).show();
            }
        });
    }

    // In RegisterActivity.java
    private void rewardReferrer(String code, User newUser) {
        Query query = usersRef.orderByChild("referralCode").equalTo(code);
        query.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                if (dataSnapshot.exists()) {
                    for (DataSnapshot snapshot : dataSnapshot.getChildren()) {
                        String referrerUid = snapshot.getKey();
                        long currentPoints = snapshot.child("points").exists() ? snapshot.child("points").getValue(Long.class) : 0;

                        // 1. Award 50 points to Referrer
                        usersRef.child(referrerUid).child("points").setValue(currentPoints + 50);

                        // 2. Award 20 points bonus to New User
                        newUser.setPoints(20);

                        // 3. NEW: Record this in ReferralHistory for the referrer to see
                        DatabaseReference historyRef = FirebaseDatabase.getInstance().getReference("ReferralHistory").child(referrerUid);
                        String historyId = historyRef.push().getKey();

                        java.util.HashMap<String, Object> historyData = new java.util.HashMap<>();
                        historyData.put("name", newUser.getName());
                        historyData.put("date", System.currentTimeMillis());
                        historyData.put("pointsEarned", 50);

                        if (historyId != null) {
                            historyRef.child(historyId).setValue(historyData);
                        }
                    }
                }
                saveUserToDatabase(newUser);
            }
            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                saveUserToDatabase(newUser);
            }
        });
    }

    private void saveUserToDatabase(User user) {
        usersRef.child(user.getUid()).setValue(user).addOnCompleteListener(task -> {
            progressDialog.dismiss();
            sendVerificationEmail();
        });
    }

    private void sendVerificationEmail() {
        FirebaseUser user = firebaseAuth.getCurrentUser();
        if (user != null) {
            user.sendEmailVerification().addOnCompleteListener(task -> {
                // IMPORTANT: Sign out so they must log in after verifying
                firebaseAuth.signOut();

                AlertDialog.Builder builder = new AlertDialog.Builder(RegisterActivity.this);
                builder.setTitle("Verification Sent")
                        .setMessage("Please verify your email before logging in.")
                        .setPositiveButton("OK", (dialog, which) -> {
                            startActivity(new Intent(RegisterActivity.this, LoginActivity.class));
                            finish();
                        }).setCancelable(false).show();
            });
        }
    }

    private String generateRandomCode(int length) {
        char[] chars = "abcdefghijklmnopqrstuvwxyz1234567890".toCharArray();
        StringBuilder sb = new StringBuilder();
        Random random = new SecureRandom();
        for (int i = 0; i < length; i++) {
            sb.append(chars[random.nextInt(chars.length)]);
        }
        return sb.toString();
    }
}