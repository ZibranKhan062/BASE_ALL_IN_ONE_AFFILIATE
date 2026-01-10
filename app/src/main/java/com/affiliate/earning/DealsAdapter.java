package com.affiliate.earning;

import android.content.Context;
import android.content.Intent;
import android.graphics.Paint;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.cardview.widget.CardView;
import androidx.recyclerview.widget.RecyclerView;

import com.amrdeveloper.lottiedialog.LottieDialog;
import com.bumptech.glide.Glide;
import com.affiliate.earning.models.DealsModel;
import com.firebase.ui.database.FirebaseRecyclerAdapter;
import com.firebase.ui.database.FirebaseRecyclerOptions;

public class DealsAdapter extends FirebaseRecyclerAdapter<DealsModel, DealsAdapter.Viewholder> {
    private final Context context;
    private final LottieDialog lottieDialog;

    public DealsAdapter(@NonNull FirebaseRecyclerOptions<DealsModel> options, Context context) {
        super(options);
        this.context = context;
        this.lottieDialog = new LottieDialog(context);
    }

    @NonNull
    @Override
    public Viewholder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(context).inflate(R.layout.deals_items, parent, false);
        return new Viewholder(v);
    }

    @Override
    protected void onBindViewHolder(@NonNull Viewholder holder, int position, @NonNull DealsModel model) {
        try {
            String selectedCurrency = Config.getSelectedCurrency(context);

            Glide.with(context)
                    .load(model.getProductImg())
                    .placeholder(R.drawable.placeholder)
                    .error(R.drawable.placeholder)
                    .into(holder.productImg);

            holder.productName.setText(model.getProductName().trim());
            holder.discountedPrice.setText(selectedCurrency + model.getDiscountedPrice().trim());
            holder.sellingPrice.setText(selectedCurrency + model.getSellingPrice().trim());
            holder.percentOff.setText(model.getPercentOff().trim() + "% off");

            holder.sellingPrice.setPaintFlags(holder.sellingPrice.getPaintFlags() | Paint.STRIKE_THRU_TEXT_FLAG);

            holder.dealsItems.setOnClickListener(v -> {
                Intent i = new Intent(context, DetailsDeals.class);
                i.putExtra("ProdName", model.getProductName().trim());
                i.putExtra("ProdImage", model.getProductImg().trim());
                i.putExtra("ProdDesc", model.getProductDesc().trim());
                i.putExtra("ProdLink", model.getProductLink().trim());
                i.putExtra("ProdDiscountPrice", model.getDiscountedPrice().trim());
                i.putExtra("ProdSellingPrice", model.getSellingPrice().trim());
                i.putExtra("ProdPercentOff", model.getPercentOff().trim());
                i.putExtra("VidID", model.getVidID().trim());
                i.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                context.startActivity(i);
            });
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Override
    public void onViewRecycled(@NonNull Viewholder holder) {
        super.onViewRecycled(holder);
        try {
            if (holder.productImg != null) {
                Glide.with(context).clear(holder.productImg);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    static class Viewholder extends RecyclerView.ViewHolder {
        ImageView productImg;
        TextView productName;
        TextView discountedPrice;
        TextView sellingPrice;
        TextView percentOff;
        CardView dealsItems;

        public Viewholder(@NonNull View itemView) {
            super(itemView);
            productImg = itemView.findViewById(R.id.productImg);
            productName = itemView.findViewById(R.id.productName);
            discountedPrice = itemView.findViewById(R.id.discountedPrice);
            sellingPrice = itemView.findViewById(R.id.sellingPrice);
            percentOff = itemView.findViewById(R.id.percentOff);
            dealsItems = itemView.findViewById(R.id.dealsItems);
        }
    }
}