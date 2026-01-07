package com.devapps.affiliate;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
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
    String vidTitle;
    String vidID;
    String vidDesc;
    CardView card1;

    protected RelativeLayout relativeLayoutOverYouTubeThumbnailView;
    TextView channel_name;
    YouTubeThumbnailView youTubeThumbnailView;
    public ImageView playButton;
    RelativeLayout parent_relativeLayout;
    TextView textDesc;
    TextView toolbarTextView;
    String Base_URL = "https://www.youtube.com/watch?v=";
    TextView buy_now;
    String URL;
    TextView dateTime;
    String textPlain = "text/plain";
    FloatingActionButton wa;
    String checkVideo = "Check this Video : ";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_detail_video);
        Toolbar toolbar = findViewById(R.id.toolbar);
        toolbarTextView = findViewById(R.id.toolbarTextView);
        dateTime = findViewById(R.id.dateTime);
        wa = findViewById(R.id.wa);
        buy_now = findViewById(R.id.buy_now);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowHomeEnabled(true);
        getSupportActionBar().setDisplayShowTitleEnabled(false);
        toolbarTextView.setText("Video Details");


        vidIntent = getIntent();
        vidTitle = vidIntent.getStringExtra("vidIDTitle");
        vidID = vidIntent.getStringExtra("VidID");
        vidDesc = vidIntent.getStringExtra("VidDesc");
        URL = vidIntent.getStringExtra("buy_now_url");

        dateTime.setText(vidIntent.getStringExtra("date").trim());

        channel_name = findViewById(R.id.channel_name);
        card1 = findViewById(R.id.card1);
        textDesc = findViewById(R.id.textDesc);
        channel_name.setText(vidTitle);
        playButton = findViewById(R.id.btnYoutube_player);
        relativeLayoutOverYouTubeThumbnailView = findViewById(R.id.relativeLayout_over_youtube_thumbnail);
        youTubeThumbnailView = findViewById(R.id.youtube_thumbnail);
        parent_relativeLayout = findViewById(R.id.parent_relativeLayout);
        if (URL.equalsIgnoreCase("null")) {
            buy_now.setVisibility(View.GONE);
        }

        final YouTubeThumbnailLoader.OnThumbnailLoadedListener onThumbnailLoadedListener = new YouTubeThumbnailLoader.OnThumbnailLoadedListener() {
            @Override
            public void onThumbnailError(YouTubeThumbnailView youTubeThumbnailView, YouTubeThumbnailLoader.ErrorReason errorReason) {
                /*error handling*/
            }

            @Override
            public void onThumbnailLoaded(YouTubeThumbnailView youTubeThumbnailView, String s) {
                youTubeThumbnailView.setVisibility(View.VISIBLE);
                relativeLayoutOverYouTubeThumbnailView.setVisibility(View.VISIBLE);
            }
        };
        youTubeThumbnailView.initialize(Config.getApiKey(), new YouTubeThumbnailView.OnInitializedListener() {
            @Override
            public void onInitializationSuccess(YouTubeThumbnailView youTubeThumbnailView, final YouTubeThumbnailLoader youTubeThumbnailLoader) {

                youTubeThumbnailLoader.setVideo(vidID);
                youTubeThumbnailLoader.setOnThumbnailLoadedListener(onThumbnailLoadedListener);
                youTubeThumbnailLoader.setOnThumbnailLoadedListener(new YouTubeThumbnailLoader.OnThumbnailLoadedListener() {
                    @Override
                    public void onThumbnailLoaded(YouTubeThumbnailView youTubeThumbnailView, String s) {

                        youTubeThumbnailView.setVisibility(View.VISIBLE);
                        relativeLayoutOverYouTubeThumbnailView.setVisibility(View.VISIBLE);
                        youTubeThumbnailLoader.release();
                    }

                    @Override
                    public void onThumbnailError(YouTubeThumbnailView youTubeThumbnailView, YouTubeThumbnailLoader.ErrorReason errorReason) {
                        /*error handling */
                    }
                });
            }

            @Override
            public void onInitializationFailure(YouTubeThumbnailView youTubeThumbnailView, YouTubeInitializationResult youTubeInitializationResult) {
                Toast.makeText(DetailVideoActivity.this, "Details : " + youTubeInitializationResult, Toast.LENGTH_LONG).show();
            }
        });
        card1.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                Intent intent = YouTubeStandalonePlayer.createVideoIntent(DetailVideoActivity.this, Config.getApiKey(), vidID, 100,
                        true,
                        false);
                startActivity(intent);
            }
        });

        String descText = vidIntent.getStringExtra("VidDesc");
        if (descText.contains("\n")) {
            descText.replaceAll("\n", "\n");
        }
        textDesc.setText(descText);


        buy_now.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                if (!URL.startsWith("http://") && !URL.startsWith("https://"))
                    URL = "http://" + URL;
                Intent browserIntent = new Intent(Intent.ACTION_VIEW, Uri.parse(URL));
                startActivity(browserIntent);
            }
        });

        wa.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent whatsappIntent = new Intent(Intent.ACTION_SEND);
                whatsappIntent.setType(textPlain);
                whatsappIntent.setPackage("com.whatsapp");
                whatsappIntent.putExtra(Intent.EXTRA_TEXT, checkVideo + Base_URL + vidID);
                try {
                    startActivity(whatsappIntent);
                } catch (android.content.ActivityNotFoundException ex) {
                    Toast.makeText(DetailVideoActivity.this, "Whatsapp have not been installed.", Toast.LENGTH_SHORT).show();
                }
            }
        });
    }

    @Override
    public boolean onSupportNavigateUp() {
        onBackPressed();
        return true;
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {

        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.toolbar_video, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();
        if (id == R.id.nav_shareVid) {
            if (URL.equalsIgnoreCase("null")) {
                Intent sharingIntent = new Intent(android.content.Intent.ACTION_SEND);
                sharingIntent.setType(textPlain);
                String shareBody = "Check this Video :" + Base_URL + vidID;
                sharingIntent.putExtra(android.content.Intent.EXTRA_TEXT, shareBody);
                startActivity(Intent.createChooser(sharingIntent, "Share via"));

            } else if (!URL.equalsIgnoreCase("null")) {
                Intent sharingIntent = new Intent(android.content.Intent.ACTION_SEND);
                sharingIntent.setType(textPlain);
                String shareBody = checkVideo + Base_URL + vidID + " .Here is the Buy link : " + URL;
                sharingIntent.putExtra(android.content.Intent.EXTRA_TEXT, shareBody);
                startActivity(Intent.createChooser(sharingIntent, "Share via"));
            }

        }

        if (id == R.id.share_email) {

            Intent emailIntent = new Intent(Intent.ACTION_SENDTO, Uri.fromParts(
                    "mailto", "", null));
            emailIntent.putExtra(Intent.EXTRA_SUBJECT, "Check this Video");
            emailIntent.putExtra(Intent.EXTRA_TEXT, "Check this Video :" + Base_URL + vidID);
            startActivity(Intent.createChooser(emailIntent, "Send email..."));


        }


        return super.onOptionsItemSelected(item);
    }
}