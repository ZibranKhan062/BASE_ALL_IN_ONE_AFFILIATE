package com.devapps.affiliate.homeelements;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.cardview.widget.CardView;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.devapps.affiliate.DetailActivity;
import com.devapps.affiliate.R;
import com.firebase.ui.database.FirebaseRecyclerAdapter;
import com.firebase.ui.database.FirebaseRecyclerOptions;

import java.util.List;

public class HomeAdapter extends FirebaseRecyclerAdapter<HomeModel, HomeAdapter.Viewholder> {


    List<HomeModel> homeModelList;
    Context context;
    Intent i;
    String x;

    public HomeAdapter(@NonNull FirebaseRecyclerOptions<HomeModel> options, Context context) {
        super(options);
        this.context = context;
    }

    @NonNull
    @Override
    public Viewholder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(context).inflate(R.layout.home_items_layout, parent, false);
        return new HomeAdapter.Viewholder(v);
    }


    @Override
    protected void onBindViewHolder(@NonNull final Viewholder holder, final int position, @NonNull HomeModel homeModel) {


        i = ((Activity) context).getIntent();
        x = i.getStringExtra("HomeSearch");
        Log.e("INTENT", String.valueOf(x));
        Glide.with(context).load(homeModel.getImage()).centerCrop().into(holder.cardImage);
        holder.textLabel.setText(homeModel.getName());
        holder.homeCards.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                Intent intent = new Intent(context, DetailActivity.class);
                intent.putExtra("CurrentPosition", getRef(position).getKey());
                intent.putExtra("itemName", holder.textLabel.getText().toString().trim());
                intent.putExtra("HomeSearch", x);


                intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                context.startActivity(intent);
            }
        });

    }


    class Viewholder extends RecyclerView.ViewHolder {

        ImageView cardImage;
        TextView textLabel;
        CardView homeCards;

        public Viewholder(@NonNull View itemView) {
            super(itemView);

            cardImage = itemView.findViewById(R.id.cardImage);
            textLabel = itemView.findViewById(R.id.text_label);
            homeCards = itemView.findViewById(R.id.home_cards);
        }
    }
}
