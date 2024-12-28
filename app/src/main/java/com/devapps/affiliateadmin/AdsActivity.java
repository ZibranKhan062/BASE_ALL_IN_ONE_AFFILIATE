package com.devapps.affiliateadmin;

import android.app.ProgressDialog;
import android.content.Context;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.Switch;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.cardview.widget.CardView;

import com.google.android.material.textfield.TextInputEditText;
import com.google.android.material.textfield.TextInputLayout;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.HashMap;
import java.util.Map;

public class AdsActivity extends AppCompatActivity {

    // UI Components
    private Switch networkSwitch;
    private CardView admobCard, facebookCard;
    private TextInputEditText appID, bannerID, interstitialID, nativeID;
    private TextInputEditText fbBannerID, fbInterstitialID;
    private TextInputLayout appIDLayout, bannerIDLayout, interstitialIDLayout, nativeIDLayout;
    private TextInputLayout fbBannerIDLayout, fbInterstitialIDLayout;
    private Button updateAdmobButton, updateFacebookButton;

    // Firebase
    private DatabaseReference adsReference;

    // Progress Dialog
    private ProgressDialog progressDialog;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_ads);

        if (!isNetworkAvailable()) {
            showNoInternetDialog();
            return;
        }

        initializeViews();
        setupToolbar();
        setupDatabase();
        loadExistingData();
        setupUpdateButtons();
        setupNetworkSwitch();
    }

    private void initializeViews() {
        // Network switch
        networkSwitch = findViewById(R.id.networkSwitch);
        admobCard = findViewById(R.id.admobCard);
        facebookCard = findViewById(R.id.facebookCard);

        // Initialize EditTexts
        appID = findViewById(R.id.appID);
        bannerID = findViewById(R.id.bannerID);
        interstitialID = findViewById(R.id.interstitialID);
        nativeID = findViewById(R.id.nativeID);
        fbBannerID = findViewById(R.id.fbBannerID);
        fbInterstitialID = findViewById(R.id.fbInterstitialID);

        // Initialize TextInputLayouts
        appIDLayout = findViewById(R.id.appIDLayout);
        bannerIDLayout = findViewById(R.id.bannerIDLayout);
        interstitialIDLayout = findViewById(R.id.interstitialIDLayout);
        nativeIDLayout = findViewById(R.id.nativeIDLayout);
        fbBannerIDLayout = findViewById(R.id.fbBannerIDLayout);
        fbInterstitialIDLayout = findViewById(R.id.fbInterstitialIDLayout);

        // Initialize Buttons
        updateAdmobButton = findViewById(R.id.updateAdmobButton);
        updateFacebookButton = findViewById(R.id.updateFacebookButton);

        // Initialize Progress Dialog
        progressDialog = new ProgressDialog(this);
        progressDialog.setMessage("Please wait...");
        progressDialog.setCancelable(false);
    }

    private void setupToolbar() {
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        if (getSupportActionBar() != null) {
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
            getSupportActionBar().setDisplayShowHomeEnabled(true);
            getSupportActionBar().setTitle("Update Ad IDs");
        }
    }

    private void setupDatabase() {
        adsReference = FirebaseDatabase.getInstance().getReference("Ads");
    }

    private void setupNetworkSwitch() {
        // Load current network state
        adsReference.child("isAdmobEnabled").addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if (snapshot.exists()) {
                    boolean isAdmobEnabled = snapshot.getValue(String.class).equals("true");
                    networkSwitch.setChecked(isAdmobEnabled);
                    updateCardVisibility(isAdmobEnabled);
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                showError("Error loading network preference: " + error.getMessage());
            }
        });

        // Setup switch listener
        networkSwitch.setOnCheckedChangeListener((buttonView, isChecked) -> {
            updateCardVisibility(isChecked);
            updateNetworkPreference(isChecked);
        });
    }

    private void updateCardVisibility(boolean isAdmobEnabled) {
        admobCard.setVisibility(isAdmobEnabled ? View.VISIBLE : View.GONE);
        facebookCard.setVisibility(isAdmobEnabled ? View.GONE : View.VISIBLE);
    }

    private void updateNetworkPreference(boolean isAdmobEnabled) {
        progressDialog.show();
        adsReference.child("isAdmobEnabled").setValue(String.valueOf(isAdmobEnabled))
                .addOnSuccessListener(aVoid -> {
                    progressDialog.dismiss();
                    String network = isAdmobEnabled ? "AdMob" : "Facebook";
                    showSuccess(network + " ads have been activated");
                })
                .addOnFailureListener(e -> {
                    progressDialog.dismiss();
                    showError("Failed to update ad network preference: " + e.getMessage());
                    // Revert switch state
                    networkSwitch.setChecked(!isAdmobEnabled);
                    updateCardVisibility(!isAdmobEnabled);
                });
    }

    private void loadExistingData() {
        progressDialog.show();

        // Load AdMob Data
        adsReference.child("Admob").addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if (snapshot.exists()) {
                    appID.setText(snapshot.child("appID").getValue(String.class));
                    bannerID.setText(snapshot.child("bannerAdsID").getValue(String.class));
                    interstitialID.setText(snapshot.child("interstitialID").getValue(String.class));
                    nativeID.setText(snapshot.child("nativeAds").getValue(String.class));
                }
                progressDialog.dismiss();
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                progressDialog.dismiss();
                showError("Error loading AdMob data: " + error.getMessage());
            }
        });

        // Load Facebook Data
        adsReference.child("Facebook").addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if (snapshot.exists()) {
                    fbBannerID.setText(snapshot.child("fbBannerID").getValue(String.class));
                    fbInterstitialID.setText(snapshot.child("fbInterID").getValue(String.class));
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                showError("Error loading Facebook data: " + error.getMessage());
            }
        });
    }

    private void setupUpdateButtons() {
        updateAdmobButton.setOnClickListener(v -> {
            if (Config.isdemoEnabled) {
                Toast.makeText(AdsActivity.this, "This feature is not available in demo mode.", Toast.LENGTH_SHORT).show();
                return;
            }
            clearAllErrors();
            updateAdmobIds();
        });

        updateFacebookButton.setOnClickListener(v -> {
            if (Config.isdemoEnabled) {
                Toast.makeText(AdsActivity.this, "This feature is not available in demo mode.", Toast.LENGTH_SHORT).show();
                return;
            }
            clearAllErrors();
            updateFacebookIds();
        });
    }

    private void updateAdmobIds() {
        if (!validateAdmobInputs()) {
            return;
        }

        progressDialog.show();
        Map<String, Object> updates = new HashMap<>();
        updates.put("appID", appID.getText().toString().trim());
        updates.put("bannerAdsID", bannerID.getText().toString().trim());
        updates.put("interstitialID", interstitialID.getText().toString().trim());
        updates.put("nativeAds", nativeID.getText().toString().trim());

        adsReference.child("Admob").updateChildren(updates)
                .addOnSuccessListener(aVoid -> {
                    progressDialog.dismiss();
                    showSuccess("AdMob IDs updated successfully");
                })
                .addOnFailureListener(e -> {
                    progressDialog.dismiss();
                    showError("Failed to update AdMob IDs: " + e.getMessage());
                });
    }

    private void updateFacebookIds() {
        if (!validateFacebookInputs()) {
            return;
        }

        progressDialog.show();
        Map<String, Object> updates = new HashMap<>();
        updates.put("fbBannerID", fbBannerID.getText().toString().trim());
        updates.put("fbInterID", fbInterstitialID.getText().toString().trim());

        adsReference.child("Facebook").updateChildren(updates)
                .addOnSuccessListener(aVoid -> {
                    progressDialog.dismiss();
                    showSuccess("Facebook IDs updated successfully");
                })
                .addOnFailureListener(e -> {
                    progressDialog.dismiss();
                    showError("Failed to update Facebook IDs: " + e.getMessage());
                });
    }

    private boolean validateAdmobInputs() {
        boolean isValid = true;

        if (TextUtils.isEmpty(appID.getText())) {
            appIDLayout.setError("App ID is required");
            isValid = false;
        }
        if (TextUtils.isEmpty(bannerID.getText())) {
            bannerIDLayout.setError("Banner ID is required");
            isValid = false;
        }
        if (TextUtils.isEmpty(interstitialID.getText())) {
            interstitialIDLayout.setError("Interstitial ID is required");
            isValid = false;
        }
        if (TextUtils.isEmpty(nativeID.getText())) {
            nativeIDLayout.setError("Native Ad ID is required");
            isValid = false;
        }

        return isValid;
    }

    private boolean validateFacebookInputs() {
        boolean isValid = true;

        if (TextUtils.isEmpty(fbBannerID.getText())) {
            fbBannerIDLayout.setError("Facebook Banner ID is required");
            isValid = false;
        }
        if (TextUtils.isEmpty(fbInterstitialID.getText())) {
            fbInterstitialIDLayout.setError("Facebook Interstitial ID is required");
            isValid = false;
        }

        return isValid;
    }

    private void clearAllErrors() {
        appIDLayout.setError(null);
        bannerIDLayout.setError(null);
        interstitialIDLayout.setError(null);
        nativeIDLayout.setError(null);
        fbBannerIDLayout.setError(null);
        fbInterstitialIDLayout.setError(null);
    }

    private void showSuccess(String message) {
        new AlertDialog.Builder(this)
                .setTitle("Success")
                .setMessage(message)
                .setPositiveButton("OK", null)
                .show();
    }

    private void showError(String message) {
        new AlertDialog.Builder(this)
                .setTitle("Error")
                .setMessage(message)
                .setPositiveButton("OK", null)
                .show();
    }

    private void showNoInternetDialog() {
        new AlertDialog.Builder(this)
                .setTitle("No Internet Connection")
                .setMessage("Please check your internet connection and try again")
                .setPositiveButton("OK", (dialog, which) -> finish())
                .setCancelable(false)
                .show();
    }

    public boolean isNetworkAvailable() {
        ConnectivityManager connectivityManager = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
        if (connectivityManager != null) {
            NetworkInfo activeNetworkInfo = connectivityManager.getActiveNetworkInfo();
            return activeNetworkInfo != null && activeNetworkInfo.isConnected();
        }
        return false;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == android.R.id.home) {
            onBackPressed();
            return true;
        }
        return super.onOptionsItemSelected(item);
    }
}