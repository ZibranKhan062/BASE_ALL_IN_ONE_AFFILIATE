package com.affiliate.affiliate.CouponPackage;

import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.affiliate.affiliate.R;

import java.util.List;

public class CouponAdapter extends RecyclerView.Adapter<CouponAdapter.ViewHolder> {

    List<CouponModel> couponModelList;
    Context context;

    public CouponAdapter(List<CouponModel> couponModelList, Context context) {
        this.couponModelList = couponModelList;
        this.context = context;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(context).inflate(R.layout.coupon_layout, parent, false);
        return new ViewHolder(v);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {

        final CouponModel couponModel = couponModelList.get(position);

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

    }

    @Override
    public int getItemCount() {
        return couponModelList.size();
    }

    class ViewHolder extends RecyclerView.ViewHolder {

        TextView coupon_title, coupon_desc;
        Button deal_button;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);

            coupon_title = itemView.findViewById(R.id.coupon_title);
            coupon_desc = itemView.findViewById(R.id.coupon_desc);
            deal_button = itemView.findViewById(R.id.deal_button);
        }
    }
}
