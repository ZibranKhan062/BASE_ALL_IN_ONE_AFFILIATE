package com.affiliate.earning.models;

public class Comment {
    private String id;
    private String userId;
    private String text;
    private long timestamp;
    private String userName; // Will be populated from user data

    public Comment() {
        // Required empty constructor for Firebase
    }

    public Comment(String id, String userId, String text, long timestamp) {
        this.id = id;
        this.userId = userId;
        this.text = text;
        this.timestamp = timestamp;
    }

    // Getters and Setters
    public String getId() { return id; }
    public void setId(String id) { this.id = id; }

    public String getUserId() { return userId; }
    public void setUserId(String userId) { this.userId = userId; }

    public String getText() { return text; }
    public void setText(String text) { this.text = text; }

    public long getTimestamp() { return timestamp; }
    public void setTimestamp(long timestamp) { this.timestamp = timestamp; }

    public String getUserName() { return userName; }
    public void setUserName(String userName) { this.userName = userName; }
}