package com.example.janiszhang.indoorposition;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Matrix;
import android.graphics.Paint;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.Timer;
import java.util.TimerTask;

public class StepShowActivity extends AppCompatActivity {

    private Bitmap mBackgroundMap;
    private Bitmap mMark;
    private Bitmap mResultBitmap;
    private Canvas mCanvas;
    private Paint mPaint;
    private ImageView mIndoorMap;
    private TextView mSetpCount;
    private TextView mStepLength;
    private TextView mStepDegree;
    private TextView mStepCoordinate;
    private ObtainStepData mObtainStepData;
    private Button mStartButton;
    private Button mResetButton;
    private Button mStopButton;

    static int[] newCoords            = new int[2];
    static int[] curTouchCoords       = {0, 0};
    static float[] myCoords           = new float[2];
    long lastTimeclickBack = 0;

    // Time variables
    final long updateItemMilliTime = 40;
    final long backSpaceTimeIntervalMilliSecond = 1000;
    private Timer mTimer;
    TimerTask mTimetask;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);
        getWindow().addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);
        setContentView(R.layout.step_show);

        mIndoorMap = (ImageView) findViewById(R.id.image_map);

        mBackgroundMap = BitmapFactory.decodeResource(getResources(), R.drawable.indoor);
        mMark = BitmapFactory.decodeResource(getResources(), R.drawable.location_24_red);
        mMark = getResizedBitmap(mMark, 50, 24);

        mResultBitmap = Bitmap.createBitmap(mBackgroundMap.getWidth(), mBackgroundMap.getHeight(), mBackgroundMap.getConfig());
        Log.i("zhangbz", "bitmap width = " + mBackgroundMap.getWidth() + ", height = " + mBackgroundMap.getHeight());// bitmap width = 1149, height = 1875
        mCanvas = new Canvas(mResultBitmap);
        mPaint = new Paint();
        mPaint.setColor(Color.GREEN);
        mPaint.setStyle(Paint.Style.STROKE);
        mPaint.setStrokeWidth(7);

        mCanvas.drawBitmap(mBackgroundMap, new Matrix(), null);
        mCanvas.drawBitmap(mMark, 1150 / 4, (1875 / 5) * 3, mPaint);//暂定测试用起始点
        mIndoorMap.setImageBitmap(mResultBitmap);

        mSetpCount = (TextView) findViewById(R.id.text_view_step_count);
        mStepLength = (TextView) findViewById(R.id.text_view_step_length);
        mStepDegree = (TextView) findViewById(R.id.text_view_step_degree);
        mStepCoordinate = (TextView) findViewById(R.id.text_view_step_coordinate);

        mObtainStepData = new ObtainStepData(this, mSetpCount, mStepLength, mStepDegree, mStepCoordinate);

        mStartButton = (Button) findViewById(R.id.button_start);
        /**
         * obtainStepSetting
         * initPoints
         * obtainStep
         */
        mStartButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mObtainStepData.obtainStepSetting();
                mObtainStepData.initPoints();
                mObtainStepData.obtainStep();
            }
        });
        mResetButton = (Button) findViewById(R.id.button_reset);
        /**
         * correctStep
         * clearPoints
         * initPoints
         * stepViewgone
         */
        mResetButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mObtainStepData.correctStep();
                mObtainStepData.clearPoints();
                mObtainStepData.initPoints();
                mObtainStepData.stepViewGone();
            }
        });
        mStopButton = (Button) findViewById(R.id.button_stop);
        /**
         * stop
         */
        mStopButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mObtainStepData.stopStep();
            }
        });

        stepShowTaskSchedule(updateItemMilliTime);
//        mIndoorMap.setOnTouchListener(new View.OnTouchListener() {
//            @Override
//            public boolean onTouch(View v, MotionEvent event) {
//                int action = event.getAction();
//                float x = event.getX();
//                float y = event.getY();
//                mCanvas.drawBitmap(mBackgroundMap, new Matrix(), null);
//                mCanvas.drawBitmap(mMark, x, y, mPaint);
//                mIndoorMap.setImageBitmap(mResultBitmap);
//                return false;
//            }
//        });
    }

    /**
     * reset the size of Bitmap to another new size
     *
     * @param bm
     * @param newHeight
     * @param newWidth
     * @return
     */
    private Bitmap getResizedBitmap(Bitmap bm, int newHeight, int newWidth) {
        int width = bm.getWidth();
        int height = bm.getHeight();

        float scaleWidth = ((float) newWidth) / width;
        float scaleHeigth = ((float) newHeight) / height;
        Matrix matrix = new Matrix();
        matrix.postScale(scaleWidth, scaleHeigth);
        Bitmap resizedBitmap = Bitmap.createBitmap(bm, 0, 0, width, height, matrix, false);
        return resizedBitmap;
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        getWindow().clearFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        if(lastTimeclickBack <= 0) {
            Toast.makeText(this, "再按一次后退键退出应用", Toast.LENGTH_SHORT).show();
            lastTimeclickBack = System.currentTimeMillis();
        } else {
            long currentClickTime = System.currentTimeMillis();
            if(currentClickTime - lastTimeclickBack < backSpaceTimeIntervalMilliSecond) {
                finish();
                //kill the process of the App
                android.os.Process.killProcess(android.os.Process.myPid());
            } else {
                Toast.makeText(this, "再按一次后退键退出应用", Toast.LENGTH_SHORT).show();
                lastTimeclickBack = System.currentTimeMillis();
            }
        }
    }

    public void stepShowTaskSchedule(long milliTime) {
        mTimer = new Timer();
        mTimetask = new TimerTask(){
            @Override
            public void run() {
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        mCanvas.drawBitmap(mBackgroundMap, new Matrix(), null);
                        myCoords = ObtainStepData.getCurCoordsOfStep();
                        curTouchCoords = convertTouchCoordinates(myCoords);
                        recordTrajectory();
                        mCanvas.drawBitmap(mMark, curTouchCoords[0], curTouchCoords[1], mPaint);
                        mIndoorMap.setImageBitmap(mResultBitmap);
                    }
                });
            }
        };
        mTimer.schedule(mTimetask, 0, milliTime);
    }

    private void recordTrajectory() {
		/*
		 * draw the line of trajectory.
		 */
        ArrayList<CoordPoint> tmpPoints = ObtainStepData.getPoints();
        for (int i = 0; i < tmpPoints.size()-1; i++) {
            CoordPoint startPoint = tmpPoints.get(i);
            startPoint = convertTouchCoordinates(startPoint);
            CoordPoint endPoint = tmpPoints.get(i+1);
            endPoint = convertTouchCoordinates(endPoint);
            mCanvas.drawLine(startPoint.px, startPoint.py, endPoint.px, endPoint.py, mPaint);
        }
    }

    public int[] convertTouchCoordinates(float[] coors) {
		/*
		 * float[] : convert coordinate to fit on the screen of mobile.
		 */
        newCoords[0] = (int)(coors[0] * ((float) mCanvas.getWidth() / mIndoorMap.getRight()));
        newCoords[1] = (int)(coors[1] * ((float) mCanvas.getHeight() / mIndoorMap.getBottom()));
        return newCoords;
    }

    public CoordPoint convertTouchCoordinates(CoordPoint coors) {
		/*
		 * CoordPoint : convert coordinate to fit on the screen of mobile.
		 */
        float xtmp = coors.px * ((float) mCanvas.getWidth() / mIndoorMap.getRight());
        float ytmp = coors.py * ((float) mCanvas.getHeight() / mIndoorMap.getBottom());
        return new CoordPoint(xtmp, ytmp);
    }
}
