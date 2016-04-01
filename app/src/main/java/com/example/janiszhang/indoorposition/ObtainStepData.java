package com.example.janiszhang.indoorposition;

import android.content.Context;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.util.Log;
import android.view.View;
import android.widget.TextView;

import java.text.DecimalFormat;
import java.util.ArrayList;

/**
 * Created by janiszhang on 2016/3/22.
 */
public class ObtainStepData implements SensorEventListener{

    Context context;
    TextView tvstepCount, tvstepLength, tvdegree, tvcoordinate;
    private SensorManager mSensorManager;
    private Sensor mAccelerometer;
    private Sensor mMagnetometer;
    static float accThreshold = 0.65f, co_k_wein = 45f, alpha = 0.25f;
    private long mLastTimeAcc;
    private long mLastTimeMag;
    private long mCurTimeAcc;
    private long mCurTimeMag;
    float[] mAccValues = new float[3];
    float[] mMagValues = new float[3];
    float[] mValues = new float[3];
    float[] R = new float[9];
    float[] I = new float[9];
    float mAccModule = 0, mMaResult = 0;
    float mMaxVal = 0f, mMinVal = 0f, mStepLength = 0f;
    int maLength = 5, stepState = 0, stepCount = 0;
    int degreeDisplay;
    static float[] initPoint = {230, 640};
    static float[] curCoordsOfStep = {230, 640};
    static ArrayList<CoordPoint> points = new ArrayList<>();
    DecimalFormat decimalF = new DecimalFormat("#.00");
    float offset = 20, degree;


    public ObtainStepData(Context context, TextView stepCount, TextView stepLength, TextView degree, TextView coordinate) {
        this.context = context;
        this.tvstepCount = stepCount;
        this.tvstepLength = stepLength;
        this.tvdegree = degree;
        this.tvcoordinate = coordinate;
        loadSystemService();
}

    private void loadSystemService() {
        mSensorManager = (SensorManager) context.getSystemService(Context.SENSOR_SERVICE);
        mAccelerometer = mSensorManager.getDefaultSensor(Sensor.TYPE_ACCELEROMETER);
        mMagnetometer = mSensorManager.getDefaultSensor(Sensor.TYPE_MAGNETIC_FIELD);
    }

    public void obtainStep() {
        mLastTimeAcc = System.currentTimeMillis();
        mLastTimeMag = System.currentTimeMillis();
        mSensorManager.registerListener(this, mAccelerometer, SensorManager.SENSOR_DELAY_GAME);
        mSensorManager.registerListener(this, mMagnetometer, SensorManager.SENSOR_DELAY_GAME);
    }

    @Override
    public void onAccuracyChanged(Sensor sensor, int accuracy) {

    }

    @Override
    public void onSensorChanged(SensorEvent event) {
        if(event.sensor.getType() == Sensor.TYPE_ACCELEROMETER) {
            mCurTimeAcc = System.currentTimeMillis();
            if(mCurTimeAcc - mLastTimeAcc > 40) {
                getStepAccInfo(event.values.clone());
                mLastTimeAcc = mCurTimeAcc;
            }
        }

        if (event.sensor.getType() == Sensor.TYPE_MAGNETIC_FIELD) {
            mCurTimeMag = System.currentTimeMillis();
            if(mCurTimeMag - mLastTimeMag > 40) {
                getAzimuthDegree(event.values.clone());
                mLastTimeMag = mCurTimeMag;
            }
        }
    }

    private void getStepAccInfo(float[] clone) {
        mAccValues = clone;
        mAccModule = (float)(Math.sqrt(Math.pow(mAccValues[0], 2) + Math.pow(mAccValues[1], 2) + Math.pow(mAccValues[2], 2)) - 9.794);
        mMaResult = MovingAverage.movingAverage(mAccModule, maLength);
        if(stepState == 0 && mMaResult > accThreshold) {
            stepState = 1;
        }
        if (stepState == 1 && mMaResult > mMaxVal) { //find peak
            mMaxVal = mMaResult;
        }
        if (stepState == 1 && mMaResult <= 0) {
            stepState = 2;
        }
        if(stepState == 2 && mMaResult < mMinVal) { //find bottom
            mMinVal = mMaResult;
        }
        if(stepState == 2 && mMaResult >= 0) {
            stepCount++;
            getStepLengthAndCoordinate();
            recordTrajectory(curCoordsOfStep.clone());
            mMaxVal = mMinVal = stepState = 0;
        }

        stepViewShow();
    }

    public void stepViewShow() {
        tvstepCount.setText("Step Count : " + stepCount);
        tvstepLength.setText("Step Length : " + decimalF.format(mStepLength) + " cm");
        tvdegree.setText("coordinate : " + "X: " + decimalF.format(curCoordsOfStep[0]) + " Y: " + decimalF.format(curCoordsOfStep[1]));
    }

    private void recordTrajectory(float[] clone) {
        points.add(new CoordPoint(clone[0], clone[1]));
    }

    public void initPoints() {
		/*
		 * add the initial position coordinate of pedestrian.
		 */
        points.add(new CoordPoint(initPoint[0], initPoint[1]));
    }

    private void getStepLengthAndCoordinate() {
        mStepLength = (float) (co_k_wein * Math.pow(mMaxVal - mMinVal, 1.0/4));
        double delta_x = Math.cos(Math.toRadians(degreeDisplay)) * mStepLength;
        double delta_y = Math.sin(Math.toRadians(degreeDisplay)) * mStepLength;
        curCoordsOfStep[0] += delta_x;
        curCoordsOfStep[1] += delta_y;
    }


    private void getAzimuthDegree(float[] MagClone) {
		/*
		 * get the azimuth degree of the pedestrian.
		 */
        mMagValues = lowPassFilter(MagClone, mMagValues);
        if (mAccValues == null || mMagValues == null) return;
        boolean sucess = SensorManager.getRotationMatrix(R, I, mAccValues, mMagValues);
        if (sucess) {
            SensorManager.getOrientation(R, mValues);
            Log.i("zhangbz5", "degree1 = " + Math.toDegrees(mValues[0]));
            degree = (int)(Math.toDegrees(mValues[0]) + 360) % 360; // translate into (0, 360).
            Log.i("zhangbz5", "degree2 = " + degree);
            degree = ((int)(degree /*+ 2*/)) / 5 * 5; // the value of degree is multiples of 5.
            Log.i("zhangbz5", "degree3 = " + degree);
            Log.i("zhangbz5", "offset = " + offset);
            if (offset == 0) {
                degreeDisplay = (int) degree;
            } else {
                degreeDisplay = roomDirection(degree, offset); // user-defined room direction.
            }
            Log.i("zhangbz5", "degreeDisplay = " + degreeDisplay);
            stepDegreeViewShow();
        }
    }

    /**
     04-01 10:22:58.027 8957-8957/com.example.janiszhang.indoorposition I/zhangbz5: degree = 295.0
     04-01 10:22:58.027 8957-8957/com.example.janiszhang.indoorposition I/zhangbz5: offset = 0.0
     04-01 10:22:58.027 8957-8957/com.example.janiszhang.indoorposition I/zhangbz5: degreeDisplay = 295
     04-01 10:22:58.091 8957-8957/com.example.janiszhang.indoorposition I/zhangbz5: degree = 295.0
     04-01 10:22:58.091 8957-8957/com.example.janiszhang.indoorposition I/zhangbz5: offset = 0.0
     04-01 10:22:58.091 8957-8957/com.example.janiszhang.indoorposition I/zhangbz5: degreeDisplay = 295


     04-01 10:24:04.016 8957-8957/com.example.janiszhang.indoorposition I/zhangbz5: degree = 290.0
     04-01 10:24:04.016 8957-8957/com.example.janiszhang.indoorposition I/zhangbz5: offset = 20.0
     04-01 10:24:04.016 8957-8957/com.example.janiszhang.indoorposition I/zhangbz5: degreeDisplay = 270
     04-01 10:24:04.075 8957-8957/com.example.janiszhang.indoorposition I/zhangbz5: degree = 290.0
     04-01 10:24:04.075 8957-8957/com.example.janiszhang.indoorposition I/zhangbz5: offset = 20.0
     */

    private void stepDegreeViewShow() {
		/*
		 * show the azimuth degree.
		 */
        tvcoordinate.setText(" Angle : " + degreeDisplay + " degree");
    }

    private int roomDirection(float myDegree, float myOffset) {
		/*
		 * define room direction as 270 degree.
		 */
        int tmp = (int)(myDegree - myOffset);
        if(tmp < 0) tmp += 360;
        else if(tmp >= 360) tmp -= 360;
        return tmp;
    }

    protected float[] lowPassFilter(float[] input, float[] output) {
		/*
		 * low pass filter algorithm implement.
		 */
        if (output == null) return input;
        for (int i = 0; i < input.length; i++) {
            output[i] = output[i] + alpha * (input[i] - output[i]);
        }
        return output;
    }

    public void stopStep() {
		/*
		 * stop listening for sensor and recording step information.
		 */
        mSensorManager.unregisterListener(this);
    }

    public void correctStep() {
		/*
		 * initialize and correct the step parameters.
		 */
        offset = 20;//degree - 270;
        curCoordsOfStep[0] = 230;
        curCoordsOfStep[1] = 640;
        stepCount = 0;
        mStepLength = 0;
    }

    public void obtainStepSetting() {
		/*
		 * before start listening sensor of Accelerometer and Magnetometer, we set and obtain some parameters.
		 */
        tvstepCount.setVisibility(View.VISIBLE);
        tvstepLength.setVisibility(View.VISIBLE);
        tvdegree.setVisibility(View.VISIBLE);
        tvcoordinate.setVisibility(View.VISIBLE);
    }

    public void stepViewGone() {
		/*
		 * set the view to gone.
		 */
        tvstepCount.setVisibility(View.INVISIBLE);
        tvstepLength.setVisibility(View.INVISIBLE);
        tvdegree.setVisibility(View.INVISIBLE);
        tvcoordinate.setVisibility(View.INVISIBLE);

    }

    public static float[] getCurCoordsOfStep() {
		/*
		 * get the current coordinate of pedestrian step.
		 */
        return curCoordsOfStep;
    }

    public static ArrayList<CoordPoint> getPoints() {
		/*
		 * get coordinate point.
		 */
        return points;
    }

    public  void clearPoints() {
		/*
		 * clear coordinate point.
		 */
        points.clear();
    }

    public static void setCurCoordsOfStep(float[] coords) {
		/*
		 * set the current coordinate of the pedestrian.
		 */
        curCoordsOfStep = coords.clone();
    }
}
