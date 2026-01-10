package com.affiliate.affiliate;

import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.affiliate.affiliate.models.TempModel;
import com.firebase.ui.database.FirebaseRecyclerAdapter;
import com.firebase.ui.database.FirebaseRecyclerOptions;

import java.util.List;

public class TempChildAdapter extends FirebaseRecyclerAdapter<TempChildModel, TempChildAdapter.Viewholder> {


    List<TempModel> tempModelList;
    Context context;

    public TempChildAdapter(@NonNull FirebaseRecyclerOptions<TempChildModel> options, Context context) {
        super(options);
        this.context = context;
    }

    @NonNull
    @Override
    public TempChildAdapter.Viewholder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(context).inflate(R.layout.home_items_temp_child, parent, false);
        return new TempChildAdapter.Viewholder(v);
    }


    @Override
    protected void onBindViewHolder(@NonNull final TempChildAdapter.Viewholder holder, final int position, @NonNull TempChildModel tempChildModel) {

        Glide.with(context).load(tempChildModel.getImages()).centerCrop().into(holder.cardImage);
        holder.text_label.setText(tempChildModel.getName());
        holder.item_click.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                Intent intent = new Intent(context, WebViewActivity.class);

                intent.putExtra("Web", String.valueOf(tempChildModel.getClick()));
                intent.putExtra("website_name", holder.text_label.getText().toString().trim());
                intent.putExtra("website_image", tempChildModel.getImages().trim());
                intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);


                intent.putExtra("CurrentPosition", getRef(position).getKey());
                intent.putExtra("itemName", holder.text_label.getText().toString().trim());
                intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                context.startActivity(intent);
            }
        });

    }


    class Viewholder extends RecyclerView.ViewHolder {


        TextView text_label;
        ImageView cardImage;
        RelativeLayout item_click;

        public Viewholder(@NonNull View itemView) {
            super(itemView);

            text_label = itemView.findViewById(R.id.text_label);
            cardImage = itemView.findViewById(R.id.cardImage);
            item_click = itemView.findViewById(R.id.item_click);

        }
    }
}

