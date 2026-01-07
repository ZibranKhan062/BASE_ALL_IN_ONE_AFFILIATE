package com.devapps.affiliate;

public class CategoryModel {

    String image,title;

    public CategoryModel() {
    }

    public CategoryModel(String image, String title) {
        this.image = image;
        this.title = title;
    }

    public String getImage() {
        return image;
    }

    public String getTitle() {
        return title;
    }
}
