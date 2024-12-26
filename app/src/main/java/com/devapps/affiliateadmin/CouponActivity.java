package com.devapps.affiliateadmin;

import android.os.Bundle;
import android.view.Gravity;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.firebase.ui.database.FirebaseRecyclerOptions;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.orhanobut.dialogplus.DialogPlus;
import com.orhanobut.dialogplus.ViewHolder;

import java.util.List;

public class CouponActivity extends AppCompatActivity {
    TextView toolbarTextView;
    RecyclerView couponRecycler;
    ProgressBar progressbar;
    CouponAdapter couponAdapter;
    DatabaseReference databaseReference;
    List<CouponModel> couponModelList;

    FirebaseRecyclerOptions<CouponModel> options;
    FloatingActionButton adddItem;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_coupon);
        databaseReference = FirebaseDatabase.getInstance().getReference("Coupons");

        Toolbar toolbar = findViewById(R.id.toolbar);
        toolbarTextView = findViewById(R.id.toolbarTextView);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowHomeEnabled(true);
        getSupportActionBar().setDisplayShowTitleEnabled(false);
        toolbarTextView.setText("Coupon Codes");
        couponRecycler = findViewById(R.id.couponRecycler);
        progressbar = findViewById(R.id.progressbar);

        adddItem = findViewById(R.id.addItem);
        adddItem.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                addItem();
            }
        });


        options = new FirebaseRecyclerOptions.Builder<CouponModel>()
                .setQuery(FirebaseDatabase.getInstance().getReference("Coupons"), CouponModel.class)
                .build();

        couponRecycler.setLayoutManager(new LinearLayoutManager(this));
        couponAdapter = new CouponAdapter(options, this);
        couponRecycler.setAdapter(couponAdapter);
        progressbar.setVisibility(View.GONE);


    }

    @Override
    protected void onStart() {
        super.onStart();
        couponAdapter.startListening();
    }

    @Override
    protected void onStop() {
        super.onStop();
        couponAdapter.stopListening();
    }

    public void addItem() {
        final DialogPlus dialog = DialogPlus.newDialog(CouponActivity.this)
                .setGravity(Gravity.CENTER)
                .setMargin(50, 0, 50, 0)
                .setContentHolder(new ViewHolder(R.layout.add_coupon_content))
                .setExpanded(false)  // This will enable the expand feature, (similar to android L share dialog)
                .create();

        View holderView = dialog.getHolderView();
        dialog.show();
        final EditText couponTitle = holderView.findViewById(R.id.couponTitle);
        final EditText couponExpiryDate = holderView.findViewById(R.id.couponExpiryDate);
        final EditText CouponCode = holderView.findViewById(R.id.CouponCode);
        final EditText couponDescription = holderView.findViewById(R.id.couponDescription);
        final EditText coupon_Website = holderView.findViewById(R.id.coupon_Website);

        Button add_coupon = holderView.findViewById(R.id.add_coupon);
        add_coupon.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String getTitle = couponTitle.getText().toString().trim();
                String getcouponExpiryDate = couponExpiryDate.getText().toString().trim();
                String getCouponCode = CouponCode.getText().toString().trim();
                String getcouponDescription = couponDescription.getText().toString().trim();
                String getcoupon_Website = coupon_Website.getText().toString().trim();

                if (Config.isdemoEnabled) {
                    Toast.makeText(CouponActivity.this, "Operation not allowed in Demo App", Toast.LENGTH_LONG).show();
                    return;
                }

                CouponModel couponModel = new CouponModel(getTitle, getcouponDescription, getcouponExpiryDate, getCouponCode, getcoupon_Website);
                databaseReference = FirebaseDatabase.getInstance().getReference("Coupons");
                String getKey = databaseReference.push().getKey();
                databaseReference.child(getKey).setValue(couponModel).addOnCompleteListener(new OnCompleteListener<Void>() {
                    @Override
                    public void onComplete(@NonNull Task<Void> task) {
                        if (task.isSuccessful()) {
                            Toast.makeText(CouponActivity.this, "Coupon Added Successfully", Toast.LENGTH_LONG).show();
                        } else {
                            Toast.makeText(CouponActivity.this, "Some Error Occurred", Toast.LENGTH_SHORT).show();
                        }

                        dialog.dismiss();
                    }


                });
            }
        });


    }

    @Override
    public boolean onSupportNavigateUp() {
        onBackPressed();
        return true;
    }
}