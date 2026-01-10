package com.affiliate.affiliate;

import android.app.Dialog;
import android.content.ClipData;
import android.content.ClipboardManager;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import com.affiliate.affiliate.LoginSignup.LoginActivity;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.security.SecureRandom;
import java.util.Random;

public class ReferActivity extends AppCompatActivity {

    TextView textView_reference_code;
    TextView toolbarTextView;
    LinearLayout linearLayout_copy_reference_code;
    Button btn_check_rewards;
    FirebaseAuth firebaseAuth;
    FirebaseUser firebaseUser;
    private DatabaseReference mDatabaseReference;
    String getCode;
    Button btn_refer;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_refer);
        firebaseAuth = FirebaseAuth.getInstance();

        // Initialize FirebaseAuth instance
        firebaseAuth = FirebaseAuth.getInstance();

        // Check if the user is logged in
        if (firebaseAuth.getCurrentUser() == null) {
            // User is not logged in, redirect to LoginActivity
            Intent loginIntent = new Intent(this, LoginActivity.class);
            startActivity(loginIntent);
            finish();  // Close the current activity
            return;  // Exit onCreate to prevent further execution
        }

        textView_reference_code = findViewById(R.id.textView_reference_code);
        linearLayout_copy_reference_code = findViewById(R.id.linearLayout_copy_reference_code);
        btn_check_rewards = findViewById(R.id.btn_check_rewards);
        Toolbar toolbar = findViewById(R.id.toolbar);
        toolbarTextView = findViewById(R.id.toolbarTextView);
        btn_refer = findViewById(R.id.btn_refer);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowHomeEnabled(true);
        getSupportActionBar().setDisplayShowTitleEnabled(false);
        toolbarTextView.setText("Refer and Earn");

        getCode = createRandomCode(8);


        linearLayout_copy_reference_code.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ClipboardManager clipboard = (ClipboardManager) getSystemService(Context.CLIPBOARD_SERVICE);
                ClipData clip = ClipData.newPlainText(textView_reference_code.getText().toString().trim(), textView_reference_code.getText().toString().trim());
                clipboard.setPrimaryClip(clip);
                Toast.makeText(ReferActivity.this, "Copied to clipboard successfully", Toast.LENGTH_SHORT).show();
            }
        });

        btn_check_rewards.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent i = new Intent(ReferActivity.this, MyRewards.class);
                startActivity(i);
            }
        });

        firebaseUser = firebaseAuth.getCurrentUser();
        if (firebaseUser != null) {
            String userUid = firebaseUser.getUid();
            fetchReferralCode(userUid);
        }
        btn_refer.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                // Get the referral code from the TextView
                String referralCode = textView_reference_code.getText().toString();

                // Check if the referral code is not empty
                if (!referralCode.isEmpty()) {
                    // Create a share message
                    String shareMessage = getResources().getString(R.string.referral_msg) + ""+referralCode+ " Get the App here : " + "https://play.google.com/store/apps/details?id=" + getPackageName();

                    // Create an Intent to share the referral code
                    Intent shareIntent = new Intent(Intent.ACTION_SEND);
                    shareIntent.setType("text/plain");
                    shareIntent.putExtra(Intent.EXTRA_TEXT, shareMessage);

                    // Start the share activity
                    startActivity(Intent.createChooser(shareIntent, "Share Referral Code"));
                } else {
                    // Referral code is empty, show a message to the user
                    Toast.makeText(ReferActivity.this, "Referral code is not available.", Toast.LENGTH_SHORT).show();
                }
            }
        });

    }

    public String createRandomCode(int codeLength) {
        char[] chars = "abcdefghijklmnopqrstuvwxyz1234567890".toCharArray();
        StringBuilder sb = new StringBuilder();
        Random random = new SecureRandom();
        for (int i = 0; i < codeLength; i++) {
            char c = chars[random.nextInt(chars.length)];
            sb.append(c);
        }
        String output = sb.toString();
        System.out.println(output);
        return output;
    }

    @Override
    public boolean onSupportNavigateUp() {
        onBackPressed();
        return true;
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.refer_menu, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();


        if (id == R.id.action_logout) {
            // Create and show the custom dialog
            Dialog customDialog = new Dialog(this);
            customDialog.setContentView(R.layout.logout_dialog);
            customDialog.getWindow().setLayout(ViewGroup.LayoutParams.FILL_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);
            // Initialize views
            ImageView dialogImage = customDialog.findViewById(R.id.dialogImage);
            Button logoutButton = customDialog.findViewById(R.id.logoutButton);

            // Set click listener for the logout button
            logoutButton.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    // Perform logout operation
                    FirebaseAuth.getInstance().signOut();
                    Toast.makeText(ReferActivity.this, "User Logged out successfully !", Toast.LENGTH_LONG).show();
                    // Dismiss the dialog
                    customDialog.dismiss();
                    Intent intent = new Intent(ReferActivity.this, MainActivity.class);
                    startActivity(intent);
                }
            });

            // Show the custom dialog
            customDialog.show();

            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        Intent intent = new Intent(ReferActivity.this, MainActivity.class);
        startActivity(intent);
        finish();
    }

    private void pushReferralCode(String code) {
        String userUid = FirebaseAuth.getInstance().getCurrentUser().getUid();
        DatabaseReference databaseReference = FirebaseDatabase.getInstance().getReference("Users").child(userUid);

        databaseReference.child("referralCode").setValue(code)
                .addOnCompleteListener(new OnCompleteListener<Void>() {
                    @Override
                    public void onComplete(@NonNull Task<Void> task) {
                        if (task.isSuccessful()) {

                            Log.e("task", "done");
                        } else {
                            Log.e("task", "failed");
                        }
                    }
                });
    }


    private void fetchReferralCode(String uid) {
        Config.showLoadingDialog(ReferActivity.this);
        mDatabaseReference = FirebaseDatabase.getInstance().getReference("Users").child(uid);

        mDatabaseReference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                Config.hideLoadingDialog();
                if (dataSnapshot.exists() && dataSnapshot.hasChild("referralCode")) {
                    String referralCode = dataSnapshot.child("referralCode").getValue(String.class);
                    if (referralCode.equalsIgnoreCase("null") || referralCode == null || referralCode.isEmpty()) {
                        pushReferralCode(getCode);
                        textView_reference_code.setText(getCode);
                    } else {
                        textView_reference_code.setText(referralCode);

                    }
                    // Do whatever you want with the referral code
                    Log.e("ReferralCode", referralCode);
                } else {
                    Log.e("ReferralCode", "Referral code not found");
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                Log.e("DatabaseError", "Error reading referral code: " + databaseError.getMessage());
            }
        });
    }

}