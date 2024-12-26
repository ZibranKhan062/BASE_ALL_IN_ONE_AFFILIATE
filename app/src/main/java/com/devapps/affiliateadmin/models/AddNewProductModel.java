package com.devapps.affiliateadmin.models;

public class AddNewProductModel {

    String image;
    String links;
    String no_of_ratings;
    String pricing;
    float ratings;
    String title;

    public AddNewProductModel() {
    }

    public AddNewProductModel(String image, String links, String no_of_ratings, String pricing, float ratings, String title) {
        this.image = image;
        this.links = links;
        this.no_of_ratings = no_of_ratings;
        this.pricing = pricing;
        this.ratings = ratings;
        this.title = title;
    }

    public String getImage() {
        return image;
    }

    public String getLinks() {
        return links;
    }

    public String getNo_of_ratings() {
        return no_of_ratings;
    }

    public String getPricing() {
        return pricing;
    }

    public float getRatings() {
        return ratings;
    }

    public String getTitle() {
        return title;
    }
}
