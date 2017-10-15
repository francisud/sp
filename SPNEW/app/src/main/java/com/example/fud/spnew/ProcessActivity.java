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
import org.opencv.core.MatOfPoint;
import org.opencv.core.Rect;
import org.opencv.core.Scalar;
import org.opencv.imgcodecs.Imgcodecs;
import org.opencv.imgproc.Imgproc;
import org.opencv.imgproc.Moments;


import java.util.ArrayList;
import java.util.List;



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
        Mat topPictureHistogram = null;
        Mat topPictureHuMoments = null;
        Mat topPictureTexture = null;

        String topPhotoPath = extras.getString("topPhotoPath");

        ArrayList<android.graphics.Point> topCoords = (ArrayList<android.graphics.Point>) getIntent().getSerializableExtra("topCoords");

        topPicture = imageSegmentation(topPhotoPath, topCoords);
        topPictureHistogram = getHistogram(topPicture);
        topPictureHuMoments = getHuMoments(topPicture);
        topPictureTexture = getGaborWavelets(topPicture);
        Log.d("debug", "after gaborwavelets");

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

        org.opencv.core.Point tl = new org.opencv.core.Point(x1, y1);
        org.opencv.core.Point br = new org.opencv.core.Point(x2, y2);

        //based on - https://github.com/schenkerx/GrabCutDemo/blob/master/app/src/main/java/cvworkout2/graphcutdemo/MainActivity.java

        Mat img = Imgcodecs.imread(photoPath);
        Mat firstMask = new Mat();
        Mat bgModel = new Mat();
        Mat fgModel = new Mat();
        Mat source = new Mat(1, 1, CvType.CV_8U, new Scalar(Imgproc.GC_PR_FGD));
        Mat dst = new Mat();
        Rect rect = new Rect(tl, br);

        Imgproc.grabCut(img, firstMask, rect, bgModel, fgModel,
                1, Imgproc.GC_INIT_WITH_RECT);

        Core.compare(firstMask, source, firstMask, Core.CMP_EQ);

        Mat foreground = new Mat(img.size(), CvType.CV_8UC3,
                new Scalar(255, 255, 255));
        img.copyTo(foreground, firstMask);

        firstMask.release();
        source.release();
        bgModel.release();
        fgModel.release();

        return foreground;
    }

    //https://github.com/vinjn/opencv-2-cookbook-src/blob/master/Chapter%2004/colorhistogram.h
    //LAB color space is used
    private Mat getHistogram(Mat image){
        Mat hist = new Mat();

        // Convert to Lab color space
        Mat lab = new Mat();
        Imgproc.cvtColor(image, lab, Imgproc.COLOR_BGR2Lab);

        List<Mat> imageList = new ArrayList<Mat>();
        imageList.add(lab);

//        get histogram
        Imgproc.calcHist(imageList, new org.opencv.core.MatOfInt(2), new Mat(), hist, new org.opencv.core.MatOfInt(256), new org.opencv.core.MatOfFloat(-128,127));

        return hist;
    }

    private Mat getHuMoments(Mat image){
        Mat grayScale = new Mat();
        Imgproc.cvtColor(image, grayScale, Imgproc.COLOR_BGR2GRAY);

        Mat threshold_output = new Mat();
        List<MatOfPoint> contours = new ArrayList<MatOfPoint>();


        ///to get the outline of the object
        Imgproc.threshold(grayScale, threshold_output, 254, 255, Imgproc.THRESH_BINARY_INV);
        ///find contours
        Imgproc.findContours(threshold_output, contours, new Mat(), Imgproc.RETR_EXTERNAL, Imgproc.CHAIN_APPROX_SIMPLE);

        double largest_area = 0;
        int index = 0;

        for( int i = 0; i< contours.size(); i++ ){
            double compare = Imgproc.contourArea( contours.get(i) );
            if( compare > largest_area ){
                largest_area = compare;
                index = i;
            }
        }

        Moments momentsHolder;
        momentsHolder = Imgproc.moments(contours.get(0), false);

        Mat hu = new Mat();
        Imgproc.HuMoments(momentsHolder, hu);

        return hu;
    }

    private Mat getGaborWavelets(Mat image){
        Mat grayScale = new Mat();
        Imgproc.cvtColor(image, grayScale, Imgproc.COLOR_BGR2GRAY);

        double ksize = 9;
        double sigma = 5, lambda = 4, gamma = 0.04, psi = Math.PI/4;
        List<Mat> destArray  = new ArrayList<Mat>();
        int theta[] = new int[8];

        //angles
        theta[0] = 0;
        theta[1] = 23;
        theta[2] = 45;
        theta[3] = 68;
        theta[4] = 90;
        theta[5] = 113;
        theta[6] = 135;
        theta[7] = 158;

        for (int j = 0; j<8; j++){
            Mat kernel;
            Mat dest = new Mat();
            kernel = Imgproc.getGaborKernel(new org.opencv.core.Size(ksize,ksize), sigma, theta[j], lambda, gamma, psi, CvType.CV_32F);
            Imgproc.filter2D(grayScale, dest, CvType.CV_32F, kernel);

            destArray.add(dest);
        }

        //fix return values later
        return grayScale;
    }

}
