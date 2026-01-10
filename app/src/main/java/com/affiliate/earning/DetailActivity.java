package com.affiliate.earning;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.SearchView;
import androidx.appcompat.widget.Toolbar;
import androidx.core.view.MenuItemCompat;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.affiliate.earning.detailactivityfiles.DetailAdapter;
import com.affiliate.earning.detailactivityfiles.DetailModel;
import com.facebook.ads.Ad;
import com.facebook.ads.AdListener;
import com.facebook.ads.AdSize;
import com.facebook.ads.AdView;
import com.facebook.ads.AudienceNetworkAds;
import com.facebook.ads.InterstitialAd;
import com.facebook.ads.InterstitialAdListener;
import com.google.android.gms.ads.AdError;
import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.FullScreenContentCallback;
import com.google.android.gms.ads.LoadAdError;
import com.google.android.gms.ads.MobileAds;
import com.google.android.gms.ads.initialization.InitializationStatus;
import com.google.android.gms.ads.initialization.OnInitializationCompleteListener;
import com.google.android.gms.ads.interstitial.InterstitialAdLoadCallback;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.List;

public class DetailActivity extends AppCompatActivity implements DetailAdapter.AdShowListener {


    RecyclerView recyclerviewDetail;

    DatabaseReference databaseReference;

    List<DetailModel> detailModelList;
    DetailAdapter detailAdapter;
    private final String tag = DetailActivity.class.getSimpleName();
    public static InterstitialAd interstitialAd;
    String currentPos;
    Intent intent, searchIntent;
    TextView toolbarTextView;
    Intent nameIntent, tempIntent;

    AdView adView;
    com.google.android.gms.ads.AdView AdmobView;
    LinearLayout adContainer;


    // Updated declaration for Google's InterstitialAd
    private static com.google.android.gms.ads.interstitial.InterstitialAd mInterstitialAd;


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
    String searchID;

    public void ShowAd() {
        if (Config.showFacebookAds) {
            // Assuming 'interstitialAd' is an instance of Facebook's InterstitialAd
            if (interstitialAd.isAdLoaded()) {
                interstitialAd.show();
            }
        } else {
            // Assuming 'mInterstitialAd' is an instance of Google's InterstitialAd
            if (mInterstitialAd != null) {
                mInterstitialAd.show(DetailActivity.this); // Pass the Activity context here
            }
        }
    }


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_detail);

        Log.e("on","DetailData");

        readAdsStatus();
        readAdmobAds();
        readFacebookAds();

        searchIntent = getIntent();
        searchID = searchIntent.getStringExtra("HomeSearch");


//        tempIntent.getStringExtra("HomeSearch");

        // Initialize the Mobile Ads SDK.
        MobileAds.initialize(this, new OnInitializationCompleteListener() {
            @Override
            public void onInitializationComplete(InitializationStatus initializationStatus) {
            }
        });
        // Load an interstitial ad for Google AdMob.
        AdRequest adRequest = new AdRequest.Builder().build();
        com.google.android.gms.ads.interstitial.InterstitialAd.load(this, admobInterstitialID, adRequest, new InterstitialAdLoadCallback() {
            @Override
            public void onAdLoaded(@NonNull com.google.android.gms.ads.interstitial.InterstitialAd interstitialAd) {
                // Code to be executed when an ad finishes loading.
                Log.e("Int ID", admobInterstitialID);
                mInterstitialAd = interstitialAd;
                Log.e("AdMob", "Ad was loaded.");

                // Set the full screen content callback.
                mInterstitialAd.setFullScreenContentCallback(new FullScreenContentCallback() {
                    @Override
                    public void onAdDismissedFullScreenContent() {
                        // Code to be executed when the interstitial ad is closed.
                        Log.d("AdMob", "Ad was dismissed.");
                    }

                    @Override
                    public void onAdFailedToShowFullScreenContent(AdError adError) {
                        // Code to be executed when the interstitial ad fails to show.
                        Log.e("AdMob", "Ad failed to show.");
                    }

                    @Override
                    public void onAdShowedFullScreenContent() {
                        // Code to be executed when the interstitial ad is shown.
                        // Make sure to set your reference to null so you don't
                        // show it a second time.
                        mInterstitialAd = null;
                        Log.d("AdMob", "Ad was shown.");
                    }
                });
            }

            @Override
            public void onAdFailedToLoad(@NonNull LoadAdError loadAdError) {
                // Code to be executed when an ad request fails.
                Log.e("AdMob", "Ad failed to load: " + loadAdError.getMessage());
                mInterstitialAd = null;
            }
        });

        AudienceNetworkAds.initialize(this);
        adContainer = (LinearLayout) findViewById(R.id.banner_container);
        interstitialAd = new InterstitialAd(this, facebookInterAds);
//        AdmobView = findViewById(R.id.AdmobView);
        adViewNew = findViewById(R.id.adViewNew);
        adView = new AdView(this, facebookBannerAds, AdSize.BANNER_HEIGHT_50);

        Toolbar toolbar = findViewById(R.id.toolbar);
        toolbarTextView = findViewById(R.id.toolbarTextView);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowHomeEnabled(true);


        getSupportActionBar().setDisplayShowTitleEnabled(false);
        nameIntent = getIntent();
        toolbarTextView.setText(nameIntent.getStringExtra("itemName"));


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
            public void onError(Ad ad, com.facebook.ads.AdError adError) {

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

        // For auto play video ads, it's recommended to load the ad
        // at least 30 seconds before it is shown
        interstitialAd.loadAd(
                interstitialAd.buildLoadAdConfig()
                        .withAdListener(interstitialAdListener)
                        .build());


        intent = getIntent();

        currentPos = intent.getStringExtra("CurrentPosition");

        loadData(FirebaseDatabase.getInstance().getReference("HomeItems").child(currentPos).child("items"));

        readAdsStatus();
        readAdmobAds();
        readFacebookAds();

        if (isAdMobEnabled.equalsIgnoreCase("true")) {
            showAdmobAds();
        } else {
            showFbAds();
        }


    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.menu, menu);
        MenuItem searchViewItem = menu.findItem(R.id.app_bar_search);

        final SearchView searchView = (SearchView) MenuItemCompat.getActionView(searchViewItem);

        try {
            if (searchID.equalsIgnoreCase("HOME")) {
                searchViewItem.expandActionView();
            }
        } catch (Exception e) {
            Log.i("Not home search", e.getLocalizedMessage());
        }


        searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String query) {
//                searchView.clearFocus();
             /*   if(list.contains(query)){
                    adapter.getFilter().filter(query);
                }else{
                    Toast.makeText(MainActivity.this, "No Match found",Toast.LENGTH_LONG).show();
                }*/
                return false;

            }

            @Override
            public boolean onQueryTextChange(String newText) {

                detailAdapter.getFilter().filter(newText);
                return false;
            }
        });
        return super.onCreateOptionsMenu(menu);
    }

    public void loadData(DatabaseReference databaseReference) {
        databaseReference.keepSynced(true);
        detailModelList = new ArrayList<>();
        recyclerviewDetail.setLayoutManager(new GridLayoutManager(getApplicationContext(), 3));

        databaseReference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                if (dataSnapshot.exists()) {
                    Log.e("Resources", String.valueOf(dataSnapshot));

                    for (DataSnapshot dataSnapshot1 : dataSnapshot.getChildren()) {

                        DetailModel detailModel = dataSnapshot1.getValue(DetailModel.class);
                        detailModelList.add(detailModel);
                    }

                    detailAdapter = new DetailAdapter(getApplicationContext(), detailModelList, DetailActivity.this);

                    recyclerviewDetail.setAdapter(detailAdapter);

                } else {
                    Toast.makeText(getApplicationContext(), "No data available !", Toast.LENGTH_SHORT).show();

                }

            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                Toast.makeText(getApplicationContext(), "Error" + databaseError.getMessage(), Toast.LENGTH_SHORT).show();

            }
        });
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
        adView = new AdView(DetailActivity.this, facebookBannerAds, AdSize.BANNER_HEIGHT_50);


        // Add the ad view to your activity layout
        adContainer.addView(adView);

        // Request an ad
        adView.loadAd();
        AdListener adListener = new AdListener() {

            @Override
            public void onError(Ad ad, com.facebook.ads.AdError adError) {

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
        com.google.android.gms.ads.AdView mAdView = new com.google.android.gms.ads.AdView(DetailActivity.this);
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
        Log.e("on","back");
        return true;
    }

    @Override
    public void onShowAd() {
        ShowAd();
    }
}