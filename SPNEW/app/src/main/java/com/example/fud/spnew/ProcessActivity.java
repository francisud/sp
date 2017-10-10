package com.example.fud.spnew;

import android.graphics.Bitmap;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;

import org.opencv.android.BaseLoaderCallback;
import org.opencv.android.LoaderCallbackInterface;
import org.opencv.android.OpenCVLoader;
import org.opencv.android.Utils;
import org.opencv.core.Core;
import org.opencv.core.CvType;
import org.opencv.core.Mat;
import org.opencv.core.Rect;
import org.opencv.core.Scalar;
import org.opencv.core.Size;
import org.opencv.core.Point;
import org.opencv.imgcodecs.Imgcodecs;
import org.opencv.imgproc.Imgproc;


import java.util.ArrayList;

import static org.opencv.core.Core.CMP_EQ;
import static org.opencv.core.CvType.CV_8UC3;
import static org.opencv.imgproc.Imgproc.GC_FGD;
import static org.opencv.imgproc.Imgproc.GC_PR_FGD;

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

        //loadImages(extras, topPicture, sidePicture, bottomPicture);

        String topPhotoPath = extras.getString("topPhotoPath");
//        topPicture = Imgcodecs.imread(topPhotoPath);
//        Imgproc.cvtColor(topPicture, topPicture, Imgproc.COLOR_BGR2RGB);

        ArrayList<android.graphics.Point> topCoords = (ArrayList<android.graphics.Point>) getIntent().getSerializableExtra("topCoords");

        topPicture = imageSegmentation(topPhotoPath, topCoords);
        //setPic(topPicture);
    }

    private void setPic(Mat topPicture) {
        Bitmap bm = Bitmap.createBitmap(topPicture.cols(), topPicture.rows(), Bitmap.Config.ARGB_8888);
        Utils.matToBitmap(topPicture, bm);

        ImageView iv = (ImageView) findViewById(R.id.oldPhoto);
        iv.setImageBitmap(bm);
    }

    private Mat imageSegmentation(String photoPath, ArrayList<android.graphics.Point> coords){

        double x1, y1, x2, y2;
        x1 = coords.get(0).x;
        y1 = coords.get(0).y;

        x2 = coords.get(1).x;
        y2 = coords.get(1).y;

        Log.d("debug", "x1 = " + x1);
        Log.d("debug", "y1 = " + y1);
        Log.d("debug", "x2 = " + x2);
        Log.d("debug", "y2 = " + y2);

        org.opencv.core.Point p1 = new org.opencv.core.Point(x1, y1);
        org.opencv.core.Point p2 = new org.opencv.core.Point(x2, y2);

        Mat picture = Imgcodecs.imread(photoPath);
        Rect rectangle = new Rect(p1, p2);

        Mat result = new Mat();
        Mat fgdModel = new Mat();
        Mat bgdModel = new Mat();


        //based on - http://answers.opencv.org/question/24463/how-to-remove-black-background-from-grabcut-output-image-in-opencv-android/

        // GrabCut segmentation
        Imgproc.grabCut(picture, result, rectangle, bgdModel, fgdModel, 1, Imgproc.GC_INIT_WITH_RECT);

        Mat source = new Mat(1, 1, CvType.CV_8U, new Scalar(3.0));

        // Get the pixels marked as likely foreground
        Core.compare(result, source, result, CMP_EQ);
        // Generate output image
        Mat foreground = new Mat(picture.size(),CV_8UC3,new Scalar(255,255,255));
        picture.copyTo(foreground,result); // bg pixels not copied

        return picture;
    }



}
