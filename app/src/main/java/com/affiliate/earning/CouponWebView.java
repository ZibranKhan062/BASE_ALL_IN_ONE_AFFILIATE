package com.affiliate.affiliate;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.view.KeyEvent;
import android.view.View;
import android.webkit.CookieManager;
import android.webkit.CookieSyncManager;
import android.webkit.WebResourceError;
import android.webkit.WebResourceRequest;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.ProgressBar;

import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;

public class CouponWebView extends AppCompatActivity {
    WebView couponWebview;
    Intent intent;
    ProgressBar progressbar;
    String recWeb;

    @SuppressLint("SetJavaScriptEnabled")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        CookieSyncManager.createInstance(getApplicationContext());
        CookieSyncManager.getInstance().startSync();
        setContentView(R.layout.activity_coupon_web_view);
        couponWebview = (WebView) findViewById(R.id.couponWebview);
        progressbar = findViewById(R.id.progressbar);
        intent = getIntent();
        recWeb = intent.getStringExtra("WebsiteLink");

        WebSettings webSettings = couponWebview.getSettings();
        webSettings.setSavePassword(true);
        webSettings.setJavaScriptEnabled(true);
        if (!recWeb.startsWith("http://") && !recWeb.startsWith("https://"))
            recWeb = "https://" + recWeb;
        CookieManager.getInstance().setAcceptCookie(true);
        webSettings.setAllowFileAccess(true);
        webSettings.setPluginState(WebSettings.PluginState.ON);
        couponWebview.loadUrl(recWeb);

        couponWebview.setWebViewClient(new WebViewClient() {
            @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
            @Override
            public boolean shouldOverrideUrlLoading(WebView view, WebResourceRequest request) {
                view.loadUrl(request.getUrl().toString());
                return false;
            }
        });
        couponWebview.setWebViewClient(new WebViewClient() {

            @RequiresApi(api = Build.VERSION_CODES.M)
            @Override
            public void onReceivedError(WebView view, WebResourceRequest request, WebResourceError error) {
                /*

                 */

            }

            @Override
            public void onPageFinished(WebView view, String url) {
                progressbar.setVisibility(View.GONE);

                CookieSyncManager.getInstance().sync();
            }
        });

    }

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if ((keyCode == KeyEvent.KEYCODE_BACK) && this.couponWebview.canGoBack()) {
            this.couponWebview.goBack();
            return true;
        }

        return super.onKeyDown(keyCode, event);
    }

    @Override
    protected void onResume() {
        super.onResume();
        CookieSyncManager.getInstance().stopSync();
    }

    @Override
    protected void onPause() {
        super.onPause();
        CookieSyncManager.getInstance().sync();
    }
}