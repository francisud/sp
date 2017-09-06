package com.example.fud.spnew;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.media.Image;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.Toast;

import org.opencv.android.BaseLoaderCallback;
import org.opencv.android.LoaderCallbackInterface;
import org.opencv.android.OpenCVLoader;
import org.opencv.android.Utils;
import org.opencv.core.CvType;
import org.opencv.core.Mat;
import org.opencv.core.Size;
import org.opencv.imgcodecs.Imgcodecs;
import org.opencv.imgproc.Imgproc;

public class ProcessActivity extends AppCompatActivity {

    //FOR LOADING OPENCV
    private BaseLoaderCallback mLoaderCallback = new BaseLoaderCallback(this) {
        @Override
        public void onManagerConnected(int status) {
            switch (status) {
                case LoaderCallbackInterface.SUCCESS:
                {
                    //Toast.makeText(ProcessActivity.this, "SUCCESS", Toast.LENGTH_SHORT).show();
                    start();
                } break;
                default:
                {
                    super.onManagerConnected(status);
                } break;
            }
        }
    };

    //FOR LOADING OPENCV
    @Override
    public void onResume()
    {
        super.onResume();
        OpenCVLoader.initAsync(OpenCVLoader.OPENCV_VERSION_3_3_0, this, mLoaderCallback);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_process);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Snackbar.make(view, "Replace with your own action", Snackbar.LENGTH_LONG)
                        .setAction("Action", null).show();
            }
        });
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
    }

    private void start(){
        //for getting the data from the previous activity
        Bundle extras = getIntent().getExtras();

        Mat topPicture = null;

        Mat rgbTopPicture = null;

        //loadImages(extras, topPicture, sidePicture, bottomPicture);

        String topPhotoPath = extras.getString("topPhotoPath");
        topPicture = Imgcodecs.imread(topPhotoPath);
        Imgproc.cvtColor(topPicture, topPicture, Imgproc.COLOR_BGR2RGB);

        rgbTopPicture = enhanceImage(topPicture);

        setPic(topPicture, rgbTopPicture);
    }


//    private void loadImages(Bundle extras, Mat topPicture){
//        //add checker if exists in extras
//        String topPhotoPath = extras.getString("topPhotoPath");
//
//        topPicture = Imgcodecs.imread(topPhotoPath);
//    }

    private Mat enhanceImage(Mat photo){
        Mat rgbPhoto;

        rgbPhoto = histogramEqualization(photo);

        return rgbPhoto;
    }



    private Mat histogramEqualization(Mat photo){
        Mat value = new Mat(photo.rows(),photo.cols(), CvType.CV_8UC1);
        Mat saturation = new Mat(photo.rows(),photo.cols(), CvType.CV_8UC1);

        Mat HSV = new Mat();
        Imgproc.cvtColor(photo, HSV, Imgproc.COLOR_RGB2HSV);

        Imgproc.equalizeHist(value, value);
        Imgproc.equalizeHist(saturation, saturation);

        Mat enhancedImage = new Mat();
        Imgproc.cvtColor(HSV,enhancedImage,Imgproc.COLOR_HSV2RGB);

        return enhancedImage;
    }

    private void setPic(Mat topPicture, Mat rgbTopPicture) {
        Mat tp = new Mat();

        Imgproc.resize(topPicture, tp, new Size(), 0.1, 0.1, Imgproc.INTER_AREA);

        Bitmap bm = Bitmap.createBitmap(tp.cols(), tp.rows(),Bitmap.Config.ARGB_8888);
        Utils.matToBitmap(tp, bm);

        ImageView iv = (ImageView) findViewById(R.id.oldPhoto);
        iv.setImageBitmap(bm);


        /****/

        Mat tp2 = new Mat();
        Imgproc.resize(rgbTopPicture, tp2, new Size(), 0.1, 0.1, Imgproc.INTER_AREA);

        Bitmap bm2 = Bitmap.createBitmap(tp2.cols(), tp2.rows(), Bitmap.Config.ARGB_8888);
        Utils.matToBitmap(tp2, bm2);

        ImageView iv2 = (ImageView) findViewById(R.id.newPhoto);
        iv2.setImageBitmap(bm2);

    }



}
