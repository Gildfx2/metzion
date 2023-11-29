package com.example.pre_alpha.models;

public class Message {

    private String message;
    private String senderUid;
    private String receiverUid;
    private String postUid;
    private long timeStamp;

    public Message(){}

    public Message(String message, String senderUid, String receiverUid, String postUid, long timeStamp){
        this.message=message;
        this.senderUid=senderUid;
        this.receiverUid=receiverUid;
        this.postUid=postUid;
        this.timeStamp=timeStamp;
    }

    public String getMessage() {
        return message;
    }
    public void setMessage(String message) {
        this.message = message;
    }

    public String getSenderUid() {
        return senderUid;
    }
    public void setSenderUid(String senderUid) {
        this.senderUid = senderUid;
    }

    public String getReceiverUid() {
        return receiverUid;
    }
    public void setReceiverUid(String receiverUid) {
        this.receiverUid = receiverUid;
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
}
