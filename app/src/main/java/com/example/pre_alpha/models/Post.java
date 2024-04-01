package com.example.pre_alpha.models;

public class Post {
    private String name;
    private String item;
    private double latitude;
    private double longitude;
    private String address;
    private int radius;
    private String about;
    private String image;
    private String lostOrFound;
    private String creatorUid;
    private String postId;
    private long timeStamp;

    public Post(){}
    public Post(String name, String item, double latitude, double longitude, String addrsss, int radius, String about, String image, String lostOrFound, String creatorUid, String postId, long  timeStamp){
        this.name=name;
        this.item=item;
        this.latitude=latitude;
        this.longitude=longitude;
        this.address=addrsss;
        this.radius=radius;
        this.about=about;
        this.image=image;
        this.lostOrFound=lostOrFound;
        this.creatorUid=creatorUid;
        this.postId=postId;
        this.timeStamp=timeStamp;
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

    public String getAddress() {
        return address;
    }

    public void setAddress(String address) {
        this.address = address;
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

    public double getLatitude() {
        return latitude;
    }

    public void setLatitude(double latitude) {
        this.latitude = latitude;
    }

    public double getLongitude() {
        return longitude;
    }

    public void setLongitude(double longitude) {
        this.longitude = longitude;
    }

    public int getRadius() {
        return radius;
    }

    public void setRadius(int radius) {
        this.radius = radius;
    }

    public long getTimeStamp() {
        return timeStamp;
    }

    public void setTimeStamp(long timeStamp) {
        this.timeStamp = timeStamp;
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
