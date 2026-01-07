package com.devapps.affiliate.Favorite;

public class DataModel {
    Integer id;

    String Title;
    String link;
    String image;

    public DataModel() {
    }

    public DataModel(Integer id, String title, String link, String image) {
        this.id = id;
        Title = title;
        this.link = link;
        this.image = image;
    }

    public Integer getId() {
        return id;
    }

    public String getTitle() {
        return Title;
    }

    public String getLink() {
        return link;
    }

    public String getImage() {
        return image;
    }
}