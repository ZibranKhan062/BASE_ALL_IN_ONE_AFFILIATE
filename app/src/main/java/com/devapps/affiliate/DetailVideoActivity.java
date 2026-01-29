package com.devapps.affiliate;

import android.content.ClipData;
import android.content.ClipboardManager;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.cardview.widget.CardView;

import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.android.youtube.player.YouTubeInitializationResult;
import com.google.android.youtube.player.YouTubeStandalonePlayer;
import com.google.android.youtube.player.YouTubeThumbnailLoader;
import com.google.android.youtube.player.YouTubeThumbnailView;

public class DetailVideoActivity extends AppCompatActivity {

    Intent vidIntent;
    String vidTitle, vidID, vidDesc, URL;
    CardView card1;

    protected RelativeLayout relativeLayoutOverYouTubeThumbnailView;
    TextView channel_name, textDesc, toolbarTextView, dateTime, buy_now;
    YouTubeThumbnailView youTubeThumbnailView;
    public ImageView playButton;
    RelativeLayout parent_relativeLayout;
    FloatingActionButton wa;

    String Base_URL = "https://www.youtube.com/watch?v=";
    String textPlain = "text/plain";
    String checkVideo = "Check this Video : ";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_detail_video);

        // UI Initialization
        Toolbar toolbar = findViewById(R.id.toolbar);
        toolbarTextView = findViewById(R.id.toolbarTextView);
        dateTime = findViewById(R.id.dateTime);
        wa = findViewById(R.id.wa);
        buy_now = findViewById(R.id.buy_now);
        channel_name = findViewById(R.id.channel_name);
        card1 = findViewById(R.id.card1);
        textDesc = findViewById(R.id.textDesc);
        playButton = findViewById(R.id.btnYoutube_player);
        relativeLayoutOverYouTubeThumbnailView = findViewById(R.id.relativeLayout_over_youtube_thumbnail);
        youTubeThumbnailView = findViewById(R.id.youtube_thumbnail);
        parent_relativeLayout = findViewById(R.id.parent_relativeLayout);

        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowHomeEnabled(true);
        getSupportActionBar().setDisplayShowTitleEnabled(false);
        toolbarTextView.setText("Video Details");

        // Data Retrieval
        vidIntent = getIntent();
        vidTitle = vidIntent.getStringExtra("vidIDTitle");
        vidDesc = vidIntent.getStringExtra("VidDesc");
        URL = vidIntent.getStringExtra("buy_now_url");

        // FIX: Extract clean Video ID from possible full URL to avoid "Login Required" error
        String rawID = vidIntent.getStringExtra("VidID");
        vidID = extractVideoId(rawID);

        String dateStr = vidIntent.getStringExtra("date");
        dateTime.setText(dateStr != null ? dateStr.trim() : "");
        channel_name.setText(vidTitle);

        if (URL == null || URL.equalsIgnoreCase("null") || TextUtils.isEmpty(URL)) {
            buy_now.setVisibility(View.GONE);
        }

        // Thumbnail Logic
        final YouTubeThumbnailLoader.OnThumbnailLoadedListener onThumbnailLoadedListener = new YouTubeThumbnailLoader.OnThumbnailLoadedListener() {
            @Override
            public void onThumbnailError(YouTubeThumbnailView view, YouTubeThumbnailLoader.ErrorReason error) {}

            @Override
            public void onThumbnailLoaded(YouTubeThumbnailView view, String s) {
                view.setVisibility(View.VISIBLE);
                relativeLayoutOverYouTubeThumbnailView.setVisibility(View.VISIBLE);
            }
        };

        youTubeThumbnailView.initialize(Config.getApiKey(), new YouTubeThumbnailView.OnInitializedListener() {
            @Override
            public void onInitializationSuccess(YouTubeThumbnailView view, final YouTubeThumbnailLoader loader) {
                if (vidID != null) {
                    loader.setVideo(vidID);
                    loader.setOnThumbnailLoadedListener(onThumbnailLoadedListener);
                }
            }

            @Override
            public void onInitializationFailure(YouTubeThumbnailView view, YouTubeInitializationResult result) {
                Toast.makeText(DetailVideoActivity.this, "YouTube Error: " + result, Toast.LENGTH_LONG).show();
            }
        });

        // Player Click Logic
        card1.setOnClickListener(view -> {
            if (vidID != null) {
                Intent intent = YouTubeStandalonePlayer.createVideoIntent(DetailVideoActivity.this,
                        Config.getApiKey(), vidID, 0, true, false);
                startActivity(intent);
            } else {
                Toast.makeText(DetailVideoActivity.this, "Video ID not found", Toast.LENGTH_SHORT).show();
            }
        });

        // Description Formatting Fix
        if (vidDesc != null) {
            textDesc.setText(vidDesc.replace("\\n", "\n"));
        }

        // Deep Linking for Buy Now Button (Task 1 Fix)
        buy_now.setOnClickListener(view -> {
            if (URL != null) {
                String targetUrl = URL;
                if (!targetUrl.startsWith("http://") && !targetUrl.startsWith("https://")) {
                    targetUrl = "https://" + targetUrl;
                }
                Intent intent = new Intent(Intent.ACTION_VIEW, Uri.parse(targetUrl));
                try {
                    startActivity(intent);
                } catch (Exception e) {
                    Toast.makeText(DetailVideoActivity.this, "App not found to open this link", Toast.LENGTH_SHORT).show();
                }
            }
        });

        // WhatsApp Share
        wa.setOnClickListener(view -> {
            Intent whatsappIntent = new Intent(Intent.ACTION_SEND);
            whatsappIntent.setType(textPlain);
            whatsappIntent.setPackage("com.whatsapp");
            whatsappIntent.putExtra(Intent.EXTRA_TEXT, checkVideo + Base_URL + vidID);
            try {
                startActivity(whatsappIntent);
            } catch (Exception ex) {
                Toast.makeText(DetailVideoActivity.this, "WhatsApp is not installed.", Toast.LENGTH_SHORT).show();
            }
        });
    }

    /**
     * Helper to extract the 11-character Video ID from a full YouTube URL.
     * Prevents Problem 9 "Login Required" crashes.
     */
    private String extractVideoId(String url) {
        if (url == null || url.length() < 11) return url;
        if (url.contains("v=")) {
            return url.split("v=")[1].split("&")[0];
        } else if (url.contains("youtu.be/")) {
            return url.split("youtu.be/")[1].split("\\?")[0];
        } else if (url.contains("embed/")) {
            return url.split("embed/")[1].split("\\?")[0];
        }
        return url;
    }

    @Override
    public boolean onSupportNavigateUp() {
        onBackPressed();
        return true;
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.toolbar_video, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();
        if (id == R.id.nav_shareVid) {
            String shareBody = checkVideo + Base_URL + vidID;
            if (URL != null && !URL.equalsIgnoreCase("null")) {
                shareBody += "\nBuy Link: " + URL;
            }
            Intent sharingIntent = new Intent(Intent.ACTION_SEND);
            sharingIntent.setType(textPlain);
            sharingIntent.putExtra(Intent.EXTRA_TEXT, shareBody);
            startActivity(Intent.createChooser(sharingIntent, "Share via"));
        }

        if (id == R.id.share_email) {
            Intent emailIntent = new Intent(Intent.ACTION_SENDTO, Uri.fromParts("mailto", "", null));
            emailIntent.putExtra(Intent.EXTRA_SUBJECT, "Check this Video");
            emailIntent.putExtra(Intent.EXTRA_TEXT, checkVideo + Base_URL + vidID);
            startActivity(Intent.createChooser(emailIntent, "Send email..."));
        }
        return super.onOptionsItemSelected(item);
    }
}