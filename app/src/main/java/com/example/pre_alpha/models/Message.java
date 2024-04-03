package com.example.pre_alpha.models;

import android.net.Uri;

public class Message {

    private String message;
    private String senderUid;
    private String receiverUid;
    private String postId;
    private String messageId;
    private long timeStamp;
    private boolean isImage;
    private String imageUrl;


    public Message() {
    }

    public Message(String message, String senderUid, String receiverUid, String postId, String messageId, long timeStamp) {
        this.message = message;
        this.senderUid = senderUid;
        this.receiverUid = receiverUid;
        this.postId = postId;
        this.messageId = messageId;
        this.timeStamp = timeStamp;
        this.isImage = false;
        this.imageUrl = "";

    }

    public Message(Message message) {
        this.message = message.message;
        this.senderUid = message.senderUid;
        this.receiverUid = message.receiverUid;
        this.postId = message.postId;
        this.messageId = message.messageId;
        this.timeStamp = message.timeStamp;
        this.isImage = message.isImage;
        this.imageUrl = message.imageUrl;

    }

    public Message(Uri imageUrl, String senderUid, String receiverUid, String postId, String messageId, long timeStamp) {
        this.message = "";
        this.senderUid = senderUid;
        this.receiverUid = receiverUid;
        this.postId = postId;
        this.messageId = messageId;
        this.timeStamp = timeStamp;
        this.isImage = true;
        this.imageUrl = String.valueOf(imageUrl);
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


    public String getMessageId() {
        return messageId;
    }

    public void setMessageId(String messageId) {
        this.messageId = messageId;
    }

    public boolean isImage() {
        return isImage;
    }

    public void setImage(boolean image) {
        isImage = image;
    }

    public String getImageUrl() {
        return imageUrl;
    }

    public void setImageUrl(String imageUrl) {
        this.imageUrl = imageUrl;
    }
}
