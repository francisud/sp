package com.example.fud.spnew;

import android.content.res.AssetManager;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.text.TextUtils;
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
import org.opencv.core.MatOfInt;
import org.opencv.core.MatOfPoint;
import org.opencv.core.Rect;
import org.opencv.core.Scalar;
import org.opencv.core.Size;
import org.opencv.imgcodecs.Imgcodecs;
import org.opencv.imgproc.Imgproc;
import org.opencv.imgproc.Moments;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;

import static java.lang.Math.sqrt;
import static org.opencv.core.Core.split;
import static org.opencv.core.CvType.CV_32F;

import umich.cse.yctung.androidlibsvm.LibSVM;


public class ProcessActivity extends AppCompatActivity {

    //FOR LOADING OPENCV
    private BaseLoaderCallback mLoaderCallback = new BaseLoaderCallback(this) {
        @Override
        public void onManagerConnected(int status) {
            switch (status) {
                case LoaderCallbackInterface.SUCCESS:
                {
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

        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
    }

    private void start(){
        //for getting the data from the previous activity
        Bundle extras = getIntent().getExtras();

        Mat topPicture = null;
        Mat topPictureHistogram = null;
        Mat topPictureHuMoments = null;
        ArrayList<Double> topPictureTexture = null;

        String topPhotoPath = extras.getString("topPhotoPath");

        ArrayList<android.graphics.Point> topCoords = (ArrayList<android.graphics.Point>) getIntent().getSerializableExtra("topCoords");
        float[] scaling = (float[]) getIntent().getSerializableExtra("topScaling");

        topPicture = imageSegmentation(topPhotoPath, topCoords, scaling);
        topPictureHistogram = getHistogram(topPicture);
        topPictureHuMoments = getHuMoments(topPicture);
        topPictureTexture = getGaborWavelets(topPicture);
        writeToFile(topPictureHistogram,topPictureHuMoments,topPictureTexture);

        readyFiles();

        classify();

        setPic(topPicture);
    }

    private void setPic(Mat topPicture) {
        Bitmap bm = Bitmap.createBitmap(topPicture.cols(), topPicture.rows(), Bitmap.Config.ARGB_8888);
        Utils.matToBitmap(topPicture, bm);

        ImageView iv = (ImageView) findViewById(R.id.oldPhoto);
        iv.setImageBitmap(bm);
    }

    private Mat imageSegmentation(String photoPath, ArrayList<android.graphics.Point> coords, float[] scaling){
        //bounding rectangle
        int x1, y1, x2, y2;
        x1 = coords.get(0).x;
        y1 = coords.get(0).y;

        x2 = coords.get(1).x;
        y2 = coords.get(1).y;

        org.opencv.core.Point tl = new org.opencv.core.Point(x1, y1);
        org.opencv.core.Point br = new org.opencv.core.Point(x2, y2);

        //for smaller size matrix
        int width = x2 - x1;
        int height = y2 - y1;
        org.opencv.core.Size fgSize = new org.opencv.core.Size(width,height);

        //based on - https://github.com/schenkerx/GrabCutDemo/blob/master/app/src/main/java/cvworkout2/graphcutdemo/MainActivity.java
        Mat img = Imgcodecs.imread(photoPath);
//        Imgproc.resize(img, img, new Size(), scaling[0], scaling[1], Imgproc.INTER_CUBIC);
        Imgproc.resize(img, img, new Size(500,500), 0, 0, Imgproc.INTER_CUBIC);

        Mat firstMask = new Mat();
        Mat bgModel = new Mat();
        Mat fgModel = new Mat();
        Mat source = new Mat(1, 1, CvType.CV_8U, new Scalar(Imgproc.GC_PR_FGD));
        Rect rect = new Rect(tl, br);

        Mat foreground = new Mat(img.size(), CvType.CV_8UC3,new Scalar(255, 255, 255));
        Mat finalForeground = new Mat(fgSize, CvType.CV_8UC3,new Scalar(255, 255, 255));

        //segment image and get foreground
        Imgproc.grabCut(img, firstMask, rect, bgModel, fgModel,1, Imgproc.GC_INIT_WITH_RECT);
        Core.compare(firstMask, source, firstMask, Core.CMP_EQ);
        img.copyTo(foreground, firstMask);

        //copy to smaller matrix for less memory and faster computation
        Rect foregroundPosition = new Rect(x1, y1, width, height);
        Mat dataHolder = foreground.submat(foregroundPosition).clone();
        dataHolder.copyTo(finalForeground);

        img.release();
        firstMask.release();
        source.release();
        bgModel.release();
        fgModel.release();
        source.release();
        foreground.release();
        dataHolder.release();

        return finalForeground;
    }

    //https://docs.opencv.org/2.4/doc/tutorials/imgproc/histograms/histogram_calculation/histogram_calculation.html
    //LAB color space is used
    private Mat getHistogram(Mat image){
        Mat grayScale = new Mat();
        Mat mask = new Mat();
        Mat b_hist = new Mat(), g_hist = new Mat(), r_hist = new Mat();

        //compute mask
        Imgproc.cvtColor(image,grayScale,Imgproc.COLOR_BGR2GRAY);
        Imgproc.threshold(grayScale,mask,254,255,Imgproc.THRESH_BINARY_INV);

        //split channels
        List<Mat> bgr_planes  = new ArrayList<>();
        split(image, bgr_planes);

        //histogram settings
        MatOfInt channels = new MatOfInt(0);
        MatOfInt histSize = new MatOfInt(256);
        MatOfFloat ranges = new MatOfFloat(0f, 256f);

        List<Mat> planesList = new ArrayList<>();

        //calculate histogram
        planesList.add(bgr_planes.get(0));
        Imgproc.calcHist(planesList, channels, mask, b_hist, histSize, ranges, false);
        planesList.remove(0);

        planesList.add(bgr_planes.get(1));
        Imgproc.calcHist(planesList, channels, mask, g_hist, histSize, ranges, false);
        planesList.remove(0);

        planesList.add(bgr_planes.get(2));
        Imgproc.calcHist(planesList, channels, mask, r_hist, histSize, ranges, false);

        //for storing/writing
        b_hist.reshape(1,1);
        g_hist.reshape(1,1);
        r_hist.reshape(1,1);

        Mat feature = new Mat();
        feature.push_back(b_hist);
        feature.push_back(g_hist);
        feature.push_back(r_hist);

        return feature;
    }

    private Mat getHuMoments(Mat image){
        Mat grayScale = new Mat();
        Imgproc.cvtColor(image, grayScale, Imgproc.COLOR_BGR2GRAY);

        Mat threshold_output = new Mat();
        List<MatOfPoint> contours = new ArrayList<>();

        ///to get the outline of the object
        Imgproc.threshold(grayScale, threshold_output, 254, 255, Imgproc.THRESH_BINARY_INV);

        ///find contours
        Imgproc.findContours(threshold_output, contours, new Mat(), Imgproc.RETR_EXTERNAL, Imgproc.CHAIN_APPROX_SIMPLE);

        //find largest contour
        double largest_area = 0;
        int index = 0;
        for( int i = 0; i< contours.size(); i++ ){
            double compare = Imgproc.contourArea( contours.get(i) );
            if( compare > largest_area ){
                largest_area = compare;
                index = i;
            }
        }

        //for getting shape descriptor
        Moments momentsHolder;
        momentsHolder = Imgproc.moments(contours.get(index), false);

        Mat hu = new Mat();
        Imgproc.HuMoments(momentsHolder, hu);

        return hu;
    }

    private ArrayList<Double> getGaborWavelets(Mat image){
        Mat imageGray = new Mat();
        Mat imageFloat = new Mat();
        Mat kernelReal;
        Mat kernelImag;
        Mat dest = new Mat();
        ArrayList<Mat> destArray  = new ArrayList<>();

        double ksize = 5;
        double sigma = 1;
        int theta[] = new int[4];
        theta[0] = 0;
        theta[1] = 45;
        theta[2] = 90;
        theta[3] = 135;
        double gamma = 0.5;
        double lambda = 4;

        Imgproc.cvtColor(image, imageGray, Imgproc.COLOR_BGR2GRAY);
        imageGray.convertTo(imageFloat, CV_32F);

        //getting real and imaginary part
        for (int i = 0; i<4; i++){
            kernelReal = Imgproc.getGaborKernel(new org.opencv.core.Size(ksize,ksize), sigma, theta[i], lambda, gamma, 0, CV_32F);
            kernelImag = Imgproc.getGaborKernel(new org.opencv.core.Size(ksize,ksize), sigma, theta[i], lambda, gamma, 3.14159265359/2, CV_32F);

            Imgproc.filter2D(imageFloat, dest, CV_32F, kernelReal);
            destArray.add(dest.clone());

            Imgproc.filter2D(imageFloat, dest, CV_32F, kernelImag);
            destArray.add(dest.clone());
        }

        //for getting the mean and variance
        Mat magnitude1 = new Mat(image.rows(), image.cols(), CV_32F);
        Mat magnitude2 = new Mat(image.rows(), image.cols(), CV_32F);
        Mat magnitude3 = new Mat(image.rows(), image.cols(), CV_32F);
        Mat magnitude4 = new Mat(image.rows(), image.cols(), CV_32F);

        double mean1 = 0.0, mean2 = 0.0, mean3 = 0.0, mean4 = 0.0;
        double variance1 = 0.0, variance2 = 0.0, variance3 = 0.0, variance4 = 0.0;
        double holder1 = 0.0, holder2 = 0.0, holder3 = 0.0, holder4 = 0.0;
        double divisor = (image.rows() * image.cols());

        for(int i = 0; i < image.rows(); i++){
            for(int j = 0; j < image.cols(); j++){
                holder1 = sqrt((destArray.get(0).get(i,j)[0] * destArray.get(0).get(i,j)[0]) + (destArray.get(1).get(i,j)[0] * destArray.get(1).get(i,j)[0]));
                magnitude1.put(i,j,holder1);
                mean1 += holder1;

                holder2 = sqrt((destArray.get(2).get(i,j)[0] * destArray.get(2).get(i,j)[0]) + (destArray.get(3).get(i,j)[0] * destArray.get(3).get(i,j)[0]));
                magnitude2.put(i,j,holder2);
                mean2 += holder2;

                holder3 = sqrt((destArray.get(4).get(i,j)[0] * destArray.get(4).get(i,j)[0]) + (destArray.get(5).get(i,j)[0] * destArray.get(5).get(i,j)[0]));
                magnitude3.put(i,j,holder3);
                mean3 += holder3;

                holder4 = sqrt((destArray.get(6).get(i,j)[0] * destArray.get(6).get(i,j)[0]) + (destArray.get(7).get(i,j)[0] * destArray.get(7).get(i,j)[0]));
                magnitude4.put(i,j,holder4);
                mean4 += holder4;
            }
        }

        mean1 = mean1 / divisor;
        mean2 = mean2 / divisor;
        mean3 = mean3 / divisor;
        mean4 = mean4 / divisor;

        holder1 = 0.0;
        holder2 = 0.0;
        holder3 = 0.0;
        holder4 = 0.0;

        //variance
        for(int i = 0; i < image.rows(); i++){
            for(int j = 0; j < image.cols(); j++){
                holder1 = (magnitude1.get(i,j)[0] - mean1) * (magnitude1.get(i,j)[0] - mean1);
                variance1 += holder1;

                holder2 = (magnitude2.get(i,j)[0] - mean2) * (magnitude2.get(i,j)[0] - mean2);
                variance2 += holder2;

                holder3 = (magnitude3.get(i,j)[0] - mean3) * (magnitude3.get(i,j)[0] - mean3);
                variance3 += holder3;

                holder4 = (magnitude4.get(i,j)[0] - mean4) * (magnitude4.get(i,j)[0] - mean4);
                variance4 += holder4;
            }
        }

        variance1 = variance1 / divisor;
        variance2 = variance2 / divisor;
        variance3 = variance3 / divisor;
        variance4 = variance4 / divisor;

        ArrayList<Double> feature = new ArrayList<>();
        feature.add(mean1);
        feature.add(mean2);
        feature.add(mean3);
        feature.add(mean4);
        feature.add(variance1);
        feature.add(variance2);
        feature.add(variance3);
        feature.add(variance4);

        return feature;
    }

    private void writeToFile(Mat topPictureHistogram, Mat topPictureHuMoments, ArrayList<Double> topPictureTexture){
        File path = ProcessActivity.this.getFilesDir();
        File file = new File(path, "features.txt");
        FileOutputStream stream;

        try {
            stream = new FileOutputStream(file);

            int counter = 1;
            double holder;
            stream.write("1 ".getBytes());

            for(int i = 0; i < 768; i++){
                holder = topPictureHistogram.get(i,0)[0];
                stream.write(Integer.toString(counter).getBytes());
                stream.write(":".getBytes());
                stream.write(Double.toString(holder).getBytes());
                stream.write(" ".getBytes());
                counter++;
            }

            for(int i = 0; i < topPictureHuMoments.rows(); i++){
                holder = topPictureHuMoments.get(i,0)[0];
                stream.write(Integer.toString(counter).getBytes());
                stream.write(":".getBytes());
                stream.write(Double.toString(holder).getBytes());
                stream.write(" ".getBytes());
                counter++;
            }

            for(int i = 0; i < topPictureTexture.size(); i++){
                holder = topPictureTexture.get(i);
                stream.write(Integer.toString(counter).getBytes());
                stream.write(":".getBytes());
                stream.write(Double.toString(holder).getBytes());
                stream.write(" ".getBytes());
                counter++;
            }

            stream.write("\n".getBytes());

            stream.close();

            Log.d("debug", "after writing");
        }

        catch (IOException e){

        }
    }

    private void readyFiles(){
        String directoryPath = getFilesDir().getAbsolutePath() + "/svm/";
        File svm = new File(directoryPath);

        if(!svm.exists()){
            svm.mkdir();
            copyFiles("svm-settings", directoryPath);
            copyFiles("svm-models/top-models", directoryPath);
            copyFiles("svm-models/underside-models", directoryPath);
        }
    }

    private void copyFiles(String assetFolder, String directoryPath ){
        AssetManager am = getAssets();

        try{
            String[] files = am.list(assetFolder);
            File output = new File(directoryPath + "/" + assetFolder);
            output.mkdirs();

            for(int i = 0; i < files.length; i++){
                try{
                    InputStream in = am.open(assetFolder + "/" + files[i]);
                    FileOutputStream out = new FileOutputStream(directoryPath + assetFolder + "/" + files[i]);

//                    Log.d("debug-writing path", directoryPath + assetFolder + "/" + files[i]);

                    byte buff[] = new byte[1024];
                    int read = 0;

                    while ((read = in.read(buff)) > 0) {
                        out.write(buff, 0, read);
                    }
                    in.close();
                    out.close();
                }
                catch (IOException e){
                }
            }
        }
        catch (Exception e){
        }
    }

    private void classify(){
        LibSVM svm = new LibSVM();

        //read features
        File path = ProcessActivity.this.getFilesDir();
        File file;
        FileInputStream in;
        String absolutePath = null;
        file = new File(path, "features.txt");
        absolutePath = file.getAbsolutePath();

        //scale
        String topScalingPath = getFilesDir().getAbsolutePath() + "/svm/svm-settings/top_settings.txt";
        List<String> options = new ArrayList<>();
        options.add("-r");
        options.add(topScalingPath);
        options.add(absolutePath);
        String optionsString = TextUtils.join(" ", options);
        svm.scale(optionsString, path + "/features.scaled");


        //classify
        File fileDirectory = new File(getFilesDir().getAbsolutePath()+"/svm/svm-models/top-models");
        File[] files = fileDirectory.listFiles();
        ArrayList<Double> percentage = new ArrayList<>();
        String outputText;
        String[] splitted;
        int length = 0;
        byte[] bytes = null;

        for(int i = 0; i < files.length; i++){
            svm.predict("-b 1 " + path + "/features.scaled " + files[i].getAbsolutePath() + " " + path + "/output");
            try{
                file = new File(path, "output");
                in = new FileInputStream(file);

                length = (int) file.length();
                bytes = new byte[length];
                try {
                    in.read(bytes);
                } finally {
                    in.close();
                }
            }
            catch (IOException e){}

            try{
                outputText = new String(bytes, "UTF-8");
                splitted = outputText.trim().split("\\s+");

                if(Integer.parseInt(splitted[1]) == 1)
                    percentage.add(Double.parseDouble(splitted[4]));

                else
                    percentage.add(Double.parseDouble(splitted[5]));

//                Log.d("debug-0",splitted[0]);
//                Log.d("debug-1",splitted[1]);
//                Log.d("debug-2",splitted[2]);
//                Log.d("debug-3",splitted[3]);
//                Log.d("debug-4",splitted[4]);
//                Log.d("debug-5",splitted[5]);

            }
            catch (Exception e){}

        }

        for(int i = 0; i < percentage.size(); i++)
            Log.d("debug - percentage", Double.toString(percentage.get(i)));
    }

}
