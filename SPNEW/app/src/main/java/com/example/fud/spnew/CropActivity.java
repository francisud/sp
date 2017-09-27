package com.example.fud.spnew;

import android.app.Activity;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Color;
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
        Point size = new Point();

        final ImageView iv = (ImageView) findViewById(R.id.imageView3);
        Display display = getWindowManager().getDefaultDisplay();
        display.getSize(size);
        int displayWidth = size.x;
        BitmapFactory.Options options = new BitmapFactory.Options();
        options.inJustDecodeBounds = true;
        options.inMutable = true;
        BitmapFactory.decodeFile(photoPath, options);
        int width = options.outWidth;
          if (width > displayWidth) {
              int widthRatio = Math.round((float) width / (float) displayWidth);
              options.inSampleSize = widthRatio;
          }
        options.inJustDecodeBounds = false;
        Bitmap scaledBitmap =  BitmapFactory.decodeFile(photoPath, options);

        Paint paint=new Paint();
        paint.setColor(Color.RED);

        Canvas canvas = new Canvas(scaledBitmap);
        canvas.drawBitmap(scaledBitmap, 0, 0, null);
        canvas.drawRect(20,20,50,50, paint);
        iv.setImageBitmap(scaledBitmap);
    }


    //to get the dimensions for the rectangle bounds
    @Override
    public void onWindowFocusChanged(boolean hasFocus) {
        super.onWindowFocusChanged(hasFocus);
        if (hasFocus) {
            ImageView iv = (ImageView) findViewById(R.id.imageView3);

            //BASED ON  - https://stackoverflow.com/a/13318469
            final int actualHeight, actualWidth;
            final int imageViewHeight = iv.getHeight(), imageViewWidth = iv.getWidth();
            final int bitmapHeight = iv.getDrawable().getIntrinsicHeight(), bitmapWidth = iv.getDrawable().getIntrinsicWidth();
            if (imageViewHeight * bitmapWidth <= imageViewWidth * bitmapHeight) {
                actualWidth = bitmapWidth * imageViewHeight / bitmapHeight;
                actualHeight = imageViewHeight;
            } else {
                actualHeight = bitmapHeight * imageViewWidth / bitmapWidth;
                actualWidth = imageViewWidth;
            }

            Log.d("debug", "image width: " + Integer.toString(actualWidth));
            Log.d("debug", "image height: " + Integer.toString(actualHeight));

            DrawView dv = (DrawView) findViewById(R.id.view);
            dv.getDimensions(actualWidth,actualHeight);

            //get location of picture in the imageview
            //BASDE ON  - https://stackoverflow.com/a/12373374
            Rect bounds = iv.getDrawable().getBounds();
            int x = (iv.getWidth() - actualWidth) / 2;
            int y = (iv.getHeight() - actualHeight) / 2;

            Log.d("debug", "imageview.getWidth: " + Integer.toString(iv.getWidth()));
            Log.d("debug", "imageview.getHeight: " + Integer.toString(iv.getHeight()));

            Log.d("debug", "starting x: " + Integer.toString(x));
            Log.d("debug", "starting y: " + Integer.toString(y));
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
