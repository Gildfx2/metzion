package com.example.pre_alpha.models;

public class ChatList {
    private String userUid;
    private String postUid;
    private String lastMessage;
    private long timeStamp;

    public ChatList(){}

    public ChatList(String userUid, String postUid, long timeStamp, String lastMessage) {
        this.userUid = userUid;
        this.postUid = postUid;
        this.lastMessage = lastMessage;
        this.timeStamp = timeStamp;
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

    public long getTimeStamp() {
        return timeStamp;
    }
    public void setTimeStamp(long timeStamp) {
        this.timeStamp = timeStamp;
    }

    public String getLastMessage() {
        return lastMessage;
    }
    public void setLastMessage(String lastMessage) {
        this.lastMessage = lastMessage;
    }
}
