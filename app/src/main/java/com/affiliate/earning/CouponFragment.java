package com.affiliate.affiliate;

import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.affiliate.affiliate.CouponPackage.CouponAdapter;
import com.affiliate.affiliate.CouponPackage.CouponModel;
import com.facebook.ads.Ad;
import com.facebook.ads.AdError;
import com.facebook.ads.AdListener;
import com.facebook.ads.AdSize;
import com.facebook.ads.AdView;
import com.facebook.ads.AudienceNetworkAds;
import com.google.android.gms.ads.AdRequest;
import com.google.android.material.progressindicator.LinearProgressIndicator;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.List;

public class CouponFragment extends Fragment {
    TextView toolbarTextView;
    RecyclerView couponRecycler;
    ProgressBar progressbar;
    CouponAdapter couponAdapter;
    DatabaseReference databaseReference;
    List<CouponModel> couponModelList;
    RelativeLayout no_notif;

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
    Context mContext;
    LinearProgressIndicator progressBar;

    public CouponFragment() {
        // Required empty public constructor
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {


        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_coupon, container, false);
        AudienceNetworkAds.initialize(mContext);
        adContainer = (LinearLayout) view.findViewById(R.id.banner_container);
        databaseReference = FirebaseDatabase.getInstance().getReference("Coupons");
        couponRecycler = view.findViewById(R.id.couponRecycler);
        progressbar = view.findViewById(R.id.progressbar);
        no_notif = view.findViewById(R.id.no_notif);
        progressBar = view.findViewById(R.id.progress);
        adViewNew = view.findViewById(R.id.adViewNew);
        readAdsStatus();
        readAdmobAds();
        readFacebookAds();
        if (isAdMobEnabled.equalsIgnoreCase("true")) {
            showAdmobAds();
        } else {
            showFbAds();
        }


        loadCoupons();
        return view;
    }

    @Override
    public void onAttach(@NonNull Context context) {
        super.onAttach(context);
        mContext = context;
    }

    private void loadCoupons() {

        couponModelList = new ArrayList<>();
        couponModelList.clear();
        databaseReference = FirebaseDatabase.getInstance().getReference("Coupons");
        databaseReference.keepSynced(true);

        LinearLayoutManager layoutManager = new LinearLayoutManager(mContext);
        couponRecycler.setLayoutManager(layoutManager);


        databaseReference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                if (dataSnapshot.exists()) {

                    for (DataSnapshot dataSnapshot1 : dataSnapshot.getChildren()) {

                        CouponModel notificationModel = dataSnapshot1.getValue(CouponModel.class);
                        couponModelList.add(notificationModel);
                    }

                    couponAdapter = new CouponAdapter(couponModelList, mContext);

                    checkEmpty();
                    couponRecycler.setAdapter(couponAdapter);
                    progressbar.setVisibility(View.GONE);
                    progressBar.setVisibility(View.GONE);
                } else {
                    Toast.makeText(mContext, "No data available !", Toast.LENGTH_SHORT).show();


                }

            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                Toast.makeText(mContext, "Error" + databaseError.getMessage(), Toast.LENGTH_SHORT).show();

            }
        });

    }

    private void checkEmpty() {
        if (couponAdapter.getItemCount() == 0) {
            Log.e("Item count", String.valueOf(couponAdapter.getItemCount()));
            no_notif.setVisibility(View.VISIBLE);
        } else {
            no_notif.setVisibility(View.GONE);
        }

    }


    public void readAdsStatus() {
        SharedPreferences sh = mContext.getSharedPreferences(isAdEnabledPREFERENCES, Context.MODE_PRIVATE);
        isAdMobEnabled = sh.getString("isAdMobEnabled", "defaultValue");
        Log.e("Ads Status Inside", isAdMobEnabled);
    }

    public void readAdmobAds() {
        SharedPreferences sh = mContext.getSharedPreferences(AdMobPREFERENCES, Context.MODE_PRIVATE);
        admobAppID = sh.getString("appID", "defaultValue");
        adMobBannerAdsID = sh.getString("bannerAdsID", "defaultValue");
        admobInterstitialID = sh.getString("interstitialID", "defaultValue");
    }

    public void readFacebookAds() {
        SharedPreferences sh = mContext.getSharedPreferences(FacebookPREFERENCES, Context.MODE_PRIVATE);
        facebookBannerAds = sh.getString("fbBannerID", "defaultValue");
        Log.e("FB Banner", facebookBannerAds);
        facebookInterAds = sh.getString("fbInterID", "defaultValue");
        Log.e("FB Ads", facebookBannerAds + " " + facebookInterAds);

    }

    public void showFbAds() {
        Log.e("fb ads enabled", "true");
        adView = new AdView(mContext, facebookBannerAds, AdSize.BANNER_HEIGHT_50);


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
        com.google.android.gms.ads.AdView mAdView = new com.google.android.gms.ads.AdView(mContext);
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