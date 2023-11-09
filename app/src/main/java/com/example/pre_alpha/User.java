package com.example.pre_alpha;

import java.util.ArrayList;

public class User {
    private String email;
    private String username;
    private String uid;
    private ArrayList<Post> posts = new ArrayList<Post>();
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
    public ArrayList<Post> getPosts() {
        return posts;
    }

    public void setPosts(ArrayList<Post> posts) {
        this.posts=posts;
    }
    public void addPost(Post post){
        posts.add(post);
    }
}
