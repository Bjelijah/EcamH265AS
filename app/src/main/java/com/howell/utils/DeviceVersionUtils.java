package com.howell.utils;

public class DeviceVersionUtils {
	public static boolean needToUpdate(String curVer,String newVer){
		String[] s = curVer.split("\\.");
		String[] s2 = newVer.split("\\.");
		int firstCurVerNum = Integer.valueOf(s[0]);
		System.out.println(firstCurVerNum);
		int secondCurVerNum = Integer.valueOf(s[1]);
		System.out.println(secondCurVerNum);
		int thirdCurVerNum = Integer.valueOf(s[2]);
		System.out.println(thirdCurVerNum);
		
		int firstNewVerNum = Integer.valueOf(s2[0]);
		System.out.println(firstNewVerNum);
		int secondNewVerNum = Integer.valueOf(s2[1]);
		System.out.println(secondNewVerNum);
		int thirdNewVerNum = Integer.valueOf(s2[2]);
		System.out.println(thirdNewVerNum);
		
		if(firstCurVerNum > firstNewVerNum){
			return false;
		}else if(firstCurVerNum < firstNewVerNum){
			return true;
		}else{
			if(secondCurVerNum  > secondNewVerNum){
				return false;
			}else if(secondCurVerNum < secondNewVerNum){
				return true;
			}else{
				if(thirdCurVerNum > thirdNewVerNum){
					return false;
				}else if(thirdCurVerNum < thirdNewVerNum){
					return true;
				}else{
					return false;
				}
			}
		}
	}
	
	//判断设备版本是否小于3.0.0
	public static boolean isNewVersionDevice(String curVer){
		String[] s = curVer.split("\\.");
		int firstCurVerNum = Integer.valueOf(s[0]);
		if(firstCurVerNum < 3){
			return false;
		}else{
			return true;
		}
	}
}
