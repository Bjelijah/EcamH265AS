package com.howell.utils;

import java.io.File;

import com.howell.entityclass.NodeDetails;
import com.howell.jni.JniUtil;

/**
 * @author 霍之昊 
 *
 * 类说明
 */
public class TakePhotoUtil {
	//拍照功能 照片存于destDirPath路径下，照片为jpg
	public static void takePhoto(String destDirPath,NodeDetails dev,InviteUtils client){
		File destDir = new File(destDirPath);
		if (!destDir.exists()) {
			destDir.mkdirs();
		}
		String path = destDirPath+"/"+dev.getDevID()+".jpg";
		if(client!=null){
			client.setCatchPictureFlag(client.getHandle(),path,path.length());
		}else{
			JniUtil.catchPic(path);
		}
		
	}
}
