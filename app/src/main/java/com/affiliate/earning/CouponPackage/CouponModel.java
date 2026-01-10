package com.affiliate.earning.CouponPackage;

public class CouponModel {

    String couponTitle;
    String couponDescription;
    String expiryDate;
    String coupon;
    String webLink;

    public CouponModel() {
    }

    public CouponModel(String couponTitle, String couponDescription, String expiryDate, String coupon, String webLink) {
        this.couponTitle = couponTitle;
        this.couponDescription = couponDescription;
        this.expiryDate = expiryDate;
        this.coupon = coupon;
        this.webLink = webLink;
    }


    public String getCouponTitle() {
        return couponTitle;
    }

    public String getCouponDescription() {
        return couponDescription;
    }

    public String getExpiryDate() {
        return expiryDate;
    }

    public String getCoupon() {
        return coupon;
    }

    public String getWebLink() {
        return webLink;
    }
}
