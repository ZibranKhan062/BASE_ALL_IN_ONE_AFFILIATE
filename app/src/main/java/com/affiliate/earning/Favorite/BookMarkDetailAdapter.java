package com.affiliate.affiliate.Favorite;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.ToggleButton;

import androidx.annotation.NonNull;
import androidx.cardview.widget.CardView;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.affiliate.affiliate.DetailWebViewFav;
import com.affiliate.affiliate.R;

import java.util.List;

import static android.content.Context.MODE_PRIVATE;

public class BookMarkDetailAdapter extends RecyclerView.Adapter<BookMarkDetailAdapter.ViewHolder> {

    Context context;
    private List<DataModel> list;
    DatabaseHelper databaseHelper;
    SharedPreferences sharedPreferences;
    SharedPreferences.Editor myEditor;

    public BookMarkDetailAdapter(Context context, List<DataModel> list) {
        this.context = context;
        this.list = list;
    }


    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(context).inflate(R.layout.sqlite_layout, parent, false);
        return new ViewHolder(v);
    }

    @Override
    public void onBindViewHolder(@NonNull final ViewHolder holder, int position) {

        databaseHelper = new DatabaseHelper(context);

        sharedPreferences = context.getSharedPreferences("FavList", MODE_PRIVATE);

        final DataModel modelClass = list.get(position);
        sharedPreferences = context.getSharedPreferences("FavList", MODE_PRIVATE);
        holder.textLabel.setText(modelClass.getTitle());
        Glide.with(context).load(modelClass.getImage()).centerCrop().into(holder.cardImage);


        holder.toggleButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {


                databaseHelper.deleteTitle(String.valueOf(modelClass.getId()));
                Toast.makeText(context, "Removed from Bookmarks", Toast.LENGTH_LONG).show();
                list.remove(holder.getAdapterPosition());
                notifyItemRemoved(holder.getAdapterPosition());
                myEditor = sharedPreferences.edit();

//                myEditor.remove(holder.textLabel.getText().toString().trim()); // will delete key key_name4
                myEditor.remove(modelClass.getLink().trim()); // will delete key key_name4

                myEditor.apply(); // commit changes


            }
        });

        holder.fav_cards.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(context, DetailWebViewFav.class);
                intent.putExtra("weblink", modelClass.getLink().trim());
                intent.putExtra("webName", modelClass.getTitle().trim());

                context.startActivity(intent);

            }
        });


    }

    @Override
    public int getItemCount() {
        return list.size();
    }

    class ViewHolder extends RecyclerView.ViewHolder {
        ImageView cardImage;
        TextView textLabel;
        CardView fav_cards;
        ToggleButton toggleButton;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);

            cardImage = itemView.findViewById(R.id.cardImage);
            textLabel = itemView.findViewById(R.id.text_label);
            fav_cards = itemView.findViewById(R.id.fav_cards);
            toggleButton = itemView.findViewById(R.id.favorite);


        }
    }
}
