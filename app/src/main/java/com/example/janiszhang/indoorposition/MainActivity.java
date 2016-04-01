package com.example.janiszhang.indoorposition;

import android.graphics.Color;
import android.os.Bundle;
import android.os.PersistableBundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.Toast;

/**
 * Created by janiszhang on 2016/3/28.
 */
public class MainActivity extends FragmentActivity implements  View.OnClickListener{

//    private LinearLayout mWifiLinearLayout;
//    private LinearLayout mShowLinearLayout;
//    private LinearLayout mSettingsLinearlayout;
//    private ImageView mWifiImageView;
//    private ImageView mShowImageView;
//    private ImageView mSettingsImageView;
//    private TextView mWifiTextView;
//    private TextView mShowTextView;
//    private TextView mSettingsTextView;
    private Button mWifiScanButton;
    private Button mShowPositionButton;
    private Button mSettingsButton;

    private WifiScanFragment mWifiScanFragment;
    private ShowPositionFragment mShowPositionFragment;
    private SettingsFragment mSettingsFragment;

    //返回键
    final long backSpaceTimeIntervalMilliSecond = 1000;
    long lastTimeclickBack = 0;


//这个是错的!错的!!错的!!!
//    @Override
//    public void onCreate(Bundle savedInstanceState, PersistableBundle persistentState) {
//        super.onCreate(savedInstanceState, persistentState);
//        setContentView(R.layout.activity_main);
//    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);
        getWindow().addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);
        setContentView(R.layout.activity_main);

        initView();
        initListener();
        initFragment(1);
    }

    private void initFragment(int index) {
        FragmentManager fragmentManager = getSupportFragmentManager();
        FragmentTransaction transaction = fragmentManager.beginTransaction();
        
        hideFragment(transaction);

        switch (index) {
            case 0:
                if(mWifiScanFragment == null) {
                    mWifiScanFragment = new WifiScanFragment();
                    transaction.add(R.id.fl_content,mWifiScanFragment);
                } else {
                    transaction.show(mWifiScanFragment);
                }
                break;

            case 1:
                if( mShowPositionFragment== null) {
                    mShowPositionFragment = new ShowPositionFragment();
                    transaction.add(R.id.fl_content,mShowPositionFragment);
                } else {
                    transaction.show(mShowPositionFragment);
                }
                break;

            case 2:
                if(mSettingsFragment == null) {
                    mSettingsFragment = new SettingsFragment();
                    transaction.add(R.id.fl_content,mSettingsFragment);
                } else {
                    transaction.show(mSettingsFragment);
                }
                break;
        }

        transaction.commit();
    }

    private void hideFragment(FragmentTransaction transaction) {
        if(mShowPositionFragment != null) {
            transaction.hide(mShowPositionFragment);
        }

        if(mSettingsFragment != null) {
            transaction.hide(mSettingsFragment);
        }

        if(mWifiScanFragment != null) {
            transaction.hide(mWifiScanFragment);
        }
    }

    private void initListener() {
//        mWifiLinearLayout.setOnClickListener(this);
//        mShowLinearLayout.setOnClickListener(this);
//        mSettingsLinearlayout.setOnClickListener(this);

        mWifiScanButton.setOnClickListener(this);
        mShowPositionButton.setOnClickListener(this);
        mSettingsButton.setOnClickListener(this);
    }

    private void initView() {
//        mWifiLinearLayout = (LinearLayout) findViewById(R.id.ll_Wifi);
//        mShowLinearLayout = (LinearLayout) findViewById(R.id.ll_show);
//        mSettingsLinearlayout = (LinearLayout) findViewById(R.id.ll_settings);
//
//        mWifiImageView = (ImageView) findViewById(R.id.iv_wifi);
//        mShowImageView = (ImageView) findViewById(R.id.iv_show);
//        mSettingsImageView = (ImageView) findViewById(R.id.iv_settings);
//
//        mWifiTextView = (TextView) findViewById(R.id.tv_wifi);
//        mShowTextView = (TextView) findViewById(R.id.tv_show);
//        mSettingsTextView = (TextView) findViewById(R.id.tv_settings);


        mWifiScanButton = (Button) findViewById(R.id.bt_wifi_scan);
        mShowPositionButton = (Button) findViewById(R.id.bt_show_position);
        mSettingsButton = (Button) findViewById(R.id.bt_settings);
    }

    @Override
    public void onClick(View v) {
        resetBottom();

        switch (v.getId()) {
//            case R.id.ll_Wifi:
//                mWifiImageView.setImageResource(R.drawable.wifi_50_mygreen);
//                mWifiTextView.setTextColor(Color.GREEN);
//                //initFragment
//                break;
//            case R.id.ll_show:
//                mShowImageView.setImageResource(R.drawable.location_50_mygreen);
//                mShowTextView.setTextColor(Color.GREEN);
//                //initFramgent()
//                break;
//            case R.id.ll_settings:
//                mSettingsImageView.setImageResource(R.drawable.configuration_50_mygreen);
//                mSettingsTextView.setTextColor(Color.GREEN);
//                break;
            case R.id.bt_wifi_scan:
//                mWifiScanButton.setCompoundDrawablesRelativeWithIntrinsicBounds(0,R.drawable.wifi_50_mygreen,0,0);
                mWifiScanButton.setTextColor(getResources().getColor(R.color.my_green));
                initFragment(0);
                break;
            case R.id.bt_show_position:
//                mShowPositionButton.setCompoundDrawablesRelativeWithIntrinsicBounds(0, R.drawable.location_50_mygreen,0,0);
                //openWifi();
//                ObtainWasproCoords.correctWaspro();
                mShowPositionButton.setTextColor(getResources().getColor(R.color.my_green));
                initFragment(1);
                break;
            case R.id.bt_settings:
//                mSettingsButton.setCompoundDrawablesRelativeWithIntrinsicBounds(0, R.drawable.configuration_50_mygreen, 0,0);
                mSettingsButton.setTextColor(getResources().getColor(R.color.my_green));
                initFragment(2);
                break;
        }
    }

    private void resetBottom() {
//        mWifiImageView.setImageResource(R.drawable.wifi_50_black);
//        mSettingsImageView.setImageResource(R.drawable.configuration_50_black);
//        mShowImageView.setImageResource(R.drawable.location_50_black);
//
//        mWifiTextView.setTextColor(Color.BLACK);
//        mShowTextView.setTextColor(Color.BLACK);
//        mSettingsTextView.setTextColor(Color.BLACK);


//        mShowPositionButton.setCompoundDrawablesRelativeWithIntrinsicBounds(0,R.drawable.location_50_black,0,0);//需要总结
//        mSettingsButton.setCompoundDrawablesRelativeWithIntrinsicBounds(0,R.drawable.configuration_50_black,0,0);
//        mWifiScanButton.setCompoundDrawablesRelativeWithIntrinsicBounds(0,R.drawable.wifi_50_black, 0,0);

        mShowPositionButton.setTextColor(getResources().getColor(android.R.color.black));
        mSettingsButton.setTextColor(getResources().getColor(android.R.color.black));
        mWifiScanButton.setTextColor(getResources().getColor(android.R.color.black));
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
}
