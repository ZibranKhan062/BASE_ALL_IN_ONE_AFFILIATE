package com.affiliate.affiliate.detailactivityfiles;

import android.content.Context;
import android.content.Intent;
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
import com.affiliate.affiliate.R;
import com.affiliate.affiliate.WebViewActivity;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

public class DetailAdapter extends RecyclerView.Adapter<DetailAdapter.ViewHolder> implements Filterable {

    private Context mCtx;
    private List<DetailModel> detailModelList;
    private List<DetailModel> detailModelListAll;

    public interface AdShowListener {
        void onShowAd();
    }

    private AdShowListener adShowListener;


    public DetailAdapter(Context mCtx, List<DetailModel> detailModelList, AdShowListener listener) {
        this.mCtx = mCtx;
        this.detailModelList = detailModelList;
        this.detailModelListAll = new ArrayList<>(detailModelList);
        this.adShowListener = listener;

    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        LayoutInflater inflater = LayoutInflater.from(mCtx);
        View view = inflater.inflate(R.layout.layout_details, null);
        return new ViewHolder(view);
    }


    @Override
    public Filter getFilter() {

        return myFilter;
    }

    Filter myFilter = new Filter() {

        //Automatic on background thread
        @Override
        protected FilterResults performFiltering(CharSequence charSequence) {

            List<DetailModel> filteredList = new ArrayList<>();

            if (charSequence == null || charSequence.length() == 0) {
                filteredList.addAll(detailModelListAll);
            } else {
                for (DetailModel movie : detailModelListAll) {
                    if (movie.getName().toLowerCase().contains(charSequence.toString().toLowerCase())) {
                        filteredList.add(movie);
                    }
                }
            }

            FilterResults filterResults = new FilterResults();
            filterResults.values = filteredList;
            return filterResults;
        }

        //Automatic on UI thread
        @Override
        protected void publishResults(CharSequence charSequence, FilterResults filterResults) {
            detailModelList.clear();
            detailModelList.addAll((Collection<? extends DetailModel>) filterResults.values);
            notifyDataSetChanged();
        }
    };

    @Override
    public void onBindViewHolder(@NonNull final ViewHolder holder, int position) {

        final DetailModel detailModel = detailModelList.get(position);
        holder.textLabel.setText(detailModel.getName());
        Glide.with(mCtx).load(detailModel.getImages()).centerCrop().into(holder.cardImage);
        holder.homeCards.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                if (adShowListener != null) {
                    adShowListener.onShowAd();
                }
                Intent intent = new Intent(mCtx, WebViewActivity.class);
                intent.putExtra("Web", String.valueOf(detailModel.getClick()));
                intent.putExtra("website_name", holder.textLabel.getText().toString().trim());
                intent.putExtra("website_image", detailModel.getImages().trim());
                intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                mCtx.startActivity(intent);

            }
        });


    }

    @Override
    public int getItemCount() {
        return detailModelList.size();
    }

    class ViewHolder extends RecyclerView.ViewHolder {

        ImageView cardImage;
        TextView textLabel;
        CardView homeCards;


        public ViewHolder(@NonNull View itemView) {
            super(itemView);

            cardImage = itemView.findViewById(R.id.cardImage);
            textLabel = itemView.findViewById(R.id.text_label);
            homeCards = itemView.findViewById(R.id.home_cards);
        }
    }
}
