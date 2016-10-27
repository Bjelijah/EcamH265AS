/*
 * Copyright (C) 2008 ZXing authors
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.wyy.twodimcode.view;

import android.content.Context;
import android.content.res.Resources;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Rect;
import android.graphics.Typeface;
import android.graphics.drawable.BitmapDrawable;
import android.util.AttributeSet;
import android.view.View;

import com.google.zxing.ResultPoint;
import com.howell.ecamh265.R;
import com.wyy.twodimcode.camera.CameraManager;

import java.util.Collection;
import java.util.HashSet;

/**
 * This view is overlaid on top of the camera preview. It adds the viewfinder rectangle and partial
 * transparency outside it, as well as the laser scanner animation and result points.
 *
 * @author dswitkin@google.com (Daniel Switkin)
 */
public final class ViewfinderView extends View {
/**扫描页面透明度*/
  private static final int[] SCANNER_ALPHA = {0, 64, 128, 192, 255, 192, 128, 64};
/**动画延迟*/
  private static final long ANIMATION_DELAY = 100L;
  private static final int OPAQUE = 0xFF;//不透明
  
  /**
	 * 四个蓝色边角对应的长度
	 */
	private int ScreenRate;
	
	/**
	 * 四个蓝色边角对应的宽度
	 */
	private static final int CORNER_WIDTH = 8;
	/**
	 * 扫描框中的中间线的宽度
	 */
	private static final int MIDDLE_LINE_WIDTH = 6;
	
	/**
	 * 扫描框中的中间线的与扫描框左右的间隙
	 */
	private static final int MIDDLE_LINE_PADDING = 5;
	
	/**
	 * 中间那条线每次刷新移动的距离
	 */
	private static final int SPEEN_DISTANCE = 5;
	
	/**
	 * 手机的屏幕密度
	 */
	private static float density;
	/**
	 * 字体大小
	 */
	private static final int TEXT_SIZE = 16;
	/**
	 * 字体距离扫描框下面的距离
	 */
	private static final int TEXT_PADDING_TOP = 30;

  private final Paint paint;
  /**返回的照片*/
  private Bitmap resultBitmap;
  /**遮盖物颜色*/
  private final int maskColor;
  /**结果颜色*/
  private final int resultColor;
  /**框架颜色*/
  private final int frameColor;
  /**扫描线颜色*/
  private final int laserColor;
  /**结果点的颜色*/
  private final int resultPointColor;
  private int scannerAlpha;//扫描透明度
 /**可能的结果点数*/
  private Collection<ResultPoint> possibleResultPoints;
  /**最后的结果点数*/
  private Collection<ResultPoint> lastPossibleResultPoints;
  
  /**
	 * 中间滑动线的最顶端位置
	 */
	private int slideTop;
	
	/**
	 * 中间滑动线的最底端位置
	 */
	private int slideBottom;
  private boolean isFirst;
  private Context context;

  // This constructor is used when the class is built from an XML resource.
  public ViewfinderView(Context context, AttributeSet attrs) {
    super(context, attrs);
    this.context = context;
    density = context.getResources().getDisplayMetrics().density;
    //将像素转化成dp
    ScreenRate = (int)(25*density);
    
    // Initialize these once for performance rather than calling them every time in onDraw().
    paint = new Paint();
    Resources resources = getResources();
    maskColor = resources.getColor(R.color.viewfinder_mask);
    resultColor = resources.getColor(R.color.backgroud);
    frameColor = resources.getColor(R.color.viewfinder_frame);
    laserColor = resources.getColor(R.color.viewfinder_laser);
    resultPointColor = resources.getColor(R.color.possible_result_points);
    scannerAlpha = 0;
    possibleResultPoints = new HashSet<ResultPoint>(5);
    
  }

  @Override
  public void onDraw(Canvas canvas) {
    Rect frame = CameraManager.get().getFramingRect();
    
    if (frame == null) {
      return;
    }
    System.out.println("frame:"+frame.toString());
    if(!isFirst){
    	isFirst = true;
    	slideTop = frame.top+CORNER_WIDTH;
    	slideBottom = frame.bottom-CORNER_WIDTH;
    }
    
    int width = canvas.getWidth();
    int height = canvas.getHeight();
    // Draw the exterior (i.e. outside the framing rect) darkened
    //画区域
    paint.setColor(resultBitmap != null ? resultColor : maskColor);
    canvas.drawRect(0, 0, width, frame.top, paint);
    canvas.drawRect(0, frame.top, frame.left, frame.bottom + 1, paint);
    canvas.drawRect(frame.right + 1, frame.top, width, frame.bottom + 1, paint);
    canvas.drawRect(0, frame.bottom + 1, width, height, paint);

    if (resultBitmap != null) {
      // Draw the opaque result bitmap over the scanning rectangle
      paint.setAlpha(OPAQUE);
      canvas.drawBitmap(resultBitmap, frame.left, frame.top, paint);
    } else {

      // Draw a two pixel solid black border inside the framing rect
    	//画框架
      paint.setColor(Color.GREEN);
//      canvas.drawRect(frame.left, frame.top, frame.right + 1, frame.top + 2, paint);
//      canvas.drawRect(frame.left, frame.top + 2, frame.left + 2, frame.bottom - 1, paint);
//      canvas.drawRect(frame.right - 1, frame.top, frame.right + 1, frame.bottom - 1, paint);
//      canvas.drawRect(frame.left, frame.bottom - 1, frame.right + 1, frame.bottom + 1, paint);
   //public void drawRect (float left, float top, float right, float bottom, Paint paint) 
//自己画（扫描框边上的角，共8个部分）
//      int phoneWidth = PhoneConfig.getPhoneWidth(context);
//      int phoneHeight = PhoneConfig.getPhoneHeight(context) - 55;
      //左上角横线
      canvas.drawRect(frame.left-CORNER_WIDTH/2, frame.top-CORNER_WIDTH/2, frame.left+ScreenRate, frame.top+CORNER_WIDTH/2, paint);
      //左上角竖线
      canvas.drawRect(frame.left-CORNER_WIDTH/2, frame.top-CORNER_WIDTH/2, frame.left+CORNER_WIDTH/2, frame.top+ScreenRate, paint);
      //左下角竖线
      canvas.drawRect(frame.left-CORNER_WIDTH/2, frame.bottom-ScreenRate, frame.left+CORNER_WIDTH/2, frame.bottom+CORNER_WIDTH/2, paint);
      //左下角横线
      canvas.drawRect(frame.left-CORNER_WIDTH/2, frame.bottom-CORNER_WIDTH/2, frame.left+ScreenRate, frame.bottom+CORNER_WIDTH/2, paint);
      //右上角横线
      canvas.drawRect(frame.right-ScreenRate, frame.top-CORNER_WIDTH/2, frame.right+CORNER_WIDTH/2, frame.top+CORNER_WIDTH/2, paint);
      //右上角竖线
      canvas.drawRect(frame.right-CORNER_WIDTH/2, frame.top-CORNER_WIDTH/2, frame.right+CORNER_WIDTH/2, frame.top+ScreenRate, paint);
      //右下角竖线
      canvas.drawRect(frame.right-CORNER_WIDTH/2, frame.bottom-ScreenRate, frame.right+CORNER_WIDTH/2, frame.bottom+CORNER_WIDTH/2, paint);
      //右下角横线
      canvas.drawRect(frame.right-ScreenRate, frame.bottom-CORNER_WIDTH/2, frame.right+CORNER_WIDTH/2, frame.bottom+CORNER_WIDTH/2, paint);
      //直接用图片  
//      Rect bigRect = new Rect();
//		bigRect.left = frame.left;
//		bigRect.right = frame.right;
//		bigRect.top = frame.top;
//		bigRect.bottom = frame.bottom;
//		Drawable drawable =  getResources().getDrawable(R.drawable.qr_mask);
//		BitmapDrawable b= (BitmapDrawable) drawable;
//		canvas.drawBitmap(b.getBitmap(), null, bigRect, paint);
      
      
      
      
      // Draw a red "laser scanner" line through the middle to show decoding is active
//      paint.setColor(laserColor);
//      paint.setAlpha(SCANNER_ALPHA[scannerAlpha]);
//      scannerAlpha = (scannerAlpha + 1) % SCANNER_ALPHA.length;
//      int middle = frame.height() / 2 + frame.top;
//      canvas.drawRect(frame.left + 2, middle - 1, frame.right - 1, middle + 2, paint);

     
      
      
      //画中间移动的线
      slideTop += SPEEN_DISTANCE;
      if(slideTop >= slideBottom){
    	  slideTop = frame.top+CORNER_WIDTH;
      }
      //自己画
//      paint.setColor(Color.GREEN);
//      canvas.drawRect(frame.left+CORNER_WIDTH, slideTop, frame.right-CORNER_WIDTH, slideTop+MIDDLE_LINE_WIDTH, paint);
      
      
      //用图片
      Rect lineRect = new Rect();
		lineRect.left = frame.left;
		lineRect.right = frame.right;
		lineRect.top = slideTop;
		lineRect.bottom = slideTop + MIDDLE_LINE_PADDING;
		canvas.drawBitmap(((BitmapDrawable)(getResources().getDrawable(R.mipmap.qrcode_scan_line))).getBitmap(), null, lineRect, paint);
		
      
      //画扫描框下面的字
		paint.setColor(Color.WHITE);
		paint.setTextSize(TEXT_SIZE*density);
		paint.setAlpha(0x40);
		paint.setTypeface(Typeface.create("System", Typeface.BOLD));
		canvas.drawText(getResources().getString(R.string.msg_default_status), frame.left, frame.bottom+TEXT_PADDING_TOP*density, paint);
      
      
      
      Collection<ResultPoint> currentPossible = possibleResultPoints;
      Collection<ResultPoint> currentLast = lastPossibleResultPoints;
      if (currentPossible.isEmpty()) {
        lastPossibleResultPoints = null;
      } else {
        possibleResultPoints = new HashSet<ResultPoint>(5);
        lastPossibleResultPoints = currentPossible;
        paint.setAlpha(OPAQUE);
        paint.setColor(resultPointColor);
        for (ResultPoint point : currentPossible) {
          canvas.drawCircle(frame.left + point.getX(), frame.top + point.getY(), 6.0f, paint);//画扫描到的可能的点
        }
      }
      if (currentLast != null) {
        paint.setAlpha(OPAQUE / 2);
        paint.setColor(resultPointColor);
        for (ResultPoint point : currentLast) {
          canvas.drawCircle(frame.left + point.getX(), frame.top + point.getY(), 3.0f, paint);
        }
      }

      // Request another update at the animation interval, but only repaint the laser line,
      // not the entire viewfinder mask.
      postInvalidateDelayed(ANIMATION_DELAY, frame.left, frame.top, frame.right, frame.bottom);
    }
  }

  public void drawViewfinder() {
    resultBitmap = null;
    invalidate();
  }

  /**
   * Draw a bitmap with the result points highlighted instead of the live scanning display.
   *
   * @param barcode An image of the decoded barcode.
   */
  public void drawResultBitmap(Bitmap barcode) {
    resultBitmap = barcode;
    invalidate();
  }

  public void addPossibleResultPoint(ResultPoint point) {
    possibleResultPoints.add(point);
  }

}
