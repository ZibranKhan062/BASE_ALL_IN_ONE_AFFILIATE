package com.devapps.affiliate.models;

public class VideoModel {

    String vid_ID;
    String channel_name;
    String vidDescription;
    String buy_url;
    String dateTime;

    public VideoModel() {
    }

    public VideoModel(String vid_ID, String channel_name, String vidDescription, String buy_url, String dateTime) {
        this.vid_ID = vid_ID;
        this.channel_name = channel_name;
        this.vidDescription = vidDescription;
        this.buy_url = buy_url;
        this.dateTime = dateTime;
    }

    public String getVid_ID() {
        return vid_ID;
    }

    public String getChannel_name() {
        return channel_name;
    }

    public String getVidDescription() {
        return vidDescription;
    }


    public String getBuy_url() {
        return buy_url;
    }

    public String getDateTime() {
        return dateTime;
    }
}
