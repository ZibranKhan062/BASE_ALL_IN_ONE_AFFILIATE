package com.devapps.affiliateadmin;

/**
 * Created by kapil on 20/01/17.
 */
import android.content.Context;
import android.content.SharedPreferences;


public class DarkModePrefManager {
    SharedPreferences pref;
    SharedPreferences.Editor editor;
    Context context;

    // shared pref mode
    int privateMode = 0;

    // Shared preferences file name
    private static final String PREF_NAME = "education-dark-mode";

    private static final String IS_NIGHT_MODE = "IsNightMode";


    public DarkModePrefManager(Context context) {
        this.context = context;
        pref = context.getSharedPreferences(PREF_NAME, privateMode);
        editor = pref.edit();
    }

    public void setDarkMode(boolean isFirstTime) {
        editor.putBoolean(IS_NIGHT_MODE, isFirstTime);
        editor.commit();
    }

    public boolean isNightMode() {
        return pref.getBoolean(IS_NIGHT_MODE, true);
    }

}