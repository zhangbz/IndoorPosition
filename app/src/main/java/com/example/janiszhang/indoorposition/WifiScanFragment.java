package com.example.janiszhang.indoorposition;

import android.annotation.SuppressLint;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Matrix;
import android.graphics.Paint;
import android.net.wifi.WifiManager;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Map;
import java.util.Timer;
import java.util.TimerTask;

/**
 * Created by janiszhang on 2016/3/30.
 */
public class WifiScanFragment extends android.support.v4.app.Fragment {

    private Bitmap mBackgroundMap;
    private Bitmap mMark;
    private Bitmap mResultBitmap;
    private Canvas mCanvas;
    private Paint mPaint;
    private ImageView mIndoorMap;

    // RSS variables
    WifiManager wifiMg;
    ProgressBar myProBar;
    ObtainRssData obtainRssData;
    Timer mTimer               = null;
    TimerTask mTimetask            = null;
    View.OnTouchListener touchSelectRp = null;
    static float[] touchCoords    = {150, 1480};
    String rssFile                = "/2Rss/rss.txt";
    static ArrayList<Map.Entry<float[], float[]>> rssAndCoords = new ArrayList<Map.Entry<float[], float[]>>();

    static int[] newCoords            = new int[2];
    static int[] curTouchCoords       = {0, 0};
    final long updateItemMilliTime = 40;
    private Button mObtainButton;
    private Button mOpenFileButton;
    private Button mCleanButton;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.wifi_scan,container,false);

        mIndoorMap = (ImageView) view.findViewById(R.id.image_map);

        mBackgroundMap = BitmapFactory.decodeResource(getResources(), R.drawable.indoor);
        mMark = BitmapFactory.decodeResource(getResources(), R.drawable.location_24_red);
//        mMark = getResizedBitmap(mMark, 50, 24);

        mResultBitmap = Bitmap.createBitmap(mBackgroundMap.getWidth(), mBackgroundMap.getHeight(), mBackgroundMap.getConfig());
        Log.i("zhangbz", "bitmap width = " + mBackgroundMap.getWidth() + ", height = " + mBackgroundMap.getHeight());// bitmap width = 1149, height = 1875
        Log.i("zhangbz", "imageview width = " + mIndoorMap.getWidth() + ", height = " + mIndoorMap.getHeight());// bitmap width = 1149, height = 1875

        mCanvas = new Canvas(mResultBitmap);
        mPaint = new Paint();
        mPaint.setColor(Color.GREEN);
        mPaint.setStyle(Paint.Style.STROKE);
        mPaint.setStrokeWidth(7);

        mCanvas.drawBitmap(mBackgroundMap, new Matrix(), null);
        mCanvas.drawBitmap(mMark, 0, 0, mPaint);//暂定测试用起始点
        mIndoorMap.setImageBitmap(mResultBitmap);

        wifiMg = (WifiManager) getActivity().getSystemService(Context.WIFI_SERVICE);
        obtainRssData = new ObtainRssData(wifiMg/*, MainActivity.this*/);
        myProBar = (ProgressBar) view.findViewById(R.id.progressBar);


        mObtainButton = (Button) view.findViewById(R.id.button_obtain);
        mObtainButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                openWifi();
                obtainRssData.obtainRssData(myProBar,obtainRssData,rssFile);
            }
        });

        mOpenFileButton = (Button) view.findViewById(R.id.button_open_file);
        mOpenFileButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                FileOperation.openFile(getActivity(),rssFile);
            }
        });

        mCleanButton = (Button) view.findViewById(R.id.button_clean);
        mCleanButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                try {
                    FileOperation.clearFile(rssFile);
                } catch (FileNotFoundException e) {
                    // TODO Auto-generated catch block
                    e.printStackTrace();
                }
            }
        });

        try {
            // read all offline RSS data
            Log.i("zhangbz", "定位??");
            ObtainRssCoords.readOffLineRssData(rssFile, rssAndCoords);
        } catch (IOException e) {
            Log.i("zhangbz", "这里是不是有问题??");
            // TODO Auto-generated catch block
            e.printStackTrace();
        }

        if (touchSelectRp == null) {
            touchSelectRp = new View.OnTouchListener() {
                @SuppressLint("ClickableViewAccessibility")
                @Override
                public boolean onTouch(View v, MotionEvent event) {
                    touchCoords[0] = (int)event.getX();
                    touchCoords[1] = (int)event.getY();
                    Log.i("zhangbz", "touchCoords[0] = " + touchCoords[0] + " ; touchCoords[1] = " + touchCoords[1]);
                    return false;
                }
            };
        }

        mIndoorMap.setOnTouchListener(touchSelectRp);
        rpShowTaskSchedule(updateItemMilliTime);
        return view;
    }

    public void openWifi() {
        if(!wifiMg.isWifiEnabled()) {
            wifiMg.setWifiEnabled(true);
        }
    }

    public void rpShowTaskSchedule(long milliTime) {
        mTimer = new Timer();
        mTimetask = new TimerTask(){
            @Override
            public void run() {
                getActivity().runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        mCanvas.drawBitmap(mBackgroundMap, new Matrix(), null);
                        curTouchCoords = convertTouchCoordinates(touchCoords);
                        mCanvas.drawBitmap(mMark, curTouchCoords[0], curTouchCoords[1], mPaint);
                        mIndoorMap.setImageBitmap(mResultBitmap);
                        Log.i("zhangbz","curTouchCoords[0] = " + curTouchCoords[0] + " ; curTouchCoords[1] = " + curTouchCoords[1]);
                    }
                });
            }
        };
        mTimer.schedule(mTimetask, 0, milliTime);
    }

    public int[] convertTouchCoordinates(float[] coors) {
		/*
		 * float[] : convert coordinate to fit on the screen of mobile.
		 */
        newCoords[0] = (int)(coors[0] * ((float) mCanvas.getWidth() / mIndoorMap.getRight()));
        newCoords[1] = (int)(coors[1] * ((float) mCanvas.getHeight() / mIndoorMap.getBottom()));
        return newCoords;
    }

    public static String getOriginalCurTouchCoords() {
		/*
		 * get original current coordinate.
		 */
        return "" + touchCoords[0] + "," + touchCoords[1];
    }

}
