package com.example.pre_alpha.adapters;

import android.net.Uri;

public class PostData {

    //attributes
    private String name, item, postId;
    private Uri image;

    //constructor
    public PostData(String name, String item, Uri image, String postId){
        this.name = name;
        this.item = item;
        this.image = image;
        this.postId = postId;
    }

    //getters and setters
    public String getName() {
        return name;
    }
    public void setName(String name) {
        this.name = name;
    }


    public String getItem() {
        return item;
    }
    public void setItem(String item) {
        this.item = item;
    }

    public Uri getImage() {
        return image;
    }
    public void setImage(Uri image) {
        this.image = image;
    }

    public String getPostId() {
        return postId;
    }
    public void setPostId(String postId) {
        this.postId = postId;
    }
}
