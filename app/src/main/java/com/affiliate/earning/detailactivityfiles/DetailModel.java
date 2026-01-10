package com.affiliate.earning.detailactivityfiles;

public class DetailModel {

    String name;
    String images;
    String click;

    public DetailModel() {
    }

    public DetailModel(String name, String images, String click) {
        this.name = name;
        this.images = images;
        this.click = click;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getImages() {
        return images;
    }

    public void setImages(String images) {
        this.images = images;
    }

    public String getClick() {
        return click;
    }

    public void setClick(String click) {
        this.click = click;
    }
}
