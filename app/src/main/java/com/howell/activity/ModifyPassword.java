package com.howell.activity;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.EditText;
import android.widget.ImageButton;

import com.howell.broadcastreceiver.HomeKeyEventBroadCastReceiver;
import com.howell.ecamh265.R;
import com.howell.protocol.LoginResponse;
import com.howell.protocol.SoapManager;
import com.howell.protocol.UpdatePasswordReq;
import com.howell.protocol.UpdatePasswordRes;
import com.howell.utils.DecodeUtils;
import com.howell.utils.MessageUtiles;

public class ModifyPassword extends Activity implements OnClickListener{
	private Activities mActivities;
	private HomeKeyEventBroadCastReceiver receiver;
	private ImageButton mBack;
	private ImageButton mOk;
	private EditText mOriginalPassword,mNewPassword,mConfirmPassword;
	private UpdatePasswordRes res;
	private SoapManager mSoapManager;
	private MyHandler handler;
	private static final int PASSWORD_DIF = 1;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		setContentView(R.layout.modify_password);
		mActivities = Activities.getInstance();
        mActivities.addActivity("ModifyPassword",ModifyPassword.this);
        receiver = new HomeKeyEventBroadCastReceiver();
		registerReceiver(receiver, new IntentFilter(
				Intent.ACTION_CLOSE_SYSTEM_DIALOGS));
		
		mSoapManager = SoapManager.getInstance();
		SoapManager.context = this;
		handler = new MyHandler();
		
		mOriginalPassword = (EditText)findViewById(R.id.et_original_password);
		mNewPassword = (EditText)findViewById(R.id.et_new_password);
		mConfirmPassword = (EditText)findViewById(R.id.et_confirm_password);
		mBack = (ImageButton)findViewById(R.id.ib_modify_password_back);
		mBack.setOnClickListener(this);
		mOk = (ImageButton)findViewById(R.id.ib_modify_password_ok);
		mOk.setOnClickListener(this);
		
		
	}
	
    @Override
    protected void onDestroy() {
    	// TODO Auto-generated method stub
    	super.onDestroy();
    	mActivities.removeActivity("ModifyPassword");
    	unregisterReceiver(receiver);
    }

    class MyHandler extends Handler{
    	@Override
    	public void handleMessage(Message msg) {
    		// TODO Auto-generated method stub
    		super.handleMessage(msg);
    		switch (msg.what) {
			case PASSWORD_DIF:
				MessageUtiles.postToast(getApplicationContext(), getResources().getString(R.string.modify_password_activity_fail_toast), 1000);
				mOriginalPassword.setText("");
				mNewPassword.setText("");
				mConfirmPassword.setText("");
				break;

			default:
				break;
			}
    	}
    }
    
	@Override
	public void onClick(View view) {
		// TODO Auto-generated method stub
		final ProgressDialog pd;
		switch (view.getId()) {
		case R.id.ib_modify_password_back:
			finish();
			break;
		case R.id.ib_modify_password_ok:
			pd = new ProgressDialog(ModifyPassword.this);  
	        pd.setTitle(getResources().getString(R.string.set_new_password)+"...");  
	        pd.setMessage(getResources().getString(R.string.please_wait)+"..."); 
	        pd.setProgressStyle(ProgressDialog.STYLE_SPINNER); 
			pd.show();
			new AsyncTask<Void, Void, Void>() {
				String originalPassword,newPassword,confirmPassword;

				@Override
				protected void onPreExecute() {
					super.onPreExecute();
					originalPassword = mOriginalPassword.getText().toString();
					newPassword = mNewPassword.getText().toString();
					confirmPassword = mConfirmPassword.getText().toString();
				}

				protected Void doInBackground(Void... params) {

					if(!newPassword.equals(confirmPassword)){
						handler.sendEmptyMessage(PASSWORD_DIF);
						return null;
					}
					System.out.println("originalPassword:"+originalPassword+" newPassword:"+newPassword);
					try{
						LoginResponse loginRes = mSoapManager.getLoginResponse();
						UpdatePasswordReq req = new UpdatePasswordReq(loginRes.getAccount(),loginRes.getLoginSession(),DecodeUtils.getEncodedPassword(originalPassword),DecodeUtils.getEncodedPassword(confirmPassword));
						res = mSoapManager.getUpdatePasswordRes(req);
						System.out.println(res.getResult());
					}catch (Exception e) {
						// TODO: handle exception
					}
					return null;
				}

				@Override
				protected void onPostExecute(Void result) {
					try{
						pd.dismiss();
						if(res.getResult().equals("OK")){
							MessageUtiles.postToast(ModifyPassword.this.getApplicationContext(), getResources().getString(R.string.modify_password_activity_success_toast), 1000);
							ModifyPassword.this.finish();
						}else if(res.getResult().equals("PasswordFormat")){
							MessageUtiles.postToast(ModifyPassword.this.getApplicationContext(), getResources().getString(R.string.modify_password_activity_passwordformat_fail), 1000);
						}else if( res.getResult().equals("Authencation")){
							MessageUtiles.postToast(ModifyPassword.this.getApplicationContext(), getResources().getString(R.string.modify_password_activity_authencation_fail), 1000);
						}
					}catch (Exception e) {
						// TODO: handle exception
					}
				}
			}.execute();
			
			break;
		default:
			break;
		}
	}
}
