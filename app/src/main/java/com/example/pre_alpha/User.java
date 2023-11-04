package com.example.pre_alpha;

public class User {
    private String email;
    private String username;
    private String uid;
    private Post post;
    public User(){ }
    public User(String email, String username, String uid){
        this.email=email;
        this.username=username;
        this.uid=uid;
        this.post=null;
    }
    public User(String email, String username, String uid, Post post){
        this.email=email;
        this.username=username;
        this.uid=uid;
        this.post=post;
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
    public Post getPost() {
        return post;
    }

    public void setPost(Post post) {
        this.post=post;
    }
}
