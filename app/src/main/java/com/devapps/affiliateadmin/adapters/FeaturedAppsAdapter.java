package com.devapps.affiliateadmin.adapters;

import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.net.Uri;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.RatingBar;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.devapps.affiliateadmin.AppListings;
import com.devapps.affiliateadmin.Config;
import com.devapps.affiliateadmin.R;
import com.devapps.affiliateadmin.models.FeaturedAppsModel;
import com.firebase.ui.database.FirebaseRecyclerAdapter;
import com.firebase.ui.database.FirebaseRecyclerOptions;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.database.FirebaseDatabase;
import com.orhanobut.dialogplus.DialogPlus;

import java.util.HashMap;
import java.util.Map;

public class FeaturedAppsAdapter extends FirebaseRecyclerAdapter<FeaturedAppsModel, FeaturedAppsAdapter.ViewHolder> {

    private Context context;

    public FeaturedAppsAdapter(@NonNull FirebaseRecyclerOptions<FeaturedAppsModel> options, Context context) {
        super(options);
        this.context = context;
    }

    @Override
    protected void onBindViewHolder(@NonNull ViewHolder holder, final int position, @NonNull final FeaturedAppsModel model) {

        Glide.with(context).load(model.getImg()).into(holder.bookImg);
        holder.bookTitle.setText(model.getName());

        holder.bookDescription.setText(String.valueOf(model.getRatings()));
        holder.ratingBar.setRating(model.getRatings());
        holder.bookPrice.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent browserIntent = new Intent(Intent.ACTION_VIEW, Uri.parse(String.valueOf(model.getLinks())));
                context.startActivity(browserIntent);

            }
        });

        holder.editbutton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                final DialogPlus dialog = DialogPlus.newDialog(context)
                        .setGravity(Gravity.CENTER)
                        .setMargin(50, 0, 50, 0)
                        .setContentHolder(new com.orhanobut.dialogplus.ViewHolder(R.layout.app_listing_content))
                        .setExpanded(false)  // This will enable the expand feature, (similar to android L share dialog)
                        .create();

                View holderView = dialog.getHolderView();

                final EditText name = holderView.findViewById(R.id.name);
                final EditText imgLink = holderView.findViewById(R.id.imgLink);
                final EditText link = holderView.findViewById(R.id.link);
                final EditText ratings = holderView.findViewById(R.id.ratings);


                name.setText(model.getName());
                imgLink.setText(model.getImg());
                link.setText(model.getLinks());
                ratings.setText(String.valueOf(model.getRatings()));


                dialog.show();


                Button update = holderView.findViewById(R.id.update);
                update.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {

                        if (Config.isdemoEnabled) {
                            Toast.makeText(context, "Operation not allowed in Demo App", Toast.LENGTH_SHORT).show();
                            return;
                        }


                        Map<String, Object> map = new HashMap<>();
                        map.put("name", name.getText().toString());
                        map.put("img", imgLink.getText().toString());
                        map.put("links", link.getText().toString());
                        map.put("ratings", Double.parseDouble(ratings.getText().toString()));
                        FirebaseDatabase.getInstance().getReference()
                                .child("FeaturedApps")
                                .child(getRef(position).getKey())
                                .updateChildren(map)
                                .addOnCompleteListener(new OnCompleteListener<Void>() {
                                    @Override
                                    public void onComplete(@NonNull Task<Void> task) {
                                        Toast.makeText(context, "Success !", Toast.LENGTH_SHORT).show();
                                        dialog.dismiss();
                                    }
                                });


                    }
                });

            }
        });

        holder.deleteButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                new AlertDialog.Builder(context)
                        .setTitle("Delete Item")
                        .setMessage("Are you sure you want to delete this Item ?")
                        .setPositiveButton("OK", new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int which) {

                                if (Config.isdemoEnabled) {
                                    Toast.makeText(context, "Operation not allowed in Demo App", Toast.LENGTH_SHORT).show();
                                    return;
                                }


                                FirebaseDatabase.getInstance().getReference()
                                        .child("FeaturedApps")
                                        .child(getRef(position).getKey())
                                        .removeValue().addOnCompleteListener(new OnCompleteListener<Void>() {
                                    @Override
                                    public void onComplete(@NonNull Task<Void> task) {
                                        if (task.isSuccessful()) {

                                            Toast.makeText(context, "Deleted Successfully", Toast.LENGTH_SHORT).show();
                                        } else {
                                            Toast.makeText(context, "Some Error Occured", Toast.LENGTH_SHORT).show();
                                        }
                                    }
                                });
                            }
                        })
                        // A null listener allows the button to dismiss the dialog and take no further action.
                        .setNegativeButton("Cancel", null)
                        .setIcon(android.R.drawable.ic_dialog_alert)
                        .show();
            }
        });

    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(context).inflate(R.layout.featuredappslayout, parent, false);
        return new FeaturedAppsAdapter.ViewHolder(v);
    }

    class ViewHolder extends RecyclerView.ViewHolder {
        TextView bookTitle;
        TextView bookDescription;
        ImageView bookImg;
        ImageView editbutton;
        ImageView deleteButton;
        Button bookPrice;
        Button update;
        RatingBar ratingBar;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            bookTitle = itemView.findViewById(R.id.book_title);
            bookDescription = itemView.findViewById(R.id.book_description);
            bookImg = itemView.findViewById(R.id.book_img);
            bookPrice = itemView.findViewById(R.id.book_price);
            ratingBar = itemView.findViewById(R.id.ratingBar);
            editbutton = itemView.findViewById(R.id.editbutton);
            deleteButton = itemView.findViewById(R.id.deleteButton);
        }
    }

    @Override
    public void onDataChanged() {
        ProgressBar progressBar = ((AppListings) context).findViewById(R.id.progressBar);
        progressBar.setVisibility(View.GONE);
    }
}
