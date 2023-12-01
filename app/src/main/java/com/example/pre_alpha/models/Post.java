package com.example.pre_alpha.models;

public class Post {
    private String name;
    private String item;
    private String area;
    private String about;
    private String image;
    private String lostOrFound;
    private String creatorUid;
    private String postId;
    public Post(){}
    public Post(String name, String item, String area, String about, String image, String lostOrFound, String creatorUid, String postId){
        this.name=name;
        this.item=item;
        this.area=area;
        this.about=about;
        this.image=image;
        this.lostOrFound=lostOrFound;
        this.creatorUid=creatorUid;
        this.postId=postId;
    }

    public String getName() {
        return name;
    }
    public void setName(String name) {
        this.name=name;
    }

    public String getItem() {
        return item;
    }
    public void setItem(String item) {
        this.item=item;
    }

    public String getArea() {
        return area;
    }
    public void setArea(String area) {
        this.area=area;
    }

    public String getAbout() {
        return about;
    }
    public void setAbout(String about) {
        this.about=about;
    }

    public String getImage() {
        return image;
    }
    public void setImage(String image) {
        this.image=image;
    }

    public String getLostOrFound() {
        return lostOrFound;
    }
    public void setLostOrFound(String lostOrFound) {
        this.lostOrFound=lostOrFound;
    }

    public String getCreatorUid() {
        return creatorUid;
    }
    public void setCreatorUid(String creatorUid) {
        this.creatorUid=creatorUid;
    }

    public String getPostId() {
        return postId;
    }
    public void setPostId(String postId) {
        this.postId = postId;
    }
}
