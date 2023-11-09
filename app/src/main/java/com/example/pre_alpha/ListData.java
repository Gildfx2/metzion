package com.example.pre_alpha;

import android.net.Uri;
import android.widget.ImageView;

public class ListData {
    String name, area, item, about;
    Uri image;

    public ListData(String name, String area, String item, Uri image, String about){
        this.name = name;
        this.area = area;
        this.item = item;
        this.image = image;
        this.about = about;
    }
}
