package com.devapps.affiliateadmin;

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
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.recyclerview.widget.RecyclerView;

import com.firebase.ui.database.FirebaseRecyclerAdapter;
import com.firebase.ui.database.FirebaseRecyclerOptions;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.database.FirebaseDatabase;
import com.orhanobut.dialogplus.DialogPlus;

import java.util.HashMap;
import java.util.Map;

public class CouponAdapter extends FirebaseRecyclerAdapter<CouponModel, CouponAdapter.ViewHolder> {

    private Context context;

    public CouponAdapter(@NonNull FirebaseRecyclerOptions<CouponModel> options, Context context) {
        super(options);
        this.context = context;
    }

    @Override
    public void onDataChanged() {
        ProgressBar progressBar = ((CouponActivity) context).findViewById(R.id.progressBar);
        progressBar.setVisibility(View.GONE);
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(context).inflate(R.layout.coupon_layout, parent, false);
        return new ViewHolder(v);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, final int position, @NonNull final CouponModel couponModel) {


        holder.coupon_title.setText(couponModel.getCouponTitle());
        holder.coupon_desc.setText(couponModel.getCouponDescription());

        holder.deal_button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(context, CouponDetails.class);
                intent.putExtra("Title", couponModel.getCouponTitle());
                intent.putExtra("Description", couponModel.getCouponDescription());
                intent.putExtra("websiteLink", couponModel.getWebLink());
                intent.putExtra("coupon", couponModel.getCoupon());
                intent.putExtra("expiryDate", couponModel.getExpiryDate());
                context.startActivity(intent);

            }
        });

        holder.editbutton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                final DialogPlus dialog = DialogPlus.newDialog(context)
                        .setGravity(Gravity.CENTER)
                        .setMargin(50, 20, 50, 20)
                        .setContentHolder(new com.orhanobut.dialogplus.ViewHolder(R.layout.coupon_update_content))
                        .setExpanded(false)  // This will enable the expand feature, (similar to android L share dialog)
                        .create();

                View holderView = dialog.getHolderView();

                final EditText couponTitle = holderView.findViewById(R.id.couponTitle);
                final EditText couponExpiryDate = holderView.findViewById(R.id.couponExpiryDate);
                final EditText CouponCode = holderView.findViewById(R.id.CouponCode);
                final EditText couponDescription = holderView.findViewById(R.id.couponDescription);
                final EditText coupon_Website = holderView.findViewById(R.id.coupon_Website);

                couponTitle.setText(couponModel.getCouponTitle());
                couponExpiryDate.setText(couponModel.getExpiryDate());
                CouponCode.setText(couponModel.getCoupon());
                couponDescription.setText(couponModel.getCouponDescription());
                coupon_Website.setText(couponModel.getWebLink());


                dialog.show();


                Button update_coupon = holderView.findViewById(R.id.update_coupon);
                update_coupon.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {

                        if (Config.isdemoEnabled) {
                            Toast.makeText(context, "Operation not allowed in Demo App", Toast.LENGTH_SHORT).show();
                            return;
                        }


                        Map<String, Object> map = new HashMap<>();
                        map.put("couponTitle", couponTitle.getText().toString());
                        map.put("couponDescription", couponDescription.getText().toString());
                        map.put("expiryDate", couponExpiryDate.getText().toString());
                        map.put("coupon", CouponCode.getText().toString());
                        map.put("webLink", coupon_Website.getText().toString());


                        FirebaseDatabase.getInstance().getReference()
                                .child("Coupons")
                                .child(getRef(position).getKey())
                                .updateChildren(map)
                                .addOnCompleteListener(new OnCompleteListener<Void>() {
                                    @Override
                                    public void onComplete(@NonNull Task<Void> task) {
                                        Toast.makeText(context, "Successfully Updated !", Toast.LENGTH_SHORT).show();
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
                                        .child("Coupons")
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


    class ViewHolder extends RecyclerView.ViewHolder {

        TextView coupon_title, coupon_desc;
        Button deal_button;
        ImageView editbutton;
        ImageView deleteButton;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);

            coupon_title = itemView.findViewById(R.id.coupon_title);
            coupon_desc = itemView.findViewById(R.id.coupon_desc);
            deal_button = itemView.findViewById(R.id.deal_button);
            editbutton = itemView.findViewById(R.id.editbutton);
            deleteButton = itemView.findViewById(R.id.deleteButton);
        }
    }
}
