package com.howell.utils;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.TimeZone;

import com.howell.entityclass.Device;
import com.howell.entityclass.VODRecord;

public class AnalyzingDoNetOutput {
    private static int findPosition(String strAll, String str) {
        return strAll.indexOf(str);
    }

    private static boolean estimateBool(String bool) {
        if (bool.equals("true")) {
            return true;
        } else {
            return false;
        }
    }

    public static ArrayList<Device> analyzing(String str) {
        int j = 0;
        String[] s = str.split(" \\}\\;");
        ArrayList<Device> list = new ArrayList<Device>();
        for (int i = 0; i < s.length - 1; i++) {
            String[] s2 = s[i].split("; ");
            String devID = s2[j].substring(findPosition(s2[j], "DevID=")
                    + "DevID=".length(), s2[j].length());
            int ChannelNo = Integer.parseInt(s2[j + 1].substring(
                    "ChannelNo=".length(), s2[j + 1].length()));
            String name = s2[j + 2].substring("Name=".length(),
                    s2[j + 2].length());
            String online = s2[j + 3].substring("OnLine=".length(),
                    s2[j + 3].length());
            String ptzFlag = s2[j + 4].substring("PtzFlag=".length(),
                    s2[j + 4].length());
            Device d = new Device(devID, ChannelNo, name, estimateBool(online),
                    estimateBool(ptzFlag));
            list.add(d);
        }
        return list;
    }

    public static String[] analyzingIPandPort(String str) {
        String[] s = str.split("\\; ");
        String[] ret = { s[7], s[8] };
        return ret;
    }
    
    private static String getTimeZoneStartTime(VODRecord record){
    	 SimpleDateFormat bar = new SimpleDateFormat(
                 "yyyy-MM-dd'T'HH:mm:ss");
         bar.setTimeZone(TimeZone.getTimeZone("UTC"));
         long startTime = 0;
		try {
			startTime = bar.parse(record.getStartTime()).getTime();
		} catch (ParseException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
         SimpleDateFormat formatter = new SimpleDateFormat(
                 "yyyy-MM-dd HH:mm:ss");
         TimeZone zone = TimeZone.getDefault();
         
         String startTimeString = TimeTransform.utcToTimeZoneDate(startTime, zone, formatter);
         return startTimeString;
    }
    
    private static String getTimeZoneEndTime(VODRecord record){
    	 SimpleDateFormat bar = new SimpleDateFormat(
                 "yyyy-MM-dd'T'HH:mm:ss");
         bar.setTimeZone(TimeZone.getTimeZone("UTC"));
         long endTime = 0;
		try {
			endTime = bar.parse(record.getEndTime()).getTime();
		} catch (ParseException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
//         System.out.println(startTime+"!!!!!!!!"+endTime);
         SimpleDateFormat formatter = new SimpleDateFormat(
                 "yyyy-MM-dd HH:mm:ss");
         
         TimeZone zone = TimeZone.getDefault();
//         System.out.println("ZONE:"+zone.getDisplayName());
         
         String endTimeString = TimeTransform.utcToTimeZoneDate(endTime, zone, formatter);
         return endTimeString;
    }
    
    public static void analyzingVODRecord(String str, ArrayList<VODRecord> list) {
        int j = 0;
        String[] s = str.split(" \\}\\;");

        if (s.length == 1) {
            String[] s2 = str.split("; ");
            String startTime = s2[j].substring(
                    findPosition(s2[j], "StartTime=") + "StartTime=".length(),
                    s2[j].length());
            String endTime = s2[j + 1].substring("EndTime=".length(),
                    s2[j + 1].length());
            String fileSize = s2[j + 2].substring("FileSize=".length(),
                    s2[j + 2].length());
            String Desc = s2[j + 3].substring("Desc=".length(),
                    s2[j + 3].length());
            VODRecord record = new VODRecord(startTime, endTime,
                    Long.parseLong(fileSize), Desc);
            record.setTimeZoneStartTime(getTimeZoneStartTime(record));
            record.setTimeZoneEndTime(getTimeZoneEndTime(record));
            list.add(record);
        } else {
            for (int i = 0; i < s.length - 1; i++) {
                String[] s2 = str.split("; ");
                String startTime = s2[j].substring(
                        findPosition(s2[j], "StartTime=")
                                + "StartTime=".length(), s2[j].length());
                String endTime = s2[j + 1].substring("EndTime=".length(),
                        s2[j + 1].length());
                String fileSize = s2[j + 2].substring("FileSize=".length(),
                        s2[j + 2].length());
                String Desc = s2[j + 3].substring("Desc=".length(),
                        s2[j + 3].length());
                VODRecord record = new VODRecord(startTime, endTime,
                        Long.parseLong(fileSize), Desc);
                record.setTimeZoneStartTime(getTimeZoneStartTime(record));
                record.setTimeZoneEndTime(getTimeZoneEndTime(record));
                list.add(record);
            }
        }
    }
}
