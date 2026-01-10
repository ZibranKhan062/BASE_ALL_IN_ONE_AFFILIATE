package com.affiliate.affiliate.featuredappsfiles;

import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.RatingBar;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.affiliate.affiliate.R;

import java.util.List;

public class FeaturedAppsAdapter extends RecyclerView.Adapter<FeaturedAppsAdapter.ViewHolder> {

    List<FeaturedAppsModel> featuredAppsModelList;
    Context context;

    public FeaturedAppsAdapter(List<FeaturedAppsModel> featuredAppsModelList, Context context) {
        this.featuredAppsModelList = featuredAppsModelList;
        this.context = context;
    }


    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(context).inflate(R.layout.featuredappslayout, parent, false);
        return new FeaturedAppsAdapter.ViewHolder(v);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {


        final FeaturedAppsModel featuredAppsModel = featuredAppsModelList.get(position);
        Glide.with(context).load(featuredAppsModel.getImg()).into(holder.bookImg);
        holder.bookTitle.setText(featuredAppsModel.getName());

        holder.bookDescription.setText(String.valueOf(featuredAppsModel.getRatings()));

        holder.ratingBar.setRating(featuredAppsModel.getRatings());

        holder.bookPrice.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent browserIntent = new Intent(Intent.ACTION_VIEW, Uri.parse(String.valueOf(featuredAppsModel.getLinks())));
                context.startActivity(browserIntent);

            }
        });
    }

    @Override
    public int getItemCount() {
        return featuredAppsModelList.size();
    }

    class ViewHolder extends RecyclerView.ViewHolder {

        TextView bookTitle;
        TextView     bookDescription;
        ImageView bookImg;
        Button bookPrice;
        RatingBar ratingBar;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            bookTitle = itemView.findViewById(R.id.book_title);
            bookDescription = itemView.findViewById(R.id.book_description);
            bookImg = itemView.findViewById(R.id.book_img);
            bookPrice = itemView.findViewById(R.id.book_price);
            ratingBar = itemView.findViewById(R.id.ratingBar);

        }
    }
}
