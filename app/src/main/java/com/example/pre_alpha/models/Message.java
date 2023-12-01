package com.example.pre_alpha.models;

public class Message {

    private String message;
    private String senderUid;
    private String receiverUid;
    private String postId;
    private boolean isSeen;
    private long timeStamp;

    public Message(){}

    public Message(String message, String senderUid, String receiverUid, String postId, long timeStamp){
        this.message=message;
        this.senderUid=senderUid;
        this.receiverUid=receiverUid;
        this.postId=postId;
        this.isSeen=false;
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

    public boolean isSeen() {
        return isSeen;
    }
    public void setSeen(boolean seen) {
        isSeen = seen;
    }
}
