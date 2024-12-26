package com.devapps.affiliateadmin;

import androidx.multidex.MultiDexApplication;

import com.google.firebase.database.FirebaseDatabase;

public class Utils extends MultiDexApplication {
    @Override
    public void onCreate() {
        super.onCreate();
//        FirebaseDatabase.getInstance().setPersistenceEnabled(true);

    }

}
