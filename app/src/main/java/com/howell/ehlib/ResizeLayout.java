package com.howell.ehlib;

import android.content.Context;
import android.util.AttributeSet;
import android.util.Log;
import android.widget.LinearLayout;

public class ResizeLayout extends LinearLayout{    
private OnResizeListener mListener;   
  
public interface OnResizeListener {   
	void OnResize(int w, int h, int oldw, int oldh);   
	}   
	     
	public void setOnResizeListener(OnResizeListener l) {   
		mListener = l;   
	}   
	 
	public ResizeLayout(Context context, AttributeSet attrs) {   
		super(context, attrs);   
	}   
	  
	@Override   
	protected void onSizeChanged(int w, int h, int oldw, int oldh) {       
		super.onSizeChanged(w, h, oldw, oldh);   
		Log.e("resizelayout", "h:"+h+",oldh:"+oldh);  
		if (mListener != null) {   
			mListener.OnResize(w, h, oldw, oldh);   
		}   
	}   
}   