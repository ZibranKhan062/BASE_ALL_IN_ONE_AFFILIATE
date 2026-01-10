package com.affiliate.earning;

import android.content.ActivityNotFoundException;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.WindowManager;
import android.webkit.CookieManager;
import android.webkit.WebChromeClient;
import android.webkit.WebResourceError;
import android.webkit.WebResourceRequest;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import com.affiliate.earning.Favorite.BookMarksActivity;
import com.affiliate.earning.Favorite.DatabaseHelper;
import com.affiliate.earning.Favorite.NewsModel;

public class WebViewActivity extends AppCompatActivity {
    private WebView webView;
    private ProgressBar progressbar;
    private TextView toolbarTextView;
    private String rec_weblink;
    private MenuItem favItem;
    private SharedPreferences sharedPreferences;
    private SharedPreferences.Editor myEditor;
    private DatabaseHelper databaseHelper;
    private boolean isLoading = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_web_view);

        try {
            initializeViews();
            setupWebView();
        } catch (Exception e) {
            e.printStackTrace();
            Toast.makeText(this, "Error initializing web view", Toast.LENGTH_SHORT).show();
            finish();
        }
    }
    private void initializeViews() {
        // Initialize views and preferences
        webView = findViewById(R.id.webview);
        progressbar = findViewById(R.id.progressbar);
        toolbarTextView = findViewById(R.id.toolbarTextView);

        sharedPreferences = getSharedPreferences("FavList", MODE_PRIVATE);
        myEditor = sharedPreferences.edit();
        databaseHelper = new DatabaseHelper(this);

        // Get intent data
        rec_weblink = getIntent().getStringExtra("Web");
        toolbarTextView.setText(getIntent().getStringExtra("website_name"));

        // Setup toolbar
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        if (getSupportActionBar() != null) {
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
            getSupportActionBar().setDisplayShowHomeEnabled(true);
            getSupportActionBar().setDisplayShowTitleEnabled(false);
        }

        // Set fullscreen flags if needed
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
                WindowManager.LayoutParams.FLAG_FULLSCREEN);
    }

    private void setupWebView() {
        if (webView == null) return;

        WebSettings webSettings = webView.getSettings();
        webSettings.setSavePassword(true);
        webSettings.setJavaScriptEnabled(true);
        webSettings.setAllowFileAccess(true);
        webSettings.setDomStorageEnabled(true);
        webSettings.setLoadWithOverviewMode(true);
        webSettings.setUseWideViewPort(true);
        webSettings.setCacheMode(WebSettings.LOAD_NO_CACHE);

        // Enable hardware acceleration
        webView.setLayerType(View.LAYER_TYPE_HARDWARE, null);

        setupCookieManager();
        setupWebViewClient();
        loadInitialUrl();
    }

    private void setupCookieManager() {
        CookieManager cookieManager = CookieManager.getInstance();
        cookieManager.setAcceptCookie(true);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            cookieManager.setAcceptThirdPartyCookies(webView, true);
        }
    }

    private void setupWebViewClient() {
        webView.setWebViewClient(new WebViewClient() {
            @Override
            public boolean shouldOverrideUrlLoading(WebView view, String url) {
                if (url == null) return false;

                // 1. Handle standard web protocols
                if (url.startsWith("http://") || url.startsWith("https://")) {
                    // Check if this is a deep link disguised as a web link (optional but recommended)
                    return false; // Let the WebView load it
                }

                // 2. Handle Deep Linking, Payments (UPI), and External Apps
                try {
                    Intent intent;
                    if (url.startsWith("intent://")) {
                        // Specific parsing for Amazon/Flipkart intent:// links
                        intent = Intent.parseUri(url, Intent.URI_INTENT_SCHEME);
                        if (intent != null) {
                            view.stopLoading(); // Prevent WebView from trying to load intent://

                            // Check if the app is installed
                            if (getPackageManager().resolveActivity(intent, 0) != null) {
                                startActivity(intent);
                                return true;
                            }

                            // If app is not installed, redirect to Play Store or fallback URL
                            String fallbackUrl = intent.getStringExtra("browser_fallback_url");
                            if (fallbackUrl != null) {
                                view.loadUrl(fallbackUrl);
                                return true;
                            }
                        }
                    } else {
                        // Handle standard schemes: upi://, tel:, mailto:, whatsapp:
                        intent = new Intent(Intent.ACTION_VIEW, Uri.parse(url));
                        startActivity(intent);
                        return true;
                    }
                } catch (Exception e) {
                    Log.e("WebViewClient", "Deep Link Error: " + e.getMessage());
                    Toast.makeText(WebViewActivity.this, "Appropriate app not found", Toast.LENGTH_SHORT).show();
                    return true; // We handled it by showing an error
                }
                return false;
            }

            @Override
            public void onPageStarted(WebView view, String url, Bitmap favicon) {
                super.onPageStarted(view, url, favicon);
                isLoading = true;
                progressbar.setVisibility(View.VISIBLE);
            }

            @Override
            public void onPageFinished(WebView view, String url) {
                super.onPageFinished(view, url);
                isLoading = false;
                progressbar.setVisibility(View.GONE);

                // Fix session issues and white screens by flushing cookies
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
                    CookieManager.getInstance().flush();
                }
            }

            @Override
            public void onReceivedError(WebView view, WebResourceRequest request, WebResourceError error) {
                super.onReceivedError(view, request, error);
                isLoading = false;
                progressbar.setVisibility(View.GONE);

                // Log the error for debugging
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                    Log.e("WebViewError", "Error Code: " + error.getErrorCode());
                }
            }
        });

        // Ensure the progress bar updates accurately during page load
        webView.setWebChromeClient(new WebChromeClient() {
            @Override
            public void onProgressChanged(WebView view, int newProgress) {
                progressbar.setProgress(newProgress);
                if (newProgress == 100) {
                    progressbar.setVisibility(View.GONE);
                }
            }
        });
    }
    private void loadInitialUrl() {
        if (rec_weblink == null) {
            Toast.makeText(this, "Invalid URL", Toast.LENGTH_SHORT).show();
            finish();
            return;
        }

        if (!rec_weblink.startsWith("http://") && !rec_weblink.startsWith("https://")) {
            rec_weblink = "https://" + rec_weblink;
        }

        try {
            webView.loadUrl(rec_weblink);
        } catch (Exception e) {
            e.printStackTrace();
            Toast.makeText(this, "Error loading URL", Toast.LENGTH_SHORT).show();
            finish();
        }
    }

    @Override
    public void onBackPressed() {
        if (webView != null && webView.canGoBack() && !isLoading) {
            webView.goBack();
        } else {
            super.onBackPressed();
        }
    }
    @Override
    protected void onResume() {
        super.onResume();
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            CookieManager.getInstance().flush();
        }
    }

    @Override
    protected void onPause() {
        super.onPause();
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            CookieManager.getInstance().flush();
        }
    }

    @Override
    public boolean onSupportNavigateUp() {
        onBackPressed();
        return true;
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.webview_toolbar, menu);
        favItem = menu.findItem(R.id.favorite);
        upadateIcon();
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.favorite:
                addToFavorites();
                return true;
            case R.id.share:
                shareUrl();
                return true;
            case R.id.whatsapp:
                shareViaWhatsapp();
                return true;
            case R.id.email:
                shareViaEmail();
                return true;
            case R.id.showAll:
                showBookmarks();
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    private void shareUrl() {
        Intent sharingIntent = new Intent(Intent.ACTION_SEND);
        sharingIntent.setType("text/plain");
        String shareBody = "Check this website: " + rec_weblink.trim();
        sharingIntent.putExtra(Intent.EXTRA_TEXT, shareBody);
        startActivity(Intent.createChooser(sharingIntent, "Share via"));
    }

    private void shareViaWhatsapp() {
        Intent whatsappIntent = new Intent(Intent.ACTION_SEND);
        whatsappIntent.setType("text/plain");
        whatsappIntent.setPackage("com.whatsapp");
        whatsappIntent.putExtra(Intent.EXTRA_TEXT, "Check this website: " + rec_weblink.trim());
        try {
            startActivity(whatsappIntent);
        } catch (ActivityNotFoundException ex) {
            Toast.makeText(this, "WhatsApp has not been installed.", Toast.LENGTH_SHORT).show();
        }
    }

    private void shareViaEmail() {
        Intent emailIntent = new Intent(Intent.ACTION_SENDTO,
                Uri.fromParts("mailto", "", null));
        emailIntent.putExtra(Intent.EXTRA_SUBJECT, "Check this Website");
        emailIntent.putExtra(Intent.EXTRA_TEXT, rec_weblink);
        startActivity(Intent.createChooser(emailIntent, "Send email..."));
    }

    private void showBookmarks() {
        Intent intent = new Intent(WebViewActivity.this, BookMarksActivity.class);
        startActivity(intent);
    }

    private void upadateIcon() {
        if (sharedPreferences.contains(rec_weblink.trim())) {
            favItem.setIcon(R.drawable.fav);
        } else {
            favItem.setIcon(R.drawable.unfav);
        }
    }

    private void addToFavorites() {
        if (sharedPreferences.contains(rec_weblink.trim())) {
            Toast.makeText(this, "Already Bookmarked", Toast.LENGTH_SHORT).show();
        } else {
            favItem.setIcon(R.drawable.fav);
            myEditor.putString(rec_weblink.trim(), rec_weblink.trim());
            myEditor.apply();

            NewsModel newsModel = new NewsModel();
            databaseHelper.insertData(
                    newsModel.getId(),
                    toolbarTextView.getText().toString().trim(),
                    rec_weblink.trim(),
                    getIntent().getStringExtra("website_image")
            );
            Toast.makeText(this, "Added to My Bookmarks", Toast.LENGTH_SHORT).show();
        }
    }
    @Override
    protected void onDestroy() {
        if (webView != null) {
            webView.stopLoading();
            webView.clearCache(true);
            webView.clearHistory();
            webView.destroy();
        }
        super.onDestroy();
    }
}