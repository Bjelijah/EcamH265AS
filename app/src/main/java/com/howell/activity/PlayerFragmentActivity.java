package com.howell.activity;


import android.content.Intent;
import android.content.IntentFilter;
import android.opengl.GLSurfaceView;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.v4.app.FragmentActivity;
import android.util.Log;
import android.view.GestureDetector;
import android.view.GestureDetector.OnGestureListener;
import android.view.KeyEvent;
import android.view.MotionEvent;
import android.view.SurfaceHolder;
import android.view.SurfaceHolder.Callback;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.View.OnTouchListener;
import android.view.WindowManager;
import android.widget.FrameLayout;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.howell.action.AudioAction;
import com.howell.action.PlatformAction;
import com.howell.action.TurnProtocolMgr;
import com.howell.action.YV12Renderer;
import com.howell.broadcastreceiver.HomeKeyEventBroadCastReceiver;
import com.howell.ecamh265.R;
import com.howell.ehlib.MySeekBar;
import com.howell.jni.JniUtil;
import com.howell.utils.IConst;
import com.howell.utils.MessageUtiles;

public class PlayerFragmentActivity extends FragmentActivity implements Callback,OnTouchListener,OnGestureListener,OnClickListener,IConst {
	
	private GLSurfaceView mGlView;
	
	private YV12Renderer mRenderer;
	private GestureDetector mGestureDetector;
	private LinearLayout mSurfaceIcon;
	private FrameLayout mTitle;
	private ImageButton mVedioList,mCatchPicture,mSound,mBack,mPause;
	private TextView mStreamChange,mStreamLen;
	private MySeekBar mReplaySeekBar;
	private ProgressBar mWaitProgressBar ;
	private TurnProtocolMgr mMgr;
	private PlatformAction mPlat;
	private boolean mIsPlayBack;
	
	private boolean mHorizontal;
	
	private static final int MSG_PLAY_PREPARE = 0x90;
	private static final int MSG_PLAY_FRAME   = 0x91;
	
	Handler handler = new Handler(){
		@Override
		public void handleMessage(Message msg) {
			switch (msg.what) {
			case MSG_TURN_CONNECT_OK:
				//TODO subscribe
				playVideo();
				break;
			case MSG_TURN_SUBSCRIBE_OK:
				//TODO 
				playViewInit();
				break;
				
			case MSG_TURN_DISSUBSCRIBE_OK:
				//TODO disconnect\
				handler.removeMessages(MSG_PLAY_FRAME);
				disconnect();
				break;
			
			case MSG_TURN_DISCONNECT_OK:
				finish();
				break;
				
			case MSG_TURN_CONNECT_FAIL:
			case MSG_TURN_SUBSCRIBE_FAIL:
			case MSG_TURN_DISSUBSCRIBE_FAIL:
			case MSG_TURN_DISCONNECT_FAIL:
				Log.e("123", "msg turn fail:"+(String)msg.obj);
				break;
			
			case MSG_PLAY_PREPARE:
				if(!checkFrameCome()){
					handler.sendEmptyMessageDelayed(MSG_PLAY_PREPARE, 200);
				}
				break;
			
			case MSG_PLAY_FRAME:
				checkFrameLen();
				handler.sendEmptyMessageDelayed(MSG_PLAY_FRAME, 1000);
				break;
				
			default:
				break;
			}
			
			
			super.handleMessage(msg);
		}
	};
	@Override
	protected void onCreate(Bundle arg0) {
		// TODO Auto-generated method stub
		super.onCreate(arg0);
		getWindow().addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);
		Activities activities = Activities.getInstance();
		activities.addActivity("PlayerActivity",PlayerFragmentActivity.this);
		HomeKeyEventBroadCastReceiver receiver = new HomeKeyEventBroadCastReceiver();
		registerReceiver(receiver, new IntentFilter(Intent.ACTION_CLOSE_SYSTEM_DIALOGS));
		setContentView(R.layout.glsurface);
		mGlView = (GLSurfaceView)findViewById(R.id.glsurface_view);
		mGlView.setEGLContextClientVersion(2);
		mRenderer = new YV12Renderer(this, mGlView, handler);
		mGlView.setRenderer(mRenderer);
		mGlView.getHolder().addCallback(this);
		mGlView.setRenderMode(GLSurfaceView.RENDERMODE_WHEN_DIRTY);
		mGlView.setOnTouchListener(this);   
		mGlView.setFocusable(true);   
		mGlView.setClickable(true);   
		mGlView.setLongClickable(true);  
		mGestureDetector = new GestureDetector(this,this);
		mGestureDetector.setIsLongpressEnabled(true);  
		mSurfaceIcon = (LinearLayout) findViewById(R.id.surface_icons);
		mTitle = (FrameLayout) findViewById(R.id.player_title_bar);
		mVedioList = (ImageButton) findViewById(R.id.vedio_list);
		mVedioList.setOnClickListener(this);
		mCatchPicture = (ImageButton) findViewById(R.id.catch_picture);
		mCatchPicture.setOnClickListener(this);
		mSound = (ImageButton) findViewById(R.id.sound);
		mSound.setOnClickListener(this);
		mBack = (ImageButton) findViewById(R.id.player_imagebutton_back);
		mBack.setOnClickListener(this);
		mPause = (ImageButton) findViewById(R.id.ib_pause);
		mPause.setOnClickListener(this);
		mStreamChange = (TextView) findViewById(R.id.player_change_stream);
		mStreamChange.setOnClickListener(this);
		mStreamLen = (TextView) findViewById(R.id.tv_stream_len);
		mReplaySeekBar = (MySeekBar)findViewById(R.id.replaySeekBar);
		mWaitProgressBar = (ProgressBar)findViewById(R.id.waitProgressBar);
		stateInit();
		mMgr = TurnProtocolMgr.getInstance();
		mMgr.setContext(this).setHander(handler);
		mPlat = PlatformAction.getInstance();
		AudioAction.getInstance().initAudio();
	}

	private void stateInit(){
		Intent intent = getIntent();
		mIsPlayBack = intent.getBooleanExtra("bPlayBack", false);
		if (mIsPlayBack) {
			mReplaySeekBar.setVisibility(View.VISIBLE);
			mPause.setVisibility(View.VISIBLE);
			mVedioList.setVisibility(View.GONE);
			mVedioList.setEnabled(false);
		}else{
			mReplaySeekBar.setVisibility(View.GONE);
			mPause.setVisibility(View.GONE);
			mVedioList.setVisibility(View.VISIBLE);
		}
		if (PlatformAction.getInstance().getCurSelNode().iseStoreFlag()) {
			mVedioList.setBackground(getResources().getDrawable(R.mipmap.img_record));
		}else{
			mVedioList.setBackground(getResources().getDrawable(R.mipmap.img_no_record));
		}
	}
	
	private void playViewInit(){
		
		if (mIsPlayBack) {
			
		}else{
			handler.sendEmptyMessage(MSG_PLAY_FRAME);
		}
		
	}
	
	private void checkFrameLen(){
		int len = JniUtil.transGetStreamLenSomeTime();
		len = len/1024*8;
		mStreamLen.setText(len + " Kbit/s");		
		if (len==0) {
			mRenderer.setTime(0);
			handler.sendEmptyMessage(MSG_PLAY_PREPARE);
		}
	}
	
	private boolean checkFrameCome(){
		if (mRenderer.getTime()!=0) {
			mWaitProgressBar.setVisibility(View.GONE);
			return true;
		}else{
			mWaitProgressBar.setVisibility(View.VISIBLE);
			return false;
		}
	}
	
	private void playVideo(){
		if (mIsPlayBack) {
			
		}else{
			mMgr.subScribeCamLiveStream(mPlat.getCurSelDeviceId(), mPlat.getCurSelNode().getChannelNo(), false);
		}
	}
	

	
	private void StopVideo(){
		mMgr.disSubscribeStream();
		
	}
	
	private void disconnect(){
		mMgr.disConnect2TurnService();
	}
	
	@Override
	protected void onStart() {
		mMgr.connect2TurnService();
		mWaitProgressBar.setVisibility(View.VISIBLE);
		handler.sendEmptyMessage(MSG_PLAY_PREPARE);
		AudioAction.getInstance().playAudio();
		super.onStart();
	}
	
	@Override
	protected void onStop() {
		StopVideo();
		AudioAction.getInstance().stopAudio();
		super.onStop();
	}
	
	@Override
	protected void onDestroy() {
		// TODO Auto-generated method stub
		AudioAction.getInstance().deInitAudio();
		super.onDestroy();
	}
	
	
	@Override
	public void surfaceCreated(SurfaceHolder holder) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void surfaceChanged(SurfaceHolder holder, int format, int width, int height) {
		// TODO Auto-generated method stub
		if (width>height) {//横
			mHorizontal = true;
			mSurfaceIcon.setVisibility(View.GONE);
			mTitle.setVisibility(View.GONE);
		}else{
			mHorizontal = false;
			mSurfaceIcon.setVisibility(View.VISIBLE);
			mTitle.setVisibility(View.VISIBLE);
		}
		
		
	}

	
	
	@Override
	public void surfaceDestroyed(SurfaceHolder holder) {
		// TODO Auto-generated method stub
		
	}
	
	@Override
	public boolean onKeyDown(int keyCode, KeyEvent event) {
		switch (keyCode) {
		case KeyEvent.KEYCODE_BACK:
			//TODO quit activity
			break;

		default:
			break;
		}
		return super.onKeyDown(keyCode, event);
	}

	@Override
	public boolean onDown(MotionEvent e) {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public void onShowPress(MotionEvent e) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public boolean onSingleTapUp(MotionEvent e) {
		// TODO Auto-generated method stub
		if (mHorizontal) {
			if (mSurfaceIcon.isShown()) {
				mSurfaceIcon.setVisibility(View.GONE);
			}else{
				mSurfaceIcon.setVisibility(View.VISIBLE);
			}
		}
		
		return false;
	}

	@Override
	public boolean onScroll(MotionEvent e1, MotionEvent e2, float distanceX, float distanceY) {
		// TODO Auto-generated method stub
		
		return false;
	}

	@Override
	public void onLongPress(MotionEvent e) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public boolean onFling(MotionEvent e1, MotionEvent e2, float velocityX, float velocityY) {
		// TODO Auto-generated method stub
		if (!mHorizontal) {
			return false;
		}

		return false;
	}

	@Override
	public boolean onTouch(View v, MotionEvent event) {
		// TODO Auto-generated method stub
		return mGestureDetector.onTouchEvent(event);   
	}

	@Override
	public void onClick(View v) {
		// TODO Auto-generated method stub
		switch (v.getId()) {
		case R.id.vedio_list:
			if (PlatformAction.getInstance().getCurSelNode().iseStoreFlag()) {
				//quit activity
				
				finish();
				//
				
			}else{
				MessageUtiles.postToast(getApplicationContext()
        				, getResources().getString(R.string.no_sdcard),2000);
			}
			break;
		case R.id.catch_picture:
			break;
		case R.id.sound:
			break;
		case R.id.player_imagebutton_back:
			break;
		case R.id.ib_pause:
			break;
		case R.id.player_change_stream:
			break;
		default:
			break;
		}
	}
}
