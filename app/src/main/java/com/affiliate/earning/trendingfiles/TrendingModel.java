package com.affiliate.affiliate.trendingfiles;

public class TrendingModel {
    private String key; //
    String id;
    String title;
    String  image;
    String  pricing;
    String  links;
    String  no_of_ratings;
    Float ratings;

    public TrendingModel() {
    }

    public TrendingModel(String id ,String title, String image, String pricing, String links, String no_of_ratings, Float ratings) {
        this.id = id;
        this.title = title;
        this.image = image;
        this.pricing = pricing;
        this.links = links;
        this.no_of_ratings = no_of_ratings;
        this.ratings = ratings;
    }


    public String getId() {
        return id;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getImage() {
        return image;
    }

    public void setImage(String image) {
        this.image = image;
    }

    public String getPricing() {
        return pricing;
    }

    public void setPricing(String pricing) {
        this.pricing = pricing;
    }

    public String getLinks() {
        return links;
    }

    public void setLinks(String links) {
        this.links = links;
    }

    public String getNo_of_ratings() {
        return no_of_ratings;
    }



    public Float getRatings() {
        return ratings;
    }

    public void setRatings(Float ratings) {
        this.ratings = ratings;
    }

    // add a getter and setter for the key field
    public String getKey() {
        return key;
    }

    public void setKey(String key) {
        this.key = key;
    }
}
