package com.example.pre_alpha.notifications;


public class Sender {
    //attributes
    private NotificationData notificationData;
    private String to;

    //constructors
    public Sender(){}
    public Sender(NotificationData notificationData, String to) {
        this.notificationData = notificationData;
        this.to = to;
    }

    //getters and setters
    public NotificationData getData() {
        return notificationData;
    }
    public void setData(NotificationData notificationData) {
        this.notificationData = notificationData;
    }

    public String getTo() {
        return to;
    }
    public void setTo(String to) {
        this.to = to;
    }
}
