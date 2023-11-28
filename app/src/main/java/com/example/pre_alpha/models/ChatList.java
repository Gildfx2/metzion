package com.example.pre_alpha.models;

public class ChatList {
    private String userUid;
    private String postUid;

    public ChatList(){}

    public ChatList(String userUid, String postUid) {
        this.userUid = userUid;
        this.postUid = postUid;
    }

    public String getUserUid() {
        return userUid;
    }

    public void setUserUid(String userUid) {
        this.userUid = userUid;
    }

    public String getPostUid() {
        return postUid;
    }

    public void setPostUid(String postUid) {
        this.postUid = postUid;
    }
}
