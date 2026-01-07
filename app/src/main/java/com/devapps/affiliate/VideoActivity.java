package com.devapps.affiliate;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.SharedPreferences;
import android.net.ConnectivityManager;
import android.os.Bundle;
import android.util.Log;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.devapps.affiliate.models.VideoModel;
import com.facebook.ads.Ad;
import com.facebook.ads.AdError;
import com.facebook.ads.AdListener;
import com.facebook.ads.AdSize;
import com.facebook.ads.AdView;
import com.facebook.ads.AudienceNetworkAds;
import com.google.android.gms.ads.AdRequest;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class VideoActivity extends AppCompatActivity {
    RecyclerView recyclerView;
    List<VideoModel> myModelList;

    ImageView img_back;
    ProgressDialog pd;
    VideoAdapter videoAdapter;
    TextView toolbarTextView;

    public static final String AdMobPREFERENCES = "AdMobPrefs";
    public static final String FacebookPREFERENCES = "FacebookPrefs";
    public static final String isAdEnabledPREFERENCES = "isAdEnabledPrefs";
    String admobAppID;
    String adMobBannerAdsID;
    String admobInterstitialID;
    String facebookBannerAds;
    String facebookInterAds;
    String isAdMobEnabled;
    LinearLayout adViewNew;
    LinearLayout adContainer;
    AdView adView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_video);
        pd = new ProgressDialog(VideoActivity.this);
        myModelList = new ArrayList<>();
        Toolbar toolbar = findViewById(R.id.toolbar);
        toolbarTextView = findViewById(R.id.toolbarTextView);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowHomeEnabled(true);

        getSupportActionBar().setDisplayShowTitleEnabled(false);
        toolbarTextView.setText("Latest Deals");

        AudienceNetworkAds.initialize(this);
        adContainer = (LinearLayout) findViewById(R.id.banner_container);

        recyclerView = findViewById(R.id.recyclerView);
        recyclerView.setLayoutManager(new LinearLayoutManager(getApplicationContext()));


        if (isNetworkConnected()) {
            pd.setMessage("Loading...");
            pd.setCanceledOnTouchOutside(false);
            pd.show();

            DatabaseReference dbProducts = FirebaseDatabase.getInstance().getReference("Videos");

            dbProducts.addValueEventListener(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot dataSnapshot) {


                    if (dataSnapshot.exists()) {
                        myModelList.clear();


                        for (DataSnapshot dataSnapshot1 : dataSnapshot.getChildren()) {
                            Log.e("ID", dataSnapshot.getValue().toString());

                            VideoModel p = dataSnapshot1.getValue(VideoModel.class);
                            myModelList.add(p);
                        }

                        videoAdapter = new VideoAdapter(myModelList, VideoActivity.this, VideoActivity.this);


                        Collections.reverse(myModelList);
                        recyclerView.setAdapter(videoAdapter);

                        pd.dismiss();

                    }
                }

                @Override
                public void onCancelled(@NonNull DatabaseError databaseError) {
                    Log.e("VideoActivity.java", databaseError.getDetails());
                }
            });


        } else {
            new AlertDialog.Builder(getApplicationContext())
                    .setTitle("No Internet Available")
                    .setMessage("Please connect to Internet to Watch Videos")


                    .setPositiveButton(android.R.string.yes, new DialogInterface.OnClickListener() {
                        public void onClick(DialogInterface dialog, int which) {
                            finish();
                        }
                    }).show();


        }

        adViewNew = findViewById(R.id.adViewNew);
        readAdsStatus();
        readAdmobAds();
        readFacebookAds();
        if (isAdMobEnabled.equalsIgnoreCase("true")) {
            showAdmobAds();
        } else {
            showFbAds();
        }


    }

    private boolean isNetworkConnected() {
        ConnectivityManager cm = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);

        return cm.getActiveNetworkInfo() != null && cm.getActiveNetworkInfo().isConnected();
    }

    @Override
    public boolean onSupportNavigateUp() {
        onBackPressed();
        return true;
    }


    public void readAdsStatus() {
        SharedPreferences sh = getSharedPreferences(isAdEnabledPREFERENCES, Context.MODE_PRIVATE);
        isAdMobEnabled = sh.getString("isAdMobEnabled", "defaultValue");
        Log.e("Ads Status Inside", isAdMobEnabled);
    }

    public void readAdmobAds() {
        SharedPreferences sh = getSharedPreferences(AdMobPREFERENCES, Context.MODE_PRIVATE);
        admobAppID = sh.getString("appID", "defaultValue");
        adMobBannerAdsID = sh.getString("bannerAdsID", "defaultValue");
        admobInterstitialID = sh.getString("interstitialID", "defaultValue");
    }

    public void readFacebookAds() {
        SharedPreferences sh = getSharedPreferences(FacebookPREFERENCES, Context.MODE_PRIVATE);
        facebookBannerAds = sh.getString("fbBannerID", "defaultValue");
        Log.e("FB Banner", facebookBannerAds);
        facebookInterAds = sh.getString("fbInterID", "defaultValue");
        Log.e("FB Ads", facebookBannerAds + " " + facebookInterAds);

    }

    public void showFbAds() {
        Log.e("fb ads enabled", "true");
        adView = new AdView(VideoActivity.this, facebookBannerAds, AdSize.BANNER_HEIGHT_50);


        // Add the ad view to your activity layout
        adContainer.addView(adView);

        // Request an ad
        adView.loadAd();
        AdListener adListener = new AdListener() {
            @Override
            public void onError(Ad ad, AdError adError) {
                /*
                error handling
                 */
            }

            @Override
            public void onAdLoaded(Ad ad) {
                // Ad loaded callback
            }

            @Override
            public void onAdClicked(Ad ad) {
                // Ad clicked callback
            }

            @Override
            public void onLoggingImpression(Ad ad) {
                // Ad impression logged callback
            }
        };

        // Request an ad
        adView.loadAd(adView.buildLoadAdConfig().withAdListener(adListener).build());
    }

    public void showAdmobAds() {
        com.google.android.gms.ads.AdView mAdView = new com.google.android.gms.ads.AdView(VideoActivity.this);
        mAdView.setAdSize(com.google.android.gms.ads.AdSize.BANNER);
        mAdView.setAdUnitId(adMobBannerAdsID);
        Log.e("Ad Unit is", adMobBannerAdsID);
        AdRequest adRequest = new AdRequest.Builder()
                .build();
        if (mAdView.getAdSize() != null || mAdView.getAdUnitId() != null)
            mAdView.loadAd(adRequest);
        // else Log state of adsize/adunit
        adViewNew.addView(mAdView);
//        ((LinearLayout)findViewById(R.id.adView)).addView(mAdView);
    }

}
