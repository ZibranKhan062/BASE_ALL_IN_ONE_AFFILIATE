package com.devapps.affiliate;

import android.content.Intent;
import android.content.SharedPreferences;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.view.KeyEvent;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
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
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import com.devapps.affiliate.Favorite.BookMarksActivity;
import com.devapps.affiliate.Favorite.DatabaseHelper;
import com.devapps.affiliate.Favorite.NewsModel;
//import com.wang.avi.AVLoadingIndicatorView;

public class DetailWebViewFav extends AppCompatActivity {
    WebView webView;
    Intent intent;

//    AVLoadingIndicatorView avi;
    ProgressBar progressbar;
    TextView toolbarTextView;
    Intent web_name;
    MenuItem favItem;
    String rec_weblink;
    SharedPreferences sharedPreferences;
    SharedPreferences.Editor myEditor;
    DatabaseHelper databaseHelper;
    Intent webImageIntent;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        CookieSyncManager.createInstance(getApplicationContext());
        CookieSyncManager.getInstance().startSync();
        setContentView(R.layout.activity_detail_web_view_fav);
        web_name = getIntent();
        webImageIntent = getIntent();

        sharedPreferences = getSharedPreferences("FavList", MODE_PRIVATE);
        myEditor = sharedPreferences.edit();
        databaseHelper = new DatabaseHelper(this);

        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
                WindowManager.LayoutParams.FLAG_FULLSCREEN);

        Toolbar toolbar = findViewById(R.id.toolbar);
        toolbarTextView = findViewById(R.id.toolbarTextView);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowHomeEnabled(true);
        getSupportActionBar().setDisplayShowTitleEnabled(false);
        toolbarTextView.setText(web_name.getStringExtra("webName"));

        webView = (WebView) findViewById(R.id.webview);
        progressbar = findViewById(R.id.progressbar);
        intent = getIntent();

        rec_weblink = intent.getStringExtra("weblink");


        WebSettings webSettings = webView.getSettings();
        webSettings.setSavePassword(true);
        webSettings.setJavaScriptEnabled(true);


        progressbar.setVisibility(View.VISIBLE);
        if (!rec_weblink.startsWith("http://") && !rec_weblink.startsWith("https://"))
            rec_weblink = "https://" + rec_weblink;
        CookieManager.getInstance().setAcceptCookie(true);
        webSettings.setAllowFileAccess(true);
        webSettings.setPluginState(WebSettings.PluginState.ON);
        webView.loadUrl(rec_weblink);

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
                progressbar.setVisibility(View.GONE);

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

    @Override
    public boolean onSupportNavigateUp() {
        onBackPressed();
        return true;
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {

        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.webview_toolbar_without_fav, menu);
        return true;

    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();


        if (id == R.id.share) {
            Intent sharingIntent = new Intent(android.content.Intent.ACTION_SEND);
            sharingIntent.setType("text/plain");
            String shareBody = "Check this website :" + rec_weblink.trim();
            sharingIntent.putExtra(android.content.Intent.EXTRA_TEXT, shareBody);
            startActivity(Intent.createChooser(sharingIntent, "Share via"));
        }

        if (id == R.id.whatsapp) {
            Intent whatsappIntent = new Intent(Intent.ACTION_SEND);
            whatsappIntent.setType("text/plain");
            whatsappIntent.setPackage("com.whatsapp");
            whatsappIntent.putExtra(Intent.EXTRA_TEXT, "Check this website :" + rec_weblink.trim());
            try {
                startActivity(whatsappIntent);
            } catch (android.content.ActivityNotFoundException ex) {
                Toast.makeText(this, "Whatsapp have not been installed.", Toast.LENGTH_SHORT).show();
            }
        }

        if (id == R.id.email) {

            Intent emailIntent = new Intent(Intent.ACTION_SENDTO, Uri.fromParts(
                    "mailto", "", null));
            emailIntent.putExtra(Intent.EXTRA_SUBJECT, "Check this Website");
            emailIntent.putExtra(Intent.EXTRA_TEXT, rec_weblink);
            startActivity(Intent.createChooser(emailIntent, "Send email..."));
        }

        if (id == R.id.showAll) {

            Intent intents = new Intent(DetailWebViewFav.this, BookMarksActivity.class);
            startActivity(intents);
        }

        return super.onOptionsItemSelected(item);
    }

    public void upadateIcon() {
        if (sharedPreferences.contains(rec_weblink.trim())) {
            favItem.setIcon(R.drawable.fav);
        } else {
            favItem.setIcon(R.drawable.unfav);

        }

    }

    private void addToFavorites() {
        sharedPreferences = getSharedPreferences("FavList", MODE_PRIVATE);

        if (sharedPreferences.contains(rec_weblink.trim())) {
            Toast.makeText(this, "Already Bookmarked", Toast.LENGTH_SHORT).show();

        } else {


            favItem.setIcon(R.drawable.fav);

            sharedPreferences = getSharedPreferences("FavList", MODE_PRIVATE);
            myEditor = sharedPreferences.edit();
            myEditor.putString(rec_weblink.trim(), rec_weblink.trim());
            myEditor.apply();
            NewsModel newsModel = new NewsModel();
            databaseHelper.insertData(newsModel.getId(), toolbarTextView.getText().toString().trim(), rec_weblink.trim(), webImageIntent.getStringExtra("website_image")
            );
            Toast.makeText(this, "Added to My Bookmarks", Toast.LENGTH_SHORT).show();


        }

    }
}