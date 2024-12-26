package com.devapps.affiliateadmin.models;

public class DealsModel {
    String discountedPrice;
    String percentOff;
    String productImg;
    String productName;
    String sellingPrice;
    String productDesc;
    String productLink;
    String VidID;

    public DealsModel() {
    }

    public DealsModel(String discountedPrice, String percentOff, String productImg, String productName, String sellingPrice, String productDesc, String productLink, String vidID) {
        this.discountedPrice = discountedPrice;
        this.percentOff = percentOff;
        this.productImg = productImg;
        this.productName = productName;
        this.sellingPrice = sellingPrice;
        this.productDesc = productDesc;
        this.productLink = productLink;
        VidID = vidID;
    }

    public String getDiscountedPrice() {
        return discountedPrice;
    }

    public String getPercentOff() {
        return percentOff;
    }

    public String getProductImg() {
        return productImg;
    }

    public String getProductName() {
        return productName;
    }

    public String getSellingPrice() {
        return sellingPrice;
    }

    public String getProductDesc() {
        return productDesc;
    }

    public String getProductLink() {
        return productLink;
    }

    public String getVidID() {
        return VidID;
    }
}
