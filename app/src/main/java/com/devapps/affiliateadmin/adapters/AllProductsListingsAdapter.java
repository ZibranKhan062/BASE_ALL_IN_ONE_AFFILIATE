package com.devapps.affiliateadmin.adapters;

import android.app.Activity;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.RatingBar;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.cardview.widget.CardView;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.devapps.affiliateadmin.Config;
import com.devapps.affiliateadmin.R;
import com.devapps.affiliateadmin.models.AllProductsListingsModel;
import com.firebase.ui.database.FirebaseRecyclerAdapter;
import com.firebase.ui.database.FirebaseRecyclerOptions;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.database.DatabaseException;
import com.google.firebase.database.FirebaseDatabase;
import com.orhanobut.dialogplus.DialogPlus;

public class AllProductsListingsAdapter extends FirebaseRecyclerAdapter<AllProductsListingsModel, AllProductsListingsAdapter.ViewHolder> {

    private Context context;

    public AllProductsListingsAdapter(@NonNull FirebaseRecyclerOptions<AllProductsListingsModel> options, Context context) {
        super(options);
        this.context = context;
    }

    @Override
    protected void onBindViewHolder(@NonNull ViewHolder holder, final int position, @NonNull final AllProductsListingsModel model) {

        Glide.with(context).load(model.getImage()).into(holder.bookImg);
        try {
            holder.bookTitle.setText(model.getTitle());
        } catch (Exception e) {
            e.printStackTrace();
        }

        try {
            holder.bookDescription.setText(model.getPricing());
        } catch (Exception e) {
            e.printStackTrace();
        }


        try {
            holder.ratingBar.setRating(model.getRatings());
        }

        catch (IllegalStateException e) {
            //catch the IllegalStateExeption
        }

        catch (Exception e) {

            Toast.makeText(context, e.getMessage(), Toast.LENGTH_SHORT).show();

        }

        try {
            holder.totalNoOfRatings.setText(model.getNo_of_ratings());
        } catch (Exception e) {
            e.printStackTrace();
        }


        holder.editbutton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                final DialogPlus dialog = DialogPlus.newDialog(context)
                        .setGravity(Gravity.CENTER)
                        .setMargin(50, 0, 50, 0)
                        .setContentHolder(new com.orhanobut.dialogplus.ViewHolder(R.layout.all_products_custom_layout))
                        .setExpanded(false)  // This will enable the expand feature, (similar to android L share dialog)
                        .create();
                dialog.show();

                View holderView = dialog.getHolderView();
                final EditText productName = holderView.findViewById(R.id.product_name);
                final EditText productPricing = holderView.findViewById(R.id.product_pricing);
                final EditText productLink = holderView.findViewById(R.id.product_link);
                final EditText productImageLink = holderView.findViewById(R.id.product_image_link);
                final EditText productRating = holderView.findViewById(R.id.product_rating);
                final EditText totalRatings = holderView.findViewById(R.id.total_ratings);
                Button saveChangesbtn = holderView.findViewById(R.id.save_changes_btn);

                productName.setText(model.getTitle());
                productPricing.setText(model.getPricing());
                productLink.setText(model.getLinks());
                productImageLink.setText(model.getImage());
                productRating.setText(String.valueOf(model.getRatings()));
                totalRatings.setText(model.getNo_of_ratings());


                saveChangesbtn.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {

                        if (Config.isdemoEnabled) {
                            Toast.makeText(context, "Operation not allowed in Demo App", Toast.LENGTH_SHORT).show();
                            return;
                        }

                        AllProductsListingsModel allProductsListingsModel = new AllProductsListingsModel(productImageLink.getText().toString().trim(),
                                productLink.getText().toString().trim(),
                                totalRatings.getText().toString().trim(),
                                productPricing.getText().toString().trim(),
                                Float.parseFloat(productRating.getText().toString().trim()),
                                productName.getText().toString().trim());

                        Intent intent = ((Activity) context).getIntent();

                        FirebaseDatabase.getInstance().getReference()
                                .child("ShopCategories").child(intent.getStringExtra("CurrentPosition")).child("items")
                                .child(getRef(position).getKey())
                                .setValue(allProductsListingsModel)
//                                .updateChildren(allProductsListingsModel)
                                .addOnCompleteListener(new OnCompleteListener<Void>() {
                                    @Override
                                    public void onComplete(@NonNull Task<Void> task) {

                                        if (task.isSuccessful()) {
                                            Toast.makeText(context, "Operation Successfully Completed", Toast.LENGTH_SHORT).show();
                                        } else {
                                            Toast.makeText(context, "Some Error Occured", Toast.LENGTH_SHORT).show();
                                        }
                                        dialog.dismiss();
                                    }
                                });


                    }
                });

            }
        });

        holder.deleteButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                final Intent intent = ((Activity) context).getIntent();

                new AlertDialog.Builder(context)
                        .setTitle("Delete Item")
                        .setMessage("Are you sure you want to delete this Item ?")
                        .setPositiveButton("OK", new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int which) {
                                if (Config.isdemoEnabled) {
                                    Toast.makeText(context, "Operation not allowed in Demo App", Toast.LENGTH_SHORT).show();
                                    return;
                                }


                                FirebaseDatabase.getInstance().getReference()
                                        .child("ShopCategories").child(intent.getStringExtra("CurrentPosition")).child("items")
                                        .child(getRef(position).getKey())
                                        .removeValue()
                                        .addOnCompleteListener(new OnCompleteListener<Void>() {
                                            @Override
                                            public void onComplete(@NonNull Task<Void> task) {

                                                if (task.isSuccessful()) {

                                                    Toast.makeText(context, "Deleted Successfully", Toast.LENGTH_SHORT).show();
                                                } else {
                                                    Toast.makeText(context, "Some Error Occurred", Toast.LENGTH_SHORT).show();
                                                }

                                            }
                                        });
                            }
                        })
                        // A null listener allows the button to dismiss the dialog and take no further action.
                        .setNegativeButton("Cancel", null)
                        .setIcon(android.R.drawable.ic_dialog_alert)
                        .show();


            }
        });


    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(context).inflate(R.layout.all_products_listings_layout, parent, false);
        return new AllProductsListingsAdapter.ViewHolder(v);
    }

    class ViewHolder extends RecyclerView.ViewHolder {
        TextView bookTitle;
        TextView bookDescription;
        TextView totalNoOfRatings;
        ImageView bookImg;
        ImageView editbutton;
        ImageView deleteButton;
        TextView bookPrice;
        CardView cardView;
        RatingBar ratingBar;


        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            bookTitle = itemView.findViewById(R.id.book_title);
            bookDescription = itemView.findViewById(R.id.book_description);
            bookImg = itemView.findViewById(R.id.book_img);
            bookPrice = itemView.findViewById(R.id.book_price);
            cardView = itemView.findViewById(R.id.cardView);
            ratingBar = itemView.findViewById(R.id.ratingBar);
            totalNoOfRatings = itemView.findViewById(R.id.total_no_of_ratings);
            editbutton = itemView.findViewById(R.id.editbutton);
            deleteButton = itemView.findViewById(R.id.deleteButton);

        }
    }
}
