package com.example.pre_alpha.models;

public class User {
    private String email;
    private String username;
    private String uid;
    private String status;
    public User(){ }
    public User(String email, String username, String uid, String status){
        this.email=email;
        this.username=username;
        this.uid=uid;
        this.status=status;
    }
    public User(User user){
        this.email=user.email;
        this.username=user.username;
        this.uid=user.uid;
        this.status=user.status;
    }

    public String getEmail() {
        return email;
    }
    public void setEmail(String email) {
        this.email=email;
    }

    public String getUsername() {
        return username;
    }
    public void setUsername(String username) {
        this.username=username;
    }

    public String getUid() {
        return uid;
    }
    public void setUid(String uid) {
        this.uid=uid;
    }

    public String getStatus() {
        return status;
    }
    public void setStatus(String status) {
        this.status = status;
    }
}
