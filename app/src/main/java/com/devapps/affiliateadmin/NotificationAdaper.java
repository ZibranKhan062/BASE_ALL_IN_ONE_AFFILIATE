package com.devapps.affiliateadmin;

import android.content.Context;
import android.content.DialogInterface;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.cardview.widget.CardView;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.devapps.affiliateadmin.models.NotificationModel;
import com.firebase.ui.database.FirebaseRecyclerAdapter;
import com.firebase.ui.database.FirebaseRecyclerOptions;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.database.FirebaseDatabase;

public class NotificationAdaper extends FirebaseRecyclerAdapter<NotificationModel, NotificationAdaper.Viewholder> {
    Context context;

    public NotificationAdaper(@NonNull FirebaseRecyclerOptions<NotificationModel> options, Context context) {
        super(options);
        this.context = context;
    }


    @Override
    public void onBindViewHolder(@NonNull Viewholder holder, final int position, @NonNull final NotificationModel notificationModel) {

        Glide.with(context).load(R.drawable.notifbell).centerCrop().placeholder(R.drawable.notifbell).error(R.drawable.notifbell).into(holder.bellIcon);
        holder.notifTitle.setText(notificationModel.getTitle());
        holder.notif_desc.setText(notificationModel.getDescription());
        holder.dateTime.setText(notificationModel.getDateTime());

        holder.deleteNotif.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                new AlertDialog.Builder(context)
                        .setTitle("Delete Item")
                        .setMessage("Are you sure you want to delete this Item ?")
                        .setPositiveButton("OK", new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int which) {
                                if (Config.isdemoEnabled) {
                                    Toast.makeText(context, "Operation not allowed in Demo App", Toast.LENGTH_SHORT).show();
                                    return;
                                }


                                FirebaseDatabase.getInstance().getReference("Notifications")
                                        .child(getRef(position).getKey())
                                        .removeValue()
                                        .addOnCompleteListener(new OnCompleteListener<Void>() {
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

    }

    @NonNull
    @Override
    public Viewholder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {

        View v = LayoutInflater.from(context).inflate(R.layout.notification_layout, parent, false);
        return new Viewholder(v);
    }


    class Viewholder extends RecyclerView.ViewHolder {
        ImageView bellIcon;
        TextView notifTitle;
        TextView notif_desc;
        TextView dateTime;
        CardView homeyCards;
        ImageButton deleteNotif;

        public Viewholder(@NonNull View itemView) {
            super(itemView);

            bellIcon = itemView.findViewById(R.id.bellIcon);
            notifTitle = itemView.findViewById(R.id.notifTitle);
            notif_desc = itemView.findViewById(R.id.notif_desc);
            dateTime = itemView.findViewById(R.id.dateTime);
            deleteNotif = itemView.findViewById(R.id.deleteNotif);
        }
    }
}
