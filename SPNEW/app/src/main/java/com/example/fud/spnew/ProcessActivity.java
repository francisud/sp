package com.example.fud.spnew;

import android.graphics.Bitmap;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.ImageView;

import org.opencv.android.BaseLoaderCallback;
import org.opencv.android.LoaderCallbackInterface;
import org.opencv.android.OpenCVLoader;
import org.opencv.android.Utils;
import org.opencv.core.CvType;
import org.opencv.core.Mat;
import org.opencv.core.Rect;
import org.opencv.core.Scalar;
import org.opencv.core.Size;
import org.opencv.core.Point;
import org.opencv.imgcodecs.Imgcodecs;
import org.opencv.imgproc.Imgproc;

import java.util.ArrayList;

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
//        topPicture = Imgcodecs.imread(topPhotoPath);
//        Imgproc.cvtColor(topPicture, topPicture, Imgproc.COLOR_BGR2RGB);

        ArrayList<Point> topCoords = (ArrayList<Point>) getIntent().getSerializableExtra("topCoords");

        topPicture = imageSegmentation(topPhotoPath, topCoords);
        setPic(topPicture, rgbTopPicture);
    }

    private void setPic(Mat topPicture, Mat rgbTopPicture) {
        Mat tp = new Mat();

        Imgproc.resize(topPicture, tp, new Size(), 0.1, 0.1, Imgproc.INTER_AREA);

        Bitmap bm = Bitmap.createBitmap(tp.cols(), tp.rows(),Bitmap.Config.ARGB_8888);
        Utils.matToBitmap(tp, bm);

        ImageView iv = (ImageView) findViewById(R.id.oldPhoto);
        iv.setImageBitmap(bm);

    }

    private Mat imageSegmentation(String photoPath, ArrayList<Point> coords){

//        double x1, y1, x2, y2;
//        x1 = coords.get(0).x;
//        y1 = coords.get(0).y;
//
//        x2 = coords.get(1).x;
//        y2 = coords.get(1).y;

        org.opencv.core.Point p1 = new org.opencv.core.Point(100, 100);
        org.opencv.core.Point p2 = new org.opencv.core.Point(100, 100);

        Mat picture = Imgcodecs.imread(photoPath);
        Rect rectangle = new Rect(p1, p2);

        Mat mask = new Mat();
        Mat fgdModel = new Mat();
        Mat bgdModel = new Mat();

        //picture converted to 3 channels
        Mat pictureC3 = new Mat();
        Imgproc.cvtColor(picture, pictureC3, Imgproc.COLOR_RGBA2RGB);
        Imgproc.grabCut(pictureC3, mask, rectangle, bgdModel, fgdModel, 1, Imgproc.GC_INIT_WITH_RECT);

        return fgdModel;
    }



}
