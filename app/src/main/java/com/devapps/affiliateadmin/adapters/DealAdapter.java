package com.devapps.affiliateadmin.adapters;

import android.graphics.Paint;
import android.text.TextUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Filter;
import android.widget.Filterable;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.cardview.widget.CardView;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.devapps.affiliateadmin.Deal;
import com.devapps.affiliateadmin.R;
import com.google.android.material.button.MaterialButton;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Locale;

public class DealAdapter extends RecyclerView.Adapter<DealAdapter.DealViewHolder> implements Filterable {
    private static final String TAG = "DealAdapter";
    private List<Deal> deals;
    private List<Deal> dealsFiltered;
    private OnDealClickListener listener;
    private SimpleDateFormat dateFormat;

    public interface OnDealClickListener {
        void onDealClick(Deal deal);

        void onDealMenuClick(Deal deal, View anchor);
    }

    public DealAdapter(List<Deal> deals, OnDealClickListener listener) {
        this.deals = deals;
        this.dealsFiltered = new ArrayList<>(deals);
        this.listener = listener;
        this.dateFormat = new SimpleDateFormat("MMM dd, yyyy", Locale.getDefault());
    }

    @NonNull
    @Override
    public DealViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_deal, parent, false);
        return new DealViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull DealViewHolder holder, int position) {
        Deal deal = dealsFiltered.get(position);
        holder.bind(deal);
    }

    @Override
    public int getItemCount() {
        return dealsFiltered.size();
    }

    public void updateDeals(List<Deal> newDeals) {
        this.deals = newDeals;
        this.dealsFiltered = new ArrayList<>(newDeals);
        notifyDataSetChanged();
    }

    // New method to get position of a deal
    public int getPosition(Deal deal) {
        for (int i = 0; i < dealsFiltered.size(); i++) {
            if (dealsFiltered.get(i).getId().equals(deal.getId())) {
                return i;
            }
        }
        return -1;
    }

    // New method to remove item
    public void removeItem(int position) {
        if (position >= 0 && position < dealsFiltered.size()) {
            // Remove from filtered list
            Deal removedDeal = dealsFiltered.remove(position);

            // Remove from original list
            for (int i = 0; i < deals.size(); i++) {
                if (deals.get(i).getId().equals(removedDeal.getId())) {
                    deals.remove(i);
                    break;
                }
            }

            notifyItemRemoved(position);
            notifyItemRangeChanged(position, getItemCount());
        }
    }

    @Override
    public Filter getFilter() {
        return new Filter() {
            @Override
            protected FilterResults performFiltering(CharSequence constraint) {
                String query = constraint.toString().toLowerCase().trim();
                List<Deal> filtered = new ArrayList<>();

                if (query.isEmpty()) {
                    filtered = new ArrayList<>(deals);
                } else {
                    for (Deal deal : deals) {
                        if (deal.getTitle().toLowerCase().contains(query) ||
                                (deal.getDescription() != null && deal.getDescription().toLowerCase().contains(query)) ||
                                (deal.getContent() != null && deal.getContent().toLowerCase().contains(query)) ||
                                (deal.getCategory() != null && deal.getCategory().toLowerCase().contains(query))) {
                            filtered.add(deal);
                        }
                    }
                }

                FilterResults results = new FilterResults();
                results.values = filtered;
                return results;
            }

            @SuppressWarnings("unchecked")
            @Override
            protected void publishResults(CharSequence constraint, FilterResults results) {
                dealsFiltered = (List<Deal>) results.values;
                notifyDataSetChanged();
            }
        };
    }

    class DealViewHolder extends RecyclerView.ViewHolder {
        private TextView tvTitle;
        private TextView tvDescription;
        private TextView tvPrice;
        private TextView tvOriginalPrice;
        private TextView tvExpiryDate;
        private TextView tvCategory;
        private ImageView ivDeal;
        private CardView cardView;
        private MaterialButton btnMenu;

        DealViewHolder(@NonNull View itemView) {
            super(itemView);
            tvTitle = itemView.findViewById(R.id.tvTitle);
            tvDescription = itemView.findViewById(R.id.tvDescription);
            tvPrice = itemView.findViewById(R.id.tvPrice);
            tvOriginalPrice = itemView.findViewById(R.id.tvOriginalPrice);
            tvExpiryDate = itemView.findViewById(R.id.tvExpiryDate);
            tvCategory = itemView.findViewById(R.id.tvCategory);
            ivDeal = itemView.findViewById(R.id.ivDeal);
            cardView = itemView.findViewById(R.id.cardView);
            btnMenu = itemView.findViewById(R.id.btnMenu);

            cardView.setOnClickListener(v -> {
                int position = getAdapterPosition();
                if (position != RecyclerView.NO_POSITION) {
                    listener.onDealClick(dealsFiltered.get(position));
                }
            });

            btnMenu.setOnClickListener(v -> {
                int position = getAdapterPosition();
                if (position != RecyclerView.NO_POSITION) {
                    listener.onDealMenuClick(dealsFiltered.get(position), btnMenu);
                }
            });
        }

        void bind(Deal deal) {
            try {
                tvTitle.setText(deal.getTitle());
                tvDescription.setText(deal.getDescription());
                tvCategory.setText(deal.getCategory());

                // Set prices
                if (!TextUtils.isEmpty(deal.getPrice())) {
                    tvPrice.setText("$" + deal.getPrice());
                    tvPrice.setVisibility(View.VISIBLE);
                } else {
                    tvPrice.setVisibility(View.GONE);
                }

                if (!TextUtils.isEmpty(deal.getOriginalPrice())) {
                    tvOriginalPrice.setText("$" + deal.getOriginalPrice());
                    tvOriginalPrice.setPaintFlags(tvOriginalPrice.getPaintFlags() | Paint.STRIKE_THRU_TEXT_FLAG);
                    tvOriginalPrice.setVisibility(View.VISIBLE);
                } else {
                    tvOriginalPrice.setVisibility(View.GONE);
                }

                // Set expiry date
                if (deal.getExpiryDate() > 0) {
                    String expiryDate = "Expires: " + dateFormat.format(new Date(deal.getExpiryDate()));
                    tvExpiryDate.setText(expiryDate);
                    tvExpiryDate.setVisibility(View.VISIBLE);
                } else {
                    tvExpiryDate.setVisibility(View.GONE);
                }

                // Load image using Glide
                if (!TextUtils.isEmpty(deal.getImageUrl())) {
                    Glide.with(itemView.getContext())
                            .load(deal.getImageUrl())
                            .placeholder(R.drawable.placeholder)
                            .error(R.drawable.placeholder)
                            .centerCrop()
                            .into(ivDeal);
                } else {
                    ivDeal.setImageResource(R.drawable.placeholder);
                }
            } catch (Exception e) {
                Log.e(TAG, "Error binding deal: " + e.getMessage(), e);
            }
        }
    }
}