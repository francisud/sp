package com.example.fud.spnew;

import android.app.Activity;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Matrix;
import android.graphics.Paint;
import android.graphics.Point;
import android.graphics.Rect;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.Display;
import android.view.View;
import android.view.ViewTreeObserver;
import android.view.Window;
import android.widget.ImageView;
import android.widget.Toast;

public class CropActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        this.requestWindowFeature(Window.FEATURE_NO_TITLE);

        setContentView(R.layout.activity_crop);

        //get photopath
        Bundle extras = getIntent().getExtras();
        String photoPath = extras.getString("photoPath");

        //display image, and resize to have faster loading
        //guide - http://www.informit.com/articles/article.aspx?p=2143148&seqNum=2
//        Point size = new Point();
//
//        final ImageView iv = (ImageView) findViewById(R.id.imageView3);
//        Display display = getWindowManager().getDefaultDisplay();
//        display.getSize(size);
//        int displayWidth = size.x;
//        BitmapFactory.Options options = new BitmapFactory.Options();
//        options.inJustDecodeBounds = true;
//        options.inMutable = true;
//        BitmapFactory.decodeFile(photoPath, options);
//        int width = options.outWidth;
//          if (width > displayWidth) {
//              int widthRatio = Math.round((float) width / (float) displayWidth);
//              options.inSampleSize = widthRatio;
//          }
//        options.inJustDecodeBounds = false;
//        Bitmap scaledBitmap =  BitmapFactory.decodeFile(photoPath, options);
//
//        iv.setImageBitmap(scaledBitmap);

        ImageView iv = (ImageView) findViewById(R.id.imageView3);
        Point size = new Point();
        Display display = getWindowManager().getDefaultDisplay();
        display.getSize(size);

        iv.setImageBitmap(decodeSampledBitmapFromResource(photoPath, size.x, size.y));

    }

    public static Bitmap decodeSampledBitmapFromResource(String photoPath,
                                                         int reqWidth, int reqHeight) {

        // First decode with inJustDecodeBounds=true to check dimensions
        final BitmapFactory.Options options = new BitmapFactory.Options();
        options.inJustDecodeBounds = true;
        BitmapFactory.decodeFile(photoPath, options);

        // Calculate inSampleSize
        options.inSampleSize = calculateInSampleSize(options, reqWidth, reqHeight);

        // Decode bitmap with inSampleSize set
        options.inJustDecodeBounds = false;
        return BitmapFactory.decodeFile(photoPath, options);

    }


    public static int calculateInSampleSize(BitmapFactory.Options options, int reqWidth, int reqHeight) {
        // Raw height and width of image
        final int height = options.outHeight;
        final int width = options.outWidth;
        int inSampleSize = 1;

        if (height > reqHeight || width > reqWidth) {

            final int halfHeight = Math.round(height / 2);
            final int halfWidth = Math.round(width / 2);

            // Calculate the largest inSampleSize value that is a power of 2 and keeps both
            // height and width larger than the requested height and width.
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
            final int origW = d.getIntrinsicWidth();
            final int origH = d.getIntrinsicHeight();

            final int actualWidth = Math.round(origW * scaleX);
            final int actualHeight = Math.round(origH * scaleY);

            int imgViewW = iv.getMeasuredWidth();
            int imgViewH = iv.getMeasuredHeight();

            int startingX = Math.round((imgViewW - actualWidth)/2);
            int startingY = Math.round((imgViewH - actualHeight)/2);

            Log.d("debug", "image width: " + Integer.toString(actualWidth));
            Log.d("debug", "image height: " + Integer.toString(actualHeight));

            Log.d("debug", "imageview.getWidth: " + Integer.toString(iv.getWidth()));
            Log.d("debug", "imageview.getHeight: " + Integer.toString(iv.getHeight()));

            Log.d("debug", "starting x: " + Integer.toString(startingX));
            Log.d("debug", "starting y: " + Integer.toString(startingY));

            DrawView dv = (DrawView) findViewById(R.id.view);
            dv.getDimensions(actualWidth,actualHeight,startingX,startingY);
        }
    }


    //return the coordinates of the rectangle
    public void toReturn(View view){
        Intent returnIntent = new Intent();
        returnIntent.putExtra("result", "result");
        setResult(Activity.RESULT_OK,returnIntent);
        finish();
    }

}
