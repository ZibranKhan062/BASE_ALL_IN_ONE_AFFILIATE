package com.devapps.affiliate.CouponPackage;

import android.app.Dialog;
import android.content.ClipData;
import android.content.ClipboardManager;
import android.content.Context;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import com.anupkumarpanwar.scratchview.ScratchView;
import com.devapps.affiliate.CouponWebView;
import com.devapps.affiliate.R;

public class CouponDetails extends AppCompatActivity {
    TextView toolbarTextView;
    Intent intent;
    String getTitle;
    String getDescription;
    String getWebsiteLink;
    String getCoupon;
    String getExpiryDate;

    Button visitWebsite;
    TextView dateTime;
    TextView description;

    Dialog dialog;
    ScratchView scratchView;
    TextView actual_coupon;
    Button copy;

    @RequiresApi(api = Build.VERSION_CODES.KITKAT)
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_coupon_details);

        Toolbar toolbar = findViewById(R.id.toolbar);
        toolbarTextView = findViewById(R.id.toolbarTextView);
        visitWebsite = findViewById(R.id.visitWebsite);
        dateTime = findViewById(R.id.dateTime);
        description = findViewById(R.id.description);
        copy = findViewById(R.id.copy);
        actual_coupon = findViewById(R.id.actual_coupon);

        dialog = new Dialog(this);
        scratchView = findViewById(R.id.scratchView);

        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowHomeEnabled(true);
        getSupportActionBar().setDisplayShowTitleEnabled(false);
        intent = getIntent();
        getTitle = intent.getStringExtra("Title");
        getDescription = intent.getStringExtra("Description");
        getWebsiteLink = intent.getStringExtra("websiteLink");
        getCoupon = intent.getStringExtra("coupon");
        getExpiryDate = intent.getStringExtra("expiryDate");

        toolbarTextView.setText(getTitle);
        dateTime.setText(getExpiryDate);
        description.setText(getDescription);
        actual_coupon.setText(getCoupon);

        visitWebsite.setVisibility(View.GONE);
        copy.setVisibility(View.GONE);
        scratchView.setRevealListener(new ScratchView
                .IRevealListener() {
            @Override
            public void onRevealed(ScratchView scratchView) {

                scratchView.clear();
                visitWebsite.setVisibility(View.VISIBLE);

                copy.setVisibility(View.VISIBLE);

//                dialog.setContentView(R.layout.popup_dialog);
//                Objects.requireNonNull(dialog
//                        .getWindow()).setBackgroundDrawable
//                        (new ColorDrawable(Color.TRANSPARENT));
//                dialog.show();
            }

            @Override
            public void onRevealPercentChangedListener
                    (ScratchView scratchView, float percent) {
                Log.d("Revealed", String.valueOf(percent));
            }
        });

        copy.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ClipboardManager cManager = (ClipboardManager) getSystemService(Context.CLIPBOARD_SERVICE);
                ClipData cData = ClipData.newPlainText("text", actual_coupon.getText().toString());
                cManager.setPrimaryClip(cData);
                Toast.makeText(CouponDetails.this, "Coupon Copied to Clipboard", Toast.LENGTH_LONG).show();
            }
        });

        visitWebsite.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                Intent i = new Intent(CouponDetails.this, CouponWebView.class);
                i.putExtra("WebsiteLink", getWebsiteLink.trim());
                startActivity(i);
            }
        });
    }


    @Override
    public boolean onSupportNavigateUp() {
        onBackPressed();
        return true;
    }
}