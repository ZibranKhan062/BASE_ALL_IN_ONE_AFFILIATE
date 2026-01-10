package com.affiliate.earning;

import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.affiliate.earning.CouponPackage.CouponDetails;
import com.firebase.ui.database.FirebaseRecyclerAdapter;
import com.firebase.ui.database.FirebaseRecyclerOptions;


public class CouponChildAdapter extends FirebaseRecyclerAdapter<CouponChildModel, CouponChildAdapter.Viewholder> {
    private final Context context;
    private boolean isDataValid = true;

    public CouponChildAdapter(@NonNull FirebaseRecyclerOptions<CouponChildModel> options, Context context) {
        super(options);
        this.context = context;
    }

    @NonNull
    @Override
    public Viewholder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(context).inflate(R.layout.coupon_items_temp_child, parent, false);
        return new Viewholder(v);
    }

    @Override
    protected void onBindViewHolder(@NonNull final Viewholder holder, int position, @NonNull CouponChildModel couponChildModel) {
        try {
            if (!isDataValid) return;

            // Get the actual position
            final int adapterPosition = holder.getBindingAdapterPosition();
            if (adapterPosition == RecyclerView.NO_POSITION) return;

            // Set text values safely
            if (couponChildModel.getCouponTitle() != null) {
                holder.coupon_title.setText(couponChildModel.getCouponTitle());
            }

            if (couponChildModel.getCouponDescription() != null) {
                holder.coupon_desc.setText(couponChildModel.getCouponDescription());
            }

            // Set click listener
            holder.deal_button.setOnClickListener(v -> {
                if (!isDataValid || context == null) return;

                try {
                    Intent intent = new Intent(context, CouponDetails.class);
                    intent.putExtra("Title", couponChildModel.getCouponTitle());
                    intent.putExtra("Description", couponChildModel.getCouponDescription());
                    intent.putExtra("websiteLink", couponChildModel.getWebLink());
                    intent.putExtra("coupon", couponChildModel.getCoupon());
                    intent.putExtra("expiryDate", couponChildModel.getExpiryDate());
                    intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                    context.startActivity(intent);
                } catch (Exception e) {
                    e.printStackTrace();
                }
            });

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Override
    public void onViewRecycled(@NonNull Viewholder holder) {
        super.onViewRecycled(holder);
        try {
            // Clear any resources if needed
            holder.coupon_title.setText("");
            holder.coupon_desc.setText("");
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Override
    public void stopListening() {
        isDataValid = false;
        super.stopListening();
    }

    @Override
    public void startListening() {
        isDataValid = true;
        super.startListening();
    }

    static class Viewholder extends RecyclerView.ViewHolder {
        final TextView coupon_title;
        final TextView coupon_desc;
        final Button deal_button;

        public Viewholder(@NonNull View itemView) {
            super(itemView);
            coupon_title = itemView.findViewById(R.id.coupon_title);
            coupon_desc = itemView.findViewById(R.id.coupon_desc);
            deal_button = itemView.findViewById(R.id.deal_button);
        }
    }
}