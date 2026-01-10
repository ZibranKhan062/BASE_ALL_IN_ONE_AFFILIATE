package com.affiliate.earning.Favorite;

import com.affiliate.earning.R;

public class NewsModel {

    public NewsModel() {
    }

    Integer id;
    String title;
    String webLink;
    String profile_image;
    int toggled = R.drawable.fav;
    int untoglled = R.drawable.unfav;
    boolean isFavorite = false;


    public NewsModel(Integer id, String title, String webLink, String profile_image, int toggled, int untoglled, boolean isFavorite) {
        this.id = id;
        this.title = title;
        this.webLink = webLink;
        this.profile_image = profile_image;
        this.toggled = toggled;
        this.untoglled = untoglled;
        this.isFavorite = isFavorite;
    }

    public Integer getId() {
        return id;
    }

    public String getTitle() {
        return title;
    }

    public String getWebLink() {
        return webLink;
    }

    public String getProfile_image() {
        return profile_image;
    }

    public int getToggled() {
        return toggled;
    }

    public int getUntoglled() {
        return untoglled;
    }

    public boolean isFavorite() {
        return isFavorite;
    }
}
