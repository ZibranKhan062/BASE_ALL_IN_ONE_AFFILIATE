package com.devapps.affiliateadmin.adapters;

import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.cardview.widget.CardView;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.devapps.affiliateadmin.Config;
import com.devapps.affiliateadmin.DetailActivity;
import com.devapps.affiliateadmin.R;
import com.devapps.affiliateadmin.SiteCategories;
import com.devapps.affiliateadmin.models.HomeModel;
import com.firebase.ui.database.FirebaseRecyclerAdapter;
import com.firebase.ui.database.FirebaseRecyclerOptions;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.database.FirebaseDatabase;
import com.orhanobut.dialogplus.DialogPlus;

import java.util.HashMap;
import java.util.Map;

public class HomeAdapter extends FirebaseRecyclerAdapter<HomeModel, HomeAdapter.Viewholder> {


    private Context context;

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
    protected void onBindViewHolder(@NonNull Viewholder holder, final int position, @NonNull final HomeModel homeModel) {
        Glide.with(context).load(homeModel.getImage()).centerCrop().into(holder.cardImage);
        holder.textLabel.setText(homeModel.getName());

        holder.editbutton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                final DialogPlus dialog = DialogPlus.newDialog(context)
                        .setGravity(Gravity.CENTER)
                        .setMargin(50, 0, 50, 0)
                        .setContentHolder(new com.orhanobut.dialogplus.ViewHolder(R.layout.home_items_edit))
                        .setExpanded(false)  // This will enable the expand feature, (similar to android L share dialog)
                        .create();

                View holderView = dialog.getHolderView();

                final EditText name = holderView.findViewById(R.id.name);
                final EditText imgLink = holderView.findViewById(R.id.imgLink);


                name.setText(homeModel.getName());
                imgLink.setText(homeModel.getImage());
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
                        map.put("image", imgLink.getText().toString());

                        FirebaseDatabase.getInstance().getReference()
                                .child("HomeItems")
                                .child(getRef(position).getKey())
                                .updateChildren(map)
                                .addOnCompleteListener(new OnCompleteListener<Void>() {
                                    @Override
                                    public void onComplete(@NonNull Task<Void> task) {
                                        Toast.makeText(context, "Task Completed Successfully !", Toast.LENGTH_LONG).show();
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
                                        .child("HomeItems")
                                        .child(getRef(position).getKey())
                                        .removeValue().addOnCompleteListener(new OnCompleteListener<Void>() {
                                    @Override
                                    public void onComplete(@NonNull Task<Void> task) {
                                        if (task.isSuccessful()) {

                                            Toast.makeText(context, "Deleted Successfully", Toast.LENGTH_SHORT).show();
                                        } else {
                                            Toast.makeText(context, "Some Error Occurred", Toast.LENGTH_SHORT).show();
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


        holder.homeCards.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                Intent intent = new Intent(context, DetailActivity.class);
                intent.putExtra("CurrentPosition", getRef(position).getKey());
                intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);

                context.startActivity(intent);
            }
        });
    }


    class Viewholder extends RecyclerView.ViewHolder {

        ImageView cardImage;
        ImageView editbutton;
        ImageView deleteButton;
        TextView textLabel;
        CardView homeCards;

        public Viewholder(@NonNull View itemView) {
            super(itemView);

            cardImage = itemView.findViewById(R.id.cardImage);
            textLabel = itemView.findViewById(R.id.text_label);
            homeCards = itemView.findViewById(R.id.home_cards);
            editbutton = itemView.findViewById(R.id.editbutton);
            deleteButton = itemView.findViewById(R.id.deleteButton);
        }
    }

    @Override
    public void onDataChanged() {
        ProgressBar progressBar = ((SiteCategories) context).findViewById(R.id.progressBar);
        progressBar.setVisibility(View.GONE);
    }

}
