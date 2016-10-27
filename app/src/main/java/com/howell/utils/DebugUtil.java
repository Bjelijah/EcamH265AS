package com.howell.utils;


import android.util.Log;	

public class DebugUtil {
	public static final String TAG = "DEBUG";
	public static final String TAG_123 = "123";
	
	private static <T> String classLog(Class<T> cls,String msg){
		String str="";
		if (cls!=null) {
			str = "["+cls.toString()+"]"+" ";
		}
		str += msg;
		return str;
	}
	
	public static void logI(String tag,String msg){
		if (MyDebug.IS_DEBUG) {
			if (tag==null) {
				tag = TAG;
			}
			Log.i(tag, msg);
		}
	}
	
	public static <T> void logI(String msg,Class<T> cls){
		if (MyDebug.IS_DEBUG) {
			Log.i(TAG_123, classLog(cls, msg));
		}
	}
	
	public static void logE(String tag,String msg){
		if (MyDebug.IS_DEBUG) {
			if (tag==null) {
				tag = TAG;
			}
			Log.e(tag, msg);
		}
	}
	
	public static <T> void logE(String msg,Class<T> cls){
		if (MyDebug.IS_DEBUG) {
			Log.e(TAG_123, classLog(cls, msg));
		}
	}
	
	public static void logD(String tag,String msg){
		if (MyDebug.IS_DEBUG) {
			if (tag==null) {
				tag = TAG;
			}
			Log.d(tag, msg);
		}
	}
	
	public static <T> void logD(String msg,Class<T> cls){
		if (MyDebug.IS_DEBUG) {
			Log.d(TAG_123, classLog(cls, msg));
		}
	}
	
	public static void logV(String tag,String msg){
		if (MyDebug.IS_DEBUG) {
			if (tag==null) {
				tag = TAG;
			}
			Log.v(tag, msg);
		}
	}
	
	public static <T> void logV(String msg,Class<T> cls){
		if (MyDebug.IS_DEBUG) {
			Log.v(TAG_123, classLog(cls, msg));
		}
	}
	
	public static void logW(String tag,String msg){
		if (MyDebug.IS_DEBUG) {
			if (tag==null) {
				tag = TAG;
			}
			Log.w(tag, msg);
		}
	}
	
	public static <T> void logW(String msg,Class<T> cls){
		if (MyDebug.IS_DEBUG) {
			Log.w(TAG_123, classLog(cls, msg));
		}
	}
	
	public static boolean isDebug(){
		return MyDebug.IS_DEBUG;
	}
	
	
	public class MyDebug{
		public static final boolean IS_DEBUG = true;
	}
}
