package com.devapps.affiliateadmin;

public class Config {
    public Config() {
        /*required empty constructor*/
    }

    public static final boolean isdemoEnabled = true;  // set it to false when releasing to Play Store

    //    add Youtube API key below
    private static final String API_KEY = "AIzaSyCZN9V6IU_AKHm2Ca4hjLanHBN2dUXKrwYw";
    public static String getApiKey() {
        return API_KEY;
    }
}
