package com.example.pre_alpha.models;

import java.util.ArrayList;

public class User {
    private String email;
    private String username;
    private String uid;
    public User(){ }
    public User(String email, String username, String uid){
        this.email=email;
        this.username=username;
        this.uid=uid;
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


}
