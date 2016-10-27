package com.howell.utils;



import android.content.Context;
import android.net.TrafficStats;
import android.view.View;
import android.view.inputmethod.InputMethodManager;

/**
 * @author yangyu
 *	��������������������
 */
public class Util {
	/**
	 * �õ��豸��Ļ�Ŀ��
	 */
	public static int getScreenWidth(Context context) {
		return context.getResources().getDisplayMetrics().widthPixels;
	}

	/**
	 * �õ��豸��Ļ�ĸ߶�
	 */
	public static int getScreenHeight(Context context) {
		return context.getResources().getDisplayMetrics().heightPixels;
	}

	/**
	 * �õ��豸���ܶ�
	 */
	public static float getScreenDensity(Context context) {
		return context.getResources().getDisplayMetrics().density;
	}

	/**
	 * ���ܶ�ת��Ϊ����
	 */
	public static int dip2px(Context context, float px) {
		final float scale = getScreenDensity(context);
		return (int) (px * scale + 0.5);
	}
	
	
	private static long lastTotalRxBytes = 0;
	private static long lastTimeStamp = 0;
	
	private static long getTotalRxBytes(Context context){
		return TrafficStats.getUidRxBytes(context.getApplicationInfo().uid) == TrafficStats.UNSUPPORTED?0:(TrafficStats.getTotalRxBytes()/1024);
	}
	
	public static String getDownloadSpeed(Context context){
		long nowTotalRxBytes = getTotalRxBytes(context);
		long nowTimeStemp = System.currentTimeMillis();
		long speed = (nowTotalRxBytes - lastTotalRxBytes)*1000 / (nowTimeStemp - lastTimeStamp);
		lastTimeStamp = nowTimeStemp;
		lastTotalRxBytes = nowTotalRxBytes;
//		if(speed == 0 ){
//			if(!isNetConnect(context)){
//				return null;
//			}
//		}
		
		return String.valueOf(speed) + "kb/s";
	}
	
	public static boolean isNewApi(){
		return android.os.Build.VERSION.SDK_INT>22;
	}
	
}
