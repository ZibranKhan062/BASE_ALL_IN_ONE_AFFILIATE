package com.affiliate.affiliate.trendingfiles;

import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.RatingBar;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.cardview.widget.CardView;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.affiliate.affiliate.Config;
import com.affiliate.affiliate.R;
import com.affiliate.affiliate.affiliateItems.AffiliateActivity;

import java.util.List;

public class TrendingAdapter extends RecyclerView.Adapter<TrendingAdapter.ViewHolder> {

    List<TrendingModel> trendingModelList;
    Context context;

    public TrendingAdapter(List<TrendingModel> trendingModelList, Context context) {
        this.trendingModelList = trendingModelList;
        this.context = context;
    }


    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {

        View v = LayoutInflater.from(context).inflate(R.layout.trending_items_layout, parent, false);
        return new TrendingAdapter.ViewHolder(v);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {

        final TrendingModel trendingModel = trendingModelList.get(position);
        Glide.with(context).load(trendingModel.getImage()).into(holder.bookImg);
        try {
            holder.bookTitle.setText(trendingModel.getTitle());
        } catch (Exception e) {
            e.printStackTrace();
        }

        try {
            String pricingData = trendingModel.getPricing();
            String selectedCurrency = Config.getSelectedCurrency(context);

// Get the currency symbol from the selected currency
            String currencySymbol = selectedCurrency.isEmpty() ? "₹" : selectedCurrency;

// Replace the currency symbol in the pricing data
            String updatedPricingData = pricingData.replace("₹", currencySymbol);

            holder.bookDescription.setText(updatedPricingData);

        } catch (Exception e) {
            e.printStackTrace();
        }


        try {
            holder.ratingBar.setRating(trendingModel.getRatings());
        } catch (Exception e) {
            e.printStackTrace();
        }


        holder.totalnoofatings.setText(trendingModel.getNo_of_ratings());

        holder.bookPrice.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(context, AffiliateActivity.class);
                intent.putExtra("affiliateLinks", trendingModel.getLinks());
                context.startActivity(intent);

            }
        });

        holder.cardView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(context, AffiliateActivity.class);
                intent.putExtra("affiliateLinks", trendingModel.getLinks());
                context.startActivity(intent);

            }
        });


    }

    @Override
    public int getItemCount() {
        return trendingModelList.size();
    }

    class ViewHolder extends RecyclerView.ViewHolder {


        TextView bookTitle;
        TextView bookDescription;
        TextView totalnoofatings;
        ImageView bookImg;
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
            totalnoofatings = itemView.findViewById(R.id.total_no_of_ratings);

        }
    }
}
