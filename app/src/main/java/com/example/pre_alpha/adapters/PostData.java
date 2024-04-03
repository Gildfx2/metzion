package com.example.pre_alpha.adapters;

import android.net.Uri;

public class PostData {
    String name, item, about, creatorUid, postId;
    long timeStamp;
    Uri image;

    public PostData(String name, String item, Uri image, String about, String creatorUid, String postId, long timeStamp){
        this.name = name;
        this.item = item;
        this.image = image;
        this.about = about;
        this.creatorUid = creatorUid;
        this.postId = postId;
        this.timeStamp = timeStamp;
    }

    public String getName() {
        return name;
    }
    public void setName(String name) {
        this.name = name;
    }

    public long getTimeStamp() {
        return timeStamp;
    }

    public void setTimeStamp(long timeStamp) {
        this.timeStamp = timeStamp;
    }

    public String getItem() {
        return item;
    }
    public void setItem(String item) {
        this.item = item;
    }

    public String getAbout() {
        return about;
    }
    public void setAbout(String about) {
        this.about = about;
    }

    public Uri getImage() {
        return image;
    }
    public void setImage(Uri image) {
        this.image = image;
    }

    public String getCreatorUid() {
        return creatorUid;
    }
    public void setCreatorUid(String creatorUid) {
        this.creatorUid = creatorUid;
    }

    public String getPostId() {
        return postId;
    }
    public void setPostId(String postId) {
        this.postId = postId;
    }
}
