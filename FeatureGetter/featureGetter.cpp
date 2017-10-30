/*
https://stackoverflow.com/a/22141475 - GUI for getting the bounding rectangle for grabCut
functions related:
    void checkBoundary();
    void showImage();
    void onMouse( int event, int x, int y, int f, void* );

https://docs.opencv.org/2.4/doc/tutorials/imgproc/histograms/histogram_calculation/histogram_calculation.html - getting the LAB histogram
functions related:
    Mat getHistogram(Mat &image);

*/

#include "opencv2/imgproc/imgproc.hpp"
#include "opencv2/highgui/highgui.hpp"
#include <stdlib.h>
#include <stdio.h>
#include <vector>
#include <iostream>
#include <math.h>
#define SIZE 3

using namespace cv;
using namespace std;

Mat src,img,ROI;
Rect cropRect(0,0,0,0);
Point P1(0,0);
Point P2(0,0);
const char* winName="Input Image";
bool clicked=false;
int i=0;
char imgName[15];

void checkBoundary();
void showImage();
void onMouse( int event, int x, int y, int f, void* );
Mat imageSegmentation(Mat &image);
Mat getHistogram(Mat &image);
Mat getHuMoments(Mat &image);
Mat getGaborWavelets(Mat &image);

int main ( int argc, char** argv ){
    cout<<" Click and drag for Selection"<<endl<<endl;
    cout<<" Press 'Space Bar' to process the image"<<endl<<endl;
    src=imread(argv[1],1);

    namedWindow(winName,WINDOW_NORMAL);
    setMouseCallback(winName,onMouse,NULL );
    imshow(winName,src);

    while(1){
        char c = waitKey();     
        if(c == 32) break;      
    }

    Mat img = src.clone();    
    Mat foreground = imageSegmentation(img);
    Mat hist = getHistogram(foreground);
    Mat hu = getHuMoments(foreground);
    Mat gw = getGaborWavelets(foreground);

    cout<<" Image is processed. Open 'features.txt' to get numerical features."<<endl<<endl;

    waitKey();
    return 0;
}


void checkBoundary(){
    //check croping rectangle exceed image boundary
    if(cropRect.width>img.cols-cropRect.x)
        cropRect.width=img.cols-cropRect.x;

    if(cropRect.height>img.rows-cropRect.y)
        cropRect.height=img.rows-cropRect.y;

    if(cropRect.x<0)
        cropRect.x=0;

    if(cropRect.y<0)
        cropRect.height=0;
}

void showImage(){
    img=src.clone();
    checkBoundary();
    if(cropRect.width>0&&cropRect.height>0){
        ROI=src(cropRect);
        imshow("Bounding Box",ROI);
    }

    rectangle(img, cropRect, Scalar(0,255,0), 1, 8, 0 );
    imshow(winName,img);
}


void onMouse( int event, int x, int y, int f, void* ){
    switch(event){

        case  CV_EVENT_LBUTTONDOWN  :
                                        clicked=true;

                                        P1.x=x;
                                        P1.y=y;
                                        P2.x=x;
                                        P2.y=y;
                                        break;

        case  CV_EVENT_LBUTTONUP    :
                                        P2.x=x;
                                        P2.y=y;
                                        clicked=false;
                                        break;

        case  CV_EVENT_MOUSEMOVE    :
                                        if(clicked){
                                        P2.x=x;
                                        P2.y=y;
                                        }
                                        break;

        default                     :   break;


    }


    if(clicked){
     if(P1.x>P2.x){ cropRect.x=P2.x;
                       cropRect.width=P1.x-P2.x; }
        else {         cropRect.x=P1.x;
                       cropRect.width=P2.x-P1.x; }

        if(P1.y>P2.y){ cropRect.y=P2.y;
                       cropRect.height=P1.y-P2.y; }
        else {         cropRect.y=P1.y;
                       cropRect.height=P2.y-P1.y; }

    }

    showImage();
}


Mat imageSegmentation(Mat &img){
  //bounding rectangle
  Point tl = P1;
  Point br = P2;
  Rect rectangle(tl,br);
  
  //for smaller size matrix
  double width = P2.x - P1.x;
  double height = P2.y - P1.y;  
  Size fgSize = Size(width,height);   

  Mat firstMask = Mat();
  Mat bgModel = Mat();
  Mat fgModel = Mat();
  Mat source = Mat(1, 1, CV_8U, Scalar(GC_PR_FGD));
  Mat dst = Mat();
  Rect rect = Rect(tl, br);
  
  Mat foreground = Mat(img.size(), CV_8UC3,Scalar(255, 255, 255));
  Mat finalForeground = Mat(fgSize, CV_8UC3,Scalar(255, 255, 255));

  //segment and get foreground
  grabCut(img, firstMask, rect, bgModel, fgModel,1, GC_INIT_WITH_RECT);
  compare(firstMask, source, firstMask, CMP_EQ);
  img.copyTo(foreground, firstMask);    

  //copy to smaller matrix for less memory and faster computation
  Rect foregroundPosition(P1.x, P1.y, width, height);
  Mat dataHolder = foreground(foregroundPosition).clone();
  dataHolder.copyTo(finalForeground);

  // display result
  imshow("Segmented Image",finalForeground);
      
  return finalForeground;
}


Mat getHistogram(Mat &image) {
  Mat grayScale;
  Mat mask;

  //compute mask
  cvtColor(image,grayScale,CV_BGR2GRAY);
  threshold(grayScale,mask,254,255,THRESH_BINARY_INV);    

  /// Separate the image in 3 places ( B, G and R )
  vector<Mat> bgr_planes;
  split( image, bgr_planes );

  /// Establish the number of bins
  int histSize = 256;

  /// Set the ranges ( for B,G,R) )
  float range[] = { 0, 256 } ;
  const float* histRange = { range };

  Mat b_hist, g_hist, r_hist;

  /// Compute the histograms:
  calcHist( &bgr_planes[0], 1, 0, mask, b_hist, 1, &histSize, &histRange);
  calcHist( &bgr_planes[1], 1, 0, mask, g_hist, 1, &histSize, &histRange);
  calcHist( &bgr_planes[2], 1, 0, mask, r_hist, 1, &histSize, &histRange);

    /*
  // Draw the histograms for B, G and R
  int hist_w = 512; int hist_h = 400;
  int bin_w = cvRound( (double) hist_w/histSize );

  Mat histImage( hist_h, hist_w, CV_8UC3, Scalar( 0,0,0) );

  /// Normalize the result to [ 0, histImage.rows ]
  normalize(b_hist, b_hist, 0, histImage.rows, NORM_MINMAX, -1, Mat() );
  normalize(g_hist, g_hist, 0, histImage.rows, NORM_MINMAX, -1, Mat() );
  normalize(r_hist, r_hist, 0, histImage.rows, NORM_MINMAX, -1, Mat() );

  /// Draw for each channel
  for( int i = 1; i < histSize; i++ ){
      line( histImage, Point( bin_w*(i-1), hist_h - cvRound(b_hist.at<float>(i-1)) ) ,
                       Point( bin_w*(i), hist_h - cvRound(b_hist.at<float>(i)) ),
                       Scalar( 255, 0, 0), 2, 8, 0  );
      line( histImage, Point( bin_w*(i-1), hist_h - cvRound(g_hist.at<float>(i-1)) ) ,
                       Point( bin_w*(i), hist_h - cvRound(g_hist.at<float>(i)) ),
                       Scalar( 0, 255, 0), 2, 8, 0  );
      line( histImage, Point( bin_w*(i-1), hist_h - cvRound(r_hist.at<float>(i-1)) ) ,
                       Point( bin_w*(i), hist_h - cvRound(r_hist.at<float>(i)) ),
                       Scalar( 0, 0, 255), 2, 8, 0  );
  }

  /// Display
  imshow("calcHist Demo", histImage );
    */  
  
  b_hist.reshape(1,1);
  g_hist.reshape(1,1);
  r_hist.reshape(1,1);
  
  Mat feature;  
  feature.push_back(b_hist);
  feature.push_back(g_hist);
  feature.push_back(r_hist);
      
  return feature;
}


Mat getHuMoments(Mat &image){
  Mat grayScale = Mat();
  cvtColor(image, grayScale, COLOR_BGR2GRAY);     
  
  Mat threshold_output;
  vector<vector<Point> > contours;
  vector<Vec4i> hierarchy;

  //to get the outline of the object
  threshold(grayScale, threshold_output, 254, 255, THRESH_BINARY_INV);
      
  //find contours
  findContours(threshold_output, contours, hierarchy, CV_RETR_EXTERNAL, CV_CHAIN_APPROX_SIMPLE, Point(0, 0));                
          
  //find larget contour
  int largest_area = 0;
  int index = 0;            
  for( int i = 0; i< contours.size(); i++ ){
      double compare = contourArea( contours[i] );             
      if( compare > largest_area ){
          largest_area = compare;
          index = i;
      }            
  }                

  Moments mom = Moments();
  mom = moments(contours[index], false);

  Mat hu = Mat();
  HuMoments(mom, hu);
  
  /*
  Mat drawing = Mat::zeros( threshold_output.size(), CV_8UC3 );
  RNG rng(12345);        
  Scalar color = Scalar( rng.uniform(0, 255), rng.uniform(0,255), rng.uniform(0,255) );
  drawContours( drawing, contours, index, color, 2, 8, hierarchy, 0, Point() );
  imshow( "Contours", drawing );
  */
    
  return hu;
}

Mat getGaborWavelets(Mat &image){
  Mat imageGray = Mat();
  Mat imageFloat = Mat();
  Mat kernelReal = Mat();
  Mat kernelImag = Mat();
  Mat dest = Mat();
  vector<Mat> destArray;
  
  int ksize = 5;
  double sigma = 1;  
  int theta[4];
  theta[0] = 0;
  theta[1] = 45;
  theta[2] = 90;
  theta[3] = 135;  
  double gamma = 0.5;
  double lambda = 4;
  
  cvtColor(image, imageGray, COLOR_BGR2GRAY);
  imageGray.convertTo(imageFloat, CV_32F);
  
  for (int i = 0; i<4; i++){
    kernelReal = getGaborKernel(Size(ksize,ksize), sigma, theta[i], lambda, gamma, 0, CV_32F); 
    kernelImag = getGaborKernel(Size(ksize,ksize), sigma, theta[i], lambda, gamma, 3.14159265359/2, CV_32F);
    
    filter2D(imageFloat, dest, CV_32F, kernelReal);
    destArray.push_back(dest.clone());    
    
    filter2D(imageFloat, dest, CV_32F, kernelImag);  
    destArray.push_back(dest.clone());
  }
  
  Mat feature;
  float magnitude = 0;
  float squareRoot = 0;
  
  for(int i = 0; i < image.rows; i++){
      for(int j = 0; j < image.cols; j++){
          magnitude = (destArray[0].at<float>(i,j) * destArray[0].at<float>(i,j)) + (destArray[1].at<float>(i,j) * destArray[1].at<float>(i,j));
          squareRoot = sqrt (magnitude);
          feature.push_back(squareRoot);
          
          magnitude = (destArray[2].at<float>(i,j) * destArray[2].at<float>(i,j)) + (destArray[3].at<float>(i,j) * destArray[3].at<float>(i,j));
          squareRoot = sqrt (magnitude);
          feature.push_back(squareRoot);
          
          magnitude = (destArray[4].at<float>(i,j) * destArray[4].at<float>(i,j)) + (destArray[5].at<float>(i,j) * destArray[5].at<float>(i,j));
          squareRoot = sqrt (magnitude);
          feature.push_back(squareRoot);
          
          magnitude = (destArray[6].at<float>(i,j) * destArray[6].at<float>(i,j)) + (destArray[7].at<float>(i,j) * destArray[7].at<float>(i,j));
          squareRoot = sqrt (magnitude);
          feature.push_back(squareRoot);
      }
  }
  
  cout<<magnitude<<endl;
  cout<<squareRoot<<endl;
           
  return feature;
}
