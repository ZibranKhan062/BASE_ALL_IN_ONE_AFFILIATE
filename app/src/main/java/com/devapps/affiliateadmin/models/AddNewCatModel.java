package com.devapps.affiliateadmin.models;

public class AddNewCatModel {

    String title, image;

    public AddNewCatModel() {
    }

    public AddNewCatModel(String title, String image) {
        this.title = title;
        this.image = image;
    }

    public String getTitle() {
        return title;
    }

    public String getImage() {
        return image;
    }
}
