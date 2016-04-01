package com.example.janiszhang.indoorposition;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Matrix;
import android.graphics.Paint;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.Timer;
import java.util.TimerTask;

/**
 * Created by janiszhang on 2016/3/28.
 */
public class ShowPositionFragment extends Fragment{

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

    static int[] newCoords = new int[2];
    static int[] curTouchCoords = {0, 0};
    static float[] myCoords = new float[2];

    // Time variables
    final long updateItemMilliTime = 40;
    private Timer mTimer;
    TimerTask mTimetask;

//    String rssFile = "/2Rss/rss.txt";
//    static ArrayList<Map.Entry<float[], float[]>> rssAndCoords = new ArrayList<Map.Entry<float[], float[]>>();

//    WifiManager wifiMg;
//    ObtainRssData obtainRssData;


//    @Override
//    public void onCreate(@Nullable Bundle savedInstanceState) {
//        super.onCreate(savedInstanceState);
//
//    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.step_show, container, false);
        mIndoorMap = (ImageView) view.findViewById(R.id.image_map);

        mBackgroundMap = BitmapFactory.decodeResource(getResources(), R.drawable.indoor);
        mMark = BitmapFactory.decodeResource(getResources(), R.drawable.location_24_red);
//        mMark = getResizedBitmap(mMark, 50, 24);

        mResultBitmap = Bitmap.createBitmap(mBackgroundMap.getWidth(), mBackgroundMap.getHeight(), mBackgroundMap.getConfig());

        mCanvas = new Canvas(mResultBitmap);
        mPaint = new Paint();
        mPaint.setColor(Color.GREEN);
        mPaint.setStyle(Paint.Style.STROKE);
        mPaint.setStrokeWidth(7);

        mCanvas.drawBitmap(mBackgroundMap, new Matrix(), null);
//        mCanvas.drawBitmap(mMark, 165, 1090, mPaint);//暂定测试用起始点
        mIndoorMap.setImageBitmap(mResultBitmap);


        mSetpCount = (TextView) view.findViewById(R.id.text_view_step_count);
        mStepLength = (TextView) view.findViewById(R.id.text_view_step_length);
        mStepDegree = (TextView) view.findViewById(R.id.text_view_step_degree);
        mStepCoordinate = (TextView) view.findViewById(R.id.text_view_step_coordinate);

        Log.i("zhangbz2", "bitmap width = " + mBackgroundMap.getWidth() + ", height = " + mBackgroundMap.getHeight());// bitmap width = 1149, height = 1875 //lg:bitmap width = 766, height = 1250
        Log.i("zhangbz2","indoorMap right = " + mIndoorMap.getRight() + ", bottom = " + mIndoorMap.getBottom());
        Log.i("zhangbz2", "canvas width = " + mCanvas.getWidth() + ", height = " + mCanvas.getHeight());
        /**
         * 04-01 09:33:55.742 2527-2527/com.example.janiszhang.indoorposition I/zhangbz2: bitmap width = 766, height = 1250
         04-01 09:33:55.742 2527-2527/com.example.janiszhang.indoorposition I/zhangbz2: indoorMap width = 0, height = 0
         04-01 09:33:55.742 2527-2527/com.example.janiszhang.indoorposition I/zhangbz2: canvas width = 766, height = 1250

         */
//        wifiMg = (WifiManager)getActivity().getSystemService(Context.WIFI_SERVICE);
//        obtainRssData = new ObtainRssData(wifiMg/*, MainActivity.this*/);
//        openWifi();
//        try {
//            // read all offline RSS data
//            Log.i("zhangbz", "定位??");
//            ObtainRssCoords.readOffLineRssData(rssFile, rssAndCoords);
//        } catch (IOException e) {
//            Log.i("zhangbz", "这里是不是有问题??");
//            // TODO Auto-generated catch block
//            e.printStackTrace();
//        }

        mObtainStepData = new ObtainStepData(getActivity(), mSetpCount, mStepLength, mStepDegree, mStepCoordinate);

        mStartButton = (Button) view.findViewById(R.id.button_start);
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
        mResetButton = (Button) view.findViewById(R.id.button_reset);
        /**
         * correctStep
         * clearPoints
         * initPoints
         * stepViewgone
         */
        mResetButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
//                ObtainWasproCoords.correctWaspro();
                mObtainStepData.correctStep();
                mObtainStepData.clearPoints();
                mObtainStepData.initPoints();
                mObtainStepData.stepViewGone();
            }
        });
        mStopButton = (Button) view.findViewById(R.id.button_stop);
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

        return view;
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

    public void stepShowTaskSchedule(long milliTime) {
        mTimer = new Timer();
        mTimetask = new TimerTask(){
            @Override
            public void run() {
                getActivity().runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        mCanvas.drawBitmap(mBackgroundMap, new Matrix(), null);
                        myCoords = ObtainStepData.getCurCoordsOfStep();
//                        myCoords = ObtainWasproCoords.getCurCoordsOfWaspro(rssAndCoords);
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
        Log.i("zhangbz3", "coors[0] = " + coors[0] + ", coors[1] = " + coors[1]);
        Log.i("zhangbz3","indoorMap right = " + mIndoorMap.getRight() + ", bottom = " + mIndoorMap.getBottom());
        Log.i("zhangbz3", "canvas width = " + mCanvas.getWidth() + ", height = " + mCanvas.getHeight());
        newCoords[0] = (int)(coors[0] * ((float) mCanvas.getWidth() / mIndoorMap.getRight()));
        newCoords[1] = (int)(coors[1] * ((float) mCanvas.getHeight() / mIndoorMap.getBottom()));
        Log.i("zhangbz3", "newCoors[0] = " + newCoords[0] + ", newCoors[1] = " + newCoords[1]);
        /**
         * 04-01 09:44:10.816 17041-17041/com.example.janiszhang.indoorposition I/zhangbz3: coors[0] = 230.0, coors[1] = 640.0
         04-01 09:44:10.816 17041-17041/com.example.janiszhang.indoorposition I/zhangbz3: indoorMap right = 768, bottom = 786   //这里是问题所在
         04-01 09:44:10.816 17041-17041/com.example.janiszhang.indoorposition I/zhangbz3: canvas width = 766, height = 1250
         04-01 09:44:10.817 17041-17041/com.example.janiszhang.indoorposition I/zhangbz3: newCoors[0] = 229, newCoors[1] = 1017
         */
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

//    public void openWifi() {
//        if(!wifiMg.isWifiEnabled()) {
//            wifiMg.setWifiEnabled(true);
//        }
//    }
}
