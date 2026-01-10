package com.affiliate.earning;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.cardview.widget.CardView;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.firebase.ui.database.FirebaseRecyclerAdapter;
import com.firebase.ui.database.FirebaseRecyclerOptions;
import com.google.android.material.progressindicator.LinearProgressIndicator;

public class CategoryAdapter extends FirebaseRecyclerAdapter<CategoryModel, CategoryAdapter.Viewholder> {

    Context context;

    public CategoryAdapter(@NonNull FirebaseRecyclerOptions<CategoryModel> options, Context context) {
        super(options);
        this.context = context;
    }

    @Override
    public void onDataChanged() {
//
        LinearProgressIndicator progressBar = ((Activity) context).findViewById(R.id.progress);
        progressBar.setVisibility(View.GONE);
    }


    @Override
    protected void onBindViewHolder(@NonNull final Viewholder holder, final int position, @NonNull CategoryModel model) {

        Glide.with(context).load(model.getImage()).centerCrop().into(holder.item_image);
        holder.item_name.setText(model.getTitle());
        holder.shopItemCard.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                Intent intent = new Intent(context, CategoryDetailActivity.class);
                intent.putExtra("CurrentPosition", getRef(position).getKey());
                intent.putExtra("itemName", holder.item_name.getText().toString().trim());
                intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                context.startActivity(intent);
            }
        });


    }

    @NonNull
    @Override
    public Viewholder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(context).inflate(R.layout.shop_recycler_items, parent, false);
        return new Viewholder(v);
    }

    class Viewholder extends RecyclerView.ViewHolder {

        ImageView item_image;
        TextView item_name;
        CardView shopItemCard;

        public Viewholder(@NonNull View itemView) {
            super(itemView);

            item_image = itemView.findViewById(R.id.item_image);
            item_name = itemView.findViewById(R.id.item_name);
            shopItemCard = itemView.findViewById(R.id.shopItemCard);

        }
    }
}
