package com.affiliate.affiliate;

import android.app.Activity;
import android.content.Context;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.view.View;

import com.airbnb.lottie.LottieDrawable;
import com.amrdeveloper.lottiedialog.LottieDialog;

public class Config extends Activity {
    public Config() {
        /*required empty constructor*/
    }

    LottieDialog lottieDialog;
    Context context;
    private static LottieDialog loadingDialog;


    public static final boolean showFacebookAds = false;  //true to enable facebook Audience Network Ads, false for Admob Ads
    private static final String API_KEY = "AIzaSyCZN9V6IU_AKHm2Ca4hjLanHBN2dUXKrwYw";

    public static String getApiKey() {
        return API_KEY;
    }

    public static LottieDialog showLoadingDialog(Context context) {
        loadingDialog = new LottieDialog(context)
                .setAnimation(R.raw.loadinganimation)
                .setAnimationRepeatCount(LottieDrawable.INFINITE)
                .setAutoPlayAnimation(true)
                .setTitle("Please wait...")
                .setTitleVisibility(View.GONE)
                .setTitleColor(Color.BLACK)
                .setMessage("Loading. Please wait...")
                .setAnimationViewHeight(200)
                .setMessageColor(Color.BLACK)
                .setDialogHeightPercentage(0.25f)
                .setDialogBackground(Color.WHITE)
                .setCancelable(false)
                .setOnShowListener(dialogInterface -> {
                })
                .setOnDismissListener(dialogInterface -> {
                })
                .setOnCancelListener(dialogInterface -> {
                });

        loadingDialog.show();
        return loadingDialog;
    }


    public  LottieDialog loadingDialog(Context context) {
        loadingDialog = new LottieDialog(context)
                .setAnimation(R.raw.loadinganimation)
                .setAnimationRepeatCount(LottieDrawable.INFINITE)
                .setAutoPlayAnimation(true)
                .setTitle("Please wait...")
                .setTitleVisibility(View.GONE)
                .setTitleColor(Color.BLACK)
                .setMessage("Loading. Please wait...")
                .setAnimationViewHeight(200)
                .setMessageColor(Color.BLACK)
                .setDialogHeightPercentage(0.25f)
                .setDialogBackground(Color.WHITE)
                .setCancelable(false)
                .setOnShowListener(dialogInterface -> {
                })
                .setOnDismissListener(dialogInterface -> {
                })
                .setOnCancelListener(dialogInterface -> {
                });
        lottieDialog.show();
        return lottieDialog;

    }

    public static void hideLoadingDialog() {
        try {
            if (loadingDialog != null && loadingDialog.isShowing()) {
                loadingDialog.dismiss();
                loadingDialog = null; // Clear reference to prevent memory leaks
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }




    public boolean isNetworkAvailable(Context context) {
        ConnectivityManager connectivityManager
                = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo activeNetworkInfo = connectivityManager.getActiveNetworkInfo();
        return activeNetworkInfo != null && activeNetworkInfo.isConnected();
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        if (lottieDialog != null) {
            lottieDialog.dismiss();
            lottieDialog = null;
        }
    }

    public static String getSelectedCurrency(Context context) {
        SharedPreferences sharedPreferences = context.getSharedPreferences("currency", Context.MODE_PRIVATE);
        return sharedPreferences.getString("selectedCurrency", "");
    }

}


