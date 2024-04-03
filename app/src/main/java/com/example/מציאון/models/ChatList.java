package com.example.מציאון.models;

public class ChatList {
    private String userUid;
    private String postId;
    private String lastMessage;
    private long timeStamp;
    private int unseenMessages;
    public ChatList(){}

    public ChatList(String userUid, String postId, long timeStamp, String lastMessage, int unseenMessages) {
        this.userUid = userUid;
        this.postId = postId;
        this.lastMessage = lastMessage;
        this.timeStamp = timeStamp;
        this.unseenMessages = unseenMessages;
    }
    public ChatList(String userUid, String postId, long timeStamp, String lastMessage) {
        this.userUid = userUid;
        this.postId = postId;
        this.lastMessage = lastMessage;
        this.timeStamp = timeStamp;
        this.unseenMessages = 0;
    }

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
