package com.affiliate.earning;

import androidx.multidex.MultiDexApplication;

import com.facebook.ads.AdSettings;
import com.facebook.ads.AudienceNetworkAds;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.messaging.FirebaseMessaging;

public class Constants extends MultiDexApplication {

    @Override
    public void onCreate() {
        super.onCreate();
        // Initialize the Audience Network SDK
        AudienceNetworkAds.initialize(this);
        //add your Test Device here
        AdSettings.addTestDevice("9faeed26-cb09-4ae1-afcc-e2c50655241c");
        FirebaseDatabase.getInstance().setPersistenceEnabled(true);
        FirebaseMessaging.getInstance().subscribeToTopic("Appusers");
    }

}


