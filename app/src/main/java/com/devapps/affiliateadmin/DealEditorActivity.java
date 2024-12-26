package com.devapps.affiliateadmin;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.widget.Button;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.google.android.material.textfield.TextInputEditText;
import com.google.android.material.textfield.TextInputLayout;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.util.HashMap;
import java.util.Map;

public class DealEditorActivity extends AppCompatActivity {
    private TextInputEditText etTitle, etDescription, etImageUrl;
    private Button btnSubmit;
    private androidx.appcompat.widget.Toolbar toolbar;
    private String dealId;
    private DatabaseReference newsRef;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_deal_editor);

        // Initialize Firebase
        newsRef = FirebaseDatabase.getInstance().getReference("news");

        initViews();
        setupToolbar();
        loadDealData();
        setupSubmitButton();
    }

    private void initViews() {
        etTitle = findViewById(R.id.etTitle);
        etDescription = findViewById(R.id.etDescription);
        etImageUrl = findViewById(R.id.etImageUrl);
        btnSubmit = findViewById(R.id.btnSubmit);
    }

    private void setupToolbar() {
        toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setTitle("Edit Deal");

        toolbar.setNavigationOnClickListener(v -> onBackPressed());
    }

    private void setupSubmitButton() {
        btnSubmit.setOnClickListener(v -> saveDeal());
    }

    private void loadDealData() {
        Intent intent = getIntent();
        if (intent != null) {
            dealId = intent.getStringExtra("deal_id");
            etTitle.setText(intent.getStringExtra("title"));
            etDescription.setText(intent.getStringExtra("description"));
            etImageUrl.setText(intent.getStringExtra("image_url"));
        }
    }

    private void saveDeal() {
        if (!validateInputs()) {
            return;
        }

        // Show loading dialog
        ProgressDialog progressDialog = new ProgressDialog(this);
        progressDialog.setMessage("Saving deal...");
        progressDialog.setCancelable(false);
        progressDialog.show();

        // Create updated deal data
        Map<String, Object> updates = new HashMap<>();
        updates.put("title", etTitle.getText().toString().trim());
        updates.put("description", etDescription.getText().toString().trim());
        updates.put("imageUrl", etImageUrl.getText().toString().trim());

        // Update in Firebase news node
        newsRef.child(dealId)
                .updateChildren(updates)
                .addOnSuccessListener(aVoid -> {
                    progressDialog.dismiss();
                    Toast.makeText(this, "Deal updated successfully", Toast.LENGTH_SHORT).show();
                    finish();
                })
                .addOnFailureListener(e -> {
                    progressDialog.dismiss();
                    Toast.makeText(this, "Failed to update deal: " + e.getMessage(),
                            Toast.LENGTH_LONG).show();
                });
    }

    private boolean validateInputs() {
        boolean isValid = true;
        TextInputLayout tilTitle = (TextInputLayout) etTitle.getParent().getParent();
        TextInputLayout tilDescription = (TextInputLayout) etDescription.getParent().getParent();

        if (TextUtils.isEmpty(etTitle.getText())) {
            tilTitle.setError("Title is required");
            isValid = false;
        } else {
            tilTitle.setError(null);
        }

        if (TextUtils.isEmpty(etDescription.getText())) {
            tilDescription.setError("Description is required");
            isValid = false;
        } else {
            tilDescription.setError(null);
        }

        return isValid;
    }
}