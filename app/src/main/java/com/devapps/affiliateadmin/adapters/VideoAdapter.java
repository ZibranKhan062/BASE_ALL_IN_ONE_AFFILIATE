package com.devapps.affiliateadmin.adapters;

import android.app.Activity;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.text.TextUtils;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.recyclerview.widget.RecyclerView;

import com.devapps.affiliateadmin.Config;
import com.devapps.affiliateadmin.DealsOffers;
import com.devapps.affiliateadmin.R;
import com.devapps.affiliateadmin.VideoActivity;
import com.devapps.affiliateadmin.models.VideoModel;
import com.firebase.ui.database.FirebaseRecyclerAdapter;
import com.firebase.ui.database.FirebaseRecyclerOptions;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.youtube.player.YouTubeInitializationResult;
import com.google.android.youtube.player.YouTubeStandalonePlayer;
import com.google.android.youtube.player.YouTubeThumbnailLoader;
import com.google.android.youtube.player.YouTubeThumbnailView;
import com.google.firebase.database.FirebaseDatabase;
import com.orhanobut.dialogplus.DialogPlus;

import java.util.HashMap;
import java.util.Map;

public class VideoAdapter extends FirebaseRecyclerAdapter<VideoModel, VideoAdapter.ViewHolder> {


    private Context context;
    Activity activity;
    String clickAction = "click";
    String imageAction = "images";
    String cancelAction = "Cancel";
    String deleteAction = "Delete Item";
    String sureDelete = "Are you sure you want to delete this Item ?";
    String deleteSuccess = "Deleted Successfully";
    String errorOccured = "Some Error Occured";

    public VideoAdapter(@NonNull FirebaseRecyclerOptions<VideoModel> options, Context context, VideoActivity activity) {
        super(options);
        this.context = context;
        this.activity = activity;
    }

    @Override
    public void onDataChanged() {
        ProgressBar progressBar = ((VideoActivity) context).findViewById(R.id.progressBar);
        progressBar.setVisibility(View.GONE);
    }
    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        LayoutInflater inflater = LayoutInflater.from(parent.getContext());
        View v = inflater.inflate(R.layout.video_layout, parent, false);
        return new ViewHolder(v);

    }

    @Override
    public void onBindViewHolder(@NonNull final ViewHolder holder, final int position, @NonNull final VideoModel myModel) {

        holder.channel_name.setText(myModel.getChannel_name());

        final YouTubeThumbnailLoader.OnThumbnailLoadedListener onThumbnailLoadedListener = new YouTubeThumbnailLoader.OnThumbnailLoadedListener() {
            @Override
            public void onThumbnailError(YouTubeThumbnailView youTubeThumbnailView, YouTubeThumbnailLoader.ErrorReason errorReason) {
               /*
                error handling
                 */
            }

            @Override
            public void onThumbnailLoaded(YouTubeThumbnailView youTubeThumbnailView, String s) {
                youTubeThumbnailView.setVisibility(View.VISIBLE);
                holder.relativeLayoutOverYouTubeThumbnailView.setVisibility(View.VISIBLE);
            }
        };


        holder.youTubeThumbnailView.initialize(Config.getApiKey(), new YouTubeThumbnailView.OnInitializedListener() {
            @Override
            public void onInitializationSuccess(YouTubeThumbnailView youTubeThumbnailView, final YouTubeThumbnailLoader youTubeThumbnailLoader) {

                youTubeThumbnailLoader.setVideo(myModel.getVid_ID());
                youTubeThumbnailLoader.setOnThumbnailLoadedListener(onThumbnailLoadedListener);
                youTubeThumbnailLoader.setOnThumbnailLoadedListener(new YouTubeThumbnailLoader.OnThumbnailLoadedListener() {
                    @Override
                    public void onThumbnailLoaded(YouTubeThumbnailView youTubeThumbnailView, String s) {

                        youTubeThumbnailView.setVisibility(View.VISIBLE);
                        holder.relativeLayoutOverYouTubeThumbnailView.setVisibility(View.VISIBLE);
                        youTubeThumbnailLoader.release();
                    }

                    @Override
                    public void onThumbnailError(YouTubeThumbnailView youTubeThumbnailView, YouTubeThumbnailLoader.ErrorReason errorReason) {
   /*
                error handling
                 */
                    }
                });
            }

            @Override
            public void onInitializationFailure(YouTubeThumbnailView youTubeThumbnailView, YouTubeInitializationResult youTubeInitializationResult) {
                Toast.makeText(context, "Details : " + youTubeInitializationResult, Toast.LENGTH_LONG).show();
            }
        });

        holder.parent_relativeLayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

            }
        });

        holder.relativeLayoutOverYouTubeThumbnailView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = YouTubeStandalonePlayer.createVideoIntent(activity, Config.getApiKey(), myModel.getVid_ID(), 100,
                        true,
                        false);
                context.startActivity(intent);
            }
        });
        holder.editbutton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                final DialogPlus dialog = DialogPlus.newDialog(context)
                        .setGravity(Gravity.CENTER)
                        .setMargin(50, 0, 50, 0)
                        .setContentHolder(new com.orhanobut.dialogplus.ViewHolder(R.layout.edit_video_popup))
                        .setExpanded(false)  // This will enable the expand feature, (similar to android L share dialog)
                        .create();

                View holderView = dialog.getHolderView();

                final EditText title = holderView.findViewById(R.id.title);
                final EditText vidID = holderView.findViewById(R.id.vidID);
                final EditText vidDesc = holderView.findViewById(R.id.vidDesc);
                ImageView info = holderView.findViewById(R.id.info);
                info.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        new AlertDialog.Builder(context)
                                .setMessage("If the Video URL is https://www.youtube.com/watch?v=HwgpmIUHQOo then Video ID is HwgpmIUHQOo")
                                .setNegativeButton("OK", null)
                                .setIcon(android.R.drawable.ic_dialog_info)
                                .show();
                    }
                });

                final EditText link = holderView.findViewById(R.id.link);
                TextView buy_label = holderView.findViewById(R.id.buy_label);


                title.setText(myModel.getChannel_name());
                vidID.setText(myModel.getVid_ID());
                vidDesc.setText(myModel.getVidDescription());
                link.setText(myModel.getBuy_url());

                if (link.getText().toString().trim().equalsIgnoreCase("null")) {
                    buy_label.setVisibility(View.GONE);
                    link.setVisibility(View.GONE);

                } else {
                    buy_label.setVisibility(View.VISIBLE);
                    link.setVisibility(View.VISIBLE);
                }

                Button update = holderView.findViewById(R.id.update);

                update.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {

                        if (TextUtils.isEmpty(title.getText().toString().trim()) || TextUtils.isEmpty(vidID.getText().toString().trim())
                                || TextUtils.isEmpty(vidDesc.getText().toString().trim())
                                || TextUtils.isEmpty(link.getText().toString().trim())


                        ) {
                            Toast.makeText(context, "Please fill all fields", Toast.LENGTH_SHORT).show();
                            return;
                        }

                        if (Config.isdemoEnabled) {
                            Toast.makeText(context, "Operation not allowed in Demo App", Toast.LENGTH_LONG).show();
                            return;
                        }
                        Map<String, Object> map = new HashMap<>();
                        map.put("channel_name", title.getText().toString());
                        map.put("vid_ID", vidID.getText().toString());
                        map.put("vidDescription", vidDesc.getText().toString());
                        map.put("buy_url", link.getText().toString());

                        FirebaseDatabase.getInstance().getReference()
                                .child("Videos")

                                .child(getRef(position).getKey())
                                .updateChildren(map)
                                .addOnCompleteListener(new OnCompleteListener<Void>() {
                                    @Override
                                    public void onComplete(@NonNull Task<Void> task) {
                                        if (task.isSuccessful()) {

                                            Toast.makeText(context, "Updated Successfully", Toast.LENGTH_LONG).show();
                                        } else {
                                            Toast.makeText(context, "Some Error Occured" + task.getException(), Toast.LENGTH_LONG).show();
                                        }
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
                                    Toast.makeText(context, "Operation not allowed in Demo App", Toast.LENGTH_LONG).show();
                                    return;
                                }


                                FirebaseDatabase.getInstance().getReference()
                                        .child("Videos")
                                        .child(getRef(position).getKey())
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
        });
    }


    class ViewHolder extends RecyclerView.ViewHolder {
        protected RelativeLayout relativeLayoutOverYouTubeThumbnailView;
        TextView channel_name;
        YouTubeThumbnailView youTubeThumbnailView;
        public ImageView playButton;
        RelativeLayout parent_relativeLayout;
        ImageView editbutton, deleteButton;


        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            channel_name = itemView.findViewById(R.id.channel_name);
            playButton = itemView.findViewById(R.id.btnYoutube_player);
            relativeLayoutOverYouTubeThumbnailView = itemView.findViewById(R.id.relativeLayout_over_youtube_thumbnail);
            youTubeThumbnailView = itemView.findViewById(R.id.youtube_thumbnail);
            parent_relativeLayout = itemView.findViewById(R.id.parent_relativeLayout);
            editbutton = itemView.findViewById(R.id.editbutton);
            deleteButton = itemView.findViewById(R.id.deleteButton);


        }
    }


}