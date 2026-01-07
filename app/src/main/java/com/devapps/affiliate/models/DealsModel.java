package com.devapps.affiliate.models;

public class DealsModel {
    String productImg;
    String productName;
    String sellingPrice;
    String discountedPrice;
    String percentOff;
    String productDesc;
    String productLink;
    String vidID;

    public DealsModel() {
    }

    public DealsModel(String productImg, String productName, String sellingPrice, String discountedPrice,
                      String percentOff, String productDesc, String productLink, String vidID) {
        this.productImg = productImg;
        this.productName = productName;
        this.sellingPrice = sellingPrice;
        this.discountedPrice = discountedPrice;
        this.percentOff = percentOff;
        this.productDesc = productDesc;
        this.productLink = productLink;
        this.vidID = vidID;
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

    public String getDiscountedPrice() {
        return discountedPrice;
    }

    public String getPercentOff() {
        return percentOff;
    }

    public String getProductDesc () {
        return productDesc;
    }


    public String getProductLink() {
        return productLink;
    }

    public String getVidID() {
        return vidID;
    }
}