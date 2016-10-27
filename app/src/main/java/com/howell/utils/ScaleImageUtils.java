package com.howell.utils;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Matrix;
import android.util.Log;

public class ScaleImageUtils {
    // decode这个图片并且按比例缩放以减少内存消耗，虚拟机对每张图片的缓存大小也是有限制的  
    public static Bitmap decodeFile(int requiredWidthSize,int requiredHeightSize,File f) {  
        try {  
            // decode image size  
            BitmapFactory.Options o = new BitmapFactory.Options();  
            o.inJustDecodeBounds = true;  
            BitmapFactory.decodeStream(new FileInputStream(f), null, o);  
            
            // Find the correct scale value. It should be the power of 2.  
            //final int REQUIRED_SIZE = 70;  
            int REQUIRED_WIDTH_SIZE = requiredWidthSize;
            int REQUIRED_HEIGHT_SIZE = requiredHeightSize;
            int width_tmp = o.outWidth, height_tmp = o.outHeight;  
            
            int scale = 1;  
            while (true) {  
                	if (width_tmp / 2 < REQUIRED_WIDTH_SIZE  
                            || height_tmp / 2 < REQUIRED_HEIGHT_SIZE)  
                        break;  
                	width_tmp /= 2;  
                    height_tmp /= 2;  
                    scale *= 2;  
            }  
  
            // decode with inSampleSize  
            BitmapFactory.Options o2 = new BitmapFactory.Options();  
            o2.inSampleSize = scale;  
            return BitmapFactory.decodeStream(new FileInputStream(f), null, o2);  
        } catch (FileNotFoundException e) {  
        }  
        return null;  
    }  
    
    public static Bitmap decodeByteArray(int requiredWidthSize,int requiredHeightSize,byte[] data) {  
    	// decode image size  
        BitmapFactory.Options o = new BitmapFactory.Options();  
        o.inJustDecodeBounds = true;  
        BitmapFactory.decodeByteArray(data, 0, data.length,o);  
            
        // Find the correct scale value. It should be the power of 2.  
        //final int REQUIRED_SIZE = 70;  
        int REQUIRED_WIDTH_SIZE = requiredWidthSize;
        int REQUIRED_HEIGHT_SIZE = requiredHeightSize;
        int width_tmp = o.outWidth, height_tmp = o.outHeight;  
        int scale = 1;  
        while (true) {  
            if (width_tmp < REQUIRED_WIDTH_SIZE  
                        || height_tmp < REQUIRED_HEIGHT_SIZE)  
                break;  
            System.out.println("缩小");
            width_tmp /= 2;  
            height_tmp /= 2;  
            scale *= 2;  
            
        }  
  
        // decode with inSampleSize  
        BitmapFactory.Options o2 = new BitmapFactory.Options();  
        o2.inSampleSize = scale;  
        o2.inJustDecodeBounds = false;
        return  BitmapFactory.decodeByteArray(data, 0, data.length,o2);
    }  
    
    public static Bitmap zoomInFile(int requiredWidthSize,int requiredHeightSize,String path) {  
            // decode image size  
            BitmapFactory.Options o = new BitmapFactory.Options();  
            o.inJustDecodeBounds = true;  
            BitmapFactory.decodeFile(path, o); 
            
            // Find the correct scale value. It should be the power of 2.  
            //final int REQUIRED_SIZE = 70;  
            int REQUIRED_WIDTH_SIZE = requiredWidthSize;
            int REQUIRED_HEIGHT_SIZE = requiredHeightSize;
            int width_tmp = o.outWidth, height_tmp = o.outHeight;  
            
            Log.e("", "放大之前的宽："+width_tmp+",放大之前的高："+height_tmp);
            int scale = 1;  
            while (true) {  
                if (width_tmp < REQUIRED_WIDTH_SIZE  
                        || height_tmp < REQUIRED_HEIGHT_SIZE)  
                    break;  
                width_tmp *= 2;  
                height_tmp *= 2;  
                scale /= 2;  
            }  
  
            // decode with inSampleSize  
            BitmapFactory.Options o2 = new BitmapFactory.Options();  
            o2.inSampleSize = scale;  
            Bitmap bmp = BitmapFactory.decodeFile(path, o2);  
            Log.e("", "压缩之后的宽："+width_tmp+",压缩之后的高："+height_tmp);
            return bmp;
    }  
    
    public static Bitmap resizeImage(Bitmap bitmap, int w, int h) {

        // load the origial Bitmap
        Bitmap bitmapOrg = bitmap;

        int width = bitmapOrg.getWidth();
        int height = bitmapOrg.getHeight();
        int newWidth = w;
        int newHeight = h;
       
        // calculate the scale
        float scaleWidth = ((float) newWidth) / width;
        float scaleHeight = ((float) newHeight) / height;

        // create a matrix for the manipulation
        Matrix matrix = new Matrix();
        // resize the Bitmap
        matrix.postScale(scaleWidth, scaleHeight);
        // if you want to rotate the Bitmap
        // recreate the new Bitmap
        Bitmap resizedBitmap = Bitmap.createBitmap(bitmapOrg, 0, 0, width,
                        height, matrix, true);

        return resizedBitmap;

    }

}
