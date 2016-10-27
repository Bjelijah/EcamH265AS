package com.howell.utils;

import java.io.UnsupportedEncodingException;
import java.lang.Character.UnicodeBlock;
import java.net.UnknownHostException;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.TimeZone;
import java.util.UUID;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import android.annotation.SuppressLint;
import android.app.ActivityManager;
import android.app.ActivityManager.RunningTaskInfo;
import android.app.AlertDialog;
import android.content.Context;
import android.net.wifi.WifiInfo;
import android.net.wifi.WifiManager;
import android.provider.Settings.Secure;
import android.telephony.TelephonyManager;
import android.util.Log;
import android.widget.Toast;

@SuppressWarnings("deprecation")
@SuppressLint("SimpleDateFormat")
public class Utils {

	public static Date StringToDate(String string){
		SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss");
		Date date = null;
		try {
			date = sdf.parse(string);
		} catch (ParseException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return date;
	}



	public static short[] getFiveSecondsBeforeDate(Date date){
		//Date date = new Date(c.alarm_date_year-1900,c.alarm_date_month-1,c.alarm_date_day,c.alarm_date_hour,c.alarm_date_minute,c.alarm_date_second);
		if (date==null) {
			DebugUtil.logE(null, "get five seconds before date = null");
		}

		SimpleDateFormat dft = new SimpleDateFormat("yyyy-MM-dd-HH-mm-ss");
		Calendar cal = Calendar.getInstance();
		cal.setTime(date);
		cal.set(Calendar.SECOND, cal.get(Calendar.SECOND) - 5);
		String fiveSecondsBeforeDateTemp = dft.format(cal.getTime());
		System.out.println("camera five sencond before:"+fiveSecondsBeforeDateTemp);
		String temp[] = fiveSecondsBeforeDateTemp.split("-");
		short fiveSecondsBeforeDate[] = new short[7];
		for(int i = 0 ; i < temp.length ; i++){
			fiveSecondsBeforeDate[i] = Short.valueOf(temp[i]);
		}
		return fiveSecondsBeforeDate;
	}

	public static short[] getTwoSecondsAfterDate(Date date){
		//Date date = new Date(c.alarm_date_year-1900,c.alarm_date_month-1,c.alarm_date_day,c.alarm_date_hour,c.alarm_date_minute,c.alarm_date_second);
		SimpleDateFormat dft = new SimpleDateFormat("yyyy-MM-dd-HH-mm-ss");
		Calendar cal = Calendar.getInstance();
		cal.setTime(date);
		cal.set(Calendar.SECOND, cal.get(Calendar.SECOND) + 2);
		String twoSecondsAfterDateTemp = dft.format(cal.getTime());
		System.out.println("camera five sencond before:"+twoSecondsAfterDateTemp);
		String temp[] = twoSecondsAfterDateTemp.split("-");
		short twoSecondsAfterDate[] = new short[7];
		for(int i = 0 ; i < temp.length ; i++){
			twoSecondsAfterDate[i] = Short.valueOf(temp[i]);
		}
		return twoSecondsAfterDate;
	}



	/**
	 * utf-8 转换成 unicode
	 * @author fanhui
	 * 2007-3-15
	 * @param inStr
	 * @return
	 */
	public static String utf8ToUnicode(String inStr) {
		char[] myBuffer = inStr.toCharArray();

		StringBuffer sb = new StringBuffer();
		for (int i = 0; i < inStr.length(); i++) {
			UnicodeBlock ub = UnicodeBlock.of(myBuffer[i]);
			if(ub == UnicodeBlock.BASIC_LATIN){
				//英文及数字等
				sb.append(myBuffer[i]);
			}else if(ub == UnicodeBlock.HALFWIDTH_AND_FULLWIDTH_FORMS){
				//全角半角字符
				int j = (int) myBuffer[i] - 65248;
				sb.append((char)j);
			}else{
				//汉字
				short s = (short) myBuffer[i];
				String hexS = Integer.toHexString(s);
				String unicode = "\\u"+hexS;
				sb.append(unicode.toLowerCase());
			}
		}
		return sb.toString();
	}

	//UTF-8->GB2312  
	public static String utf8Togb2312(String str){   

		StringBuffer sb = new StringBuffer();   

		for ( int i=0; i<str.length(); i++) {   

			char c = str.charAt(i);   
			switch (c) {   
			case '+' :   
				sb.append( ' ' );   
				break ;   
			case '%' :   
				try {   
					sb.append(( char )Integer.parseInt (   
							str.substring(i+1,i+3),16));   
				}   
				catch (NumberFormatException e) {   
					throw new IllegalArgumentException();   
				}   

				i += 2;   

				break ;   

			default :   

				sb.append(c);   

				break ;   

			}   

		}   

		String result = sb.toString();   

		String res= null ;   

		try {   

			byte [] inputBytes = result.getBytes( "8859_1" );   

			res= new String(inputBytes, "UTF-8" );   

		}   

		catch (Exception e){}   

		return res;   

	}  
	public static void postAlerDialog(Context context,String message){
		new AlertDialog.Builder(context)   
		//        .setTitle("�û�����������")   
		.setMessage(message)                 
		.setPositiveButton("确定", null)   
		.show();  
	}

	public static String host2ip(String host){
		java.net.InetAddress x;
		String ip = null;
		try {
			x = java.net.InetAddress.getByName(host);
			ip = x.getHostAddress();//得到字符串形式的ip地址
			System.out.println(ip);
		} catch (UnknownHostException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} 
		return ip;
	}

	public static String getCharacterAndNumber() {
		String rel="";
		SimpleDateFormat formatter = new SimpleDateFormat("yyyyMMddHHmmss");
		Date curDate = new Date(System.currentTimeMillis());
		rel = formatter.format(curDate);
		return rel;
	}

	public static String getFileName() {
		// mu
		//String fileNameRandom = getCharacterAndNumber(8);
		String fileNameRandom = getCharacterAndNumber();
		return fileNameRandom;
	}

	public static void postToast(Context context,String message,int time){
		//		Toast toast= Toast.makeText(context, message, 1000);
		//		toast.setGravity(Gravity.CENTER, 0, 0);
		//		toast.show();
		Toast.makeText(context, message, time).show();
	}

	//ImageUrl:["http:\/\/192.168.1.1\/1.jpg"]
	public static String parseUrl(String url){
		if(url.equals("")){
			return "";
		}
		String[] strarray=url.split("\\/");
		strarray[0] = strarray[0].substring(2);
		strarray[strarray.length - 1] = strarray[strarray.length - 1].substring(0,strarray[strarray.length - 1].length() - 2);
		StringBuffer sb = new StringBuffer();
		for (int i = 0; i < strarray.length; i++){
			sb.append(strarray[i]);
			//			if(i != strarray.length - 1){
			//				sb.append("/");
			//			}
			System.out.println("aaaaaaa:"+sb);
		}
		//		System.out.println(sb);
		System.out.println(strarray[2]);
		System.out.println("ttttttttt:"+strarray[2].substring(0, strarray[2].length() - 1));
		return strarray[2].substring(0, strarray[2].length() - 1);
	}

	@SuppressLint("SimpleDateFormat")
	public static String utc2TimeZone(String string){
		SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss");
		sdf.setTimeZone(TimeZone.getTimeZone("UTC"));
		Date dateTemp = null;
		try {
			dateTemp = sdf.parse(string);
		} catch (ParseException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

		SimpleDateFormat formatter = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss");
		TimeZone zone = TimeZone.getDefault();
		formatter.setTimeZone(zone);
		String s = formatter.format(dateTemp);
		//	    System.out.println(s);
		return s;
	}

	public static boolean isThisApp(Context context){
		ActivityManager mActivityManager = (ActivityManager)context.getSystemService(Context.ACTIVITY_SERVICE);
		List<RunningTaskInfo> rti = mActivityManager.getRunningTasks(1); 
		Log.i("123",""+rti.get(0).topActivity.getPackageName());
		if(rti.get(0).topActivity.getPackageName().equals("com.howell.formuseum")){
			return true;
		}else{
			return false;
		}
	}

	public static String getPhoneNum(Context context){

		try {
			TelephonyManager manager = (TelephonyManager)context.getSystemService(Context.TELEPHONY_SERVICE);
			return manager.getLine1Number();

		} catch (Exception e) {
			e.printStackTrace();
			return null;
		}
	}

	public static String getPhoneMac(Context context){
		WifiManager wifiMgr = (WifiManager)context.getSystemService(Context.WIFI_SERVICE);
		WifiInfo info = wifiMgr.getConnectionInfo();
		String mac = info.getMacAddress();
		DebugUtil.logE("getPhoneMac", mac);
		mac = mac.replace(":", "");
		mac = mac.replace("-", "");
		DebugUtil.logE("getPhoneMac", mac);
		return mac;
	}

	

	public static boolean checkIsIP(String ip){
		 Pattern ipPattern=Pattern.compile("([1-9]|[1-9]\\d|1\\d{2}|2[0-1]\\d|22[0-3])(\\.(\\d|[1-9]\\d|1\\d{2}|2[0-4]\\d|25[0-5])){3}");
	     Matcher matcher=ipPattern.matcher(ip);
	    return matcher.matches();
	}



}
