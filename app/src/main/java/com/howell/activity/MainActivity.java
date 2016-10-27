package com.howell.activity;


import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.DialogInterface.OnCancelListener;
import android.content.DialogInterface.OnClickListener;
import android.content.DialogInterface.OnDismissListener;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.view.Window;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;

import com.howell.action.PlatformAction;
import com.howell.broadcastreceiver.HomeKeyEventBroadCastReceiver;
import com.howell.db.UserLoginDao;
import com.howell.ecamh265.R;
import com.howell.protocol.GetNATServerReq;
import com.howell.protocol.GetNATServerRes;
import com.howell.protocol.LoginRequest;
import com.howell.protocol.LoginResponse;
import com.howell.protocol.QueryDeviceAuthenticatedReq;
import com.howell.protocol.QueryDeviceAuthenticatedRes;
import com.howell.protocol.SoapManager;
import com.howell.protocol.UpdataDeviceAuthenticatedReq;
import com.howell.protocol.UpdataDeviceAuthenticatedRes;
import com.howell.utils.DecodeUtils;
import com.howell.utils.MessageUtiles;
import com.howell.utils.PhoneConfig;
import com.howell.utils.SharedPreferencesUtil;
import com.howell.utils.Util;

import bean.UserLoginDBBean;

public class MainActivity extends Activity implements View.OnClickListener {

	private EditText mUserName;
	private EditText mPassWord;
	private Button mButton;
	private SoapManager mSoapManager;
	private TextView mtvIEMI,mtvAuthenticated;
	private String mLoginServiceIP;
	int mLoginServciePort;
	public ProgressDialog mLoadingDialog;
	PhoneInfoSendDailog mInfoSendDailog;
	private LoginTemp mLoginTemp;
	private static final int POSTPASSWORDERROR = 1;
	private static final int POSTNULLINFO = 2;
	private static final int POSTTOAST = 3;
	private static final int POSTLINKERROR = 4;
	private static final int POSTACCOUNTERROR = 5;
	private static final int POSTLOGINFINISH = 6;
	public static final int POSTSAVEOK = 7;
	
	
	
	private MessageHandler handler;

	private static MainActivity mActivity;

	private int intentFlag;

	private Activities mActivities;
	private HomeKeyEventBroadCastReceiver receiver;

	//    private ResizeLayout layout;

	private ImageButton mBack,mSetting;
	private Dialog waitDialog;
	private boolean isSendDialogShow = false;

	//login task
	LoginTaskThread mLoginTask = null;

	
	AsyncTask mBindTask = null;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);

		mActivities = Activities.getInstance();
		mActivities.addActivity("MainActivity",MainActivity.this);
		receiver = new HomeKeyEventBroadCastReceiver();
		registerReceiver(receiver, new IntentFilter(
				Intent.ACTION_CLOSE_SYSTEM_DIALOGS));

		mActivity = this;
		mSoapManager = SoapManager.getInstance();
		SoapManager.context = this;
		mUserName = (EditText) findViewById(R.id.username);
		mPassWord = (EditText) findViewById(R.id.password);
		mButton = (Button) findViewById(R.id.ok);

		mBack = (ImageButton)findViewById(R.id.ib_login_back);
		mSetting = (ImageButton)findViewById(R.id.ib_login_setting_service);
		SharedPreferences sharedPreferences = getSharedPreferences("set",Context.MODE_PRIVATE);
		String account = sharedPreferences.getString("account", "");
		String password = sharedPreferences.getString("password", "");

		//        mUserName.setText(account);//FIXME not set TEXT 
		//        mPassWord.setText(password);

		handler = new MessageHandler();
		mButton.setOnClickListener(this);
		mBack.setOnClickListener(this);
		mSetting.setOnClickListener(this);
		Intent intent = getIntent();
		intentFlag = intent.getIntExtra("intentFlag", 0);
		if(intentFlag == 1){
			MessageUtiles.postAlertDialog(this, getResources().getString(R.string.login_fail), getResources().getString(R.string.message), R.drawable.expander_ic_minimized
					, null, getResources().getString(R.string.ok), null, null);
			//        	MessageUtiles.postNewUIDialog(this, getResources().getString(R.string.message), getResources().getString(R.string.ok), 1);
		}else if(intentFlag == 2){
			MessageUtiles.postAlertDialog(this, getResources().getString(R.string.login_fail), getResources().getString(R.string.login_error), R.drawable.expander_ic_minimized
					, null, getResources().getString(R.string.ok), null, null);
			//        	MessageUtiles.postNewUIDialog(this, getResources().getString(R.string.login_error), getResources().getString(R.string.ok), 1);
		}

		/*layout = (ResizeLayout) findViewById(R.id.layout);   
		layout.setOnResizeListener(new ResizeLayout.OnResizeListener() {   

			public void OnResize(int w, int h, int oldw, int oldh) { 
				if(oldh == 0){
					return;
				}
				if(oldh > h){
					if(oldh - h < 100)return;
					layout.setBackgroundDrawable(getResources().getDrawable(R.drawable.backgroundclear1));
				}else if(oldh < h){
					if(h - oldh < 100)return;
					layout.setBackgroundDrawable(getResources().getDrawable(R.drawable.backgroundclear));
				}else{
					return;
				}
			}   
		});   */

		mtvIEMI = (TextView) findViewById(R.id.main_tv_imei);
		mtvIEMI.setText(getResources().getString(R.string.device_id)+" : "+PhoneConfig.getPhoneDeveceID(this));

		mtvAuthenticated = (TextView) findViewById(R.id.main_tv_authenticated);
		mtvAuthenticated.setVisibility(View.GONE);
	}	

	@Override
	protected void onStart() {
		if (doCheckServiceIP()) {
			dobindLoginService();
		}


		super.onStart();
	}




	@Override
	protected void onPause() {
		//dismissSendDailog();
		super.onPause();
	}


	private boolean doCheckServiceIP(){
		boolean res = true;
		String ip = SharedPreferencesUtil.getLoginServiceIP(this);
		Log.e("123", "doCheckServiceIP="+ip);
		if (ip.equals("")) {
			Intent intent = new Intent(this,LoginSettingActivity.class);
			startActivity(intent);
			return false;
		}
		
		SoapManager.initUrl(this);
		
		return res;
	} 

	private void dobindLoginService(){
		//
		Log.e("123", "do bind login server");
		mBindTask = new AsyncTask<Void, Void, Boolean>(){
			@Override
			protected Boolean doInBackground(Void... params) {
				boolean res = false;
				QueryDeviceAuthenticatedReq req = new QueryDeviceAuthenticatedReq(PhoneConfig.getPhoneDeveceID(MainActivity.this));
				QueryDeviceAuthenticatedRes resObJ = null;
				if (isCancelled()) {
					return false;
				}
				try {
					resObJ = mSoapManager.getDeviceAuthenticatedRes(req);
					if (isCancelled()) {
						return false;
					}
					if (resObJ.getResult().toString().equals("OK")) {
						res = true;
					}
					if (!resObJ.isAuthenticated()) {
						mtvAuthenticated.post(new Runnable() {
							public void run() {
								mtvAuthenticated.setVisibility(View.GONE);
							}
						});
					}else{
						mtvAuthenticated.post(new Runnable() {
							public void run() {
								mtvAuthenticated.setVisibility(View.GONE);
							}
						});
					}
				} catch (Exception e) {
					e.printStackTrace();
					return false;
				}
				return res;
			}

			protected void onPostExecute(Boolean result) {
				if (!result) {
					Log.e("123", "show send dailog");
					new Runnable() {
						public void run() {
							showSendDailog();
						}
					}.run();
				}
			};
		}.execute();
	}


	private void showSendDailog(){
		if (Util.isNewApi()) {
			if (mInfoSendDailog!=null && mInfoSendDailog.isShow() ) {
				return;
			}
			mInfoSendDailog = new PhoneInfoSendDailog();
			//			FragmentManager fragmentManager =  getFragmentManager();
			//			FragmentTransaction ft = fragmentManager.beginTransaction();
			//			ft.setTransitionStyle(R.style.DialogAnimation);
			//			ft.commit();

			mInfoSendDailog.show( getFragmentManager(), "phoneInfoSend");
			//			Window window =mInfoSendDailog.getDialog().getWindow();
			//			window.setWindowAnimations(R.style.DialogAnimation);

		}else{
			//用 alart 发送
			//			SendInfoDialog sDialog = new SendInfoDialog(this);
			//			sDialog.show();
			showAlartDialog();
		}


	}

	private void showAlartDialog(){
		if (isSendDialogShow) {
			return;
		}
		String IEMI = PhoneConfig.getPhoneDeveceID(this);
		AlertDialog alertDialog = new AlertDialog.Builder(this,R.style.myLightAlertDialog) 
				.setTitle(getString(R.string.phone_info_send_title))   
				.setMessage("设备ID："+IEMI) 
				.setNegativeButton(getString(R.string.cancel), null)
				.setPositiveButton(getString(R.string.send_wifi_config_activity_btn_send), new OnClickListener() {

					@Override
					public void onClick(DialogInterface dialog, int which) {
						new AsyncTask<Void, Void, Boolean>(){
							@Override
							protected Boolean doInBackground(Void... params) {
								boolean res = false;
								UpdataDeviceAuthenticatedReq req = new UpdataDeviceAuthenticatedReq(PhoneConfig.getPhoneDeveceID(MainActivity.this),
										PhoneConfig.getPhoneModel(),
										PhoneConfig.getOSVersion(),
										PhoneConfig.getPhoneManufactory(),
										PhoneConfig.getPhoneDeveceID(MainActivity.this));
								UpdataDeviceAuthenticatedRes resObj = null;
								try {
									resObj = mSoapManager.getUpdataDeviceAuthenticatedRes(req);
									if ( resObj.getResult().equals("OK")) {
										res = true;
									}
								} catch (Exception e) {
									e.printStackTrace();
									return false;
								}
								return res;
							}
							protected void onPostExecute(Boolean result) {
								String string = "";
								if (result) {
									string = MainActivity.this.getString(R.string.phone_info_send_ok);
								}else{
									string = MainActivity.this.getString(R.string.phone_info_send_fail);
								}
								Toast.makeText(MainActivity.this, string, Toast.LENGTH_LONG).show();
							};
						}.execute();
					}
				})   
				.setOnCancelListener(new OnCancelListener() {

					@Override
					public void onCancel(DialogInterface dialog) {
						Log.i("123", "OnCancelListener");
						isSendDialogShow = false;
					}
				})
				.setOnDismissListener(new OnDismissListener() {

					@Override
					public void onDismiss(DialogInterface dialog) {
						Log.i("123", "onDismiss");
						isSendDialogShow = false;
					}
				}).create();
		Window window = alertDialog.getWindow();
		window.setWindowAnimations(R.style.DialogAnimation);
		//		window.setWindowAnimations(R.style.DialogSend);
		alertDialog.show();  
		isSendDialogShow = true;
	}


	private static MainActivity getContext(){
		return mActivity;
	}

	@Override
	public void onClick(View v) {
		switch (v.getId()) {
		case R.id.ib_login_back:
			finish();
			break;
		case R.id.ib_login_setting_service:
			Intent intent = new Intent(this,LoginSettingActivity.class);
			startActivity(intent);
			break;
		case R.id.ok:
			final String account = mUserName.getText().toString().trim();
			final String password = mPassWord.getText().toString().trim();
			if (TextUtils.isEmpty(account) && TextUtils.isEmpty(password)) {
				MessageUtiles.postAlertDialog(this, getResources().getString(R.string.login_fail), getResources().getString(R.string.verification), R.drawable.expander_ic_minimized
						, null, getResources().getString(R.string.ok), null, null);
				//				MessageUtiles.postNewUIDialog2(MainActivity.getContext(), MainActivity.getContext().getString(R.string.verification), MainActivity.getContext().getString(R.string.ok), 1);
				return;
			}
			PlatformAction.getInstance().setIsTest(false);
			SoapManager.initUrl(this);
			waitDialog = MessageUtiles.postWaitingDialog(MainActivity.this);
			waitDialog.setOnCancelListener(new OnCancelListener() {

				@Override
				public void onCancel(DialogInterface dialog) {
					Log.e("123", "wait Dialog on cancel");
					if (mLoginTask!=null) {
						Log.i("123", "mlogintask cancel");
						mLoginTask.cancel();
						mLoginTask = null;
					}
				}
			});
			waitDialog.show();
			Log.i("123", "execute task");
			mLoginTask = new LoginTaskThread(password,account);
			mLoginTask.start();
			break;
		default:
			break;
		}

	}

	@Deprecated
	private void saveUserInfo2Db(){
		long sn = PhoneConfig.showUserSerialNum(this);
		if (sn<0) {
			Toast.makeText(this, getResources().getString(R.string.save_db_error), Toast.LENGTH_SHORT).show();
			return ;
		}
		int userNum = (int)sn;
		String userName = PlatformAction.getInstance().getAccount();
		String userPassword = PlatformAction.getInstance().getPassword();
		UserLoginDBBean info = new UserLoginDBBean(userNum, userName, userPassword);

		UserLoginDao dao = new UserLoginDao(this, "user.db", 1);
		if (dao.findByNum(userNum)) {
			dao.updataByNum(info);
		}else{
			dao.insert(info);
		}
		/*test show*/
		//		List<UserLoginDBBean> list = dao.queryAll();
		//		for(UserLoginDBBean o: list){
		//			Log.i("123", o.toString());
		//		}
		dao.close();
		//		Toast.makeText(this, getResources().getString(R.string.save_db_ok), Toast.LENGTH_SHORT).show();

	}


	public  class MessageHandler extends Handler{

		@Override
		public void handleMessage(Message msg) {
			super.handleMessage(msg);
			if (msg.what == POSTPASSWORDERROR) {
				MessageUtiles.postAlertDialog(MainActivity.getContext(), MainActivity.getContext().getString(R.string.login_fail), MainActivity.getContext().getString(R.string.password_error), R.drawable.expander_ic_minimized
						, null, MainActivity.getContext().getString(R.string.ok), null, null);
				// 				MessageUtiles.postNewUIDialog2(MainActivity.getContext(), MainActivity.getContext().getString(R.string.password_error), MainActivity.getContext().getString(R.string.ok), 1);
			}
			if (msg.what == POSTACCOUNTERROR) {
				MessageUtiles.postAlertDialog(MainActivity.getContext(), MainActivity.getContext().getString(R.string.login_fail), MainActivity.getContext().getString(R.string.account_error), R.drawable.expander_ic_minimized
						, null, MainActivity.getContext().getString(R.string.ok), null, null);
				// 				MessageUtiles.postNewUIDialog2(MainActivity.getContext(), MainActivity.getContext().getString(R.string.account_error), MainActivity.getContext().getString(R.string.ok), 1);
			}
			if (msg.what == POSTLINKERROR) {
				MessageUtiles.postAlertDialog(MainActivity.getContext(), MainActivity.getContext().getString(R.string.login_fail), MainActivity.getContext().getString(R.string.login_error), R.drawable.expander_ic_minimized
						, null, MainActivity.getContext().getString(R.string.ok), null, null);
				// 				MessageUtiles.postNewUIDialog2(MainActivity.getContext(), MainActivity.getContext().getString(R.string.login_error), MainActivity.getContext().getString(R.string.ok), 1);
				//取消waitdialog //FIXME
				if (waitDialog.isShowing()) {
					waitDialog.dismiss();
				}
				
			}
			if (msg.what == POSTNULLINFO) {
				MessageUtiles.postAlertDialog(MainActivity.getContext(), MainActivity.getContext().getString(R.string.login_fail), MainActivity.getContext().getString(R.string.verification), R.drawable.expander_ic_minimized
						, null, MainActivity.getContext().getString(R.string.ok), null, null);
				// 				MessageUtiles.postNewUIDialog2(MainActivity.getContext(), MainActivity.getContext().getString(R.string.verification), MainActivity.getContext().getString(R.string.ok), 1);
			}
			if(msg.what == POSTTOAST){
				MessageUtiles.postToast(MainActivity.getContext(), MainActivity.getContext().getString(R.string.loading), 1000);
			}
			if (msg.what == POSTLOGINFINISH) {
				Bundle b = (Bundle) msg.obj;
				LoginResponse l = (LoginResponse) b.getSerializable("loginRes");
				String pwd = b.getString("password");
				String acc = b.getString("account");

				loginRes(l,pwd,acc);
			}
			if (msg.what == POSTSAVEOK) {
				foo();
			}
		}
	}

	@Override
	protected void onDestroy() {
		super.onDestroy();
		if (mBindTask!=null) {
			mBindTask.cancel(true);
			mBindTask = null;
		}
		mActivities.removeActivity("MainActivity");
		unregisterReceiver(receiver);
	}


	
	
	
	private class LoginTaskThread extends Thread{
		LoginResponse loginRes;
		String password= null;
		String account = null;
		boolean isCancel = false;
		public LoginTaskThread(String pwd,String acc) {
			this.password = pwd;
			this.account = acc;
			isCancel = false;
		}

		public void cancel(){
			isCancel = true;
		}

		@Override
		public void run() {
			try{
				String encodedPassword = DecodeUtils.getEncodedPassword(password);
				String imei = PhoneConfig.getPhoneDeveceID(MainActivity.this);
				LoginRequest loginReq = new LoginRequest(account, "Common",encodedPassword, "1.0.0.1",imei);
				loginRes = mSoapManager.getUserLoginRes(loginReq);
				Log.e("loginRes",loginRes.getResult().toString());
			}catch (Exception e) {
				if (!isCancel) {
					handler.sendEmptyMessage(POSTLINKERROR);
				}
				return;
			}
			if (isCancel) {
				return;
			}
			
			
			Message msg = new Message();
			msg.what = POSTLOGINFINISH;


			Bundle bundle = new Bundle();
			bundle.putSerializable("loginRes", loginRes);
			bundle.putString("password", password);
			bundle.putString("account", account);
			msg.obj = bundle;
	
			handler.sendMessage(msg);
			


			super.run();
		}

	}








	private void loginRes(LoginResponse loginRes ,String password,String account){
		waitDialog.dismiss();
		if(loginRes == null){
			Log.e("123", "loginRes==null");
			return;
		}
		if (loginRes.getResult().toString().equals("OK")) {	
			mLoginTemp = new LoginTemp(loginRes, password, account);
			
			//绑定指纹
			FingerPrintSaveFragment fragment = new FingerPrintSaveFragment();
			fragment.setHandler(handler).setUserName(account).setUserPassword(password);
			fragment.show(getFragmentManager(), "fingerSave");
		}else if(loginRes.getResult().toString().equals("AccountNotExist")){
			MessageUtiles.postAlertDialog(MainActivity.this, getResources().getString(R.string.login_fail), getResources().getString(R.string.account_error), R.drawable.expander_ic_minimized
					, null, getResources().getString(R.string.ok), null, null);
			//		            	 MessageUtiles.postNewUIDialog2(MainActivity.getContext(), MainActivity.getContext().getString(R.string.account_error), MainActivity.getContext().getString(R.string.ok), 1);
		}else if(loginRes.getResult().toString().equals("Authencation")){
			MessageUtiles.postAlertDialog(MainActivity.this, getResources().getString(R.string.login_fail), getResources().getString(R.string.password_error), R.drawable.expander_ic_minimized
					, null, getResources().getString(R.string.ok), null, null);
			//		            	 MessageUtiles.postNewUIDialog2(MainActivity.getContext(), MainActivity.getContext().getString(R.string.password_error), MainActivity.getContext().getString(R.string.ok), 1);
		}else if(loginRes.getResult().toString().equals("DeviceUnauthorized")){
			//设备未授权
			Log.i("123", "设备未授权 或没有注册");
			MessageUtiles.postAlertDialog(MainActivity.this, getResources().getString(R.string.login_fail), getResources().getString(R.string.phone_info_not_authenticated), R.drawable.expander_ic_minimized
					, null,  getResources().getString(R.string.ok), null, null);
		}
		else{
			Log.e("123", "loging error result="+loginRes.getResult().toString());
			MessageUtiles.postAlertDialog(MainActivity.this, getResources().getString(R.string.login_fail), getResources().getString(R.string.login_error), R.drawable.expander_ic_minimized
					, null, getResources().getString(R.string.ok), null, null);
			//		            	 MessageUtiles.postNewUIDialog2(MainActivity.getContext(), MainActivity.getContext().getString(R.string.login_error), MainActivity.getContext().getString(R.string.ok), 1);
		}
	}
	
	private void foo(){
		
		
		LoginResponse loginRes = mLoginTemp.getLoginRes();
		String account = mLoginTemp.getAccount();
		String password = mLoginTemp.getPassword();
		SharedPreferences sharedPreferences = getSharedPreferences(
				"set", Context.MODE_PRIVATE);
		Editor editor = sharedPreferences.edit();
		editor.putString("account", account);
		editor.putString("password", password);
		editor.commit();
		PlatformAction.getInstance().setAccount(account);
		PlatformAction.getInstance().setPassword(password);	                     
		PlatformAction.getInstance().setDeviceList(loginRes.getNodeList());
		GetNATServerRes res = mSoapManager.getGetNATServerRes(new GetNATServerReq(account, loginRes.getLoginSession()));
		Log.e("MainActivity", res.toString());
		PlatformAction.getInstance().setTurnServerIP(res.getTURNServerAddress());
		PlatformAction.getInstance().setTurnServerPort(res.getTURNServerPort());
		
//		saveUserInfo2Db();
		
		
		Intent intent = new Intent(MainActivity.this,CamTabActivity.class);
		startActivity(intent);
		finish();

		if (mActivities.getmActivityList().get("RegisterOrLogin")!=null) {
			mActivities.getmActivityList().get("RegisterOrLogin").finish();
		}else{
			Log.e("123", "RegisterOrLogin == null");
		}
	}
	
	
	
	class LoginTemp{
		LoginResponse loginRes ;
		String password;
		String account;
		public LoginResponse getLoginRes() {
			return loginRes;
		}
		public void setLoginRes(LoginResponse loginRes) {
			this.loginRes = loginRes;
		}
		public String getPassword() {
			return password;
		}
		public void setPassword(String password) {
			this.password = password;
		}
		public String getAccount() {
			return account;
		}
		public void setAccount(String account) {
			this.account = account;
		}
		public LoginTemp(LoginResponse loginRes, String password, String account) {
			super();
			this.loginRes = loginRes;
			this.password = password;
			this.account = account;
		}
	}
	
}
