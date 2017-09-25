package com.example.fud.spnew;

import android.app.Activity;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Point;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.Display;
import android.view.View;
import android.view.Window;
import android.widget.ImageView;

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

        ImageView iv = (ImageView) findViewById(R.id.imageView3);
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
//        iv.setImageBitmap(scaledBitmap);

        Paint paint=new Paint();
        paint.setColor(Color.RED);

        Canvas canvas = new Canvas(scaledBitmap);
        canvas.drawBitmap(scaledBitmap, 0, 0, null);
        canvas.drawRect(20,20,50,50, paint);
        iv.setImageBitmap(scaledBitmap);


    }


    //return the coordinates of the rectangle
    public void toReturn(View view){
        Intent returnIntent = new Intent();
        returnIntent.putExtra("result", "result");
        setResult(Activity.RESULT_OK,returnIntent);
        finish();
    }

}
