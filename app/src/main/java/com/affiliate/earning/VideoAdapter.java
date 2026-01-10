package com.affiliate.affiliate;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.cardview.widget.CardView;
import androidx.recyclerview.widget.RecyclerView;

import com.affiliate.affiliate.models.VideoModel;
import com.google.android.youtube.player.YouTubeInitializationResult;
import com.google.android.youtube.player.YouTubeThumbnailLoader;
import com.google.android.youtube.player.YouTubeThumbnailView;

import java.util.List;

public class VideoAdapter extends RecyclerView.Adapter<VideoAdapter.ViewHolder> {

    List<VideoModel> list;
    Context context;
    Activity activity;

    public VideoAdapter(List<VideoModel> list, Context context, VideoActivity activity) {
        this.list = list;
        this.context = context;
        this.activity = activity;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        LayoutInflater inflater = LayoutInflater.from(parent.getContext());
        View v = inflater.inflate(R.layout.video_layout, parent, false);
        return new ViewHolder(v);

    }

    @Override
    public void onBindViewHolder(@NonNull final ViewHolder holder, final int position) {

        final VideoModel myModel = list.get(position);
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



        holder.card1.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String id = myModel.getVid_ID();
                Intent Newintent = new Intent(context, DetailVideoActivity.class);
                Newintent.putExtra("vidIDTitle", holder.channel_name.getText().toString().trim());
                Newintent.putExtra("VidID", id);
                Newintent.putExtra("VidDesc", String.valueOf(myModel.getVidDescription()));
                Newintent.putExtra("buy_now_url", String.valueOf(myModel.getBuy_url()));
                Newintent.putExtra("date", myModel.getDateTime());
                context.startActivity(Newintent);
            }
        });

        holder.dateTime.setText(myModel.getDateTime().trim());
    }

    @Override
    public int getItemCount() {
        return list.size();
    }

    class ViewHolder extends RecyclerView.ViewHolder {
        protected RelativeLayout relativeLayoutOverYouTubeThumbnailView;
        TextView channel_name;
        YouTubeThumbnailView youTubeThumbnailView;
        public ImageView playButton;
        TextView dateTime;
        RelativeLayout parent_relativeLayout;
        CardView card1;


        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            channel_name = itemView.findViewById(R.id.channel_name);
            playButton = itemView.findViewById(R.id.btnYoutube_player);
            relativeLayoutOverYouTubeThumbnailView = itemView.findViewById(R.id.relativeLayout_over_youtube_thumbnail);
            youTubeThumbnailView = itemView.findViewById(R.id.youtube_thumbnail);
            parent_relativeLayout = itemView.findViewById(R.id.parent_relativeLayout);
            dateTime = itemView.findViewById(R.id.dateTime);
            card1 = itemView.findViewById(R.id.card1);


        }


    }


}