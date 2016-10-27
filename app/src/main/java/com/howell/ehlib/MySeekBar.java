package com.howell.ehlib;

import android.content.Context;
import android.graphics.Canvas;
import android.util.AttributeSet;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.widget.PopupWindow;
import android.widget.SeekBar;
import android.widget.TextView;

import com.howell.ecamh265.R;

public class MySeekBar extends SeekBar {
	private PopupWindow mPopupWindow;

	private LayoutInflater mInflater;
	private View mView;
	private int[] mPosition;

	private final int mThumbWidth = 25;
	private TextView mTvProgress;
	
//	private int phoneWidth,phoneHeight = 800;
//	private LinearLayout iv;
	public MySeekBar(Context context, AttributeSet attrs) {
		super(context, attrs);
		// TODO Auto-generated constructor stub
		
		mInflater = LayoutInflater.from(context);
		mView = mInflater.inflate(R.layout.popwindow_layout, null);
		mTvProgress = (TextView)mView.findViewById(R.id.tvPop);
		mPopupWindow = new PopupWindow(mView, mView.getWidth(),mView.getHeight(), true);
		mPosition = new int[2];
	}

	public void setSeekBarText(String str){
		mTvProgress.setText(str);
	}
	
	@Override
	public boolean onTouchEvent(MotionEvent event) {
		// TODO Auto-generated method stub

		switch (event.getAction()) {
		case MotionEvent.ACTION_DOWN:
			this.getLocationOnScreen(mPosition);
//			mPopupWindow.showAsDropDown(this, (int) event.getX(),
//					mPosition[1] - 30);
			int thumb_x = this.getProgress() * (this.getWidth() - mThumbWidth)
					/ this.getMax();
			int middle = mPosition[1] - mPopupWindow.getHeight();
			System.out.println(middle+",,,"+getViewHeight(this));
			mPopupWindow.showAtLocation(mView, Gravity.LEFT|Gravity.TOP ,thumb_x+mPosition[0] - getViewWidth(mView) / 2+mThumbWidth/2,
					middle);
			
			break;
		case MotionEvent.ACTION_UP:
			mPopupWindow.dismiss();
			break;
		}

		return super.onTouchEvent(event);
	}

	private int getViewWidth(View v){
		int w = MeasureSpec.makeMeasureSpec(0, MeasureSpec.UNSPECIFIED);
        int h = MeasureSpec.makeMeasureSpec(0, MeasureSpec.UNSPECIFIED);
        v.measure(w, h);   
        return v.getMeasuredWidth();
	}
	private int getViewHeight(View v){
		int w = MeasureSpec.makeMeasureSpec(0, MeasureSpec.UNSPECIFIED);
        int h = MeasureSpec.makeMeasureSpec(0, MeasureSpec.UNSPECIFIED);
        v.measure(w, h);
        return v.getMeasuredHeight();
	}
	@Override
	protected synchronized void onDraw(Canvas canvas) {
		// TODO Auto-generated method stub
		int thumb_x = 0;
		try{
			thumb_x = this.getProgress() * (this.getWidth() - mThumbWidth)
				/ this.getMax();
		}catch(Exception e){
			
		}
		int middle = mPosition[1] - mPopupWindow.getHeight();
		super.onDraw(canvas);

		if (mPopupWindow != null) {	
			try {
				this.getLocationOnScreen(mPosition);
				mPopupWindow.update(thumb_x+mPosition[0] - getViewWidth(mView) / 2+mThumbWidth/2,
						middle,getViewWidth(mView),getViewHeight(mView));
				
			} catch (Exception e) {
				// TODO: handle exception
			}
		}

	}

}
