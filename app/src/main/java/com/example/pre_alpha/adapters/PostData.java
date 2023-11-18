package com.example.pre_alpha.adapters;

import android.net.Uri;

public class PostData {
    String name, area, item, about;
    Uri image;

    public PostData(String name, String area, String item, Uri image, String about){
        this.name = name;
        this.area = area;
        this.item = item;
        this.image = image;
        this.about = about;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getArea() {
        return area;
    }

    public void setArea(String area) {
        this.area = area;
    }

    public String getItem() {
        return item;
    }

    public void setItem(String item) {
        this.item = item;
    }

    public String getAbout() {
        return about;
    }

    public void setAbout(String about) {
        this.about = about;
    }

    public Uri getImage() {
        return image;
    }

    public void setImage(Uri image) {
        this.image = image;
    }
}
