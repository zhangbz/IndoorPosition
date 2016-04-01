package com.example.janiszhang.indoorposition;


import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;

/**
 * Created by janiszhang on 2016/3/28.
 */
public class SettingsFragment extends Fragment {



    private View mView;
    private EditText mStepThresholdEditText;
    private EditText mCo_k_weinEditText;
    private EditText mStepObtainDelaySecEditText;
    private EditText mTotalNumEditText;
    private EditText mInterval_msEditView;
    private EditText mSsidEditText;

    private static float sStepThreshold = 0.65f;
    private static float sCo_k_wein = 45f;
    private static int sStepObtainDelaySec = 0;
    private static int sTotalNum = 5;
    private static long sInterval_ms = 1000;
    private static String sSsid = "E523,Dijkstra,Dijkstra_5G,E2-423,E323";

//    @Override
//    public void onCreate(Bundle savedInstanceState) {
//        super.onCreate(savedInstanceState);
//
//    }



    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
//        return super.onCreateView(inflater, container, savedInstanceState);
        mView = inflater.inflate(R.layout.settings_view, container, false);

        mStepThresholdEditText = (EditText) mView.findViewById(R.id.ed_step_threshold);
        mCo_k_weinEditText = (EditText) mView.findViewById(R.id.ed_Co_K_wein);
        mStepObtainDelaySecEditText = (EditText) mView.findViewById(R.id.ed_step_obtain_delay_sec);

        mTotalNumEditText = (EditText) mView.findViewById(R.id.ed_total_num);
        mInterval_msEditView = (EditText) mView.findViewById(R.id.ed_interval_ms);
        mSsidEditText = (EditText) mView.findViewById(R.id.ed_ssid);

        mStepThresholdEditText.addTextChangedListener(new myTextWatcher(mStepThresholdEditText));
        mCo_k_weinEditText.addTextChangedListener(new myTextWatcher(mCo_k_weinEditText));
        mStepObtainDelaySecEditText.addTextChangedListener(new myTextWatcher(mStepObtainDelaySecEditText));
        mTotalNumEditText.addTextChangedListener(new myTextWatcher(mTotalNumEditText));
        mInterval_msEditView.addTextChangedListener(new myTextWatcher(mInterval_msEditView));
        mSsidEditText.addTextChangedListener(new myTextWatcher(mSsidEditText));
        return mView;
    }

//    @Override
//    public void onPause() {
//        super.onPause();
//    }

    private class myTextWatcher implements TextWatcher {

        private View view;
        private myTextWatcher(View view) {
            this.view = view;
        }

        @Override
        public void beforeTextChanged(CharSequence s, int start, int count, int after) {

        }

        @Override
        public void onTextChanged(CharSequence s, int start, int before, int count) {

        }

        @Override
        public void afterTextChanged(Editable s) {
            String text = s.toString();
            if(!text.isEmpty()) {
                switch (view.getId()) {
                    case R.id.ed_Co_K_wein:
                        sCo_k_wein = Float.parseFloat(text);
                        break;
                    case R.id.ed_interval_ms:
                        sInterval_ms = Long.parseLong(text);
                        break;
                    case R.id.ed_ssid:
                        sSsid = text;
                        break;
                    case R.id.ed_step_obtain_delay_sec:
                        sStepObtainDelaySec = Integer.parseInt(text);
                        break;
                    case R.id.ed_step_threshold:
                        sStepThreshold = Float.parseFloat(text);
                        break;
                    case R.id.ed_total_num:
                        sTotalNum = Integer.parseInt(text);
                        break;
                }
            }
        }
    }

    public static int getTotalNum() {
        return sTotalNum;
    }

    public static long getInterval_ms() {
        return sInterval_ms;
    }

    public static String getSsid() {
        return sSsid;
    }

}
