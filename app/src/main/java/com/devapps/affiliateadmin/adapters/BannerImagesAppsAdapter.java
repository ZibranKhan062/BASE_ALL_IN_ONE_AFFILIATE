package com.devapps.affiliateadmin.adapters;

import android.content.Context;
import android.content.DialogInterface;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.devapps.affiliateadmin.BannerImages;
import com.devapps.affiliateadmin.Config;
import com.devapps.affiliateadmin.DealsOffers;
import com.devapps.affiliateadmin.R;
import com.devapps.affiliateadmin.models.BannerModel;
import com.firebase.ui.database.FirebaseRecyclerAdapter;
import com.firebase.ui.database.FirebaseRecyclerOptions;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.database.FirebaseDatabase;
import com.orhanobut.dialogplus.DialogPlus;

import java.util.HashMap;
import java.util.Map;

public class BannerImagesAppsAdapter extends FirebaseRecyclerAdapter<BannerModel, BannerImagesAppsAdapter.ViewHolder> {

    private Context context;

    public BannerImagesAppsAdapter(@NonNull FirebaseRecyclerOptions<BannerModel> options, Context context) {
        super(options);
        this.context = context;
    }

    @Override
    public void onDataChanged() {
        ProgressBar progressBar = ((BannerImages) context).findViewById(R.id.progressBar);
        progressBar.setVisibility(View.GONE);
    }

    @Override
    protected void onBindViewHolder(@NonNull ViewHolder holder, final int position, @NonNull final BannerModel model) {

        Glide.with(context).load(model.getImage()).into(holder.ivAutoImagSlider);


        holder.editbutton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                final DialogPlus dialog = DialogPlus.newDialog(context)
                        .setGravity(Gravity.CENTER)
                        .setMargin(50, 0, 50, 0)
                        .setContentHolder(new com.orhanobut.dialogplus.ViewHolder(R.layout.banner_images_listing_content))
                        .setExpanded(false)  // This will enable the expand feature, (similar to android L share dialog)
                        .create();

                View holderView = dialog.getHolderView();

                final EditText imgLink = holderView.findViewById(R.id.imgLink);
                final EditText url = holderView.findViewById(R.id.url);
                imgLink.setText(model.getImage());
                url.setText(model.getClick());
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
                        map.put("click", url.getText().toString());
                        map.put("image", imgLink.getText().toString());

                        FirebaseDatabase.getInstance().getReference()
                                .child("SlidingImages")
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
                                        .child("SlidingImages")
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
        View v = LayoutInflater.from(context).inflate(R.layout.banner_apps_layout, parent, false);
        return new BannerImagesAppsAdapter.ViewHolder(v);
    }

    class ViewHolder extends RecyclerView.ViewHolder {
        ImageView ivAutoImagSlider;
        ImageView editbutton;
        ImageView deleteButton;


        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            ivAutoImagSlider = itemView.findViewById(R.id.iv_auto_image_slider);
            editbutton = itemView.findViewById(R.id.editbutton);
            deleteButton = itemView.findViewById(R.id.deleteButton);

        }
    }
}
