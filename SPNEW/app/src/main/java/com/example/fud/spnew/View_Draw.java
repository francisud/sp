package com.example.fud.spnew;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Point;
import android.util.AttributeSet;
import android.util.Log;
import android.view.Gravity;
import android.view.MotionEvent;
import android.view.View;
import android.widget.Toast;

import java.util.ArrayList;

/**
 * BASED ON:
 * https://github.com/rathodchintan/ResizableRectangleOverlay
 * https://stackoverflow.com/a/17807469
 **/

public class View_Draw extends View {

    int imageWidth, imageHeight, startingX, startingY, origWidth, origHeight;
    float scaleX, scaleY;
    float scaleX500, scaleY500;

    Point[] points = new Point[4];
    /**
     * point1 and point 3 are of same group and same as point 2 and point4
     */
    int groupId = -1;
    private ArrayList<ColorBall> colorballs = new ArrayList<ColorBall>();
    // array that holds the balls
    private int balID = 0;
    // variable to know what ball is being dragged
    Paint paint;
    Canvas canvas;

    Toast toast;
    Boolean first = true;

    public View_Draw(Context context) {
        super(context);
        paint = new Paint();
        setFocusable(true); // necessary for getting the touch events
        canvas = new Canvas();
    }

    public View_Draw(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
    }

    public View_Draw(Context context, AttributeSet attrs) {
        super(context, attrs);
        paint = new Paint();
        setFocusable(true); // necessary for getting the touch events
        canvas = new Canvas();
    }

    public void showToast(){
        toast.setGravity(Gravity.CENTER, 0, 0);
        toast.show();
    }

    public void closeToast(){
        toast.cancel();
    }

    public ArrayList getCoordinates(){
        ArrayList<Point> coordinates = new ArrayList<Point>();

        //to send notice to activity that user did not select bounding box
        if(points[0] == null){
            coordinates.add(new Point(0,0));
            coordinates.add(new Point(0,0));
            toast.setGravity(Gravity.CENTER, 0, 0);
            toast.show();
            return coordinates;
        }

        float left, top, right, bottom;
        left = points[0].x;
        top = points[0].y;
        right = points[0].x;
        bottom = points[0].y;
        for (int i = 1; i < points.length; i++) {
            left = left > points[i].x ? points[i].x:left;
            top = top > points[i].y ? points[i].y:top;
            right = right < points[i].x ? points[i].x:right;
            bottom = bottom < points[i].y ? points[i].y:bottom;
        }

        float xPercent, yPercent, xCoord, yCoord;

        left = left - startingX;
        top = top - startingY;

        right = right - startingX;
        bottom = bottom - startingY;

        //top left
        xPercent = left / imageWidth;
        yPercent = top / imageHeight;
        xCoord = xPercent * (origWidth*scaleX500);
        yCoord = yPercent * (origHeight*scaleY500);
        coordinates.add(new Point(Math.round(xCoord), Math.round(yCoord)));

        //bottom right
        xPercent = right / imageWidth;
        yPercent = bottom / imageHeight;
        xCoord = xPercent * (origWidth*scaleX500);
        yCoord = yPercent * (origHeight*scaleY500);
        coordinates.add(new Point(Math.round(xCoord), Math.round(yCoord)));

        coordinates.add(new Point(Math.round(left), Math.round(top)));
        coordinates.add(new Point(Math.round(right), Math.round(bottom)));

        return coordinates;
    }

    public float[] getScaling(){
        float[] holder = {scaleX500,scaleY500};
        return holder;
    }

    public void getDimensions(int imageWidth, int imageHeight, int startingX, int startingY, int origWidth, int origHeight, float scaleX, float scaleY, float scaleX500, float scaleY500){
        this.imageWidth  = imageWidth;
        this.imageHeight = imageHeight;
        this.startingX   = startingX;
        this.startingY   = startingY;
        this.origWidth   = origWidth;
        this.origHeight  = origHeight;
        this.scaleX = scaleX;
        this.scaleY = scaleY;
        this.scaleX500 = scaleX500;
        this.scaleY500 = scaleY500;
    }

    // the method that draws the balls
    @Override
    protected void onDraw(Canvas canvas) {
        if(first){
            toast = Toast.makeText(this.getContext(), "Please select the fungi using the smallest bounding box possible", Toast.LENGTH_LONG);
            toast.setGravity(Gravity.CENTER, 0, 0);
            toast.show();
            first = false;
        }

        if(points[3]==null) //point4 null when user did not touch and move on screen.
            return;
        int left, top, right, bottom;
        left = points[0].x;
        top = points[0].y;
        right = points[0].x;
        bottom = points[0].y;
        for (int i = 1; i < points.length; i++) {
            left = left > points[i].x ? points[i].x:left;
            top = top > points[i].y ? points[i].y:top;
            right = right < points[i].x ? points[i].x:right;
            bottom = bottom < points[i].y ? points[i].y:bottom;
        }
        paint.setAntiAlias(true);
        paint.setDither(true);
        paint.setStrokeJoin(Paint.Join.ROUND);
        paint.setStrokeWidth(5);

        //draw stroke
        paint.setStyle(Paint.Style.STROKE);
        paint.setColor(Color.parseColor("#AADB1255"));
        paint.setStrokeWidth(2);
        canvas.drawRect(
                left,
                top,
                right,
                bottom, paint);
        //fill the rectangle
        paint.setStyle(Paint.Style.FILL);
        paint.setColor(Color.parseColor("#55DB1255"));
        paint.setStrokeWidth(0);
        canvas.drawRect(
                left,
                top,
                right,
                bottom, paint);

        // draw the balls on the canvas
        paint.setColor(Color.BLUE);
        paint.setTextSize(18);
        paint.setStrokeWidth(0);
        for (int i =0; i < colorballs.size(); i ++) {
            ColorBall ball = colorballs.get(i);

            canvas.drawBitmap(ball.getBitmap(), ball.getX() - ball.getWidthOfBall()/2, ball.getY() - ball.getHeightOfBall()/2, paint);
        }
    }

    // events when touching the screen
    public boolean onTouchEvent(MotionEvent event) {
        toast.cancel();

        int eventaction = event.getAction();

        int X = (int) event.getX();
        int Y = (int) event.getY();


        if(X > startingX + imageWidth){
            return false;
        }

        else if(X < startingX){
            return false;
        }

        else if(Y > startingY + imageHeight){
            return false;
        }

        else if(Y < startingY){
            return false;
        }

        switch (eventaction) {

            case MotionEvent.ACTION_DOWN: // touch down so check if the finger is on
                // a ball
                if (points[0] == null) {
                    //initialize rectangle.
                    points[0] = new Point();
                    points[0].x = X;
                    points[0].y = Y;

                    points[1] = new Point();
                    points[1].x = X;
                    points[1].y = Y + 30;

                    points[2] = new Point();
                    points[2].x = X + 30;
                    points[2].y = Y + 30;

                    points[3] = new Point();
                    points[3].x = X +30;
                    points[3].y = Y;

                    balID = 2;
                    groupId = 1;
                    // declare each ball with the ColorBall class
                    int count = 0;
                    for (Point pt : points) {
                        colorballs.add(new ColorBall(getContext(), R.drawable.ball, pt, count));
                        count++;
                    }
                } else {
                    //resize rectangle
                    balID = -1;
                    groupId = -1;
                    for (int i = colorballs.size()-1; i>=0; i--) {
                        ColorBall ball = colorballs.get(i);
                        // check if inside the bounds of the ball (circle)
                        // get the center for the ball
                        int centerX = ball.getX() + ball.getWidthOfBall()/2;
                        int centerY = ball.getY() + ball.getHeightOfBall()/2;
                        paint.setColor(Color.CYAN);
                        // calculate the radius from the touch to the center of the
                        // ball
                        double radCircle = Math
                                .sqrt((double) (((centerX - X) * (centerX - X)) + (centerY - Y)
                                        * (centerY - Y)));

                        if (radCircle < ball.getWidthOfBall()) {

                            balID = ball.getID();
                            if (balID == 1 || balID == 3) {
                                groupId = 2;
                            } else {
                                groupId = 1;
                            }
                            invalidate();
                            break;
                        }
                        invalidate();
                    }
                }
                break;

            case MotionEvent.ACTION_MOVE: // touch drag with the ball

                if (balID > -1) {
                    // move the balls the same as the finger

                    if(X >= startingX + imageWidth){
                        colorballs.get(balID).setX(startingX + imageWidth);
                        colorballs.get(balID).setY(Y);
                    }

                    else if(X <= startingX){
                        colorballs.get(balID).setX(startingX);
                        colorballs.get(balID).setY(Y);
                    }

                    else if(Y >= startingY + imageHeight){
                        colorballs.get(balID).setX(X);
                        colorballs.get(balID).setY(startingY + imageHeight);
                    }

                    else if(Y <= startingY){
                        colorballs.get(balID).setX(X);
                        colorballs.get(balID).setY(startingY);
                    }

                    else{
                        colorballs.get(balID).setX(X);
                        colorballs.get(balID).setY(Y);
                    }


                    paint.setColor(Color.CYAN);
                    if (groupId == 1) {
                        colorballs.get(1).setX(colorballs.get(0).getX());
                        colorballs.get(1).setY(colorballs.get(2).getY());
                        colorballs.get(3).setX(colorballs.get(2).getX());
                        colorballs.get(3).setY(colorballs.get(0).getY());
                    } else {
                        colorballs.get(0).setX(colorballs.get(1).getX());
                        colorballs.get(0).setY(colorballs.get(3).getY());
                        colorballs.get(2).setX(colorballs.get(3).getX());
                        colorballs.get(2).setY(colorballs.get(1).getY());
                    }

                    invalidate();
                }

                break;

            case MotionEvent.ACTION_UP:
                // touch drop - just do things here after dropping

                break;
        }
        // redraw the canvas
        invalidate();
        return true;
    }


    public static class ColorBall {

        Bitmap bitmap;
        Context mContext;
        Point point;
        int id;

        public ColorBall(Context context, int resourceId, Point point, int count) {
            this.id = count;
            bitmap = BitmapFactory.decodeResource(context.getResources(),
                    resourceId);
            mContext = context;
            this.point = point;
        }

        public int getWidthOfBall() {
            return bitmap.getWidth();
        }

        public int getHeightOfBall() {
            return bitmap.getHeight();
        }

        public Bitmap getBitmap() {
            return bitmap;
        }

        public int getX() {
            return point.x;
        }

        public int getY() {
            return point.y;
        }

        public int getID() {
            return id;
        }

        public void setX(int x) {
            point.x = x;
        }

        public void setY(int y) {
            point.y = y;
        }
    }
}
