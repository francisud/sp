package com.example.fud.spnew;

import android.graphics.Bitmap;

public class Class_MyMushroomGridItem {
    private Bitmap image;
    private String date;
    private int id;
    private int is_uploaded;

    public Class_MyMushroomGridItem(Bitmap image, String date, int id, int is_uploaded) {
        super();
        this.image = image;
        this.date = date;
        this.id = id;
        this.is_uploaded = is_uploaded;
    }

    public Bitmap getImage() {
        return image;
    }

    public String getDate() {
        return date;
    }

    public int getId(){
        return id;
    }

    public int getIs_uploaded(){return is_uploaded;}
}
