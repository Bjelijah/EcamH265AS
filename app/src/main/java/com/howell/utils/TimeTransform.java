package com.howell.utils;

import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.TimeZone;



public class TimeTransform {
     public static String utcToTimeZoneDate(long date, TimeZone timeZone, DateFormat format){
         Date dateTemp = new Date(date);
         format.setTimeZone(timeZone);
         return format.format(dateTemp);
     }
     
     public static String dateToString(Date date){
    	 SimpleDateFormat foo = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss");
         String stringTime = foo.format(date);
         return stringTime;
     }
     
     public static String reduceTenDays(Date date ){
    	 SimpleDateFormat dft = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss");
 		 Calendar cal = Calendar.getInstance();
 		 cal.setTime(date);
 		 cal.set(Calendar.DATE, cal.get(Calendar.DATE) - 10);
		 String endDate = dft.format(cal.getTime());
		 return endDate;
     }
     
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
}


