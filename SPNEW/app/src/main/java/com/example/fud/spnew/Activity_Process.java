package com.example.fud.spnew;

import android.app.AlertDialog;
import android.app.FragmentManager;
import android.content.ContentValues;
import android.content.DialogInterface;
import android.content.Intent;
import android.database.sqlite.SQLiteDatabase;
import android.app.ProgressDialog;
import android.content.res.AssetManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.text.TextUtils;
import android.util.Log;
import android.view.Gravity;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;

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
import org.opencv.core.Size;
import org.opencv.imgproc.Imgproc;
import org.opencv.imgproc.Moments;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collections;
import java.util.List;

import static java.lang.Math.cbrt;
import static java.lang.Math.sqrt;
import static org.opencv.core.CvType.CV_32F;
import static org.opencv.core.CvType.CV_8UC3;

import umich.cse.yctung.androidlibsvm.LibSVM;


public class Activity_Process extends AppCompatActivity {

    ProgressDialog progressDialog;

    String substrate =  null;
    Mat topPicture = null;
    ArrayList<Double> topPictureColorMoments = null;
    Mat topPictureHuMoments = null;
    ArrayList<Double> topPictureTexture = null;
    Uri topPhotoPath = null;
    ArrayList<android.graphics.Point> topCoords = null;
    ArrayList<Double> topPercentage = null;
    List<String> topSavingSpecies = new ArrayList<>();
    List<String> topSavingPercentage = new ArrayList<>();
    List<String> topSavingData = new ArrayList<>();
    float[] topScaling = null;

    Mat undersidePicture = null;
    ArrayList<Double> undersidePictureColorMoments = null;
    Mat undersidePictureHuMoments = null;
    ArrayList<Double> undersidePictureTexture = null;
    Uri bottomPhotoPath = null;
    ArrayList<android.graphics.Point> bottomCoords = null;
    ArrayList<Double> bottomPercentage = null;
    List<String> bottomSavingSpecies = new ArrayList<>();
    List<String> bottomSavingPercentage = new ArrayList<>();
    List<String> bottomSavingData = new ArrayList<>();
    float[] bottomScaling = null;

    //FOR LOADING OPENCV
    private BaseLoaderCallback mLoaderCallback = new BaseLoaderCallback(this) {
        @Override
        public void onManagerConnected(int status) {
            switch (status) {
                case LoaderCallbackInterface.SUCCESS:
                {
                    progressDialog = new ProgressDialog(Activity_Process.this);
                    new AsyncClassifyTask().execute();
                } break;
                default:
                {
                    super.onManagerConnected(status);
                } break;
            }
        }
    };

    @Override
    public void onResume()
    {
        super.onResume();
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_process);
        OpenCVLoader.initAsync(OpenCVLoader.OPENCV_VERSION_3_3_0, this, mLoaderCallback);
    }

    private class AsyncClassifyTask extends AsyncTask<String, Void, Void>
    {
        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            progressDialog.setMessage("Classifying, please wait...");
            progressDialog.show();
        }

        @Override
        protected Void doInBackground(String... params) {
            start();
            return null;
        }
        @Override
        protected void onPostExecute(Void result) {
            if(topPicture != null)
                displayResults(topPhotoPath, topPercentage, 0);

            if(undersidePicture != null)
                displayResults(bottomPhotoPath, bottomPercentage, 1);

            //add button
            LinearLayout layout = (LinearLayout) findViewById(R.id.layout);
            Button button = new Button(Activity_Process.this);
            button.setLayoutParams(new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.WRAP_CONTENT));
            button.setText("Done");
            button.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {

                    final AlertDialog.Builder builder = new AlertDialog.Builder(Activity_Process.this);
                    builder.setMessage(R.string.save_data);

                    builder.setPositiveButton(R.string.save, new DialogInterface.OnClickListener() {
                        public void onClick(DialogInterface dialog, int id) {
                            if(saveClassified()){
                                dialog.dismiss();

                                //clean activities
                                Intent intent = new Intent(getApplicationContext(), Activity_Main.class);
                                intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                                startActivity(intent);
                            }
                        }
                    });
                    builder.setNegativeButton(R.string.no, new DialogInterface.OnClickListener() {
                        public void onClick(DialogInterface dialog, int id) {
                            dialog.dismiss();

                            //clean activities
                            Intent intent = new Intent(getApplicationContext(), Activity_Main.class);
                            intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                            startActivity(intent);
                        }
                    });

                    AlertDialog dialog = builder.create();
                    dialog.show();
                }
            });
            layout.addView(button);

            progressDialog.dismiss();
        }
    }

    private void start(){
        readySVMFiles();

        Bundle extras = getIntent().getExtras();

        substrate = extras.getString("substrate");

        if(getIntent().hasExtra("topPhotoPath")){
            topPhotoPath = Uri.parse(extras.getString("topPhotoPath"));
            topCoords = (ArrayList<android.graphics.Point>) getIntent().getSerializableExtra("topCoords");

            topPicture = readPicture(topPhotoPath);
            topScaling = (float[]) getIntent().getSerializableExtra("topScaling");
            topPicture = imageSegmentation(topCoords, topPicture, topScaling);
            topPictureColorMoments = getColorMoments(topPicture);
            topPictureHuMoments = getHuMoments(topPicture);
            topPictureTexture = getGaborWavelets(topPicture);
            writeToFile(topPictureColorMoments,topPictureHuMoments,topPictureTexture,substrate,0);
            topPercentage = classify(0);
        }

        if(getIntent().hasExtra("bottomPhotoPath")){
            bottomPhotoPath = Uri.parse(extras.getString("bottomPhotoPath"));
            bottomCoords = (ArrayList<android.graphics.Point>) getIntent().getSerializableExtra("bottomCoords");

            undersidePicture = readPicture(bottomPhotoPath);
            bottomScaling = (float[]) getIntent().getSerializableExtra("bottomScaling");
            undersidePicture = imageSegmentation(bottomCoords, undersidePicture, bottomScaling);
            undersidePictureColorMoments = getColorMoments(undersidePicture);
            undersidePictureHuMoments = getHuMoments(undersidePicture);
            undersidePictureTexture = getGaborWavelets(undersidePicture);
            writeToFile(undersidePictureColorMoments,undersidePictureHuMoments,undersidePictureTexture,substrate,1);
            bottomPercentage = classify(1);
        }
    }

    //based on https://www.journaldev.com/10416/android-listview-with-custom-adapter-example-tutorial
    private void displayResults(Uri photoPath, ArrayList<Double> percentage, int which) {
        //read image
        Mat picture = readPicture(photoPath);
        Imgproc.cvtColor(picture, picture, Imgproc.COLOR_BGR2RGB);
        Bitmap bm = Bitmap.createBitmap(picture.cols(), picture.rows(), Bitmap.Config.ARGB_8888);
        Utils.matToBitmap(picture, bm);

        //display image
        ImageView iv = new ImageView(this);
        LinearLayout.LayoutParams lp1 = new LinearLayout.LayoutParams((int) getResources().getDimension(R.dimen.content_process_width), (int) getResources().getDimension(R.dimen.content_process_height));
        lp1.gravity = Gravity.CENTER_HORIZONTAL;
        iv.setLayoutParams(lp1);
        iv.setImageBitmap(bm);

        final ArrayList<Class_ResultRow> rrc;
        Adapter_Result adapter;

        String[] species = getResources().getStringArray(R.array.species_array);
        Double index = null;

        ListView listView = new ListView(this);
        LinearLayout.LayoutParams lp2 = new LinearLayout.LayoutParams(
                ActionBar.LayoutParams.MATCH_PARENT,
                (int) getResources().getDimension(R.dimen.zerodp),
                1.0f);
        listView.setLayoutParams(lp2);

        //add all species and percentage
        rrc = new ArrayList<>();
        for(int i = 0; i < 10; i = i + 2){
            index = percentage.get(i);
            rrc.add(new Class_ResultRow(species[index.intValue()].toString(), percentage.get(i+1).toString()));

            if(which == 0){
                topSavingSpecies.add(species[index.intValue()].toString());
                topSavingPercentage.add(percentage.get(i+1).toString());
            }

            if(which == 1){
                bottomSavingSpecies.add(species[index.intValue()].toString());
                bottomSavingPercentage.add(percentage.get(i+1).toString());
            }
        }

        adapter = new Adapter_Result(rrc, Activity_Process.this);

        listView.setAdapter(adapter);
        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                Class_ResultRow picked = rrc.get(position);
                showDetails(picked.getSpecies());
            }
        });

        LinearLayout layout = (LinearLayout) findViewById(R.id.layout);
        layout.addView(iv);
        layout.addView(listView);
    }

    //dialog for showing details
    private void showDetails(String species){
        FragmentManager fm = Activity_Process.this.getFragmentManager();
        Fragment_Details details = Fragment_Details.newInstance(species);
        details.show(getSupportFragmentManager(), "dialog");
    }


    public boolean saveClassified(){
        Helper_Database helperDatabase = new Helper_Database(this);
        SQLiteDatabase db = helperDatabase.getReadableDatabase();
        ContentValues values = new ContentValues();

        //get date
        String date = Calendar.getInstance().getTime().toString();

        byte[] top_picture = null;
        int top_picture_type = -1;
        int top_picture_width = -1;
        int top_picture_height = -1;
        String top_species = null;
        String top_percentage = null;
        String top_data = null;

        byte[] underside_picture = null;
        int underside_picture_type = -1;
        int underside_picture_width = -1;
        int underside_picture_height = -1;
        String underside_species = null;
        String underside_percentage = null;
        String underside_data = null;

        if(topPhotoPath != null){
            //read image and convert to blob
            Mat image = readPicture(topPhotoPath);
            long nbytes = image.total() * image.elemSize();
            top_picture = new byte[ (int)nbytes ];
            image.get(0, 0,top_picture);

            top_picture_type = image.type();
            top_picture_width = image.cols();
            top_picture_height = image.rows();

            //top species, percentage, numerical data
            top_species = TextUtils.join(",", topSavingSpecies);
            top_percentage = TextUtils.join(",", topSavingPercentage);
            top_data = TextUtils.join("", topSavingData);
        }

        if(bottomPhotoPath != null){
            //read image and convert to blob
            Mat image = readPicture(bottomPhotoPath);
            long nbytes = image.total() * image.elemSize();
            underside_picture = new byte[ (int)nbytes ];
            image.get(0, 0,underside_picture);

            underside_picture_type = image.type();
            underside_picture_width = image.cols();
            underside_picture_height = image.rows();

            //underside species, percentage, numerical data
            underside_species = TextUtils.join(",", bottomSavingSpecies);
            underside_percentage = TextUtils.join(",", bottomSavingPercentage);
            underside_data = TextUtils.join("", bottomSavingData);
        }

        values.put("date", date);

        values.put("top_picture",top_picture);
        values.put("top_picture_type",top_picture_type);
        values.put("top_picture_width",top_picture_width);
        values.put("top_picture_height",top_picture_height);
        values.put("top_species",top_species);
        values.put("top_percentage",top_percentage);
        values.put("top_data",top_data);

        values.put("underside_picture",underside_picture);
        values.put("underside_picture_type",underside_picture_type);
        values.put("underside_picture_width",underside_picture_width);
        values.put("underside_picture_height",underside_picture_height);
        values.put("underside_species",underside_species);
        values.put("underside_percentage",underside_percentage);
        values.put("underside_data",underside_data);

        long checker = db.insert("identified", null, values);
        db.close();

        return true;
    }


    //based on https://stackoverflow.com/a/39085038
    private Mat readPicture(Uri photoPath){
        InputStream stream = null;
        try {
            stream = getContentResolver().openInputStream(photoPath);
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }

        BitmapFactory.Options bmpFactoryOptions = new BitmapFactory.Options();
        bmpFactoryOptions.inPreferredConfig = Bitmap.Config.ARGB_8888;

        Bitmap bmp = BitmapFactory.decodeStream(stream, null, bmpFactoryOptions);
        Mat ImageMat = new Mat(bmp.getWidth(), bmp.getHeight(), CV_8UC3);
        Utils.bitmapToMat(bmp, ImageMat);
        Imgproc.cvtColor(ImageMat, ImageMat, Imgproc.COLOR_RGBA2BGR);

        return ImageMat;
    }

    private Mat imageSegmentation(ArrayList<android.graphics.Point> coords, Mat img, float[] scaling){
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
        Imgproc.resize(img, img, new Size(), scaling[0], scaling[1], Imgproc.INTER_CUBIC);
//        Imgproc.resize(img, img, new Size(500,500), 0, 0, Imgproc.INTER_CUBIC);

        Mat firstMask = new Mat();
        Mat bgModel = new Mat();
        Mat fgModel = new Mat();
        Mat source = new Mat(1, 1, CvType.CV_8U, new Scalar(Imgproc.GC_PR_FGD));
        Rect rect = new Rect(tl, br);

        Mat foreground = new Mat(img.size(), CV_8UC3,new Scalar(255, 255, 255));
        Mat finalForeground = new Mat(fgSize, CV_8UC3,new Scalar(255, 255, 255));

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

    private ArrayList<Double> getColorMoments(Mat image){
        int totalPixels = 0;

        double b_mean = 0;
        double g_mean = 0;
        double r_mean = 0;

        double b_stddev = 0;
        double g_stddev = 0;
        double r_stddev = 0;

        double b_skewness = 0;
        double g_skewness = 0;
        double r_skewness = 0;

        double[] pixel;

        //computing total pixels
        for(int i = 0; i < image.rows(); i++){
            for(int j = 0; j < image.cols(); j++){
                pixel = image.get(i,j);

                //if not background
                if(pixel[0] != 255 && pixel[1] != 255 && pixel[2] != 255){
                    totalPixels += 1;
                }
            }
        }

        //computing means
        for(int i = 0; i < image.rows(); i++){
            for(int j = 0; j < image.cols(); j++){
                pixel = image.get(i,j);

                if(pixel[0] != 255 && pixel[1] != 255 && pixel[2] != 255){
                    b_mean += pixel[0];
                    g_mean += pixel[1];
                    r_mean += pixel[2];
                }
            }
        }

        b_mean = b_mean / totalPixels;
        g_mean = g_mean / totalPixels;
        r_mean = r_mean / totalPixels;

        b_mean = (double)Math.round(b_mean * 1000d) / 1000d;
        g_mean = (double)Math.round(g_mean * 1000d) / 1000d;
        r_mean = (double)Math.round(r_mean * 1000d) / 1000d;

        //computing stddev and skewness
        for(int i = 0; i < image.rows(); i++){
            for(int j = 0; j < image.cols(); j++){
                pixel = image.get(i,j);

                if(pixel[0] != 255 && pixel[1] != 255 && pixel[2] != 255){
                    b_stddev += (pixel[0] - b_mean) * (pixel[0] - b_mean);
                    g_stddev += (pixel[1] - g_mean) * (pixel[1] - g_mean);
                    r_stddev += (pixel[2] - r_mean) * (pixel[2] - r_mean);

                    b_skewness += (pixel[0] - b_mean) * (pixel[0] - b_mean) * (pixel[0] - b_mean);
                    g_skewness += (pixel[1] - g_mean) * (pixel[1] - g_mean) * (pixel[1] - g_mean);
                    r_skewness += (pixel[2] - r_mean) * (pixel[2] - r_mean) * (pixel[2] - r_mean);
                }
            }
        }

        b_stddev /= totalPixels;
        g_stddev /= totalPixels;
        r_stddev /= totalPixels;

        b_stddev = (double)Math.round(b_stddev * 1000d) / 1000d;
        g_stddev = (double)Math.round(g_stddev * 1000d) / 1000d;
        r_stddev = (double)Math.round(r_stddev * 1000d) / 1000d;

        b_stddev = sqrt(b_stddev);
        g_stddev = sqrt(g_stddev);
        r_stddev = sqrt(r_stddev);

        b_stddev = (double)Math.round(b_stddev * 1000d) / 1000d;
        g_stddev = (double)Math.round(g_stddev * 1000d) / 1000d;
        r_stddev = (double)Math.round(r_stddev * 1000d) / 1000d;

        b_skewness /= totalPixels;
        g_skewness /= totalPixels;
        r_skewness /= totalPixels;

        b_skewness = (double)Math.round(b_skewness * 1000d) / 1000d;
        g_skewness = (double)Math.round(g_skewness * 1000d) / 1000d;
        r_skewness = (double)Math.round(r_skewness * 1000d) / 1000d;

        b_skewness = cbrt(b_skewness);
        g_skewness = cbrt(g_skewness);
        r_skewness = cbrt(r_skewness);

        b_skewness = (double)Math.round(b_skewness * 1000d) / 1000d;
        g_skewness = (double)Math.round(g_skewness * 1000d) / 1000d;
        r_skewness = (double)Math.round(r_skewness * 1000d) / 1000d;

        ArrayList<Double> feature = new ArrayList<>();
        feature.add(b_mean);
        feature.add(g_mean);
        feature.add(r_mean);

        feature.add(b_stddev);
        feature.add(g_stddev);
        feature.add(r_stddev);

        feature.add(b_skewness);
        feature.add(g_skewness);
        feature.add(r_skewness);

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

        mean1 = (double)Math.round(mean1 * 1000d) / 1000d;
        mean2 = (double)Math.round(mean2 * 1000d) / 1000d;
        mean3 = (double)Math.round(mean3 * 1000d) / 1000d;
        mean4 = (double)Math.round(mean4 * 1000d) / 1000d;

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

        variance1 = (double)Math.round(variance1 * 1000d) / 1000d;
        variance2 = (double)Math.round(variance2 * 1000d) / 1000d;
        variance3 = (double)Math.round(variance3 * 1000d) / 1000d;
        variance4 = (double)Math.round(variance4 * 1000d) / 1000d;

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

    private void writeToFile(ArrayList<Double> topPictureColorMoments, Mat topPictureHuMoments, ArrayList<Double> topPictureTexture, String substrate, int which){
        File path = Activity_Process.this.getFilesDir();
        File file = new File(path, "features.txt");
        FileOutputStream stream;
        List<String> holderStrings = null;

        if(which == 0)
            holderStrings = topSavingData;
        if(which == 1)
            holderStrings = bottomSavingData;

        try {
            stream = new FileOutputStream(file);

            int counter = 1;
            double holder;
            String stringToWrite;
            stream.write("1 ".getBytes());
            holderStrings.add("1 ");

            for(int i = 0; i < topPictureColorMoments.size(); i++){
                holder = topPictureColorMoments.get(i);
                stringToWrite = Integer.toString(counter) + ":" + Double.toString(holder) + " ";
                stream.write(stringToWrite.getBytes());
                counter++;

                holderStrings.add(stringToWrite);
            }

            for(int i = 0; i < topPictureHuMoments.rows()-1; i++){
                holder = topPictureHuMoments.get(i,0)[0];
                stringToWrite = Integer.toString(counter) + ":" + Double.toString(holder) + " ";
                stream.write(stringToWrite.getBytes());
                counter++;

                holderStrings.add(stringToWrite);
            }

            for(int i = 0; i < topPictureTexture.size(); i++){
                holder = topPictureTexture.get(i);
                stringToWrite = Integer.toString(counter) + ":" + Double.toString(holder) + " ";
                stream.write(stringToWrite.getBytes());
                counter++;

                holderStrings.add(stringToWrite);
            }


            //write substrate
            stringToWrite = Integer.toString(counter) + ":" + substrate + "\n";
            stream.write(stringToWrite.getBytes());

            holderStrings.add(stringToWrite);

            stream.close();
        }

        catch (IOException e){

        }
    }

    private void readySVMFiles(){
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

    private ArrayList<Double> classify(int checker){
        LibSVM svm = new LibSVM();

        //read features
        File path = Activity_Process.this.getFilesDir();
        File file;
        FileInputStream in;
        String absolutePath = null;
        file = new File(path, "features.txt");
        absolutePath = file.getAbsolutePath();

        //scale
        String scalingPath;
        if(checker == 0)
            scalingPath = getFilesDir().getAbsolutePath() + "/svm/svm-settings/top_settings.txt";
        else
            scalingPath = getFilesDir().getAbsolutePath() + "/svm/svm-settings/underside_settings.txt";
        List<String> options = new ArrayList<>();
        options.add("-r");
        options.add(scalingPath);
        options.add(absolutePath);
        String optionsString = TextUtils.join(" ", options);
        svm.scale(optionsString, path + "/features.scaled");


        //classify
        File fileDirectory;
        if(checker == 0)
            fileDirectory = new File(getFilesDir().getAbsolutePath()+"/svm/svm-models/top-models");
        else
            fileDirectory = new File(getFilesDir().getAbsolutePath()+"/svm/svm-models/underside-models");
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
            }
            catch (Exception e){}

        }

        ArrayList<Double> toReturn = new ArrayList<>();
        int index;
        for(int i = 0; i < 5; i++){
            index = percentage.indexOf(Collections.max(percentage));
            toReturn.add((double)index);
            toReturn.add(percentage.get(index));
            percentage.remove(index);
        }

        return toReturn;
    }

}
