package com.affiliate.earning.nested;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.affiliate.earning.models.HorizontalModel;
import com.affiliate.earning.models.VerticalModel;
import com.affiliate.earning.R;

import java.util.ArrayList;

public class VerticalRecyclerAdapter extends RecyclerView.Adapter<VerticalRecyclerAdapter.ViewHolder> {

    Context context;
    ArrayList<VerticalModel> arrayList;

    public VerticalRecyclerAdapter(Context context, ArrayList<VerticalModel> arrayList) {
        this.context = context;
        this.arrayList = arrayList;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {

        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_vertical, parent, false);

        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {

        VerticalModel verticalModel = arrayList.get(position);
        String title = verticalModel.getCategories();
        ArrayList<HorizontalModel> singeItem = verticalModel.getArrayList();

        holder.txtTitle1.setText(title);

        HorizontalRecyclerViewAdapter horizontalRecyclerViewAdapter = new HorizontalRecyclerViewAdapter(context, singeItem);
        holder.recyclerView.setHasFixedSize(true);

        holder.recyclerView.setLayoutManager(new LinearLayoutManager(context, RecyclerView.HORIZONTAL, false));
        holder.recyclerView.setAdapter(horizontalRecyclerViewAdapter);


    }

    @Override
    public int getItemCount() {
        return arrayList.size();
    }

    class ViewHolder extends RecyclerView.ViewHolder {

        RecyclerView recyclerView;
        TextView txtTitle1;
        TextView  txtMore;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);

            recyclerView = itemView.findViewById(R.id.recyclerView1);
            txtTitle1 = itemView.findViewById(R.id.txtTitle1);
            txtMore = itemView.findViewById(R.id.txtMore);

        }
    }
}
