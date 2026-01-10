package com.affiliate;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.cardview.widget.CardView;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.affiliate.affiliate.R;
import com.affiliate.affiliate.models.NotificationModel;

import java.util.List;

public class NotificationAdaper extends RecyclerView.Adapter<NotificationAdaper.Viewholder> {
    List<NotificationModel> notificationModelList;
    Context context;

    public NotificationAdaper(List<NotificationModel> notificationModelList, Context context) {
        this.notificationModelList = notificationModelList;
        this.context = context;
    }

    @Override
    public void onBindViewHolder(@NonNull Viewholder holder, int position) {

        final NotificationModel notificationModel = notificationModelList.get(position);


        Glide.with(context).load(R.drawable.notifbell).centerCrop().placeholder(R.drawable.notifbell).error(R.drawable.notifbell).into(holder.bellIcon);
        holder.notifTitle.setText(notificationModel.getTitle());
        holder.notif_desc.setText(notificationModel.getDescription());
        holder.dateTime.setText(notificationModel.getDateTime());

    }

    @NonNull
    @Override
    public Viewholder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {

        View v = LayoutInflater.from(context).inflate(R.layout.notification_layout, parent, false);
        return new Viewholder(v);
    }

    @Override
    public int getItemCount() {
        return notificationModelList.size();
    }


    class Viewholder extends RecyclerView.ViewHolder {
        ImageView bellIcon;
        TextView notifTitle;
        TextView notif_desc;
        TextView dateTime;
        CardView homeyCards;

        public Viewholder(@NonNull View itemView) {
            super(itemView);

            bellIcon = itemView.findViewById(R.id.bellIcon);
            notifTitle = itemView.findViewById(R.id.notifTitle);
            notif_desc = itemView.findViewById(R.id.notif_desc);
            dateTime = itemView.findViewById(R.id.dateTime);
        }
    }
}
