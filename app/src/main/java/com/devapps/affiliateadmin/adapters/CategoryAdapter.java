package com.devapps.affiliateadmin.adapters;

import android.text.TextUtils;
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
import com.devapps.affiliateadmin.R;
import com.devapps.affiliateadmin.models.Category;

import java.util.ArrayList;
import java.util.List;


// CategoryAdapter inner class
public  class CategoryAdapter extends RecyclerView.Adapter<CategoryAdapter.CategoryViewHolder> implements Filterable {
    private List<Category> categories;
    private List<Category> categoriesFiltered;
    private OnCategoryClickListener listener;

    public interface OnCategoryClickListener {
        void onCategoryClick(Category category);
    }

    public CategoryAdapter(List<Category> categories, OnCategoryClickListener listener) {
        this.categories = categories;
        this.categoriesFiltered = new ArrayList<>(categories);
        this.listener = listener;
    }

    @NonNull
    @Override
    public CategoryViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_category, parent, false);
        return new CategoryViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull CategoryViewHolder holder, int position) {
        Category category = categoriesFiltered.get(position);
        holder.bind(category);
    }

    @Override
    public int getItemCount() {
        return categoriesFiltered.size();
    }

    @Override
    public Filter getFilter() {
        return new Filter() {
            @Override
            protected FilterResults performFiltering(CharSequence constraint) {
                String query = constraint.toString().toLowerCase().trim();
                List<Category> filtered = new ArrayList<>();

                if (query.isEmpty()) {
                    filtered = new ArrayList<>(categories);
                } else {
                    for (Category category : categories) {
                        if (category.getName().toLowerCase().contains(query) ||
                                category.getDescription().toLowerCase().contains(query)) {
                            filtered.add(category);
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
                categoriesFiltered = (List<Category>) results.values;
                notifyDataSetChanged();
            }
        };
    }

    class CategoryViewHolder extends RecyclerView.ViewHolder {
        private TextView tvName;
        private TextView tvDescription;
        private ImageView ivCategory;
        private CardView cardView;

        CategoryViewHolder(@NonNull View itemView) {
            super(itemView);
            tvName = itemView.findViewById(R.id.tvCategoryName);
            tvDescription = itemView.findViewById(R.id.tvCategoryDescription);
            ivCategory = itemView.findViewById(R.id.ivCategory);
            cardView = itemView.findViewById(R.id.cardView);

            cardView.setOnClickListener(v -> {
                int position = getAdapterPosition();
                if (position != RecyclerView.NO_POSITION) {
                    listener.onCategoryClick(categoriesFiltered.get(position));
                }
            });
        }

        void bind(Category category) {
            tvName.setText(category.getName());
            tvDescription.setText(category.getDescription());

            if (!TextUtils.isEmpty(category.getImageUrl())) {
                Glide.with(itemView.getContext())
                        .load(category.getImageUrl())
                        .placeholder(R.drawable.placeholder)
                        .error(R.drawable.placeholder)
                        .centerCrop()
                        .into(ivCategory);
            } else {
                ivCategory.setImageResource(R.drawable.placeholder);
            }
        }
    }

}