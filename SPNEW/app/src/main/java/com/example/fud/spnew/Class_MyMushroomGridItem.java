package com.example.fud.spnew;

import android.graphics.Bitmap;

public class Class_MyMushroomGridItem {
    private Bitmap image;
    private String title;

    public Class_MyMushroomGridItem(Bitmap image, String title) {
        super();
        this.image = image;
        this.title = title;
    }

    public Bitmap getImage() {
        return image;
    }

    public String getTitle() {
        return title;
    }

}
