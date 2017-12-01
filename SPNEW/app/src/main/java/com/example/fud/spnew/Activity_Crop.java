package com.example.fud.spnew;

import android.app.Activity;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Matrix;
import android.graphics.Point;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.Display;
import android.view.Gravity;
import android.view.MotionEvent;
import android.view.View;
import android.view.Window;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.Toast;

import java.io.FileNotFoundException;
import java.io.InputStream;
import java.util.ArrayList;

public class Activity_Crop extends AppCompatActivity {
    Uri photoPath;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        this.requestWindowFeature(Window.FEATURE_NO_TITLE);

        setContentView(R.layout.activity_crop);

        //get photopath
        Bundle extras = getIntent().getExtras();
        photoPath = Uri.parse(extras.getString("photoPath"));

        ImageView iv = (ImageView) findViewById(R.id.imageView3);
        Point size = new Point();
        Display display = getWindowManager().getDefaultDisplay();
        display.getSize(size);

        iv.setImageBitmap(decodeSampledBitmapFromResource(photoPath, size.x, size.y));
    }

    //BASED ON - https://developer.android.com/topic/performance/graphics/load-bitmap.html
    public Bitmap decodeSampledBitmapFromResource(Uri photoPath, int reqWidth, int reqHeight) {
        InputStream stream = null;

        final BitmapFactory.Options options = new BitmapFactory.Options();
        options.inJustDecodeBounds = true;

        try {
            stream = getContentResolver().openInputStream(photoPath);
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }
        BitmapFactory.decodeStream(stream, null, options);

        options.inSampleSize = calculateInSampleSize(options, reqWidth, reqHeight);

        options.inJustDecodeBounds = false;

        try {
            stream = getContentResolver().openInputStream(photoPath);
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }
        return BitmapFactory.decodeStream(stream, null, options);
    }

    public int calculateInSampleSize(BitmapFactory.Options options, int reqWidth, int reqHeight) {
        final int height = options.outHeight;
        final int width = options.outWidth;
        int inSampleSize = 1;

        if (height > reqHeight || width > reqWidth) {
            final int halfHeight = Math.round(height / 2);
            final int halfWidth = Math.round(width / 2);

            while ((halfHeight / inSampleSize) >= reqHeight
                    && (halfWidth / inSampleSize) >= reqWidth) {
                inSampleSize *= 2;
            }
        }
        return inSampleSize;
    }


    //to get the dimensions for the rectangle bounds
    @Override
    public void onWindowFocusChanged(boolean hasFocus) {
        super.onWindowFocusChanged(hasFocus);
        if (hasFocus) {
            ImageView iv = (ImageView) findViewById(R.id.imageView3);

            //BASED ON - https://stackoverflow.com/a/26930938
            float[] f = new float[9];
            iv.getImageMatrix().getValues(f);

            // Extract the scale values using the constants (if aspect ratio maintained, scaleX == scaleY)
            final float scaleX = f[Matrix.MSCALE_X];
            final float scaleY = f[Matrix.MSCALE_Y];

            final Drawable d = iv.getDrawable();
            final int origWidth = d.getIntrinsicWidth();
            final int origHeight = d.getIntrinsicHeight();

            final int actualWidth = Math.round(origWidth * scaleX);
            final int actualHeight = Math.round(origHeight * scaleY);

            int imgViewW = iv.getMeasuredWidth();
            int imgViewH = iv.getMeasuredHeight();

            int startingX = Math.round((imgViewW - actualWidth)/2);
            int startingY = Math.round((imgViewH - actualHeight)/2);

            //getting 500px scaling
            float xScale =  ((float) 500) / origWidth;
            float yScale =  ((float) 500) / origHeight;
            float scale = (xScale <= yScale) ? xScale : yScale;

            View_Draw dv = (View_Draw) findViewById(R.id.view);
            dv.getDimensions(actualWidth,actualHeight,startingX,startingY,origWidth,origHeight, scaleX, scaleY, scale, scale);
        }
    }

    //return the coordinates of the rectangle
    public void toReturn(View view){
        View_Draw dv = (View_Draw) findViewById(R.id.view);
        ArrayList<Point> coordinates = dv.getCoordinates();

        //check if bounding box exists
        if(coordinates.get(0).x == coordinates.get(1).x && coordinates.get(0).y == coordinates.get(1).y){
            return;
        }

        float[] scaling = dv.getScaling();
        Intent returnIntent = new Intent();
        returnIntent.putExtra("coordinates", coordinates);
        returnIntent.putExtra("scaling", scaling);
        setResult(Activity.RESULT_OK,returnIntent);
        finish();
    }

    @Override
    public void onBackPressed() {
        View_Draw dv = (View_Draw) findViewById(R.id.view);
        ArrayList<Point> coordinates = dv.getCoordinates();

        //check if bounding box exists
        if(coordinates.get(0).x == coordinates.get(1).x && coordinates.get(0).y == coordinates.get(1).y){
            dv.showToast();
            return;
        }
    }

    public boolean onTouchEvent(MotionEvent event) {
        View_Draw dv = (View_Draw) findViewById(R.id.view);
        dv.closeToast();
        return true;
    }
}
