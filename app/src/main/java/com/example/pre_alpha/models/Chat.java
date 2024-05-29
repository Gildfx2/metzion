package com.example.pre_alpha.models;

public class Chat {

    //attributes
    private String userUid;
    private String postId;
    private String lastMessage;
    private long timeStamp;
    private int unseenMessages;
    public Chat(){}

    public Chat(String userUid, String postId, long timeStamp, String lastMessage, int unseenMessages) { //constructor for the other user
        this.userUid = userUid;
        this.postId = postId;
        this.lastMessage = lastMessage;
        this.timeStamp = timeStamp;
        this.unseenMessages = unseenMessages;
    }
    public Chat(String userUid, String postId, long timeStamp, String lastMessage) { //constructor for the current user
        this.userUid = userUid;
        this.postId = postId;
        this.lastMessage = lastMessage;
        this.timeStamp = timeStamp;
        this.unseenMessages = 0;
    }

    //getters and setters
    public String getUserUid() {
        return userUid;
    }
    public void setUserUid(String userUid) {
        this.userUid = userUid;
    }

    public String getPostId() {
        return postId;
    }
    public void setPostId(String postId) {
        this.postId = postId;
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
    public int getUnseenMessages() {
        return unseenMessages;
    }
    public void setUnseenMessages(int unseenMessages) {
        this.unseenMessages = unseenMessages;
    }

}
