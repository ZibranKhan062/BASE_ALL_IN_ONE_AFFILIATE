package com.devapps.affiliate;

public class SliderItem {

    String click;
    String  image;

    public SliderItem() {
    }

    public SliderItem(String click, String image) {
        this.click = click;
        this.image = image;
    }

    public String getClick() {
        return click;
    }

    public void setClick(String click) {
        this.click = click;
    }

    public String getImage() {
        return image;
    }

    public void setImage(String image) {
        this.image = image;
    }
}
