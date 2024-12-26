package com.devapps.affiliateadmin.models;

public class NotificationModel {
    String title;
    String description;
    String dateTime;


    public NotificationModel() {
    }

    public NotificationModel(String title, String description, String dateTime) {
        this.title = title;
        this.description = description;
        this.dateTime = dateTime;
    }

    public String getTitle() {
        return title;
    }

    public String getDescription() {
        return description;
    }

    public String getDateTime() {
        return dateTime;
    }
}
