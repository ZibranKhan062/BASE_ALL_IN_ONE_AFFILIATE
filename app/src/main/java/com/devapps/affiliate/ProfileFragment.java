package com.devapps.affiliate;

import android.content.Context;
import android.content.SharedPreferences;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import com.devapps.affiliate.featuredappsfiles.FeaturedAppsAdapter;
import com.devapps.affiliate.featuredappsfiles.FeaturedAppsModel;
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


public class ProfileFragment extends Fragment {

    List<FeaturedAppsModel> featuredAppsModelList;

    RecyclerView featuredappsRecycler;

    FeaturedAppsAdapter featuredAppsAdapter;
    DatabaseReference databaseReference;
    private AdView adView;
    RelativeLayout parent;

    Context mContext;
    SwipeRefreshLayout mSwipeRefreshLayout;

    ProgressBar progressbar;
    LinearLayout adContainer;
    com.google.android.gms.ads.AdView AdmobView;

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
    LinearProgressIndicator progressBar;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {

        View view = inflater.inflate(R.layout.fragment_profile, null);

        getActivity().setTitle("Featured Apps");
        featuredappsRecycler = view.findViewById(R.id.featuredappsRecycler);
        mSwipeRefreshLayout = view.findViewById(R.id.swiperefresh);
        progressbar = view.findViewById(R.id.progressbar);
//        progressbar.setVisibility(View.VISIBLE);

        progressBar = view.findViewById(R.id.progress);

        AudienceNetworkAds.initialize(mContext);
        adContainer = (LinearLayout) view.findViewById(R.id.banner_container);
//        AdmobView = view.findViewById(R.id.AdmobView);
        adViewNew = view.findViewById(R.id.adViewNew);
        if (!isNetworkAvailable()) {
            Toast.makeText(mContext, "No Internet Connection Available", Toast.LENGTH_LONG).show();

        }


        mSwipeRefreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                loadResources();
                featuredAppsAdapter.notifyDataSetChanged();
                mSwipeRefreshLayout.setRefreshing(false);
            }
        });

        readAdsStatus();
        readAdmobAds();
        readFacebookAds();
        if (isAdMobEnabled.equalsIgnoreCase("true")) {
            showAdmobAds();
        } else {
            showFbAds();
        }


        loadResources();


        return view;
    }

    public void loadResources() {

        featuredAppsModelList = new ArrayList<>();
        featuredAppsModelList.clear();
        databaseReference = FirebaseDatabase.getInstance().getReference("FeaturedApps");
        databaseReference.keepSynced(true);

        LinearLayoutManager layoutManager = new LinearLayoutManager(mContext);
        featuredappsRecycler.setLayoutManager(layoutManager);


        databaseReference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                if (dataSnapshot.exists()) {


                    Log.e("Resources", String.valueOf(dataSnapshot));

                    for (DataSnapshot dataSnapshot1 : dataSnapshot.getChildren()) {

                        FeaturedAppsModel featuredAppsModel = dataSnapshot1.getValue(FeaturedAppsModel.class);
                        featuredAppsModelList.add(featuredAppsModel);
                    }

                    featuredAppsAdapter = new FeaturedAppsAdapter(featuredAppsModelList, getContext());


                    featuredappsRecycler.setAdapter(featuredAppsAdapter);


                    progressbar.setVisibility(View.GONE);
                    progressBar.setVisibility(View.GONE);


                } else {
                    Toast.makeText(getContext(), "No data available !", Toast.LENGTH_SHORT).show();


                }

            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                Toast.makeText(getContext(), "Error" + databaseError.getMessage(), Toast.LENGTH_SHORT).show();

            }
        });
    }

    @Override
    public void onDestroy() {
        if (adView != null) {
            adView.destroy();
        }
        super.onDestroy();
    }

    @Override
    public void onAttach(@NonNull Context context) {
        super.onAttach(context);
        mContext = context;
    }

    public boolean isNetworkAvailable() {
        ConnectivityManager connectivityManager
                = (ConnectivityManager) requireActivity().getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo activeNetworkInfo = connectivityManager.getActiveNetworkInfo();
        return activeNetworkInfo != null && activeNetworkInfo.isConnected();
    }


    public void readAdsStatus() {
        SharedPreferences sh = getActivity().getSharedPreferences(isAdEnabledPREFERENCES, Context.MODE_PRIVATE);
        isAdMobEnabled = sh.getString("isAdMobEnabled", "defaultValue");
        Log.e("Ads Status Inside", isAdMobEnabled);
    }

    public void readAdmobAds() {
        SharedPreferences sh = getActivity().getSharedPreferences(AdMobPREFERENCES, Context.MODE_PRIVATE);
        admobAppID = sh.getString("appID", "defaultValue");
        adMobBannerAdsID = sh.getString("bannerAdsID", "defaultValue");
        admobInterstitialID = sh.getString("interstitialID", "defaultValue");
    }

    public void readFacebookAds() {
        SharedPreferences sh = getActivity().getSharedPreferences(FacebookPREFERENCES, Context.MODE_PRIVATE);
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