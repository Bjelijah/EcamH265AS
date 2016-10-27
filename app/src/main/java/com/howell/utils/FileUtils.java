package com.howell.utils;

import java.io.File;
import java.text.SimpleDateFormat;
import java.util.Date;

public class FileUtils {
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
	
	public static void deleteImage(File file){
		if (file.exists()) { // 判断文件是否存在
			if (file.isFile()) { // 判断是否是文件
				file.delete(); // delete()方法 你应该知道 是删除的意思;
			}
		} 
	}
	
}
