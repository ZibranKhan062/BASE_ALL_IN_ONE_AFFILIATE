package com.devapps.affiliateadmin;

public class StoreBannerModel {

    String click, image;

    public StoreBannerModel() {
    }

    public StoreBannerModel(String click, String image) {
        this.click = click;
        this.image = image;
    }

    public String getClick() {
        return click;
    }

    public String getImage() {
        return image;
    }
}
