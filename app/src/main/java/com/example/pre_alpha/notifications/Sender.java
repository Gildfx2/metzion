package com.example.pre_alpha.notifications;


public class Sender {
    //attributes
    Data data;
    String to;

    //constructors
    public Sender(){}
    public Sender(Data data, String to) {
        this.data = data;
        this.to = to;
    }

    //getters and setters
    public Data getData() {
        return data;
    }
    public void setData(Data data) {
        this.data = data;
    }

    public String getTo() {
        return to;
    }
    public void setTo(String to) {
        this.to = to;
    }
}
