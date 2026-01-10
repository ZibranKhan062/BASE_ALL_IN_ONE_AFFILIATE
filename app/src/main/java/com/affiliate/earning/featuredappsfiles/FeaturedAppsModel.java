package com.affiliate.earning.featuredappsfiles;

public class FeaturedAppsModel {


    String img;
    String name;
    String links;
    Float ratings;

    public FeaturedAppsModel() {
    }

    public FeaturedAppsModel(String img, String name, String links, Float ratings) {
        this.img = img;
        this.name = name;
        this.links = links;
        this.ratings = ratings;
    }

    public String getImg() {
        return img;
    }

    public void setImg(String img) {
        this.img = img;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getLinks() {
        return links;
    }

    public void setLinks(String links) {
        this.links = links;
    }

    public Float getRatings() {
        return ratings;
    }

    public void setRatings(Float ratings) {
        this.ratings = ratings;
    }
}
