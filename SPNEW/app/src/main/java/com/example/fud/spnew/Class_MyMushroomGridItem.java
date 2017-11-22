package com.example.fud.spnew;

import android.graphics.Bitmap;

public class Class_MyMushroomGridItem {
    private Bitmap image;
    private String date;
    private int id;

    public Class_MyMushroomGridItem(Bitmap image, String date, int id) {
        super();
        this.image = image;
        this.date = date;
        this.id = id;
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


}
