package com.devapps.affiliate.models;

import android.os.Parcel;
import android.os.Parcelable;

public class NewsItem implements Parcelable {
    private String id;
    private String title;
    private String description;
    private String imageUrl;
    private String category;
    private long timestamp;
    private long likeCount;      // Changed to long and standardized naming
    private long commentCount;   // Changed to long and standardized naming
    private int bookmarksCount;
    private String authorId;
    private boolean isBookmarked;
    private boolean isLiked;

    public NewsItem() {
        // Required empty constructor for Firebase
    }

    public NewsItem(String title, String description, String imageUrl, String category,
                    String authorId, long timestamp) {
        this.title = title;
        this.description = description;
        this.imageUrl = imageUrl;
        this.category = category;
        this.authorId = authorId;
        this.timestamp = timestamp;
        this.likeCount = 0;
        this.commentCount = 0;
        this.bookmarksCount = 0;
    }

    protected NewsItem(Parcel in) {
        id = in.readString();
        title = in.readString();
        description = in.readString();
        imageUrl = in.readString();
        category = in.readString();
        timestamp = in.readLong();
        likeCount = in.readLong();      // Updated to readLong
        commentCount = in.readLong();    // Updated to readLong
        bookmarksCount = in.readInt();
        authorId = in.readString();
        isBookmarked = in.readByte() != 0;
        isLiked = in.readByte() != 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(id);
        dest.writeString(title);
        dest.writeString(description);
        dest.writeString(imageUrl);
        dest.writeString(category);
        dest.writeLong(timestamp);
        dest.writeLong(likeCount);       // Updated to writeLong
        dest.writeLong(commentCount);    // Updated to writeLong
        dest.writeInt(bookmarksCount);
        dest.writeString(authorId);
        dest.writeByte((byte) (isBookmarked ? 1 : 0));
        dest.writeByte((byte) (isLiked ? 1 : 0));
    }

    public static final Creator<NewsItem> CREATOR = new Creator<NewsItem>() {
        @Override
        public NewsItem createFromParcel(Parcel in) {
            return new NewsItem(in);
        }

        @Override
        public NewsItem[] newArray(int size) {
            return new NewsItem[size];
        }
    };

    @Override
    public int describeContents() {
        return 0;
    }

    // Getters and Setters
    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getImageUrl() {
        return imageUrl;
    }

    public void setImageUrl(String imageUrl) {
        this.imageUrl = imageUrl;
    }

    public String getCategory() {
        return category;
    }

    public void setCategory(String category) {
        this.category = category;
    }

    public long getTimestamp() {
        return timestamp;
    }

    public void setTimestamp(long timestamp) {
        this.timestamp = timestamp;
    }

    public long getLikeCount() {
        return likeCount;
    }

    public void setLikeCount(long likeCount) {
        this.likeCount = likeCount;
    }

    public long getCommentCount() {
        return commentCount;
    }

    public void setCommentCount(long commentCount) {
        this.commentCount = commentCount;
    }

    public int getBookmarksCount() {
        return bookmarksCount;
    }

    public void setBookmarksCount(int bookmarksCount) {
        this.bookmarksCount = bookmarksCount;
    }

    public String getAuthorId() {
        return authorId;
    }

    public void setAuthorId(String authorId) {
        this.authorId = authorId;
    }

    public boolean isBookmarked() {
        return isBookmarked;
    }

    public void setBookmarked(boolean bookmarked) {
        isBookmarked = bookmarked;
    }

    public boolean isLiked() {
        return isLiked;
    }

    public void setLiked(boolean liked) {
        isLiked = liked;
    }
}