package com.devapps.affiliateadmin.models;

public class UpdateAdsModel {

    String isAdmobEnabled;

    public UpdateAdsModel() {
    }

    public UpdateAdsModel(String isAdmobEnabled) {
        this.isAdmobEnabled = isAdmobEnabled;
    }

    public String getIsAdmobEnabled() {
        return isAdmobEnabled;
    }

    public void setIsAdmobEnabled(String isAdmobEnabled) {
        this.isAdmobEnabled = isAdmobEnabled;
    }
}
