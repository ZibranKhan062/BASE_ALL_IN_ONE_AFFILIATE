package com.affiliate.affiliate;

import android.content.Intent;
import android.graphics.Paint;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.webkit.WebChromeClient;
import android.webkit.WebView;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import com.bumptech.glide.Glide;

public class DetailsDeals extends AppCompatActivity {

    ImageView prodImage;
    TextView prodName, sellingPrice, discountedPrice, percentOff, prodDesc;
    Button grabDealButton;
    Intent intent;
    String getProdName, getProdImage, getProdDesc, getProdLink, getProdDiscountPrice, getProdSellingPrice, getProdPercentOff, getVidID;

    public ImageView playButton;

    TextView videoLabel;
    ImageButton shareDeal, shareWADeal;
    WebView webView ;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_details_deals);

        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowHomeEnabled(true);
        getSupportActionBar().setDisplayShowTitleEnabled(false);
        intent = getIntent();
        getProdName = intent.getStringExtra("ProdName");
        getProdImage = intent.getStringExtra("ProdImage");
        getProdDesc = intent.getStringExtra("ProdDesc");
        getProdLink = intent.getStringExtra("ProdLink");
        getProdDiscountPrice = intent.getStringExtra("ProdDiscountPrice");
        getProdSellingPrice = intent.getStringExtra("ProdSellingPrice");
        getProdPercentOff = intent.getStringExtra("ProdPercentOff");
        getVidID = intent.getStringExtra("VidID");
        Log.e("VID ID", getVidID);

        prodImage = findViewById(R.id.prodImage);
        prodName = findViewById(R.id.prodName);
        sellingPrice = findViewById(R.id.sellingPrice);
        discountedPrice = findViewById(R.id.discountedPrice);
        percentOff = findViewById(R.id.percentOff);
        prodDesc = findViewById(R.id.prodDesc);
        grabDealButton = findViewById(R.id.grabDealButton);
        playButton = findViewById(R.id.btnYoutube_player);

        videoLabel = findViewById(R.id.videoLabel);
        shareDeal = findViewById(R.id.shareDeal);
        shareWADeal = findViewById(R.id.shareWADeal);
        webView = findViewById(R.id.webView);
        Glide.with(DetailsDeals.this).load(getProdImage).placeholder(R.drawable.placeholder).error(R.drawable.placeholder).into(prodImage);
        prodName.setText(getProdName.trim());
        String selectedCurrency = Config.getSelectedCurrency(DetailsDeals.this);
        sellingPrice.setText(selectedCurrency + "" + getProdSellingPrice.trim());
        sellingPrice.setPaintFlags(sellingPrice.getPaintFlags() | Paint.STRIKE_THRU_TEXT_FLAG);

        discountedPrice.setText(selectedCurrency + "" + getProdDiscountPrice.trim());
        percentOff.setText(getProdPercentOff.trim() + " % off");
        prodDesc.setText(getProdDesc.trim());

        if (getVidID.equalsIgnoreCase("n/a")) {
            webView.setVisibility(View.GONE);
            videoLabel.setVisibility(View.GONE);
        }


        grabDealButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                takeActionOnDeal();
            }
        });


        shareDeal.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent sharingIntent = new Intent(android.content.Intent.ACTION_SEND);
                sharingIntent.setType("text/plain");
                String shareBody = "Check this out: " + prodName.getText().toString().trim() + " " + getProdLink;
                sharingIntent.putExtra(android.content.Intent.EXTRA_TEXT, shareBody);
                startActivity(Intent.createChooser(sharingIntent, "Share via"));
            }
        });

        shareWADeal.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent whatsappIntent = new Intent(Intent.ACTION_SEND);
                whatsappIntent.setType("text/plain");
                whatsappIntent.setPackage("com.whatsapp");
                whatsappIntent.putExtra(Intent.EXTRA_TEXT, "Check this out: " + prodName.getText().toString().trim() + " " + getProdLink);
                try {
                    startActivity(whatsappIntent);
                } catch (android.content.ActivityNotFoundException ex) {
                    Toast.makeText(DetailsDeals.this,
                            "Whatsapp have not been installed.",
                            Toast.LENGTH_SHORT).show();
                }
            }
        });



        try {

            String videoTemplate = "<iframe width=\"100%%\" height=\"100%%\" src=\"https://www.youtube.com/embed/%s\" title=\"YouTube video player\" frameborder=\"0\" allow=\"accelerometer; autoplay; clipboard-write; encrypted-media; gyroscope; picture-in-picture; web-share\" allowfullscreen></iframe>";
            String video = String.format(videoTemplate, getVidID);
            webView.loadData(video, "text/html", "utf-8");
            webView.getSettings().setJavaScriptEnabled(true);
            webView.setWebChromeClient(new WebChromeClient());
        } catch (Exception e) {
            Log.e("Exception",""+e.getLocalizedMessage());
            e.printStackTrace();
        }

    }

    private void takeActionOnDeal() {

        Intent i = new Intent(DetailsDeals.this, DealsWebView.class);
        i.putExtra("Link", getProdLink);
        i.putExtra("Title", getProdName);
        startActivity(i);

    }

    @Override
    public boolean onSupportNavigateUp() {
        onBackPressed();
        return true;
    }

}