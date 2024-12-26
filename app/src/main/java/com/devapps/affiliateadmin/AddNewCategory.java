package com.devapps.affiliateadmin;

import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.devapps.affiliateadmin.models.AllProductsListingsModel;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

public class AddNewCategory extends AppCompatActivity {
    DatabaseReference databaseReference;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_new_category);

        final EditText productcat = findViewById(R.id.product_cat);
        final EditText productname = findViewById(R.id.product_name);
        final EditText productpricing = findViewById(R.id.product_pricing);
        final EditText productLink = findViewById(R.id.product_link);
        final EditText productImageLink = findViewById(R.id.product_image_link);
        final EditText productRating = findViewById(R.id.product_rating);
        final EditText totalRatings = findViewById(R.id.total_ratings);
        Button addCategory = findViewById(R.id.add_category);

        addCategory.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String getProdCategory = productcat.getText().toString().trim();
                String getProdName = productname.getText().toString().trim();
                String getProdPricing = productpricing.getText().toString().trim();
                String getProdLink = productLink.getText().toString().trim();
                String getProdImageLink = productImageLink.getText().toString().trim();
                float getProdRatings = Float.parseFloat(productRating.getText().toString().trim());
                String getTotalRatings = totalRatings.getText().toString().trim();

                databaseReference = FirebaseDatabase.getInstance().getReference("ShopCategories");

                //String image, String links, String no_of_ratings, String pricing, float ratings, String title
                AllProductsListingsModel allProductsListingsModel = new AllProductsListingsModel(getProdImageLink, getProdLink,
                        getTotalRatings, getProdPricing, getProdRatings, getProdName


                );
                databaseReference.child(getProdCategory).push().setValue(allProductsListingsModel).addOnCompleteListener(new OnCompleteListener<Void>() {
                    @Override
                    public void onComplete(@NonNull Task<Void> task) {
                        if (task.isSuccessful()) {

                            Toast.makeText(AddNewCategory.this, "Category Added Successfully", Toast.LENGTH_SHORT).show();
                            productcat.setText("");
                            productname.setText("");
                            productpricing.setText("");
                            productLink.setText("");
                            productImageLink.setText("");
                            productRating.setText("");
                            totalRatings.setText("");
                        } else {
                            Toast.makeText(AddNewCategory.this, "Some Error Occurred", Toast.LENGTH_SHORT).show();
                        }
                    }
                });


            }
        });
    }
}