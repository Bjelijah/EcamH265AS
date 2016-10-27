package com.howell.action;

import android.os.Handler;
import android.os.Message;
import android.util.Log;

import com.howell.activity.FingerPrintFragment;

import java.util.Timer;
import java.util.TimerTask;

public class MyTimeMgr {
	
	private static MyTimeMgr mInstance = null;
	public static MyTimeMgr getInstance(){
		if (mInstance==null) {
			mInstance = new MyTimeMgr();
		}
		return mInstance;
	}
	private MyTimeMgr(){}
	
	private Handler mHandler;
	private int mSec;
	private boolean isRunning;
	public Handler getmHandler() {
		return mHandler;
	}
	public MyTimeMgr setmHandler(Handler mHandler) {
		this.mHandler = mHandler;
		return this;
	}
	public int getmSec() {
		return mSec;
	}
	public MyTimeMgr setmSec(int mSec) {
		this.mSec = mSec;
		return this;
	}
	public boolean isRunning() {
		return isRunning;
	}
	public MyTimeMgr setRunning(boolean isRunning) {
		this.isRunning = isRunning;
		return this;
	}

	Timer mWaitTimer = null;
	TimerTask mTimerTask = null;
	
	private TimerTask cerateTimeTask(){
		return new TimerTask() {
			@Override
			public boolean cancel() {
				isRunning = false;
				return super.cancel();
			}
			@Override
			public void run() {
				isRunning = true;
				if (mSec<0 ) {
					if(mHandler!=null) mHandler.sendEmptyMessage(FingerPrintFragment.MSG_ERROR_WAIT_OK);
					stopTimeTask();
				}else {
					//绘制
					Message msg = new Message();
					msg.what = FingerPrintFragment.MSG_WAIT_SEC;
					msg.arg1 = mSec;
					if(mHandler!=null)mHandler.sendMessage(msg);
				}
				mSec--;
			}
		};
	}
	
	public void startTimeTask(int sec){
		Log.e("123", "start time task");
		if (!isRunning) {
			setmSec(sec);
			mWaitTimer = new Timer();
			mTimerTask = cerateTimeTask();
			mWaitTimer.schedule(mTimerTask, 0,1000);
		}
	}
	
	private void stopTimeTask(){
		if (mWaitTimer!=null) {
			mWaitTimer.cancel();
			mWaitTimer.purge();
			mWaitTimer = null;
		}
		if (mTimerTask!=null) {
			mTimerTask.cancel();
			mTimerTask = null;
		}
	}
	
	

	
}
