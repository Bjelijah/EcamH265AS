package com.howell.activity;

import android.app.Dialog;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.text.TextPaint;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.TextView;

import com.howell.action.FingerprintUiHelper;
import com.howell.action.PlatformAction;
import com.howell.broadcastreceiver.HomeKeyEventBroadCastReceiver;
import com.howell.ecamh265.R;
import com.howell.protocol.GetNATServerReq;
import com.howell.protocol.GetNATServerRes;
import com.howell.protocol.LoginRequest;
import com.howell.protocol.LoginResponse;
import com.howell.protocol.SoapManager;
import com.howell.utils.DecodeUtils;
import com.howell.utils.MessageUtiles;
import com.howell.utils.PhoneConfig;
import com.howell.utils.Util;
import com.zys.brokenview.BrokenCallback;

public class RegisterOrLogin extends AppCompatActivity implements OnClickListener{
	private TextView mRegister,mLogin,mTest;
	private SoapManager mSoapManager;
	private Activities mActivities;
	private HomeKeyEventBroadCastReceiver receiver;
	private Dialog waitDialog;
	

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.register_or_login);
		mActivities = Activities.getInstance();
		mActivities.addActivity("RegisterOrLogin",RegisterOrLogin.this);
		mSoapManager = SoapManager.getInstance();
		SoapManager.context = this;
		receiver = new HomeKeyEventBroadCastReceiver();
		registerReceiver(receiver, new IntentFilter(Intent.ACTION_CLOSE_SYSTEM_DIALOGS));

		mRegister = (TextView)findViewById(R.id.btn_register);
		mLogin = (TextView)findViewById(R.id.btn_login);
		mTest = (TextView)findViewById(R.id.btn_test);

		TextPaint tp = mRegister.getPaint();
		tp.setFakeBoldText(true);

		tp = mLogin.getPaint();
		tp.setFakeBoldText(true);

		tp = mTest.getPaint();
		tp.setFakeBoldText(true);

		mRegister.setOnClickListener(this);
		mLogin.setOnClickListener(this);
		mTest.setOnClickListener(this);
	}

	@Override
	protected void onStart() {
		super.onStart();

		if (!Util.isNewApi()) {
			return;
		}

		if(FingerprintUiHelper.isFingerAvailable(this)){
			FingerPrintFragment fingerFragment = new FingerPrintFragment();
	
			fingerFragment.show(getFragmentManager(), "fingerLogin");
			
			//set brokeView
			
		
			
			
		}
	}



	@Override
	public void onClick(View view) {
		switch (view.getId()) {
		case R.id.btn_register:
			Intent intent = new Intent(RegisterOrLogin.this,Register.class);
			startActivity(intent);
			break;

		case R.id.btn_login:
			intent = new Intent(RegisterOrLogin.this,MainActivity.class);
			startActivity(intent);
			break;

		case R.id.btn_test:
			waitDialog = MessageUtiles.postWaitingDialog(RegisterOrLogin.this);
			waitDialog.show();
			new AsyncTask<Void, Integer, Boolean>() {

				@Override
				protected Boolean doInBackground(Void... params) {
					PlatformAction.getInstance().setIsTest(true);
					SoapManager.initUrl(RegisterOrLogin.this);
					String encodedPassword = DecodeUtils.getEncodedPassword("100868");
					String imei = PhoneConfig.getPhoneDeveceID(RegisterOrLogin.this);
					LoginRequest loginReq = null;
					LoginResponse loginRes = null;
					try {
						loginReq = new LoginRequest("100868", "Common",encodedPassword, "1.0.0.1",imei);
						loginRes = mSoapManager.getUserLoginRes(loginReq);
					} catch (Exception e) {
						
						return false;
					}
				

					if (loginRes.getResult().toString().equals("OK")) {
						GetNATServerRes res = mSoapManager.getGetNATServerRes(new GetNATServerReq("100868", loginRes.getLoginSession()));
						Log.e("Register ", res.toString());
						Intent intent = new Intent(RegisterOrLogin.this,CameraList.class);
						startActivity(intent);
					}else{
						return false;
					}
					return true;
				}

				@Override
				protected void onPostExecute(Boolean result) {
					super.onPostExecute(result);
					if (!result) {
//						Toast.makeText(RegisterOrLogin.this, getResources().getString(R.string.login_fail), Toast.LENGTH_SHORT).show();
						Snackbar.make(mTest,getResources().getString(R.string.login_fail),Snackbar.LENGTH_LONG).show();
						
					}
					waitDialog.dismiss();
				}
			}.execute();
			break;

		default:
			break;
		}	
	}

	@Override
	protected void onDestroy() {
		super.onDestroy();
		mActivities.removeActivity("RegisterOrLogin");
		mActivities.toString();
		unregisterReceiver(receiver);
	}
	
	private class MyBrokenCallback extends BrokenCallback{

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

	
	
}
