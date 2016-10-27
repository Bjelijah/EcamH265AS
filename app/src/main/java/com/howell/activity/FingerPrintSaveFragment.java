package com.howell.activity;

import android.annotation.SuppressLint;
import android.app.DialogFragment;
import android.content.Context;
import android.hardware.fingerprint.FingerprintManager;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnTouchListener;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.howell.action.FingerprintUiHelper;
import com.howell.action.MyTimeMgr;
import com.howell.db.UserLoginDao;
import com.howell.ecamh265.R;
import com.zys.brokenview.BrokenTouchListener;
import com.zys.brokenview.BrokenView;

import bean.UserLoginDBBean;

public class FingerPrintSaveFragment extends DialogFragment implements FingerprintUiHelper.Callback,OnTouchListener{

	
	private static final int MSG_SIGN_IN_FAIL 	= 0xa0;
	private static final int MSG_SIGN_IN_OK 	= 0xa1;
	public static final int MSG_ERROR_WAIT_OK		= 0xa2;
	public static final int MSG_WAIT_SEC       = 0xa3;
	private BrokenView mBrokenView;
	private BrokenTouchListener colorfulListener;
	private OnCreateViewFinish o;
	
	TextView mTvCancel,mTvPassword,mTvFingerState,mTvFingerWait,mTvDescription;
	FingerprintUiHelper m;
	Context mContext;
	FingerprintUiHelper mFinger;
	ImageView mIvFingerState;
//	Timer mWaitTimer = null;
//	MyWaitTimerTask mWaitTimeTask = null;
	MyTimeMgr mTimemgr = MyTimeMgr.getInstance();
	
	Handler mParentHandler;
	String mUserName;
	String mUserPsd;
	
	public FingerPrintSaveFragment setHandler(Handler h){
		this.mParentHandler = h;
		return this;
	}
	public FingerPrintSaveFragment setUserName(String name){
		mUserName = name;
		return this;
	}
	public FingerPrintSaveFragment setUserPassword(String pwd){
		mUserPsd = pwd;
		return this;
	}
	
	
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

	@SuppressLint("NewApi")
	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
		
		mContext = getContext();
		getDialog().setTitle(getString(R.string.finger_save_title));

		
		
		View v = inflater.inflate(R.layout.fingerprint_dialog_container, container, false);
		mTvCancel = (TextView) v.findViewById(R.id.tv_finger_cancel);
		mTvCancel.setOnTouchListener(this);
		mTvPassword = (TextView) v.findViewById(R.id.tv_finger_password);
		mTvPassword.setVisibility(View.GONE);
		mTvDescription = (TextView) v.findViewById(R.id.fingerprint_description);
		mTvDescription.setText(mContext.getString(R.string.fingerprint_save_description));
		
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
		if(saveDB(fingerID)){
			mParentHandler.sendEmptyMessageDelayed(MainActivity.POSTSAVEOK,500);
		}else{
			Toast.makeText(mContext, "保存失败！！", Toast.LENGTH_LONG).show();
		}
		mTvCancel.postDelayed(new Runnable() {
			public void run() {
				dismiss();
			}
		}, 300);
		
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
		case 1001://等待手指放下
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
	
	
	private boolean saveDB(int id){
		boolean res = true;
		int userNum = id;
		String userName = mUserName;
		String userPassword = mUserPsd;
		UserLoginDBBean info = new UserLoginDBBean(userNum, userName, userPassword);

		UserLoginDao dao = new UserLoginDao(mContext, "user.db", 1);
		if (dao.findByNum(userNum)) {
			dao.updataByNum(info);
		}else{
			dao.insert(info);
		}
		dao.close();
		return res;
	}
	
	
	
	public interface OnCreateViewFinish{
		void onShowListener();
	}



	@Override
	public void onFingerCancel() {
		// TODO Auto-generated method stub
		dismiss();
	}

}
