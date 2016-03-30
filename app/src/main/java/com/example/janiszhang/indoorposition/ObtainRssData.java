package com.example.janiszhang.indoorposition;

import android.net.wifi.ScanResult;
import android.net.wifi.WifiManager;
import android.util.Log;
import android.view.View;
import android.widget.ProgressBar;
import android.widget.Toast;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Timer;
import java.util.TimerTask;

public class ObtainRssData {
	/*
	 * obtain RSS data and position coordinate, including offline and online.
	 */
	
	static WifiManager wifiMg = null;
	static List<ScanResult> list = null;
	static StringBuilder record_data = new StringBuilder();
	static List<String> routeSSIDSet = new ArrayList<String>();
	static int[] ssidSet = new int[5];
	static int acquireSignalNum = 0;
	static int acquireSignalTotalNum = 5;
	static long acquireTimeInterval_ms = 1000;
	static String ssid = "E523,Dijkstra,Dijkstra_5G,E2-423,E323";//"lab524_1,lab524_2,lab524_3";
	
	Timer timer;
	TimerTask task;
//	MainActivity mainAty;
	
	public ObtainRssData(WifiManager wifiManager/*, MainActivity mainAty*/) {
		ObtainRssData.wifiMg = wifiManager;
//		this.mainAty = mainAty;
	}
	
	public void obtainRssData(final ProgressBar progressBar, final ObtainRssData obtainRssData, final String fileStr) {
		/*
		 * obtain the RSS data and the position coordinate of offline.
		 */
		getEditableParameters();
		progressBar.setMax(acquireSignalTotalNum);
		progressBar.setVisibility(View.VISIBLE);
		
		if (timer == null) {
			timer = new Timer();
			task = new TimerTask() {
				
				@Override
				public void run() {
					getOffLineRssAndCoordinateData();
					acquireSignalNum ++;
					progressBar.setProgress(acquireSignalNum);
					if (acquireSignalNum >= acquireSignalTotalNum) {
						if (timer != null) {
							cancelRssTask();
							saveRssToFile(progressBar, obtainRssData, fileStr);
						}
					}
				}
			};
			timer.schedule(task, 0, acquireTimeInterval_ms);
		}
	}
	
	private void getEditableParameters() {
		/*
		 * get the newest editable configure parameters.
		 */
		routeSSIDSet = myStrToList(SettingsFragment.getSsid());
		acquireSignalTotalNum = SettingsFragment.getTotalNum();
		acquireTimeInterval_ms = SettingsFragment.getInterval_ms();
	}

	private static List<String> myStrToList(String ssid2) {
		/*
		 * convert String to List.
		 */
		return Arrays.asList(ssid2.split(","));
	}

	private void cancelRssTask() {
		/*
		 * cancel RSS task schedule.
		 */
		task.cancel();
		timer.cancel();
		task = null;
		timer = null;
	}
	boolean first = true;
	private void saveRssToFile(final ProgressBar progressBar, ObtainRssData obtainRssData, String fileStr) {
		/*
		 * save RSS data and position coordinate to file.
		 */
//		mainAty.runOnUiThread(new Runnable() {
//
//			@Override
//			public void run() {
//				progressBar.setVisibility(View.GONE);
//				Toast.makeText(mainAty, "saving", Toast.LENGTH_SHORT).show();
//			}
//		});
		
		try {
			Log.i("zhangbz", "mark1");

			if(first) {
				first = false;
				Log.i("zhangbz", "(1)first = " + first);
				FileOperation.saveToFile(record_data.toString(),fileStr);
			} else {
				Log.i("zhangbz", "(2)first = " + first);
				FileOperation.appendToFile(record_data.toString(), fileStr);
			}
//			FileOperation.appendToFile(record_data.toString(), fileStr);
			acquireSignalNum = 0;
		} catch (IOException e) {
			Log.i("zhangbz", "mark2");
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	
	private static void getCommonRssAndCoordinateData() {
		/*
		 * get RSS data from WiFi. this method is common to online and offline. 
		 */
		record_data.delete(0, record_data.length());
		wifiMg.startScan();
		list = wifiMg.getScanResults();
		if (list != null) {
			for (ScanResult scanResult:list) {
				if (routeSSIDSet.contains(scanResult.SSID)) {
					int ssidIndex = routeSSIDSet.indexOf(scanResult.SSID);
					ssidSet[ssidIndex] = scanResult.level;
				}
			}
		}
		for (int i = 0; i < ssidSet.length; i++) {
			record_data.append(routeSSIDSet.get(i) + "," + ssidSet[i] + ",");
		}
		record_data.deleteCharAt(record_data.length()-1);
	}
	
	private void getOffLineRssAndCoordinateData() {
		/*
		 * get RSS data from WiFi. this method is only used to offline. 
		 */
		getCommonRssAndCoordinateData();
		record_data.append(" ").append(WifiScanFragment.getOriginalCurTouchCoords() + "\n");//至此,record_data的一行记录就生成了 : (ap的名字,leve) * 3, 坐标
	}
	
	public static String getOnLineRssAndCoordinateData() {
		/*
		 * get RSS data from WiFi. this method is only used to online. 
		 */
		routeSSIDSet = myStrToList(SettingsFragment.getSsid());
		getCommonRssAndCoordinateData();
		return record_data.toString();
	}

}
