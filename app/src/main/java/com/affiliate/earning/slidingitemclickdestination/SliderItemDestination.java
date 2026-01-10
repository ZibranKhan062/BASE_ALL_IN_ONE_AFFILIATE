package com.affiliate.earning.slidingitemclickdestination;

import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.view.WindowManager;
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

import com.affiliate.earning.R;
//import com.wang.avi.AVLoadingIndicatorView;

/**
 * An example full-screen activity that shows and hides the system UI (i.e.
 * status bar and navigation/system bar) with user interaction.
 */
public class SliderItemDestination extends AppCompatActivity {

    WebView webView;
    Intent intent;

//    AVLoadingIndicatorView avi;
    ProgressBar progressBar;
    String sliderlink;

    @RequiresApi(api = Build.VERSION_CODES.KITKAT)
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        CookieSyncManager.createInstance(getApplicationContext());
        CookieSyncManager.getInstance().startSync();
        setContentView(R.layout.activity_slider_view);

        progressBar = findViewById(R.id.progressbar);

        progressBar.setVisibility(View.VISIBLE); // To show the ProgressBar

        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
                WindowManager.LayoutParams.FLAG_FULLSCREEN);


        webView = (WebView) findViewById(R.id.webview);
        intent = getIntent();
        sliderlink = intent.getStringExtra("sliderlink");
        Log.e("Data Received is", sliderlink);

        WebSettings webSettings = webView.getSettings();
        webSettings.setSavePassword(true);
        webSettings.setJavaScriptEnabled(true);


        if (!sliderlink.startsWith("http://") && !sliderlink.startsWith("https://"))
            sliderlink = "https://" + sliderlink;
        CookieManager.getInstance().setAcceptCookie(true);
        webSettings.setAllowFileAccess(true);
        webSettings.setPluginState(WebSettings.PluginState.ON);
        webView.loadUrl(sliderlink);

        webView.setWebViewClient(new WebViewClient() {
            @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
            @Override
            public boolean shouldOverrideUrlLoading(WebView view, WebResourceRequest request) {
                view.loadUrl(request.getUrl().toString());
                return false;
            }
        });


        webView.setWebViewClient(new WebViewClient() {

            @RequiresApi(api = Build.VERSION_CODES.M)
            @Override
            public void onReceivedError(WebView view, WebResourceRequest request, WebResourceError error) {

                /*

                 */
            }

            @Override
            public void onPageFinished(WebView view, String url) {
                progressBar.setVisibility(View.GONE);

                CookieSyncManager.getInstance().sync();
            }
        });


    }


    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if ((keyCode == KeyEvent.KEYCODE_BACK) && this.webView.canGoBack()) {
            this.webView.goBack();
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