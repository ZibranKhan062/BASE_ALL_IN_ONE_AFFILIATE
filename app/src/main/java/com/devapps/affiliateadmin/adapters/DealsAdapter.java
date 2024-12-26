package com.devapps.affiliateadmin.adapters;

import android.content.Context;
import android.content.DialogInterface;
import android.graphics.Paint;
import android.text.TextUtils;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.devapps.affiliateadmin.Config;
import com.devapps.affiliateadmin.DealsOffers;
import com.devapps.affiliateadmin.R;
import com.devapps.affiliateadmin.models.DealsModel;
import com.firebase.ui.database.FirebaseRecyclerAdapter;
import com.firebase.ui.database.FirebaseRecyclerOptions;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.database.FirebaseDatabase;
import com.orhanobut.dialogplus.DialogPlus;

import java.util.HashMap;
import java.util.Map;

public class DealsAdapter extends FirebaseRecyclerAdapter<DealsModel, DealsAdapter.Viewholder> {

    Context context;


    public DealsAdapter(@NonNull FirebaseRecyclerOptions<DealsModel> options, Context context) {
        super(options);
        this.context = context;
    }

    @Override
    protected void onBindViewHolder(@NonNull Viewholder holder, final int position, @NonNull final DealsModel model) {

        Glide.with(context).load(model.getProductImg()).into(holder.productImg);
        holder.productName.setText(model.getProductName().trim());
        holder.discountedPrice.setText(context.getResources().getString(R.string.Rs) + model.getDiscountedPrice().trim());
        holder.sellingPrice.setText(context.getResources().getString(R.string.Rs) + model.getSellingPrice().trim());
        holder.percentOff.setText(model.getPercentOff().trim() + "% off");

        holder.discountedPrice.setPaintFlags(holder.discountedPrice.getPaintFlags() | Paint.STRIKE_THRU_TEXT_FLAG);

        holder.editbutton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                final DialogPlus dialog = DialogPlus.newDialog(context)
                        .setGravity(Gravity.CENTER)
                        .setMargin(50, 20, 50, 20)
                        .setContentHolder(new com.orhanobut.dialogplus.ViewHolder(R.layout.deals_update_content))
                        .setExpanded(false)  // This will enable the expand feature, (similar to android L share dialog)
                        .create();

                View holderView = dialog.getHolderView();

                final EditText prodName = holderView.findViewById(R.id.prodName);
                final EditText prodImage = holderView.findViewById(R.id.prodImage);
                final EditText sellingPrice = holderView.findViewById(R.id.sellingPrice);
                final EditText discountedPrice = holderView.findViewById(R.id.discountedPrice);
                final EditText percentOff = holderView.findViewById(R.id.percentOff);
                final EditText prodDescription = holderView.findViewById(R.id.prodDescription);
                final EditText prodLink = holderView.findViewById(R.id.prodLink);
                Button update_deal_item = holderView.findViewById(R.id.update_deal_item);
                final ImageView info = holderView.findViewById(R.id.info);
                info.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        new AlertDialog.Builder(context)
                                .setTitle("Maximize use of Free Database")
                                .setMessage("Upload your Hi res images to Free websites like postimages.org/imgbb.com and just paste the Image Link here. 512x512 px recommended")
                                .setNegativeButton("OK", null)
                                .setIcon(android.R.drawable.ic_dialog_info)
                                .show();
                    }
                });

                prodName.setText(model.getProductName());
                prodImage.setText(model.getProductImg());
                sellingPrice.setText(model.getSellingPrice());
                discountedPrice.setText(model.getDiscountedPrice());
                percentOff.setText(model.getPercentOff());
                prodDescription.setText(model.getProductDesc());
                prodLink.setText(model.getProductLink());


                dialog.show();
                update_deal_item.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {

                        if (Config.isdemoEnabled) {
                            Toast.makeText(context, "Operation not allowed in Demo App", Toast.LENGTH_SHORT).show();
                            return;
                        }

                        if (TextUtils.isEmpty(prodName.getText().toString().trim()) || TextUtils.isEmpty(prodImage.getText().toString().trim())
                                || TextUtils.isEmpty(sellingPrice.getText().toString().trim())
                                || TextUtils.isEmpty(discountedPrice.getText().toString().trim())
                                || TextUtils.isEmpty(percentOff.getText().toString().trim())
                                || TextUtils.isEmpty(prodDescription.getText().toString().trim()) || TextUtils.isEmpty(prodLink.getText().toString().trim())

                        ) {
                            Toast.makeText(context, "Please fill all fields", Toast.LENGTH_SHORT).show();
                            return;
                        }


                        Map<String, Object> map = new HashMap<>();
                        map.put("discountedPrice", discountedPrice.getText().toString().trim());
                        map.put("percentOff", percentOff.getText().toString().trim());
                        map.put("productImg", prodImage.getText().toString().trim());
                        map.put("productName", prodName.getText().toString().trim());
                        map.put("sellingPrice", sellingPrice.getText().toString().trim());
                        map.put("productDesc", prodDescription.getText().toString().trim());
                        map.put("productLink", prodLink.getText().toString().trim());


                        FirebaseDatabase.getInstance().getReference()
                                .child("Deals")
                                .child(getRef(position).getKey())
                                .updateChildren(map)
                                .addOnCompleteListener(new OnCompleteListener<Void>() {
                                    @Override
                                    public void onComplete(@NonNull Task<Void> task) {
                                        Toast.makeText(context, "Successfully Updated !", Toast.LENGTH_LONG).show();
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
                                        .child("Deals")
                                        .child(getRef(position).getKey())
                                        .removeValue().addOnCompleteListener(new OnCompleteListener<Void>() {
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
    public Viewholder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(context).inflate(R.layout.deals_layout, parent, false);
        return new Viewholder(v);
    }

    public class Viewholder extends RecyclerView.ViewHolder {

        ImageView productImg;
        TextView productName;
        TextView discountedPrice;
        TextView sellingPrice;
        TextView percentOff;

        ImageView editbutton;
        ImageView deleteButton;

        public Viewholder(@NonNull View itemView) {
            super(itemView);

            productImg = itemView.findViewById(R.id.productImg);
            productName = itemView.findViewById(R.id.productName);
            discountedPrice = itemView.findViewById(R.id.discountedPrice);
            sellingPrice = itemView.findViewById(R.id.sellingPrice);
            percentOff = itemView.findViewById(R.id.percentOff);
            editbutton = itemView.findViewById(R.id.editbutton);
            deleteButton = itemView.findViewById(R.id.deleteButton);


        }
    }

    @Override
    public void onDataChanged() {
        ProgressBar progressBar = ((DealsOffers) context).findViewById(R.id.progressBar);
        progressBar.setVisibility(View.GONE);
    }
}
