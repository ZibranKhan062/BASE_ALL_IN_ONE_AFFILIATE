package com.affiliate.earning.Favorite;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.affiliate.earning.DetailActivity;
import com.affiliate.earning.R;
import com.affiliate.earning.detailactivityfiles.DetailAdapter;
import com.affiliate.earning.detailactivityfiles.DetailModel;
import com.facebook.ads.Ad;
import com.facebook.ads.AdError;
import com.facebook.ads.AdListener;
import com.facebook.ads.AdSize;
import com.facebook.ads.AdView;
import com.facebook.ads.AudienceNetworkAds;
import com.facebook.ads.InterstitialAd;
import com.facebook.ads.InterstitialAdListener;
import com.google.android.gms.ads.AdRequest;
import com.google.firebase.database.DatabaseReference;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class BookMarksActivity extends AppCompatActivity {

    RecyclerView recyclerviewDetail;

    DatabaseReference databaseReference;

    List<DetailModel> detailModelList;
    DetailAdapter detailAdapter;
    private final String tag = DetailActivity.class.getSimpleName();
    private InterstitialAd interstitialAd;
    String currentPos;
    Intent intent;
    TextView toolbarTextView;
    Intent nameIntent;

    AdView adView;
    com.google.android.gms.ads.AdView AdmobView;
    LinearLayout adContainer;
    Cursor cursor;
    DatabaseHelper databaseHelper;
    Integer id;
    String name;
    String link;
    String image;
    ArrayList<DataModel> list = new ArrayList<>();
    RelativeLayout noitemfound;


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

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_book_marks);


        AudienceNetworkAds.initialize(this);
        adContainer = (LinearLayout) findViewById(R.id.banner_container);
        interstitialAd = new InterstitialAd(this, getResources().getString(R.string.fb_inter_ads));
//        AdmobView = findViewById(R.id.AdmobView);
        adView = new AdView(this, getResources().getString(R.string.fb_banner_ads), AdSize.BANNER_HEIGHT_50);
        databaseHelper = new DatabaseHelper(this);
        Toolbar toolbar = findViewById(R.id.toolbar);
        noitemfound = findViewById(R.id.noitemfound);
        toolbarTextView = findViewById(R.id.toolbarTextView);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowHomeEnabled(true);
        adViewNew = findViewById(R.id.adViewNew);

        getSupportActionBar().setDisplayShowTitleEnabled(false);
        nameIntent = getIntent();
        toolbarTextView.setText("My Bookmarks");


        // Find the Ad Container
        recyclerviewDetail = findViewById(R.id.recyclerviewDetail);

        //Interstitial Ads
        InterstitialAdListener interstitialAdListener = new InterstitialAdListener() {
            @Override
            public void onInterstitialDisplayed(Ad ad) {
                // Interstitial ad displayed callback
                Log.e(tag, "Interstitial ad displayed.");
            }

            @Override
            public void onInterstitialDismissed(Ad ad) {
                // Interstitial dismissed callback
                Log.e(tag, "Interstitial ad dismissed.");
            }

            @Override
            public void onError(Ad ad, AdError adError) {
                // Ad error callback
                Log.e(tag, "Interstitial ad failed to load: " + adError.getErrorMessage());
            }

            @Override
            public void onAdLoaded(Ad ad) {
                // Interstitial ad is loaded and ready to be displayed
                Log.d(tag, "Interstitial ad is loaded and ready to be displayed!");
                // Show the ad
                interstitialAd.show();
            }

            @Override
            public void onAdClicked(Ad ad) {
                // Ad clicked callback
                Log.d(tag, "Interstitial ad clicked!");
            }

            @Override
            public void onLoggingImpression(Ad ad) {
                // Ad impression logged callback
                Log.d(tag, "Interstitial ad impression logged!");
            }
        };
        interstitialAd.loadAd(
                interstitialAd.buildLoadAdConfig()
                        .withAdListener(interstitialAdListener)
                        .build());


        intent = getIntent();

        currentPos = intent.getStringExtra("CurrentPosition");


        readAdsStatus();
        readAdmobAds();
        readFacebookAds();
        if (isAdMobEnabled.equalsIgnoreCase("true")) {
            showAdmobAds();
        } else {
            showFbAds();
        }

        ReadFromDB();
    }


    @Override
    protected void onDestroy() {
        if (interstitialAd != null) {
            interstitialAd.destroy();
        }
        if (adView != null) {
            adView.destroy();
        }
        super.onDestroy();
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
        adView = new AdView(BookMarksActivity.this, facebookBannerAds, AdSize.BANNER_HEIGHT_50);


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
        com.google.android.gms.ads.AdView mAdView = new com.google.android.gms.ads.AdView(BookMarksActivity.this);
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

    @Override
    public boolean onSupportNavigateUp() {
        onBackPressed();
        return true;
    }

    public void ReadFromDB() {


        cursor = databaseHelper.readData();
        if (cursor.getCount() > 0) {
            if (cursor.moveToFirst()) {


                do {
                    id = cursor.getInt(0);
                    name = cursor.getString(1);
                    link = cursor.getString(2);
                    image = cursor.getString(3);


                    DataModel modelClass = new DataModel(id, name, link, image);
                    list.add(modelClass);
                    Collections.reverse(list);

                } while (cursor.moveToNext());

                checkEmpty();
            }
        }

        recyclerviewDetail.setLayoutManager(new GridLayoutManager(getApplicationContext(), 3));


        BookMarkDetailAdapter adapter = new BookMarkDetailAdapter(BookMarksActivity.this, list);
        recyclerviewDetail.setAdapter(adapter);
    }

    private void checkEmpty() {
        if (list.isEmpty()) {
            noitemfound.setVisibility(View.VISIBLE);
        } else {
            noitemfound.setVisibility(View.GONE);
        }
    }

}