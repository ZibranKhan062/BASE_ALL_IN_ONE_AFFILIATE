package com.devapps.affiliateadmin;

import android.os.Parcel;
import android.os.Parcelable;
import com.google.firebase.database.Exclude;
import java.util.HashMap;
import java.util.Map;
public class Deal {
    private String id;
    private String adType;
    private String affiliateLink;
    private String authorId;
    private String category;
    private Map<String, Comment> comments;
    private String content;
    private String couponCode;
    private String description;
    private long expiryDate;
    private String imageUrl;
    private boolean isSponsored;
    private int likeCount;
    private int likes;
    private String originalPrice;
    private String price;
    private long timestamp;
    private String title;
    private String videoUrl;

    // Comment nested class
    public static class Comment {
        private String id;
        private String text;
        private long timestamp;
        private String userId;
        private String userName;

        // Getters and setters
        public String getId() { return id; }
        public void setId(String id) { this.id = id; }
        public String getText() { return text; }
        public void setText(String text) { this.text = text; }
        public long getTimestamp() { return timestamp; }
        public void setTimestamp(long timestamp) { this.timestamp = timestamp; }
        public String getUserId() { return userId; }
        public void setUserId(String userId) { this.userId = userId; }
        public String getUserName() { return userName; }
        public void setUserName(String userName) { this.userName = userName; }
    }

    // Getters and setters for Deal class
    public String getId() { return id; }
    public void setId(String id) { this.id = id; }

    public String getAdType() { return adType; }
    public void setAdType(String adType) { this.adType = adType; }

    public String getAffiliateLink() { return affiliateLink; }
    public void setAffiliateLink(String affiliateLink) { this.affiliateLink = affiliateLink; }

    public String getAuthorId() { return authorId; }
    public void setAuthorId(String authorId) { this.authorId = authorId; }

    public String getCategory() { return category; }
    public void setCategory(String category) { this.category = category; }

    public Map<String, Comment> getComments() { return comments; }
    public void setComments(Map<String, Comment> comments) { this.comments = comments; }

    public String getContent() { return content; }
    public void setContent(String content) { this.content = content; }

    public String getCouponCode() { return couponCode; }
    public void setCouponCode(String couponCode) { this.couponCode = couponCode; }

    public String getDescription() { return description; }
    public void setDescription(String description) { this.description = description; }

    public long getExpiryDate() { return expiryDate; }
    public void setExpiryDate(long expiryDate) { this.expiryDate = expiryDate; }

    public String getImageUrl() { return imageUrl; }
    public void setImageUrl(String imageUrl) { this.imageUrl = imageUrl; }

    public boolean isSponsored() { return isSponsored; }
    public void setSponsored(boolean sponsored) { isSponsored = sponsored; }

    public int getLikeCount() { return likeCount; }
    public void setLikeCount(int likeCount) { this.likeCount = likeCount; }

    public int getLikes() { return likes; }
    public void setLikes(int likes) { this.likes = likes; }

    public String getOriginalPrice() { return originalPrice; }
    public void setOriginalPrice(String originalPrice) { this.originalPrice = originalPrice; }

    public String getPrice() { return price; }
    public void setPrice(String price) { this.price = price; }

    public long getTimestamp() { return timestamp; }
    public void setTimestamp(long timestamp) { this.timestamp = timestamp; }

    public String getTitle() { return title; }
    public void setTitle(String title) { this.title = title; }

    public String getVideoUrl() { return videoUrl; }
    public void setVideoUrl(String videoUrl) { this.videoUrl = videoUrl; }
}