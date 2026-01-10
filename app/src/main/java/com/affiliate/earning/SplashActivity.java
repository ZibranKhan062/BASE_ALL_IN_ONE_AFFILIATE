package com.affiliate.affiliate;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;


public class SplashActivity extends AppCompatActivity {

    DatabaseReference adsReference, isAdmobEnabledReference;
    public static final String AdMobPREFERENCES = "AdMobPrefs";
    public static final String FacebookPREFERENCES = "FacebookPrefs";
    public static final String isAdEnabledPREFERENCES = "isAdEnabledPrefs";
    SharedPreferences admobPreferences, facebookPreferences, isAdEnabledPreferences;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
                WindowManager.LayoutParams.FLAG_FULLSCREEN);
        setContentView(R.layout.activity_splash);


        /* New Handler to start the Menu-Activity
         * and close this Splash-Screen after some seconds.*/
        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                /* Create an Intent that will start the Menu-Activity. */
                Intent mainIntent = new Intent(SplashActivity.this, MainActivity.class);
                SplashActivity.this.startActivity(mainIntent);
                SplashActivity.this.finish();
            }
        }, 3000);

        admobPreferences = getSharedPreferences(AdMobPREFERENCES, Context.MODE_PRIVATE);
        facebookPreferences = getSharedPreferences(FacebookPREFERENCES, Context.MODE_PRIVATE);
        isAdEnabledPreferences = getSharedPreferences(isAdEnabledPREFERENCES, Context.MODE_PRIVATE);
        getAdsStatus();
        getAdMobAds();
        getFacebookAds();

    }

    private void getAdsStatus() {
        isAdmobEnabledReference = FirebaseDatabase.getInstance().getReference("Ads");
        isAdmobEnabledReference.keepSynced(true);
        isAdmobEnabledReference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                if (dataSnapshot.exists()) {
                    String isAdmobEnabled = dataSnapshot.child("isAdmobEnabled").getValue(String.class);
                    Log.e("isAdmobEnabled", isAdmobEnabled);
                    SharedPreferences.Editor editor = isAdEnabledPreferences.edit();
                    editor.putString("isAdMobEnabled", isAdmobEnabled);
                    editor.apply();


                } else {
                    Toast.makeText(SplashActivity.this, "No data available !", Toast.LENGTH_SHORT).show();

                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                Toast.makeText(SplashActivity.this, "Error" + databaseError.getMessage(), Toast.LENGTH_SHORT).show();

            }
        });
    }

    private void getAdMobAds() {
        adsReference = FirebaseDatabase.getInstance().getReference("Ads").child("Admob");
        adsReference.keepSynced(true);
        adsReference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                if (dataSnapshot.exists()) {
                    String appID = dataSnapshot.child("appID").getValue(String.class);
                    String bannerAdsID = dataSnapshot.child("bannerAdsID").getValue(String.class);
                    String interstitialID = dataSnapshot.child("interstitialID").getValue(String.class);

                    SharedPreferences.Editor editor = admobPreferences.edit();

                    editor.putString("appID", appID);
                    editor.putString("bannerAdsID", bannerAdsID);
                    editor.putString("interstitialID", interstitialID);
                    editor.apply();


                } else {
                    Toast.makeText(SplashActivity.this, "No data available !", Toast.LENGTH_SHORT).show();

                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                Toast.makeText(SplashActivity.this, "Error" + databaseError.getMessage(), Toast.LENGTH_SHORT).show();

            }
        });
    }


    private void getFacebookAds() {
        adsReference = FirebaseDatabase.getInstance().getReference("Ads").child("Facebook");
        adsReference.keepSynced(true);
        adsReference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                if (dataSnapshot.exists()) {
                    String fbBannerID = dataSnapshot.child("fbBannerID").getValue(String.class);
                    String fbInterID = dataSnapshot.child("fbInterID").getValue(String.class);

                    SharedPreferences.Editor editor = facebookPreferences.edit();

                    editor.putString("fbBannerID", fbBannerID);
                    editor.putString("fbInterID", fbInterID);
                    editor.apply();


                } else {
                    Toast.makeText(SplashActivity.this, "No data available !", Toast.LENGTH_SHORT).show();

                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                Toast.makeText(SplashActivity.this, "Error" + databaseError.getMessage(), Toast.LENGTH_SHORT).show();

            }
        });
    }
}