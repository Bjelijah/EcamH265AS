package com.howell.activity;

import android.app.DialogFragment;
import android.content.Context;
import android.content.Intent;
import android.hardware.fingerprint.FingerprintManager;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.annotation.RequiresApi;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnTouchListener;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.howell.action.FingerprintUiHelper;
import com.howell.action.MyTimeMgr;
import com.howell.action.PlatformAction;
import com.howell.db.UserLoginDao;
import com.howell.ecamh265.R;
import com.howell.protocol.GetNATServerReq;
import com.howell.protocol.GetNATServerRes;
import com.howell.protocol.LoginRequest;
import com.howell.protocol.LoginResponse;
import com.howell.protocol.SoapManager;
import com.howell.utils.DecodeUtils;
import com.howell.utils.PhoneConfig;
import com.zys.brokenview.BrokenCallback;
import com.zys.brokenview.BrokenTouchListener;
import com.zys.brokenview.BrokenView;

import java.util.List;

import bean.UserLoginDBBean;

public class FingerPrintFragment extends DialogFragment implements FingerprintUiHelper.Callback,OnTouchListener{
	private static final int MSG_SIGN_IN_FAIL 	= 0xa0;
	private static final int MSG_SIGN_IN_OK 	= 0xa1;
	public static final int MSG_ERROR_WAIT_OK		= 0xa2;
	public static final int MSG_WAIT_SEC       = 0xa3;
	private BrokenView mBrokenView;
	private BrokenTouchListener colorfulListener;
	private OnCreateViewFinish o;
	
	TextView mTvCancel,mTvPassword,mTvFingerState,mTvFingerWait;
	FingerprintUiHelper m;
	Context mContext;
	FingerprintUiHelper mFinger;
	ImageView mIvFingerState;
//	Timer mWaitTimer = null;
//	MyWaitTimerTask mWaitTimeTask = null;
	MyTimeMgr mTimemgr = MyTimeMgr.getInstance();
	
	
	Handler mHandler = new Handler(){

		@Override
		public void handleMessage(Message msg) {
			switch (msg.what) {
			case MSG_SIGN_IN_OK:
				dismiss();
				Activities.getInstance().getmActivityList().get("RegisterOrLogin").finish();
				break;
			case MSG_SIGN_IN_FAIL:
				showAuthenticationInfo(MyState.SIGN_ERROR);
				break;
			case MSG_ERROR_WAIT_OK:
				Log.e("123", "msg error wait ok ");
				mTvFingerWait.setVisibility(View.GONE);
				showAuthenticationInfo(MyState.WAIT);
//				mFinger.stopListening();
				mFinger.startListening(null);
				break;
			case MSG_WAIT_SEC:
				int sec = msg.arg1;
				mTvFingerWait.setText(sec+"");
				break;
			default:
				break;
			}
			super.handleMessage(msg);
		}
	};
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setRetainInstance(true);
		setStyle(DialogFragment.STYLE_NORMAL, android.R.style.Theme_Material_Light_Dialog);
	}

	@RequiresApi(api = Build.VERSION_CODES.M)
	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
		mContext = getContext();
		getDialog().setTitle(getString(R.string.finger_title));

		View v = inflater.inflate(R.layout.fingerprint_dialog_container, container, false);
		mTvCancel = (TextView) v.findViewById(R.id.tv_finger_cancel);
		mTvCancel.setOnTouchListener(this);
		mTvPassword = (TextView) v.findViewById(R.id.tv_finger_password);
		mTvPassword.setOnTouchListener(this);
		mFinger = new FingerprintUiHelper(mContext,mContext.getSystemService(FingerprintManager.class), this);
		mFinger.startListening(null);
		mTvFingerState = (TextView) v.findViewById(R.id.fingerprint_status);
		mIvFingerState = (ImageView) v.findViewById(R.id.fingerprint_icon);
		mTvFingerWait = (TextView) v.findViewById(R.id.tv_finger_wait);
		getDialog().setCanceledOnTouchOutside(false);
		
		//set brokeView
//		MyBrokenCallback callback = new MyBrokenCallback();
//		mBrokenView = BrokenView.add2Window(getActivity());
//		
//		colorfulListener = new BrokenTouchListener.Builder(mBrokenView).
//				setComplexity(8).
//                setBreakDuration(500).
//                setFallDuration(2000).
//                setCircleRiftsRadius(200).
//                build();
//		mBrokenView.setEnable(true);
//		mBrokenView.setCallback(callback);
//		mIvFingerState.setOnTouchListener(colorfulListener);
		
		Log.e("123", "on create view");
		mTimemgr.setmHandler(mHandler);
		if (o!=null) {
			Log.i("123", "o show listener");
			o.onShowListener();
		}
		return v;
	}

	public void setOnCreateViewFinish(OnCreateViewFinish o){
		this.o = o;
	}
	
	@Override
	public void onStart() {
		Log.i("123", "finget on start");
		super.onStart();
	}
	
	public void setBrokenViewListener(BrokenTouchListener brokenTouchListener){
		mTvCancel.setOnTouchListener(brokenTouchListener);
	}
	
	@Override
	public void onDestroyView() {
		Log.e("123", "on destroy view");
		
		mFinger.stopListening();
//		mWaitTimeTask.setHandler(null);
		mTimemgr.setmHandler(null);
//		stopTimeTask();
		super.onDestroyView();
	}
	
	
	@Override
	public void onAuthenticated(int fingerID) {
		Log.i("123", "识别到了");
		showAuthenticationInfo(MyState.OK);
		//开始登入
		signIn(fingerID);
	}

	@Override
	public void onFailed() {
		Log.i("123", "识别失败");
		showAuthenticationInfo(MyState.FAIL);
	}

	@Override
	public void onHelp(int helpCode, CharSequence str) {
		Log.i("123", "helpCode="+helpCode+" str="+str);
		switch (helpCode) {
		case 1001:
			showAuthenticationInfo(MyState.WAIT);
			break;
		default:
			break;
		}	
	}

	@Override
	public void onError(int code,CharSequence s) {
		Log.i("123", "识别error  code="+code+" s="+s.toString()) ;
		if (code==7 || code == 5) {//7：连续验证失败后继续 验证     5：上次验证error后 还剩时间 秒
			
			int waitSec = 60;
			try {
				waitSec = Integer.valueOf((String) s);
			} catch (Exception e) {
				// TODO: handle exception
			}
			
			mTimemgr.startTimeTask(waitSec);
			mTvFingerWait.setVisibility(View.VISIBLE);
			
		}else{
			Log.e("123", "code="+code+"  s="+s);
		}
		showAuthenticationInfo(MyState.ERROR);
	}
	
	@SuppressWarnings("deprecation")
	private void showAuthenticationInfo(MyState state){
		switch (state) {
		case WAIT:
			mIvFingerState.setImageDrawable(mContext.getResources().getDrawable(R.mipmap.ic_fp));
			mTvFingerState.setText(mContext.getString(R.string.fingerprint_hint));
			mTvFingerState.setTextColor(mContext.getResources().getColor(R.color.hint_color));
			break;
		case FAIL:
//			mIvFingerState.setImageDrawable(getResources().getDrawable(R.drawable.common_no_default));
			mIvFingerState.setImageDrawable(mContext.getResources().getDrawable(R.mipmap.ic_fp_fail));
			mTvFingerState.setText(mContext.getString(R.string.fingerprint_failed));
			mTvFingerState.setTextColor(mContext.getResources().getColor(R.color.finger_fail));
			break;
		case OK:
//			mIvFingerState.setImageDrawable(getResources().getDrawable(R.drawable.ok_default_green));
			mIvFingerState.setImageDrawable(mContext.getResources().getDrawable(R.mipmap.ic_fp_ok));
			mTvFingerState.setText(mContext.getString(R.string.fingerprint_ok));
			mTvFingerState.setTextColor(mContext.getResources().getColor(R.color.finger_green));
			break;
		case ERROR:
			mIvFingerState.setImageDrawable(mContext.getResources().getDrawable(R.mipmap.common_no_highlighted));
			mTvFingerState.setText(mContext.getString(R.string.fingerprint_error));
			mTvFingerState.setTextColor(mContext.getResources().getColor(R.color.red));
			break;
		case SIGN_ERROR:
			mIvFingerState.setImageDrawable(mContext.getResources().getDrawable(R.mipmap.common_no_default));
			mTvFingerState.setText(mContext.getString(R.string.fingerprint_sign_error));
			mTvFingerState.setTextColor(mContext.getResources().getColor(R.color.finger_orgeen));
			break;
		default:
			break;
		}
	}
	
	
	

	@Override
	public boolean onTouch(View v, MotionEvent event) {
		Log.i("123", "on touch");
		switch (v.getId()) {
		case R.id.tv_finger_cancel:
			Log.i("123", "tv_finger_cancel"+" act="+event.getAction());
			if (event.getAction()==MotionEvent.ACTION_DOWN) {
				mTvCancel.postDelayed(new Runnable() {
					public void run() {
						dismiss();
					}
				}, 300);
			}
			break;

		case R.id.tv_finger_password:
			Log.i("123", "tv_finger_password"+"act="+event.getAction());
			if (event.getAction()==MotionEvent.ACTION_DOWN) {
				mTvPassword.postDelayed(new Runnable() {
					public void run() {
						dismiss();
						Intent intent = new Intent(mContext,MainActivity.class);
						startActivity(intent);
					}
				}, 300);	
			}
			break;
		default:
			break;
		}
		return false;
	}

	private enum MyState{
		WAIT,
		FAIL,
		OK,
		ERROR,
		SIGN_ERROR;
	}
	
	private void signIn(final int fingerID){
		new AsyncTask<Void, Void, Boolean>() {

			@Override
			protected Boolean doInBackground(Void... params) {
//				int sn = (int)PhoneConfig.showUserSerialNum(mContext);
				UserLoginDao dao = new UserLoginDao(mContext, "user.db", 1);
				
				List<UserLoginDBBean> l = dao.queryByNum(fingerID);
				if (l.size()!=1) {
					Log.e("123", "数据库  find  l.size  !=1  size="+l.size());
					dao.close();
					return false;
				}
				dao.close();
				String userName = l.get(0).getUserName();
				String userPassword = l.get(0).getUserPassword();
				
				Log.e("123", "sign in    finger start login"+"   user name="+userName+"  psw="+userPassword);
				//start login
				PlatformAction.getInstance().setIsTest(false);
				SoapManager.initUrl(mContext);
				String encodedPassword = DecodeUtils.getEncodedPassword(userPassword);
				String imei = PhoneConfig.getPhoneDeveceID(mContext);
				LoginRequest loginReq = new LoginRequest(userName, "Common",encodedPassword, "1.0.0.1",imei);
				LoginResponse loginRes = null;
				try{
					loginRes=SoapManager.getInstance().getUserLoginRes(loginReq);
				}catch(Exception e){
					e.printStackTrace();
					Log.e("123", "finger start error  we return false");
					return false;
				}
			
				
				Log.i("123", "login get result");
				if (loginRes.getResult().toString().equals("OK")) {
//                    SharedPreferences sharedPreferences = getSharedPreferences(
//                            "set", Context.MODE_PRIVATE);
//                    Editor editor = sharedPreferences.edit();
//                    editor.putString("account", account);
//                    editor.putString("password", password);
//                    editor.commit();
                    PlatformAction.getInstance().setAccount(userName);
                    PlatformAction.getInstance().setPassword(userPassword);	                     
                	PlatformAction.getInstance().setDeviceList(loginRes.getNodeList());
                    GetNATServerRes res = SoapManager.getInstance().getGetNATServerRes(new GetNATServerReq(userName, loginRes.getLoginSession()));
                    Log.e("FingerPrintFragment", res.toString()+"  getTurnSAddress="+res.getTURNServerAddress()+" turnServerPort="+res.getTURNServerPort());
                    PlatformAction.getInstance().setTurnServerIP(res.getTURNServerAddress());
                    PlatformAction.getInstance().setTurnServerPort(res.getTURNServerPort());//FIXME 
                    Intent intent = new Intent(mContext,CamTabActivity.class);
                    startActivity(intent);
	            }else{
	            	return false;
	            }
				return true;	
			}
			protected void onPostExecute(Boolean result) {
				if (result) {
					mHandler.sendEmptyMessage(MSG_SIGN_IN_OK);
				}else{
					mHandler.sendEmptyMessage(MSG_SIGN_IN_FAIL);
				}
				
			};
		
		}.execute();
		
		
		
	}

	
	class MyBrokenCallback extends BrokenCallback{

		@Override
		public void onStart(View v) {
			Log.i("123", "MyBrokenCallback :"+v.getId());
			super.onStart(v);
		}

		@Override
		public void onCancel(View v) {
			Log.i("123", "onCancel :"+v.getId());
			super.onCancel(v);
		}

		@Override
		public void onRestart(View v) {
			Log.i("123", "onRestart :"+v.getId());
			super.onRestart(v);
		}

		@Override
		public void onFalling(View v) {
			Log.i("123", "onFalling :"+v.getId());
			super.onFalling(v);
		}

		@Override
		public void onFallingEnd(View v) {
			Log.i("123", "onFallingEnd :"+v.getId());
			super.onFallingEnd(v);
		}

		@Override
		public void onCancelEnd(View v) {
			Log.i("123", "onCancelEnd :"+v.getId());
			super.onCancelEnd(v);
		}
		
	}
	
	public interface OnCreateViewFinish{
		void onShowListener();
	}

	@Override
	public void onFingerCancel() {
		Log.i("123", "onfingercancel");
		dismiss();
	}

	
	
}
