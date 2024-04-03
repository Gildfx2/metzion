package com.example.pre_alpha.adapters;

import android.net.Uri;

public class ChatData {

    String name, username, creatorUid, postId, otherUserUid, lastMessage, date;
    int unseenMessages;
    Uri image;

    public ChatData(String name, String username, Uri image, String creatorUid, String postId, String otherUserUid, String lastMessage, String date, int unseenMessages) {
        this.name = name;
        this.username = username;
        this.image = image;
        this.creatorUid = creatorUid;
        this.postId = postId;
        this.otherUserUid = otherUserUid;
        this.lastMessage = lastMessage;
        this.date = date;
        this.unseenMessages = unseenMessages;
    }

    public String getName() {
        return name;
    }
    public void setName(String name) {
        this.name = name;
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

    public String getPostId() {
        return postId;
    }
    public void setPostId(String postId) {
        this.postId = postId;
    }

    public String getOtherUserUid() {
        return otherUserUid;
    }
    public void setOtherUserUid(String otherUserUid) {
        this.otherUserUid = otherUserUid;
    }

    public String getLastMessage() {
        return lastMessage;
    }
    public void setLastMessage(String lastMessage) {
        this.lastMessage = lastMessage;
    }

    public String getDate() {
        return date;
    }
    public void setDate(String date) {
        this.date = date;
    }
    public int getUnseenMessages() {
        return unseenMessages;
    }
    public void setUnseenMessages(int unseenMessages) {
        this.unseenMessages = unseenMessages;
    }

}
