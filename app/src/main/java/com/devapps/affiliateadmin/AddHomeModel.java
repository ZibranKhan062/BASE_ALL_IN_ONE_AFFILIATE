package com.devapps.affiliateadmin;

public class AddHomeModel {

    String name;
    String image;

    public AddHomeModel() {
    }

    public AddHomeModel(String name, String image) {
        this.name = name;
        this.image = image;
    }

    public String getName() {
        return name;
    }

    public String getImage() {
        return image;
    }
}
