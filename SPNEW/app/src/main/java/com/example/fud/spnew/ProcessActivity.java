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
import org.opencv.core.MatOfFloat;
import org.opencv.core.MatOfPoint;
import org.opencv.core.Rect;
import org.opencv.core.Scalar;
import org.opencv.imgcodecs.Imgcodecs;
import org.opencv.imgproc.Imgproc;
import org.opencv.imgproc.Moments;

import java.util.ArrayList;
import java.util.List;

import umich.cse.yctung.androidlibsvm.LibSVM;

import static org.opencv.core.CvType.CV_32F;


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
        classify();
        Log.d("debug", "lib svm");

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

    //https://docs.opencv.org/2.4/doc/tutorials/imgproc/histograms/histogram_calculation/histogram_calculation.html
    //LAB color space is used
    private Mat getHistogram(Mat image){
        Mat lab = new Mat();
        Mat grayScale = new Mat();
        Mat mask = new Mat();
        Mat hist = new Mat();

        //compute mask
        Imgproc.cvtColor(image,grayScale,Imgproc.COLOR_BGR2GRAY);
        Imgproc.threshold(grayScale,mask,254,255,Imgproc.THRESH_BINARY_INV);

        ///change color space image
        Imgproc.cvtColor(image, lab, Imgproc.COLOR_BGR2Lab);

        List<Mat> imageList = new ArrayList<Mat>();
        imageList.add(lab);

        Imgproc.calcHist(imageList, new org.opencv.core.MatOfInt(2), mask, hist, new org.opencv.core.MatOfInt(256), new org.opencv.core.MatOfFloat(-127,127));

        return new Mat();
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
        momentsHolder = Imgproc.moments(contours.get(index), true);

        Mat hu = new Mat();
        Imgproc.HuMoments(momentsHolder, hu);

        return hu;
    }

    private Mat getGaborWavelets(Mat image){
        Mat imageGray = new Mat();
        Mat imageFloat = new Mat();
        List<Mat> destArray  = new ArrayList<Mat>();

        double ksize = 9;
        double sigma = 5;
        double gamma = 0.04;
        double psi = Math.PI/4;
        int theta[] = new int[4];
        double lambda[] = new double[3];

        //angles
        theta[0] = 0;
        theta[1] = 45;
        theta[2] = 90;
        theta[3] = 135;

        //scale
        lambda[0] = 2;
        lambda[1] = 4;
        lambda[2] = 8;

        Imgproc.cvtColor(image, imageGray, Imgproc.COLOR_BGR2GRAY);
        imageGray.convertTo(imageFloat, CV_32F, 1.0/256.0);

        Mat kernelReal = new Mat();
        Mat kernelImag = new Mat();
        Mat dest = new Mat();

        for (int i = 0; i<4; i++){
            for (int j = 0; j<3; j++){
                kernelReal = Imgproc.getGaborKernel(new org.opencv.core.Size(ksize,ksize), sigma, theta[i], lambda[j], gamma, 0, CV_32F);
                kernelImag = Imgproc.getGaborKernel(new org.opencv.core.Size(ksize,ksize), sigma, theta[i], lambda[j], gamma, Math.PI/2, CV_32F);

                Imgproc.filter2D(imageFloat, dest, -1, kernelReal);
                destArray.add(dest);

                Imgproc.filter2D(imageFloat, dest, -1, kernelImag);
                destArray.add(dest);
            }
        }

        //still need fix value
        double energy1a=0, energy1b=0, energy1c=0, energy1d=0;
        double energy2a=0, energy2b=0, energy2c=0, energy2d=0;
        double energy3a=0, energy3b=0, energy3c=0, energy3d=0;

        for(int i = 0; i < image.rows()-1; i++){
            for(int j = 0; j < image.cols()-1; j++){
                energy1a += (destArray.get(0).get(i,j)[0] * destArray.get(0).get(i,j)[0]) + (destArray.get(1).get(i,j)[0] * destArray.get(1).get(i,j)[0]);
                energy1b += (destArray.get(2).get(i,j)[0] * destArray.get(2).get(i,j)[0]) + (destArray.get(3).get(i,j)[0] * destArray.get(3).get(i,j)[0]);
                energy1c += (destArray.get(4).get(i,j)[0] * destArray.get(4).get(i,j)[0]) + (destArray.get(5).get(i,j)[0] * destArray.get(5).get(i,j)[0]);
                energy1d += (destArray.get(6).get(i,j)[0] * destArray.get(6).get(i,j)[0]) + (destArray.get(7).get(i,j)[0] * destArray.get(7).get(i,j)[0]);

                energy2a += (destArray.get(8).get(i,j)[0] * destArray.get(8).get(i,j)[0]) + (destArray.get(9).get(i,j)[0] * destArray.get(9).get(i,j)[0]);
                energy2b += (destArray.get(10).get(i,j)[0] * destArray.get(10).get(i,j)[0]) + (destArray.get(11).get(i,j)[0] * destArray.get(11).get(i,j)[0]);
                energy2c += (destArray.get(12).get(i,j)[0] * destArray.get(12).get(i,j)[0]) + (destArray.get(13).get(i,j)[0] * destArray.get(13).get(i,j)[0]);
                energy2d += (destArray.get(14).get(i,j)[0] * destArray.get(14).get(i,j)[0]) + (destArray.get(15).get(i,j)[0] * destArray.get(15).get(i,j)[0]);

                energy3a += (destArray.get(16).get(i,j)[0] * destArray.get(16).get(i,j)[0]) + (destArray.get(17).get(i,j)[0] * destArray.get(17).get(i,j)[0]);
                energy3b += (destArray.get(18).get(i,j)[0] * destArray.get(18).get(i,j)[0]) + (destArray.get(19).get(i,j)[0] * destArray.get(19).get(i,j)[0]);
                energy3c += (destArray.get(20).get(i,j)[0] * destArray.get(20).get(i,j)[0]) + (destArray.get(21).get(i,j)[0] * destArray.get(21).get(i,j)[0]);
                energy3d += (destArray.get(22).get(i,j)[0] * destArray.get(22).get(i,j)[0]) + (destArray.get(23).get(i,j)[0] * destArray.get(23).get(i,j)[0]);
            }
        }

        //fix return values later
        return imageGray;
    }

    private void classify(){
        LibSVM svm = new LibSVM();

    }

}
