package com.howell.action;

import android.content.Context;
import android.os.AsyncTask;
import android.os.Handler;
import android.util.Log;

import com.howell.activity.PlayerActivity;
import com.howell.entityclass.VODRecord;
import com.howell.jni.JniUtil;
import com.howell.utils.IConst;
import com.howell.utils.JsonUtil;
import com.howell.utils.PhoneConfig;
import com.howell.utils.SDCardUtils;
import com.howell.utils.SharedPreferencesUtil;
import com.howell.utils.TurnJsonUtil;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.Timer;
import java.util.TimerTask;

import bean.Subscribe;
import bean.TurnGetRecordedFilesBean;
import bean.TurnSubScribeAckBean;

public class PlayerManager implements IConst{
	
	private static final int F_TIME = 1;
	
	Handler handler;
	private PlayerManager(){}
	private static PlayerManager mInstance = null;
	public static PlayerManager getInstance(){
		if(mInstance==null){
			mInstance = new PlayerManager();
		}
		return mInstance;
	}
	public void setHandler(Handler handler){
		this.handler = handler;
	}
	private long dialogId = 0;
	private String turnServiceIP = null;
	private int turnServicePort = -1;
	private String sessionID = null;
	private Context context;
	private int mUnexpectNoFrame = 0;  // if we get no frame > 10sec we reLink(stop and start again) 
	private Timer timer = null;
	private MyTimerTask myTimerTask = null;
	boolean mIsTransDeinit = false;
	boolean mHasDisConnectCallback = false;
	ArrayList<VODRecord> mList = null;
	
	public ArrayList<VODRecord> getMList(){
		return mList;
	}
	

	
	public void setContext(Context context){
		this.context = context;
	}
	
	public void onConnect(String sessionId){
		Log.i("123", "session id = "+sessionId);
		sessionID = sessionId;
		handler.sendEmptyMessage(MSG_LOGIN_CAM_OK);
	}
	
	public void onDisConnect(){
		Log.i("123", "onDisConnect ");
		
		handler.sendEmptyMessage(MSG_DISCONNECT);
	}
	
	public void onDisconnectUnexpect(int flag){
		Log.i("123", "on disConnectUnexpect  we need reLink");
		stopTimerTask();
//		PlayerActivity.showStreamLen(0);
		PlayerActivity.ShowStreamSpeed(0);
		handler.sendEmptyMessageDelayed(PlayerActivity.MSG_DISCONNECT_UNEXPECT, 5000);
	}

	public void onSubscribe(String jsonStr){
		Log.i("123","!!!onASubscribe   jsonStr="+jsonStr);

		if(JniUtil.readyPlayTurnLive(TurnJsonUtil.getTurnSubscribeAckAllFromJsonStr(jsonStr),0)){
			JniUtil.playView();
			startTimerTask();
		}else{
			Log.e("123", "ready play live error");
		}

//		if(JniUtil.readyPlayLive()){
//			Log.i("123", "play view cam  deviceID=  "+PlatformAction.getInstance().getDeviceId());
//			Subscribe s = new Subscribe(sessionID, (int)getDialogId(), PlatformAction.getInstance().getDeviceId(), "live",is_sub);
//			s.setStartTime(null);
//			s.setEndTime(null);
//			String jsonStr = JsonUtil.subScribeJson(s);
//			Log.i("123", "jsonStr="+jsonStr);
//			JniUtil.transSubscribe(jsonStr, jsonStr.length());
//			JniUtil.playView();
//			startTimerTask();
//		}else{
//
//		}


	}


	
	public long getDialogId(){
		this.dialogId++;
		return dialogId;
	}
	private boolean doOnce = false;
	private void startTimerTask(){
		timer = new Timer();
		myTimerTask = new MyTimerTask();
		timer.schedule(myTimerTask, 0,F_TIME*1000);
	}

	private void stopTimerTask(){
		if (timer!=null) {
			timer.cancel();
			timer.purge();
			timer = null;
		}
		if (myTimerTask!=null) {
			myTimerTask.cancel();
			myTimerTask = null;
		}
	}
	
	
	public void	loginCam(){
//		if(doOnce){
//			handler.sendEmptyMessage(MSG_LOGIN_CAM_OK);
//			return ;
//		}
		turnServiceIP = PlatformAction.getInstance().getTurnServerIP();
		turnServicePort = PlatformAction.getInstance().getTurnServerPort();
		Log.i("123","get serviceIP from NET  ip="+turnServiceIP+" port="+turnServicePort);


//		turnServiceIP = TEST_IP;//FIXME
		if (PlatformAction.getInstance().isTest()) {
			turnServiceIP = TEST_TURN_SERVICE_IP;
		}else{
			turnServiceIP = SharedPreferencesUtil.getTurnServerIP(context);
		}

//		turnServicePort = TEST_TURN_SERCICE_PORT;
		turnServicePort = SharedPreferencesUtil.getTurnServerPort(context);
//		turnServicePort = 8862;//FIXME
		Log.i("123", "login cam   trunServiceIP="+turnServiceIP+"  turnServicePort="+turnServicePort);
		new AsyncTask<Void, Void, Void>(){

			@Override
			protected Void doInBackground(Void... params) {
				Log.i("123", "doinback");	
				JniUtil.netInit();
				JniUtil.transInit();//FIXME
				mIsTransDeinit = false;
				JniUtil.transSetCallBackObj(PlayerManager.this, 0);
				JniUtil.transSetCallbackMethodName("onConnect", 0);
				JniUtil.transSetCallbackMethodName("onDisConnect", 1);
				JniUtil.transSetCallbackMethodName("onRecordFileList", 2);
				Log.i("123","disconnectunexpect");
				JniUtil.transSetCallbackMethodName("onDisconnectUnexpect", 3);
				JniUtil.transSetCallbackMethodName("onSubscribe",4);
				InputStream ca = getClass().getResourceAsStream("/assets/ca.crt");
				InputStream client = getClass().getResourceAsStream("/assets/client.crt");
				InputStream key = getClass().getResourceAsStream("/assets/client.key");
				String castr = new String(SDCardUtils.saveCreateCertificate(ca, "ca.crt",context));
				String clstr = new String(SDCardUtils.saveCreateCertificate(client, "client.crt",context));
				String keystr = new String(SDCardUtils.saveCreateCertificate(key, "client.key",context));
//				JniUtil.transSetCrt(castr, clstr, keystr);
				Log.i("123", "castr="+castr);
				JniUtil.transSetCrtPaht(castr, clstr, keystr);	
				
				try {
					ca.close();
					client.close();
					key.close();
				} catch (IOException e) {
					e.printStackTrace();
				}
				
				int type = 101;
				String id = PhoneConfig.getPhoneUid(context);//FIXME  android id
				String imei = PhoneConfig.getPhoneDeveceID(context);  //imei
				
				JniUtil.transConnect(turnServiceIP, turnServicePort,USING_TURN_ENCRYPTION,type, imei, PlatformAction.getInstance().getAccount(), PlatformAction.getInstance().getPassword());
				Log.i("PlayManager", "transConnect ok");
				
				AudioAction.getInstance().initAudio();
				AudioAction.getInstance().playAudio();
				
				return null;
			}
			
		}.execute();
		doOnce = true;	
		Log.i("123", "login task exe over");
	}
	
	
	public void logoutCam(){
		//JniUtil.loginOut();

		JniUtil.transDisconnect();
		AudioAction.getInstance().stopAudio();
		AudioAction.getInstance().deInitAudio();
	}
	
	
	
	public void quitPlay(){
		handler.removeMessages(PlayerActivity.MSG_DISCONNECT_UNEXPECT);
	}
	
	
	
	public void playViewCam(int is_sub){

		Subscribe s = new Subscribe(sessionID, (int)getDialogId(), PlatformAction.getInstance().getDeviceId(), "live",is_sub);
		s.setStartTime(null);
		s.setEndTime(null);
		String jsonStr = JsonUtil.subScribeJson(s);
		Log.i("123", "jsonStr="+jsonStr);
		JniUtil.transSubscribe(jsonStr, jsonStr.length());



	}
	
	public void playBackCam(int is_sub,String startTime,String endTime){
		Log.i("123", "playbackcam   startTime="+startTime+" endtime="+endTime);
		if(JniUtil.readyPlayPlayback()){
			Log.i("123", "play back cam");
			Subscribe s = new Subscribe(sessionID, (int)getDialogId(), PlatformAction.getInstance().getDeviceId(), "playback",is_sub);
			s.setStartTime(startTime);
			s.setEndTime(endTime);
			String jsonStr = JsonUtil.subScribeJson(s);
			Log.i("123", "jsonStr = "+jsonStr);
			JniUtil.transSubscribe(jsonStr, jsonStr.length());
			JniUtil.playView();
			startTimerTask();
		}else{
			Log.e("123", "read play playback error");
		}
	}
	
	public void stopPlaybackCam(){
		stopViewCam();
	}
	
	public void stopViewCam(){
		 new Runnable() {
			public void run() {
				Log.d("123", "stop View cam");
				JniUtil.stopView();
				JniUtil.transUnsubscribe();
			}
		}.run();
		stopTimerTask();
	}
	
	public void transDeInit(){
		if (!mIsTransDeinit) {
			JniUtil.transDeinit();
			mIsTransDeinit = true;
		}
	}
	

	
	public void stransSubscribe(String jsonStr,int jsonLen){
		JniUtil.transSubscribe(jsonStr, jsonLen);
	}
	
	public void getRecordFiles(String startTime,String endTime){
		String [] str = new String[2];
		str[0] = startTime;
		str[1] = endTime;
		new AsyncTask<String, Void, Void>(){
			@Override
			protected Void doInBackground(String... params) {
				String deviceId = PlatformAction.getInstance().getDevice_id();
				TurnGetRecordedFilesBean bean = new TurnGetRecordedFilesBean(deviceId, 0, params[0], params[1]);
				String jsonStr = JsonUtil.getRecordFilesJson(bean);
				JniUtil.transGetRecordFiles(jsonStr, jsonStr.length());
				return null;
			}
		}.execute(str);
	}
	
	public void onRecordFileList(String jsonStr){
		try {
			mList = JsonUtil.parseRecordFileList(new JSONObject(jsonStr));
			handler.sendEmptyMessage(MSG_RECORD_LIST_GET);
		} catch (JSONException e) {
			e.printStackTrace();
		}
	}
	
	public void reLink(){
		//stop
		Log.d("123", "relink........");
		handler.sendEmptyMessage(PlayerActivity.SHOWPROGRESSBAR);
		new Thread(){
			public void run() {
				stopViewCam();
				logoutCam();
				transDeInit();
				
				try {
					Thread.sleep(1000);//
				} catch (InterruptedException e) {
					e.printStackTrace();
				}
				loginCam();
			};
		}.start();
	}
	
	class MyTimerTask extends TimerTask{
		@Override
		public void run() {
			int streamLen = JniUtil.transGetStreamLenSomeTime();
			Log.i("123","from my time task   "+ streamLen+"    speed="+streamLen*8/1024/F_TIME+"kbit");
//			PlayerActivity.showStreamLen(streamLen*8/1024);
			PlayerActivity.ShowStreamSpeed(streamLen*8/1024/F_TIME);
			if (streamLen == 0) {
				mUnexpectNoFrame++;
			}else{
				handler.sendEmptyMessage(PlayerActivity.HIDEPROGRESSBAR);
				mUnexpectNoFrame = 0;
			}
			
			if (mUnexpectNoFrame == 10) {// 10s / 1000ms 
				handler.sendEmptyMessage(PlayerActivity.MSG_DISCONNECT_UNEXPECT);
			}
		}
	}
}
