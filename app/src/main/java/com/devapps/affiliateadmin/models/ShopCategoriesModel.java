package com.devapps.affiliateadmin.models;

public class ShopCategoriesModel {

    String image;
    String links;
    String noOfRatings;
    String pricing;
    String title;
    float ratings;

    public ShopCategoriesModel() {
    }

    public ShopCategoriesModel(String image, String links, String noOfRatings, String pricing, String title, float ratings) {
        this.image = image;
        this.links = links;
        this.noOfRatings = noOfRatings;
        this.pricing = pricing;
        this.title = title;
        this.ratings = ratings;
    }

    public String getImage() {
        return image;
    }

    public String getLinks() {
        return links;
    }

    public String getNoOfRatings() {
        return noOfRatings;
    }

    public String getPricing() {
        return pricing;
    }

    public String getTitle() {
        return title;
    }

    public float getRatings() {
        return ratings;
    }
}
