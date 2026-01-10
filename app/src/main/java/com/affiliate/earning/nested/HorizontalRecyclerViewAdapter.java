package com.affiliate.affiliate.nested;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.affiliate.affiliate.models.HorizontalModel;
import com.affiliate.affiliate.R;

import java.util.ArrayList;

public class HorizontalRecyclerViewAdapter extends RecyclerView.Adapter<HorizontalRecyclerViewAdapter.HorizontalViewHolder> {

    Context context;
    ArrayList<HorizontalModel > arrayList;

    public HorizontalRecyclerViewAdapter(Context context, ArrayList<HorizontalModel> arrayList) {
        this.context = context;
        this.arrayList = arrayList;
    }

    @NonNull
    @Override
    public HorizontalViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
       View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_horizontal,parent, false);
       return  new HorizontalViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull HorizontalViewHolder holder, int position) {

        HorizontalModel horizontalModel = arrayList.get(position);
        holder.textViewTitle.setText(horizontalModel.getName());

    }

    @Override
    public int getItemCount() {
        return arrayList.size();
    }

    class HorizontalViewHolder extends RecyclerView.ViewHolder{

        TextView textViewTitle;
        ImageView imageViewThumb;

        public HorizontalViewHolder(@NonNull View itemView) {
            super(itemView);

            textViewTitle = itemView.findViewById(R.id.txtTitle);
            imageViewThumb = itemView.findViewById(R.id.ivThumb);

        }
    }
}
