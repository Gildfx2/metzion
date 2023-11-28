package com.example.pre_alpha.adapters;

import android.net.Uri;

public class ChatData {

    String name, area, username, creatorUid, postUid, otherUserUid;
    Uri image;

    public ChatData(String name, String area, String username, Uri image, String creatorUid, String postUid, String otherUserUid) {
        this.name = name;
        this.area = area;
        this.username = username;
        this.image = image;
        this.creatorUid = creatorUid;
        this.postUid = postUid;
        this.otherUserUid = otherUserUid;
    }

    public String getName() {
        return name;
    }
    public void setName(String name) {
        this.name = name;
    }

    public String getArea() {
        return area;
    }
    public void setArea(String area) {
        this.area = area;
    }

    public String getUsername() {
        return username;
    }
    public void setUsername(String username) {
        this.username = username;
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

    public String getPostUid() {
        return postUid;
    }
    public void setPostUid(String postUid) {
        this.postUid = postUid;
    }

    public String getOtherUserUid() {
        return otherUserUid;
    }
    public void setOtherUserUid(String otherUserUid) {
        this.otherUserUid = otherUserUid;
    }
}
