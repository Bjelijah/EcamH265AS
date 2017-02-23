package com.howell.activity;


import java.io.File;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.TimeZone;
import java.util.Timer;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.content.res.Configuration;
import android.drm.DrmStore.Action;
import android.graphics.drawable.ColorDrawable;
import android.media.AudioFormat;
import android.media.AudioManager;
import android.media.AudioTrack;
import android.opengl.GLSurfaceView;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.os.Message;
import android.os.StatFs;
import android.util.Log;
import android.view.GestureDetector;
import android.view.GestureDetector.OnGestureListener;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.SurfaceHolder;
import android.view.SurfaceHolder.Callback;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.View.OnTouchListener;
import android.view.WindowManager;
import android.view.animation.Animation;
import android.view.animation.Animation.AnimationListener;
import android.view.animation.TranslateAnimation;
import android.widget.Button;
import android.widget.FrameLayout;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.PopupWindow;
import android.widget.ProgressBar;
import android.widget.SeekBar;
import android.widget.SeekBar.OnSeekBarChangeListener;
import android.widget.TextView;
import android.widget.Toast;


import com.howell.action.AudioAction;
import com.howell.action.PlayerManager;
import com.howell.broadcastreceiver.HomeKeyEventBroadCastReceiver;
import com.howell.ecamh265.R;
import com.howell.ehlib.MySeekBar;
import com.howell.entityclass.NodeDetails;
import com.howell.entityclass.VODRecord;
import com.howell.jni.JniUtil;
import com.howell.playerrender.YV12Renderer;
import com.howell.protocol.GetDevVerReq;
import com.howell.protocol.GetDevVerRes;
import com.howell.protocol.LoginResponse;
import com.howell.protocol.PtzControlReq;
import com.howell.protocol.PtzControlRes;
import com.howell.protocol.SoapManager;
import com.howell.utils.DeviceVersionUtils;
import com.howell.utils.FileUtils;
import com.howell.utils.IConst;
import com.howell.utils.InviteUtils;
import com.howell.utils.MessageUtiles;
import com.howell.utils.PhoneConfig;
import com.howell.utils.TakePhotoUtil;
import com.howell.utils.TalkManager;
import com.howell.utils.Util;

public class PlayerActivity extends Activity implements Callback, OnTouchListener, OnGestureListener,OnClickListener,IConst {
	
	
	
	public static InviteUtils client;
	private static PlayerActivity mPlayer;
	private Thread inviteThread;
	private static boolean playback=false;
	private static NodeDetails dev;
	private boolean mPausing=false;
	private GLSurfaceView mGlView;
	private VODRecord mRecord;
	private AudioTrack mAudioTrack;
	private byte[] mAudioData;
	private int mAudioDataLength;
	private static int backCount;
	private static long startTime,endTime;
	private boolean isAudioOpen;
	private boolean isShowSurfaceIcon;
	public static boolean stopSendMessage;
	private static long firstFrameTime,endFrameTime;
    private static int frameFlag;
    private static int streamLenFlag;
    private static int streamLen;
    
	private LinearLayout mSurfaceIcon;
    private static MySeekBar mReplaySeekBar;
    private static ProgressBar mWaitProgressBar;
    private static PlayerHandler mPlayerHandler;
    private static ImageButton mVedioList;
	private ImageButton mSound,mCatchPicture/*,mStreamChange*/;
	private TextView mStreamChange;
    private static TextView mStreamLen;
    private ImageButton mPause,mBack;
    private FrameLayout mTitle;
	
	public static final Integer REPLAYSEEKBAR = 0x0001;
	public static final Integer STOPPROGRESSBAR = 0x0002;
	public static final Integer SHOWPROGRESSBAR = 0x0003;
	public static final Integer HIDEPROGRESSBAR = 0x0004;
	public static final Integer TIMEOUT = 0x0005;
	public static final Integer POSTERROR = 0x0006;
	public static final Integer SHOWSTREAMLEN = 0x0007;
	public static final Integer SETVEDIOLISTENABLE = 0x0008;
	public static final Integer SHOW_NO_STREAM_ARRIVE_PROGRESS = 0x0009;
	public static final Integer HIDE_HAS_STREAM_ARRIVE_PROGRESS = 0x0010;
	public static final Integer DETECT_IF_NO_STREAM_ARRIVE = 0x0011;
	public static final Integer MSG_DISCONNECT_UNEXPECT		   = 0x0012;
	
	
	private SoapManager mSoapManger;
	private String account,loginSession,devID;
	private int channelNo;
	private MyFlingTask mFlingTask;
	private GestureDetector mGestureDetector;
	
	private Animation translateAnimation;
	private ImageView animationAim,animationBackground;
	private boolean inviteRet;
	
	private static int nowFrames;
	private static int lastSecondFrames;
	
//	private TitlePopup titlePopup;
	
	private static Timer mTimer;
	private static boolean progressHasStop;
	
	public  AudioManager audiomanage;  
	private int maxVolume ;  
	
	private static long correctedStartTime;
	private static long correctedEndTime;
	private static int stopTrackingTouchProgress;
	
	boolean bPause ;
	boolean isAnimationStart;
	private Activities mActivities;
	private HomeKeyEventBroadCastReceiver receiver;
	
	private int stream;//主次码流
	private PopupWindow mPopupWindow;  
	private LinearLayout hd,sd;
	
	
	
	
	public PlayerActivity() {   
        mGestureDetector = new GestureDetector(this);   
    } 
	
	static {
        System.loadLibrary("hwplay");
        System.loadLibrary("player_jni");
    }
	
	public native void nativeAudioInit();
	public static native void nativeAudioStop();
	
	private TextView talk;
	private TalkManager talkManger;
	private Button btTalk;
	private static PlayerManager playMgr = PlayerManager.getInstance();
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		mPlayerHandler = new PlayerHandler();
		getWindow().addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);
		
		mActivities = Activities.getInstance();
        mActivities.addActivity("PlayerActivity",PlayerActivity.this);
        receiver = new HomeKeyEventBroadCastReceiver();
        
		registerReceiver(receiver, new IntentFilter(
				Intent.ACTION_CLOSE_SYSTEM_DIALOGS));
		Log.e("main","activity on create");
		setContentView(R.layout.glsurface);
		mGlView = (GLSurfaceView)findViewById(R.id.glsurface_view);
		System.out.println("mGlView:"+mGlView.toString());
		mGlView.setEGLContextClientVersion(2);
		//mGlView.setRenderer(new YV12Renderer(this,mGlView));
		mGlView.setRenderer(new com.howell.action.YV12Renderer(this, mGlView, mPlayerHandler));
		mGlView.getHolder().addCallback((Callback) this);
		mGlView.setRenderMode(GLSurfaceView.RENDERMODE_WHEN_DIRTY);
		
		mGlView.setOnTouchListener(this);   
		mGlView.setFocusable(true);   
		mGlView.setClickable(true);   
		mGlView.setLongClickable(true);   
        mGestureDetector.setIsLongpressEnabled(true);  
        
        Intent intent = getIntent();
		if (intent.getSerializableExtra("arg") instanceof NodeDetails) {
            dev = (NodeDetails) intent.getSerializableExtra("arg");
			Log.e("123","dev="+dev.toString());
            playback = false;
		} else if (intent.getSerializableExtra("arg") instanceof VODRecord) {
            mRecord = (VODRecord) intent.getSerializableExtra("arg");
            dev = (NodeDetails) intent.getSerializableExtra("nodeDetails");
            playback = true;
            mPlayerHandler.setRecord(mRecord);
        }
		
        mSoapManger = SoapManager.getInstance();
    	SoapManager.context = this;
        LoginResponse res = mSoapManger.getLoginResponse();
        account = res.getAccount();
        loginSession = res.getLoginSession();
        devID = dev.getDevID();
        channelNo =	dev.getChannelNo();
        
		backCount = 0;
		isAudioOpen = true;
		frameFlag = 0;
		stopSendMessage = false;
		YV12Renderer.time = 0;
		mPlayer = this;
		streamLenFlag = 0;
		streamLen = 0;
		isShowSurfaceIcon = true;
		client = null;
		nowFrames = 0;
		lastSecondFrames = 0;
		progressHasStop = false;
		bPause = true;
		isAnimationStart = false;
		correctedStartTime = -1;
		correctedEndTime = -1;
		stopTrackingTouchProgress = 0;
		stream = 1;//默认次码流
		
		SharedPreferences sharedPreferences = getSharedPreferences("set",
                Context.MODE_PRIVATE);
        boolean soundMode = sharedPreferences.getBoolean("sound_mode", true);
        System.out.println("soundMode:"+soundMode);
		if (mRecord != null) {
            try {
                SimpleDateFormat bar = new SimpleDateFormat(
                        "yyyy-MM-dd'T'HH:mm:ss");
                bar.setTimeZone(TimeZone.getTimeZone("UTC"));
                startTime = bar.parse(mRecord.getStartTime()).getTime()/1000;
                endTime = bar.parse(mRecord.getEndTime()).getTime()/1000;
            } catch (ParseException e) {
                e.printStackTrace();
            }
        }
		
		mVedioList = (ImageButton) findViewById(R.id.vedio_list);
		if(dev.iseStoreFlag()){
			//.setEnabled(true);
			mVedioList.setImageResource(R.mipmap.img_record);
		}else{
			//mVedioList.setEnabled(false);
			mVedioList.setImageResource(R.mipmap.img_no_record);
		}
	    mVedioList.setOnClickListener(new OnClickListener() {
	        @Override
	        public void onClick(View v) {
	        	if(!dev.iseStoreFlag()){
	        		MessageUtiles.postToast(getApplicationContext()
	        				, getResources().getString(R.string.no_sdcard),2000);
	        	}else{
		        	if(null != client)
		        		client.setQuit(true);
		        	quitDisplay();
	        		if(!playback){
	            		TakePhotoUtil.takePhoto("/sdcard/eCamera/cache", dev, client);
	    			}
	        		finish();
		            Log.e("", "00000000");
		            Intent intent = new Intent(PlayerActivity.this, VideoList.class);
		            intent.putExtra("Device", dev);
		            startActivity(intent);
	        	}
	        }
	    });
	    
	    talk = (TextView)findViewById(R.id.player_talk);
	    talk.setVisibility(View.GONE);//FIXME
	    talk.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				if(talkManger != null){
					if(talkManger.getTalkState() == TalkManager.TALKING){
						talk.setText("开始对讲");
						if(mAudioTrack != null){
							mAudioTrack.play();
						}
						talkManger.stopTalk();
					}else{
						talk.setText("停止对讲");
						if(mAudioTrack != null){
							mAudioTrack.pause();
						}
						talkManger.startTalk();
					}
				}
			}
		});
	   
	    btTalk = (Button)findViewById(R.id.play_talk);
	    btTalk.setVisibility(View.GONE);//FIXME
	    btTalk.setOnTouchListener(new OnTouchListener() {
			
			@Override
			public boolean onTouch(View v, MotionEvent event) {
				
				switch (event.getAction()) {
				case MotionEvent.ACTION_DOWN:
					Log.i("123", "按下了   开始对讲");
					if(mAudioTrack != null){
						mAudioTrack.pause();
					}
					talkManger.startTalk();
					break;
				case MotionEvent.ACTION_UP:
					Log.i("123", "ACTION_UP   停止对讲");
					if(mAudioTrack != null){
						mAudioTrack.play();
					}
					talkManger.stopTalk();		
					break;
				case MotionEvent.ACTION_CANCEL:
					Log.i("123", "ACTION_CANCEL   停止对讲");
					if(mAudioTrack != null){
						mAudioTrack.play();
					}
					talkManger.stopTalk();
					break;
				default:
					//Log.i("123", "default");
					break;
				}
				
				
				return false;
			}
		});

	    mCatchPicture = (ImageButton)findViewById(R.id.catch_picture);
	    mCatchPicture.setOnClickListener(new OnClickListener() {
				
			@Override
			public void onClick(View v) {
				if(!existSDCard()){
					MessageUtiles.postToast(getApplicationContext(), getResources().getString(R.string.no_sdcard),2000);
					return;
				}
				File destDir = new File("/sdcard/eCamera");
				if (!destDir.exists()) {
					destDir.mkdirs();
				}
				String path = "/sdcard/eCamera/"+FileUtils.getFileName()+".jpg";
//				if(client.setCatchPictureFlag(client.getHandle(),path,path.length()) == 1)
//					MessageUtiles.postToast(getApplicationContext(), getResources().getString(R.string.save_picture),2000);
			
				JniUtil.catchPic(path);
				MessageUtiles.postToast(getApplicationContext(), getResources().getString(R.string.save_picture),2000);
			}
	    });
	    System.out.println("audio init");
	    audioInit();
	    audiomanage = (AudioManager)getSystemService(Context.AUDIO_SERVICE); 
	    maxVolume = audiomanage.getStreamMaxVolume(AudioManager.STREAM_MUSIC);  
	    System.out.println("maxVolume:"+maxVolume);
	    
	  
	    mSound = (ImageButton)findViewById(R.id.sound);
	
	    
	    
	    if(soundMode){
	    	System.out.println("soundMode:"+soundMode);
	    	isAudioOpen = true;
			mSound.setImageDrawable(getResources().getDrawable(R.mipmap.img_sound));
	    }
        else {
        	System.out.println("soundMode:"+soundMode);
        	audioPause();
        }
	    mSound.setOnClickListener(new OnClickListener() {
				
			@Override
			public void onClick(View v) {
				Log.e("sdl--->", "mSound.setOnClickListener");
				if(isAudioOpen){
					audioPause();
					
				}else {
					audioPlay();
				}
				SharedPreferences sharedPreferences = getSharedPreferences(
		                "set", Context.MODE_PRIVATE);
		        Editor editor = sharedPreferences.edit();
		        editor.putBoolean("sound_mode", isAudioOpen);
		        editor.commit();
					
			}
	    });
	    
	    mTitle = (FrameLayout)findViewById(R.id.player_title_bar);
	    
	    mStreamChange = (TextView)findViewById(R.id.player_change_stream);
	    mStreamChange.setText("高清");
//	    mStreamChange.setVisibility(View.GONE);
//	    LayoutConfig config = mesureViewWidth(mStreamChange);
	    mStreamChange.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				getPopupWindowInstance();  
		        mPopupWindow.showAsDropDown(v);  
			}
		});
	    
	    new AsyncTask<Void, Integer, Void>(){
	    	boolean isNewVer = false;
	    	@Override
			protected Void doInBackground(Void... arg0) {
				try{
					isNewVer = checkDevVer();
				}catch (Exception e){
					Log.e("123","dev ver error");
					isNewVer = false;
				}

				return null;
			}
	    	
	    	protected void onPostExecute(Void result) {
	    		if(isNewVer){
	    			mStreamChange.setVisibility(View.VISIBLE);
	    		}
	    	};
	    }.execute();
	    
	    /*mStreamChange = (ImageButton)findViewById(R.id.stream);
	    mStreamChange.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				int isPlayBack = -1;
				boolean ret;
				if(playback){
					isPlayBack = 1;
				}else{
					isPlayBack = 0;
					startTime = 0;
					endTime = 0;
				}
				if(stream == 1){
					ret = client.Replay(isPlayBack, startTime, endTime, 0);
					if(ret){
						stream = 0;
						mStreamChange.setImageDrawable(getResources().getDrawable(R.drawable.img_sd));
					}
				}else{
					ret = client.Replay(isPlayBack, startTime, endTime, 1);
					if(ret){
						stream = 1;
						mStreamChange.setImageDrawable(getResources().getDrawable(R.drawable.img_hd));
					}
				}
			}
		});*/
	    
	    mPause = (ImageButton)findViewById(R.id.ib_pause);
	    mPause.setOnClickListener(new OnClickListener() {
	    	
			@Override
			public void onClick(View arg0) {
				if(playback){
					if(bPause){
						client.playbackPause(client.getHandle(), true);
						bPause = false;
						mPause.setImageDrawable(getResources().getDrawable(R.mipmap.img_play));
					}
					else{
						client.playbackPause(client.getHandle(), false);
						bPause = true;
						mPause.setImageDrawable(getResources().getDrawable(R.mipmap.img_pause));
					}
				}
			}
		});
	    
	    mBack = (ImageButton)findViewById(R.id.player_imagebutton_back);
	    mBack.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View arg0) {
				if(null != client)
	        		client.setQuit(true);
	        	quitDisplay();
				if(!playback){
					TakePhotoUtil.takePhoto("/sdcard/eCamera/cache", dev, client);
				}
				finish();
			}
		});
	    
        mReplaySeekBar = (MySeekBar)findViewById(R.id.replaySeekBar);
        if(playback){
			mReplaySeekBar.setVisibility(View.VISIBLE);
			mPause.setVisibility(View.VISIBLE);
			mVedioList.setVisibility(View.GONE);
		}else{
			mReplaySeekBar.setVisibility(View.GONE);
			mPause.setVisibility(View.GONE);
			mVedioList.setVisibility(View.VISIBLE);
		}
        mSurfaceIcon = (LinearLayout)findViewById(R.id.surface_icons);
        System.out.println("activity start progress Bar");
        mWaitProgressBar = (ProgressBar)findViewById(R.id.waitProgressBar);
     
        mPlayerHandler.setContext(this);
        if(playback){
        	Log.e("----------->>>", "onS totoal time:"+endTime +","+ startTime);
        	mVedioList.setEnabled(false);
        	Log.e("---------->>>>", "frames send message");
        	mPlayerHandler.sendEmptyMessage(REPLAYSEEKBAR);
        }
        Log.e("----------->>>", "send stopprogress message!!!!!!!!!!");
        mPlayerHandler.sendEmptyMessage(STOPPROGRESSBAR);
        
        mReplaySeekBar.setOnSeekBarChangeListener(new OnSeekBarChangeListener() {
			
			@Override
			public void onStopTrackingTouch(SeekBar seekBar) {
				int progress = mReplaySeekBar.getProgress();
				Log.e("----------->>>", "onStopTrackingTouch progress:"+progress);
				long replayStartTime = correctedStartTime + (long)progress/1000;
				if(replayStartTime < startTime){
					replayStartTime = startTime;
				}
				Log.e("---------->>>>", "onS startTime:"+replayStartTime+"onS endTime:"+endTime);
				client.Replay(1,replayStartTime, endTime,stream);
				Log.e("---------->>>>", "replay end");
				stopSendMessage = false;
				progressHasStop = false;
				stopTrackingTouchProgress = progress;
				mPlayerHandler.sendEmptyMessage(REPLAYSEEKBAR);
				//client.playbackPause(client.getHandle(), false);
				bPause = true;
				mPause.setImageDrawable(getResources().getDrawable(R.mipmap.img_pause));
			}
			
			@Override
			public void onStartTrackingTouch(SeekBar seekBar) {
				int progress = mReplaySeekBar.getProgress();
				Log.e("----------->>>", "onStartTrackingTouch progress:"+progress);
				mReplaySeekBar.setSeekBarText(translateTime(progress));
				mPlayerHandler.sendEmptyMessage(SHOWPROGRESSBAR);
				stopSendMessage = true;
			}
			
			@Override
			public void onProgressChanged(SeekBar seekBar, int progress,
					boolean fromUser) {
				if(fromUser){
					mReplaySeekBar.setSeekBarText(translateTime(progress));
				}
			}
		});
        
        if(PhoneConfig.getPhoneHeight(this) < PhoneConfig.getPhoneWidth(this)){
        	mTitle.setVisibility(View.GONE);
			mSurfaceIcon.setVisibility(View.GONE);
			System.out.println("onSingleTapUp:"+mSurfaceIcon.isShown());
			isShowSurfaceIcon = false;
			mStreamLen.setVisibility(View.VISIBLE);
        }
 
        mStreamLen = (TextView)findViewById(R.id.tv_stream_len);
        animationAim = (ImageView)findViewById(R.id.animation_aim);
        animationBackground = (ImageView)findViewById(R.id.animation_back);
        
		//InviteThread thread = new InviteThread();
		//thread.start();
		
		playMgr.setHandler(mPlayerHandler);
		playMgr.setContext(this);
		playMgr.loginCam();
		
	}
	
	private boolean checkDevVer(){
		GetDevVerReq getDevVerReq = new GetDevVerReq(SoapManager.getInstance().getLoginResponse().getAccount(),SoapManager.getInstance().getLoginResponse().getLoginSession(),dev.getDevID());
		GetDevVerRes res = SoapManager.getInstance().getGetDevVerRes(getDevVerReq);
		Log.e("", "CurDevVer:"+res.getCurDevVer());
		return DeviceVersionUtils.isNewVersionDevice(res.getCurDevVer());
	}
	
    /* 
     * 获取PopupWindow实例 
     */  
    private void getPopupWindowInstance() {  
        if (null != mPopupWindow) {  
            mPopupWindow.dismiss();  
            return;  
        } else {  
            initPopuptWindow();  
        }  
    }  
  
    /* 
     * 创建PopupWindow 
     */  
    private void initPopuptWindow() {  
        LayoutInflater layoutInflater = LayoutInflater.from(this);  
        View popupWindow = layoutInflater.inflate(R.layout.popup_window, null);  
//        RadioGroup radioGroup = (RadioGroup) popupWindow.findViewById(R.id.radioGroup);  
//        radioGroup.setOnCheckedChangeListener(this);  
        hd = (LinearLayout)popupWindow.findViewById(R.id.pop_layout_hd);
        sd = (LinearLayout)popupWindow.findViewById(R.id.pop_layout_sd);
        hd.setOnClickListener(this);
        sd.setOnClickListener(this);
        // 创建一个PopupWindow  
        // 参数1：contentView 指定PopupWindow的内容  
        // 参数2：width 指定PopupWindow的width  
        // 参数3：height 指定PopupWindow的height  
        int width = PhoneConfig.getPhoneWidth(this);
        int height = width * 5 / 3;
        mPopupWindow = new PopupWindow(popupWindow, width/4, height);  
        
        ColorDrawable dw = new ColorDrawable(0000000000);
		// ��back�������ط�ʹ����ʧ,������������ܴ���OnDismisslistener ����������ؼ��仯�Ȳ���
        mPopupWindow.setBackgroundDrawable(dw);
        mPopupWindow.setFocusable(true);  
        mPopupWindow.setOutsideTouchable(true);  
  
        // 获取屏幕和PopupWindow的width和height  
//        mScreenWidth = getWindowManager().getDefaultDisplay().getWidth();  
//        mScreenWidth = getWindowManager().getDefaultDisplay().getHeight();  
//        mPopupWindowWidth = mPopupWindow.getWidth();  
//        mPopupWindowHeight = mPopupWindow.getHeight();  
    }  
	
    public long getSDAllSize(){  
        File path = Environment.getExternalStorageDirectory();   
        StatFs sf = new StatFs(path.getPath());   
        long blockSize = sf.getBlockSize();   
        long allBlocks = sf.getBlockCount();  
        return (allBlocks * blockSize)/1024/1024; //锟斤拷位MB  
    }    
    
    public long getSDFreeSize(){  
        File path = Environment.getExternalStorageDirectory();   
        StatFs sf = new StatFs(path.getPath());   
        long blockSize = sf.getBlockSize();   
        long freeBlocks = sf.getAvailableBlocks();  
        return (freeBlocks * blockSize)/1024 /1024; //锟斤拷位MB  
    }      
    
    private boolean existSDCard() {  
    	if (Environment.getExternalStorageState().equals(
    		Environment.MEDIA_MOUNTED)) {
        	return true;  
        } else  
        	return false;  
    }  
	
	private String translateTime(int progress){
		SimpleDateFormat bar = new SimpleDateFormat("HH:mm:ss");
		bar.setTimeZone(TimeZone.getDefault());
        String text = bar.format(correctedStartTime*1000 + progress);
        return text;
	}
	
    public static Context getContext() {
        return mPlayer;
    }
    
    public static PlayerHandler getHandler(){
    	return mPlayerHandler;
    }
    
    private int getFrames(){
    	return nowFrames;
    }
    
    public static void addFrames(){
    	nowFrames += 1;
    } 

	private void audioInit() {
		int buffer_size = AudioTrack.getMinBufferSize(8000, AudioFormat.CHANNEL_OUT_MONO, AudioFormat.ENCODING_PCM_16BIT);
		mAudioTrack = new AudioTrack(AudioManager.STREAM_MUSIC, 8000, AudioFormat.CHANNEL_OUT_MONO, AudioFormat.ENCODING_PCM_16BIT, buffer_size*8, AudioTrack.MODE_STREAM);
		mAudioData = new byte[buffer_size*8];
		nativeAudioInit();
		mAudioTrack.play();
	}
	
	private void audioPause(){
		audiomanage.setStreamVolume(AudioManager.STREAM_MUSIC, 0 , 0);
		isAudioOpen = false;
		mSound.setImageDrawable(getResources().getDrawable(R.mipmap.img_no_sound));
	}
	
	private void audioPlay(){
		audiomanage.setStreamVolume(AudioManager.STREAM_MUSIC, maxVolume/2 , 0);
		isAudioOpen = true;
		mSound.setImageDrawable(getResources().getDrawable(R.mipmap.img_sound));
	}
	
	private void audioStop(){
		if(mAudioTrack != null){
			mAudioTrack.flush();
			mAudioTrack.stop();
		}
	}
	
	private void audioRelease(){
		System.out.println(mAudioTrack.toString());
		mAudioTrack.release();
		mAudioTrack = null;
	}
	
	class InviteThread extends Thread{
		@Override
		public void run() {
			super.run();
			client = new InviteUtils(dev);
			System.out.println("start invite live");
			
//			client.testMainJni();
			//FIXME
			
			if (playback) {
				Log.e("---------->>>>", "1111111111111111111");
				System.out.println("startTime:"+startTime+"endTime:"+endTime);
				inviteRet = PlayerActivity.client.InvitePlayback(startTime, endTime,stream);
		    } else {
		        Log.e("---------->>>>", "2222222222222222222");
		        inviteRet = PlayerActivity.client.InviteLive(stream);
		        talkManger = new TalkManager(client.getHandle());
		    }
			System.out.println("finish invite live");		
		}
	}
	
	
	@Deprecated
	public static void showStreamLen(int streamLen){
		Message msg = new Message();
		msg.what = SHOWSTREAMLEN;
		msg.obj = streamLen;
		mPlayerHandler.sendMessage(msg);
	}
	
	public static void ShowStreamSpeed(final int kbitPerSec){
		if (mStreamLen!=null) {
			mStreamLen.post(new Runnable() {
				public void run() {
					mStreamLen.setText(kbitPerSec+" Kbit/s");
				}
			});
		}
		
	}
	
	
    public static class PlayerHandler extends Handler{
    	private boolean isTimeStampBreak;	//时标溢出标志位
    	private int progress,progressTemp;	//progressTemp：记录时标未溢出时的拖动条播放长度
    	private long firstBreakFrameTime;	//记录时标溢出时的第一帧数据的时标
    	Context context;
    	private VODRecord record;
		public PlayerHandler() {
			super();
			this.isTimeStampBreak = false;
			this.progress = 0;
			this.progressTemp = 0;
			this.firstBreakFrameTime = 0;
		}

		public void setContext(Context context) {
			this.context = context;
		}
		
		public void setRecord(VODRecord record){
			this.record = record;
		}
		@SuppressWarnings("unused")
		@Override
		public void handleMessage(Message msg) {
			super.handleMessage(msg);
			if (msg.what == REPLAYSEEKBAR) {
				//-------------------------------------------------
				if(stopSendMessage){
					return;
				}
				if(YV12Renderer.time != 0 && frameFlag == 0){
					firstFrameTime = YV12Renderer.time;
					frameFlag++;
//					System.out.println("test firstFrame:"+firstFrameTime);
					while(true){
						correctedStartTime = client.getBeg();
						correctedEndTime = client.getEnd();
						Log.e("----------->>>", "onS totoal time:"+correctedEndTime +","+ correctedStartTime);
						Log.e("----------->>>", "onS totoal time:"+(correctedEndTime - correctedStartTime));
						if(correctedStartTime != -1 && correctedEndTime != -1)
							break;
					}
					mReplaySeekBar.setMax((int)(correctedEndTime - correctedStartTime)*1000);
//					System.out.println("test maxFrame:"+(int)(correctedEndTime - correctedStartTime)*1000);
				}else if(YV12Renderer.time != 0 && frameFlag > 0){
					
					endFrameTime = YV12Renderer.time;
					
//					System.out.println("test endFrameTime:"+endFrameTime);
//					System.out.println("test progress:"+(int)(endFrameTime - firstFrameTime));
					if(!progressHasStop){
						mPlayerHandler.sendEmptyMessage(HIDEPROGRESSBAR);
						progressHasStop = true;
					}
					if((int)(endFrameTime - firstFrameTime) < 0 && !isTimeStampBreak ){
						isTimeStampBreak = true;
						firstBreakFrameTime = endFrameTime;
//						System.out.println("test isTimeStampBreak"+isTimeStampBreak);
						progress = stopTrackingTouchProgress;
						if(progress == 0){
							progress = progressTemp;
							System.out.println("test progressTemp:"+progressTemp);
						}
					}else if((int)(endFrameTime - firstFrameTime) > 0 && isTimeStampBreak){
						isTimeStampBreak = false;
					}
					if(isTimeStampBreak){
//						System.out.println("test stopTrackingTouchProgress:"+progress);
//						System.out.println("test new progress:"+(int)(endFrameTime - firstBreakFrameTime));
						mReplaySeekBar.setProgress(progress + (int)(endFrameTime - firstBreakFrameTime));
					}else{
						mReplaySeekBar.setProgress((int)(endFrameTime - firstFrameTime));
						progressTemp = (int)(endFrameTime - firstFrameTime);
						
					}
					if(stopTrackingTouchProgress != 0){
						stopTrackingTouchProgress = 0;
					}
				}
				mPlayerHandler.sendEmptyMessageDelayed(REPLAYSEEKBAR,100);
			}
			if (msg.what == STOPPROGRESSBAR) {
				//System.out.println("frames: "+stopSendMessage);
				if(stopSendMessage){
					return;
				}
				//if(YV12Renderer.time == 0){
				if(com.howell.action.YV12Renderer.time == 0){
					mPlayerHandler.sendEmptyMessageDelayed(STOPPROGRESSBAR,100);
				}else{
					mWaitProgressBar.setVisibility(View.GONE);
					System.out.println("frames: send message DETECT_IF_NO_STREAM_ARRIVE");
					mPlayerHandler.sendEmptyMessage(DETECT_IF_NO_STREAM_ARRIVE);
				}
			}
			if(msg.what == SHOWPROGRESSBAR){
				if(!mWaitProgressBar.isShown()){
					System.out.println("frames progress visible");
					mWaitProgressBar.setVisibility(View.VISIBLE);
				}
			}
			if(msg.what == HIDEPROGRESSBAR){
				if(mWaitProgressBar.isShown()){
					System.out.println("frames progress gone");
					mWaitProgressBar.setVisibility(View.GONE);
				}
			}
//			if(msg.what == TIMEOUT){
//				if(!stopSendMessage && YV12Renderer.time == 0){
//					MessageUtiles.postNewUIDialog(PlayerActivity.getContext(), PlayerActivity.getContext().getString(R.string.link_timeout), PlayerActivity.getContext().getString(R.string.ok),0);				
//				}
//			}
			if (msg.what == POSTERROR) {
				//MessageUtiles.postNewUIDialog(PlayerActivity.getContext(), PlayerActivity.getContext().getString(R.string.link_error), PlayerActivity.getContext().getString(R.string.ok), 1);
				Dialog alertDialog = new AlertDialog.Builder(PlayerActivity.getContext()).   
        	            setTitle("登录失败").   
        	            setMessage(PlayerActivity.getContext().getString(R.string.link_error)).   
        	            setIcon(R.drawable.expander_ic_minimized).   
        	            setPositiveButton("确定", new DialogInterface.OnClickListener() {   
        	                @Override   
        	                public void onClick(DialogInterface dialog, int which) {   
//        	                	if(null != client)
//        	    	        		client.setQuit(true);
//        	    	        	quitDisplay();
        	                	
        	                }   
        	            }).   
        	    create();   
        		alertDialog.show();   
			}
			if (msg.what == SHOWSTREAMLEN) {//FIXME
				int msg_boj = Integer.valueOf(msg.obj.toString());
				if(mStreamLen != null){
					streamLenFlag++;
					if(streamLenFlag % 10 == 0){
						streamLen += msg_boj;
						mStreamLen.setText(streamLen/2 + " Kbit/s");
						
						streamLen = 0;
					}else{
						streamLen += Integer.valueOf(msg.obj.toString());
					}
				}
				
				
//				mStreamLen.setText(Util.getDownloadSpeed(context));
				
						
			}
//			if(msg.what == SETVEDIOLISTENABLE){
//				mVedioList.setEnabled(true);
//				mVedioList.setImageResource(R.drawable.img_record);
//			}
			if(msg.what == DETECT_IF_NO_STREAM_ARRIVE){
				
				if(stopSendMessage||true){
					return;
				}
				if(!client.isQuit()){
					if(nowFrames == lastSecondFrames){
						mPlayerHandler.sendEmptyMessage(SHOWPROGRESSBAR);
					}else{
						mPlayerHandler.sendEmptyMessage(HIDEPROGRESSBAR);
					}
					lastSecondFrames = nowFrames;
					if(nowFrames >= 2000000000/*1000*/){
						nowFrames = 0;
						lastSecondFrames = 0;
					}
					mPlayerHandler.sendEmptyMessageDelayed(DETECT_IF_NO_STREAM_ARRIVE, 1000);
				}
			}
			
			if(msg.what == MSG_LOGIN_CAM_OK){
				if(playback){
					String _startTime = record.getStartTime();
					String _endTime = record.getEndTime(); 
					String _startTimeZone = record.getTimeZoneStartTime();
					String _endTimeZone = record.getTimeZoneEndTime();
					
					Log.i("123", "startTIme="+_startTime+" endTime="+_endTime+" startZone="+_startTimeZone+
							" endTimeZone"+_endTimeZone);
					playMgr.playBackCam(0, _startTimeZone, _endTimeZone);
				}else{
					playMgr.playViewCam(0);
				}
				AudioAction.getInstance().playAudio();
			}
			
			if(msg.what == MSG_DISCONNECT){
				Log.i("123", "msg what = MSG_DISCONNECT");
				playMgr.transDeInit();
			}
			if(msg.what == MSG_DISCONNECT_UNEXPECT){
				Log.i("123", "PlayerActivity   msg what == MSG_DISCONNECT_UNEXPECT");
				playMgr.reLink();
			}
			
		}
	}

    

    
    
	@Override
	public void onConfigurationChanged(Configuration newConfig) {
		super.onConfigurationChanged(newConfig);
		Log.e("main","config change");
		if (this.getResources().getConfiguration().orientation == Configuration.ORIENTATION_LANDSCAPE){
			Log.i("info", "onConfigurationChanged landscape"); // 锟斤拷锟斤拷
			mTitle.setVisibility(View.GONE);
			mSurfaceIcon.setVisibility(View.GONE);
			System.out.println("onSingleTapUp:"+mSurfaceIcon.isShown());
			isShowSurfaceIcon = false;
			mStreamLen.setVisibility(View.VISIBLE);
		} else if(this.getResources().getConfiguration().orientation == Configuration.ORIENTATION_PORTRAIT){
			Log.i("info", "onConfigurationChanged PORTRAIT"); // 锟斤拷锟斤拷
			mTitle.setVisibility(View.VISIBLE);
			mSurfaceIcon.setVisibility(View.VISIBLE);
			isShowSurfaceIcon = true;
		}
	}
	
	@Override
	protected void onPause() {
		Log.e("PA", "onPause");
		//quitDisplay();
		mPausing = true;
		this.mGlView.onPause();
		super.onPause();
	}

	private void stopCam(){
		Log.d("123", "stop cam");
		playMgr.stopViewCam();
		playMgr.logoutCam();
		Log.d("123", "stop cam ok");
	}
	
	@Override
	protected void onDestroy() {
		Log.e("PA", "onDestroy");
		mActivities.removeActivity("PlayerActivity");
    	unregisterReceiver(receiver);
    	if(talkManger != null){
    		talkManger.release();
        	talkManger = null;
    	}
    
    	
//		if(null != client){
//			client.setQuit(true);
//			quitDisplay();
//		}
		stopCam();
		super.onDestroy();
	//	System.runFinalization();
	}

	@Override
	protected void onResume() {
		Log.e("PA", "onResume");
		mPausing = false;
		mGlView.onResume();
		super.onResume();
	}
	
	@Override
	public void surfaceChanged(SurfaceHolder holder, int format, int width,int height) {}

	@Override
	public void surfaceCreated(SurfaceHolder holder) {
	}

	@Override
	public void surfaceDestroyed(SurfaceHolder holder) {}

	public void audioWrite() {
		mAudioTrack.write(mAudioData,0,mAudioDataLength);
	}
	
	public class MyQuitTask extends AsyncTask<Void, Integer, Void> {
		private InviteUtils client;
		public MyQuitTask(InviteUtils client){
			this.client = client;
		}
        @Override
        protected Void doInBackground(Void... params) {
			if(client != null && client.getHandle() != -1){
				System.out.println("isStartFinish:"+client.isStartFinish()+","+client.toString());
            	while(true){
            		if(client.isStartFinish()){
		        		System.out.println("free handle");
						client.freeHandle(client.getHandle());
						break;
            		}
            	}
			}
			System.out.println("release audio");
			audioRelease();
            if(client != null)
	            client.bye(client.getAccount(),client.getLoginSession(),client.getDevID(),client.getChannelNo(),client.getStreamType(),client.getDialogID());	
	        System.out.println("finish activity");
            return null;
        }
    }
	
	private void quitDisplay(){
		Log.i("123", "quitDisplay");
		PlayerManager.getInstance().quitPlay();
		if (backCount == 0) {
			audioStop();
			stopSendMessage = true;
			
			while(true){
				if(client != null){
					client.setQuit(true);
					client.joinThread(client.getHandle());
					break;
				}
				break;
			}
			System.out.println("stop audio");
			//audioStop();
			YV12Renderer.nativeDeinit();
			finish();
			MyQuitTask mTask = new MyQuitTask(client);
            mTask.execute();
        }
        System.out.println(backCount);
        backCount++;
	}
	
	@Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        super.onKeyDown(keyCode, event);
      
        if (keyCode == KeyEvent.KEYCODE_BACK) {
        	Log.e("backCount", "press back button backCount:"+backCount);
        	if(null != client){
        		client.setQuit(true);
        		quitDisplay();
        	}
        	//stopCam();
        	if(!playback){
        		TakePhotoUtil.takePhoto("/sdcard/eCamera/cache", dev, client);
			}
			
        	finish();
        }
        return false;
    }

	@Override
	public boolean onDown(MotionEvent e) {
		return false;
	}
	
	public class MyFlingTask extends AsyncTask<Void, Integer, Void> {
		private String direction;
		private int time;
		public MyFlingTask(String direction,int time) {
			this.direction = direction;
			this.time = time;
		}
        @Override
        protected Void doInBackground(Void... params) {
            System.out.println("call doInBackground");
            Log.e("start direction", direction);
            
            PtzControlReq req = new PtzControlReq(account,loginSession,devID,channelNo,direction);
        	PtzControlRes ptzRes = mSoapManger.GetPtzControlRes(req);
        	if(ptzRes != null){
	            Log.e("start Res", ptzRes.getResult());
	            try {
	            	Thread.sleep(time);
				} catch (InterruptedException e) {
					e.printStackTrace();
				}
	            req = new PtzControlReq(account,loginSession,devID,channelNo,"Stop");
	            ptzRes = mSoapManger.GetPtzControlRes(req);
	            Log.e("stop Res", ptzRes.getResult());
        	}else{
        		loginSession = mSoapManger.getLoginResponse().getLoginSession();
        	}
            return null;
        }
    }

	private void animationStart(float fromXDelta, float toXDelta, float fromYDelta, float toYDelta){
		System.out.println("Fling isAnimationStart:"+isAnimationStart);
		isAnimationStart = true;
	    translateAnimation = new TranslateAnimation(fromXDelta, toXDelta,fromYDelta,toYDelta);
	    translateAnimation.setDuration(2000);  
		
		translateAnimation.setAnimationListener(new AnimationListener() {
			
			@Override
			public void onAnimationStart(Animation arg0) {
			}
			
			@Override
			public void onAnimationRepeat(Animation arg0) {
				
			}
			
			@Override
			public void onAnimationEnd(Animation arg0) {
				animationAim.setVisibility(View.GONE);
				animationBackground.setVisibility(View.GONE);
				animationAim.clearAnimation();
				isAnimationStart = false;
			}
        });
		
		animationAim.startAnimation(translateAnimation);  
		
	}
	
	@Override
	public boolean onFling(MotionEvent e1, MotionEvent e2, float velocityX,
			float velocityY) {
		if(isAnimationStart || !dev.isPtzFlag() || playback){
			System.out.println("is not PTZ");
			return false;
		}
		
        String direction = "Stop";
        int time = 0;
        
        if(mFlingTask != null){
        	Log.e("start status", mFlingTask.getStatus().toString());
	        if( mFlingTask.getStatus() != AsyncTask.Status.FINISHED){
	        	Log.e("return", mFlingTask.getStatus().toString());
	        	Log.e("return", "return");
	        	return true;
	        }
        }
        
        //显示平移动画素材
        System.out.println("Fling00000000");
		animationAim.setVisibility(View.VISIBLE);
		System.out.println("Fling1111111");
		animationBackground.setVisibility(View.VISIBLE);
		System.out.println("Fling2222222");
		
        final int FLING_MIN_DISTANCE = 100, FLING_MIN_VELOCITY = 200;   
        if (e1.getX() - e2.getX() > FLING_MIN_DISTANCE && Math.abs(velocityX) > FLING_MIN_VELOCITY) {   
            // Fling left   
        	direction = "Right";
        	time = 700;
        	animationStart(0,40,0,0);
        	Log.e("MyGesture", "Fling left "+"x:"+Math.abs(e1.getX() - e2.getX())+"y:"+Math.abs(e1.getY() - e2.getY()));  
        } else if (e2.getX() - e1.getX() > FLING_MIN_DISTANCE && Math.abs(velocityX) > FLING_MIN_VELOCITY) {   
            // Fling right   
        	Log.e("MyGesture", "Fling right "+"x:"+Math.abs(e1.getX() - e2.getX())+"y:"+Math.abs(e1.getY() - e2.getY()));   
        	direction = "Left";
        	animationStart(0, -40,0,0);
        	time = 700;
        }  else if (e2.getY() - e1.getY() > FLING_MIN_DISTANCE && Math.abs(velocityY) > FLING_MIN_VELOCITY) {   
            // Fling Down   
        	Log.e("MyGesture", "Fling Down "+"y:"+Math.abs(e1.getY() - e2.getY())+"x:"+Math.abs(e1.getX() - e2.getX()));   
        	direction = "Up";
        	animationStart(0, 0,0,-40);
        	time = 500;
        }   else if (e1.getY() - e2.getY() > FLING_MIN_DISTANCE && Math.abs(velocityY) > FLING_MIN_VELOCITY) {   
            // Fling Up   
        	Log.e("MyGesture", "Fling Up "+"y:"+Math.abs(e1.getY() - e2.getY())+"x:"+Math.abs(e1.getX() - e2.getX()));   
        	direction = "Down";
        	time = 500;
        	animationStart(0, 0,0,40);
        }   else{
        	return true;
        }
        mFlingTask = new MyFlingTask(direction,time);
        mFlingTask.execute();
        return true;   
	}
	
	@Override
	public void onLongPress(MotionEvent e) {
		
	}

	@Override
	public boolean onScroll(MotionEvent e1, MotionEvent e2, float distanceX,
			float distanceY) {
		return false;
	}

	@Override
	public void onShowPress(MotionEvent e) {
		
	}

	@Override
	public boolean onSingleTapUp(MotionEvent e) {
		Log.e("MyGesture", "onSingleTapUp");  
		System.out.println("playback:"+playback);
		if(PhoneConfig.getPhoneHeight(this) < PhoneConfig.getPhoneWidth(this)){
			System.out.println("onSingleTapUp000:"+isShowSurfaceIcon);
			if(isShowSurfaceIcon){
				System.out.println("onSingleTapUp111:"+isShowSurfaceIcon);
				mSurfaceIcon.setVisibility(View.GONE);
				mTitle.setVisibility(View.GONE);
				isShowSurfaceIcon = false;
			}else{
				System.out.println("onSingleTapUp222:"+isShowSurfaceIcon);
				mSurfaceIcon.setVisibility(View.VISIBLE);
				mTitle.setVisibility(View.VISIBLE);
				System.out.println("onSingleTapUp:"+mSurfaceIcon.isShown());
				isShowSurfaceIcon = true;
			}
		}
		if(playback){
			mReplaySeekBar.setVisibility(View.VISIBLE);
		}else{
			mReplaySeekBar.setVisibility(View.GONE);
		}
		return true;
	}
	
	@Override
	public boolean onTouch(View v, MotionEvent event) {
		return mGestureDetector.onTouchEvent(event);   
	}
//	@Override
//	public void onItemClick(ActionItem item, int position) {
//		int isPlayBack = -1;
//		boolean ret;
//		if(playback){
//			isPlayBack = 1;
//		}else{
//			isPlayBack = 0;
//			startTime = 0;
//			endTime = 0;
//		}
//		if(position == 0){
//			ret = client.Replay(isPlayBack, startTime, endTime, 0);
//			if(ret){
//				stream = 0;
//			}
//		}else if(position == 1){
//			ret = client.Replay(isPlayBack, startTime, endTime, 1);
//			if(ret){
//				stream = 1;
//			}
//		}
//	}
	
	@Override
	public void onClick(View v) {
		switch (v.getId()) {
		case R.id.pop_layout_hd:
			mPopupWindow.dismiss();
			new AsyncTask<Void, Integer, Void>(){
				int isPlayBack = -1;
				boolean ret;
				long replayStartTime = 0;
				@Override
				protected Void doInBackground(Void... arg0) {
//					if(playback){
//						isPlayBack = 1;
//						replayStartTime = correctedStartTime + (long)(mReplaySeekBar.getProgress())/1000;
//						if(replayStartTime < startTime){
//							replayStartTime = startTime;
//						}
//					}else{
//						isPlayBack = 0;
//						replayStartTime = 0;
//						endTime = 0;
//					}
//					ret = client.Replay(isPlayBack, replayStartTime, endTime, 0);
					
					if (playback) {
						playMgr.stopPlaybackCam();
						String startTime = mRecord.getStartTime();
						String endTime = mRecord.getEndTime();
						String startTimeZone = mRecord.getTimeZoneStartTime();
						String endTimeZone = mRecord.getTimeZoneEndTime();
						Log.i("123", "startTIme="+startTime+" endTime="+endTime+" startZone="+startTimeZone+
								" endTimeZone"+endTimeZone);
						playMgr.playBackCam(0, startTimeZone, endTimeZone);
					}else{
						playMgr.stopViewCam();
						playMgr.playViewCam(0);
					}
					ret = true;
					return null;
				}
				
				protected void onPostExecute(Void result) {
					if(ret){
						stream = 0;
						mStreamChange.setText("高清");
					}
				};
				
			}.execute();
			
			break;
		case R.id.pop_layout_sd:
			mPopupWindow.dismiss();
			new AsyncTask<Void, Integer, Void>(){
				int isPlayBack = -1;
				boolean ret;
				long replayStartTime = 0;
				@Override
				protected Void doInBackground(Void... arg0) {
//					if(playback){
//						isPlayBack = 1;
//						replayStartTime = correctedStartTime + (long)(mReplaySeekBar.getProgress())/1000;
//						if(replayStartTime < startTime){
//							replayStartTime = startTime;
//						}
//					}else{
//						isPlayBack = 0;
//						replayStartTime = 0;
//						endTime = 0;
//					}
//					ret = client.Replay(isPlayBack, replayStartTime, endTime, 1);
					
					if (playback) {
						playMgr.stopPlaybackCam();
						String startTime = mRecord.getStartTime();
						String endTime = mRecord.getEndTime();
						String startTimeZone = mRecord.getTimeZoneStartTime();
						String endTimeZone = mRecord.getTimeZoneEndTime();
						Log.i("123", "startTIme="+startTime+" endTime="+endTime+" startZone="+startTimeZone+
								" endTimeZone"+endTimeZone);
						playMgr.playBackCam(1, startTimeZone, endTimeZone);
					}else{
						playMgr.stopViewCam();
						playMgr.playViewCam(1);
						
					}
					ret = true;
					return null;
				}
				
				protected void onPostExecute(Void result) {
					if(ret){
						stream = 1;
						mStreamChange.setText("标清");
					}
				};
				
			}.execute();
			
			break;
		default:
			break;
		}
	}
	
}
