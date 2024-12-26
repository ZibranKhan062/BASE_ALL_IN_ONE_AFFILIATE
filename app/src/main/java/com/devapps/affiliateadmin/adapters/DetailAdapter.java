package com.devapps.affiliateadmin.adapters;

import android.app.Activity;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.cardview.widget.CardView;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.devapps.affiliateadmin.Config;
import com.devapps.affiliateadmin.R;
import com.devapps.affiliateadmin.models.DetailModel;
import com.firebase.ui.database.FirebaseRecyclerAdapter;
import com.firebase.ui.database.FirebaseRecyclerOptions;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.database.FirebaseDatabase;
import com.orhanobut.dialogplus.DialogPlus;
import com.orhanobut.dialogplus.ViewHolder;

import java.util.HashMap;
import java.util.Map;

public class DetailAdapter extends FirebaseRecyclerAdapter<DetailModel, DetailAdapter.PastViewHolder> {

    private Context context;
    String clickAction = "click";
    String imageAction = "images";
    String cancelAction = "Cancel";
    String deleteAction = "Delete Item";
    String sureDelete = "Are you sure you want to delete this Item ?";
    String deleteSuccess = "Deleted Successfully";
    String errorOccured = "Some Error Occured";


    public DetailAdapter(@NonNull FirebaseRecyclerOptions<DetailModel> options, Context context) {
        super(options);
        this.context = context;
    }


    @Override
    protected void onBindViewHolder(@NonNull final PastViewHolder holder, final int i, @NonNull final DetailModel post) {


        Intent intent = ((Activity) context).getIntent();
        final String target = intent.getStringExtra("CurrentPosition");
        Log.e("Target value", String.valueOf(target));


        holder.textLabel.setText(post.getName());
        Glide.with(context).load(post.getImages()).centerCrop().into(holder.cardImage);


        holder.editbutton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                final DialogPlus dialog = DialogPlus.newDialog(context)
                        .setGravity(Gravity.CENTER)
                        .setMargin(50, 0, 50, 0)
                        .setContentHolder(new ViewHolder(R.layout.content))
                        .setExpanded(false)  // This will enable the expand feature, (similar to android L share dialog)
                        .create();

                View holderView = dialog.getHolderView();

                final EditText title = holderView.findViewById(R.id.title);
                final EditText description = holderView.findViewById(R.id.description);
                final EditText author = holderView.findViewById(R.id.author);


                title.setText(post.getName());
                description.setText(post.getClick());
                author.setText(post.getImages());

                Button update = holderView.findViewById(R.id.update);

                update.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {

                        if (Config.isdemoEnabled) {
                            Toast.makeText(context, "Operation not allowed in Demo App", Toast.LENGTH_SHORT).show();
                            return;
                        }


                        Map<String, Object> map = new HashMap<>();
                        map.put("name", title.getText().toString());
                        map.put(clickAction, description.getText().toString());
                        map.put(imageAction, author.getText().toString());

                        FirebaseDatabase.getInstance().getReference()
                                .child("HomeItems")
                                .child(target).child("items")
                                .child(getRef(i).getKey())
                                .updateChildren(map)
                                .addOnCompleteListener(new OnCompleteListener<Void>() {
                                    @Override
                                    public void onComplete(@NonNull Task<Void> task) {
                                        dialog.dismiss();
                                    }
                                });

                    }
                });

                dialog.show();
            }
        });

        holder.deleteButton.setOnClickListener(new View.OnClickListener() {
                                                   @Override
                                                   public void onClick(View view) {


                                                       new AlertDialog.Builder(context)
                                                               .setTitle(deleteAction)
                                                               .setMessage(sureDelete)
                                                               .setPositiveButton("OK", new DialogInterface.OnClickListener() {
                                                                   public void onClick(DialogInterface dialog, int which) {

                                                                       if (Config.isdemoEnabled) {
                                                                           Toast.makeText(context, "Operation not allowed in Demo App", Toast.LENGTH_SHORT).show();
                                                                           return;
                                                                       }


                                                                       FirebaseDatabase.getInstance().getReference()
                                                                               .child("HomeItems")
                                                                               .child(target).child("items")
                                                                               .child(getRef(i).getKey())
                                                                               .removeValue().addOnCompleteListener(new OnCompleteListener<Void>() {
                                                                           @Override
                                                                           public void onComplete(@NonNull Task<Void> task) {
                                                                               if (task.isSuccessful()) {

                                                                                   Toast.makeText(context, deleteSuccess, Toast.LENGTH_SHORT).show();
                                                                               } else {
                                                                                   Toast.makeText(context, errorOccured, Toast.LENGTH_SHORT).show();
                                                                               }
                                                                           }
                                                                       });
                                                                   }
                                                               })
                                                               // A null listener allows the button to dismiss the dialog and take no further action.
                                                               .setNegativeButton(cancelAction, null)
                                                               .setIcon(android.R.drawable.ic_dialog_alert)
                                                               .show();

                                                   }
                                               }
        );


    }

    @NonNull
    @Override
    public PastViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.layout_details, parent, false);
        return new PastViewHolder(view);
    }

    class PastViewHolder extends RecyclerView.ViewHolder {

        ImageView cardImage;
        ImageView editbutton;
        ImageView deleteButton;
        TextView textLabel;
        CardView homeCards;


        public PastViewHolder(@NonNull View itemView) {
            super(itemView);
            cardImage = itemView.findViewById(R.id.cardImage);
            textLabel = itemView.findViewById(R.id.text_label);
            homeCards = itemView.findViewById(R.id.home_cards);
            editbutton = itemView.findViewById(R.id.editbutton);
            deleteButton = itemView.findViewById(R.id.deleteButton);


        }


    }
}