package com.howell.utils;

import android.content.Context;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;

public class SharedPreferencesUtil implements IConst {
	private static final String LOGIN_SERVICE = "login_set";
	private static final String TURN_SERVICE = "turn_set";
	public static void saveLoginInfo(Context context,String ip,int port){
		SharedPreferences sharedPreferences = context.getSharedPreferences(LOGIN_SERVICE, Context.MODE_PRIVATE);
        Editor editor = sharedPreferences.edit();
        editor.putString("login_service_ip", ip);  
        editor.putInt("login_service_port", port);
        editor.commit();
	}
	
	public static void saveTurnServerInfo(Context context,String ip,int port){
		SharedPreferences sharedPreferences = context.getSharedPreferences(TURN_SERVICE, Context.MODE_PRIVATE);
        Editor editor = sharedPreferences.edit();
        editor.putString("turn_service_ip", ip);  
        editor.putInt("turn_service_port", port);
        editor.commit();
	}
	
	public static String getLoginServiceIP(Context mContext){
		SharedPreferences sharedPreferences = mContext.getSharedPreferences(LOGIN_SERVICE,Context.MODE_PRIVATE);
		return sharedPreferences.getString("login_service_ip", "");
	}
	
	public static int getLoginServicePort(Context mContext){
		SharedPreferences sharedPreferences = mContext.getSharedPreferences(LOGIN_SERVICE,Context.MODE_PRIVATE);
		return sharedPreferences.getInt("login_service_port",-1);
	}
	
	public static String getTurnServerIP(Context mContext){
		SharedPreferences sharedPreferences = mContext.getSharedPreferences(TURN_SERVICE,Context.MODE_PRIVATE);
		return sharedPreferences.getString("turn_service_ip", "180.166.7.214");//now should be 116.228.67.70
	}
	
	public static int getTurnServerPort(Context mContext){
		SharedPreferences sharedPreferences = mContext.getSharedPreferences(LOGIN_SERVICE,Context.MODE_PRIVATE);
		return sharedPreferences.getInt("turn_service_port",USING_TURN_ENCRYPTION?8862:8812);
	}
}
