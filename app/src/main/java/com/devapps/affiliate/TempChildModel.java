package com.devapps.affiliate;

public class TempChildModel {

    String name, images,  click;

    public TempChildModel() {
    }

    public TempChildModel(String name, String images, String click) {
        this.name = name;
        this.images = images;
        this.click = click;
    }

    public String getName() {
        return name;
    }

    public String getImages() {
        return images;
    }

    public String getClick() {
        return click;
    }
}
