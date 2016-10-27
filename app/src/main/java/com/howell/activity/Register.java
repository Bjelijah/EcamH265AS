package com.howell.activity;

import android.app.Activity;
import android.app.Dialog;
import android.os.AsyncTask;
import android.os.Bundle;
import android.text.InputType;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.CompoundButton.OnCheckedChangeListener;
import android.widget.EditText;
import android.widget.ImageButton;

import com.howell.ecamh265.R;
import com.howell.protocol.CreateAccountReq;
import com.howell.protocol.CreateAccountRes;
import com.howell.protocol.SoapManager;
import com.howell.utils.DecodeUtils;
import com.howell.utils.MessageUtiles;

public class Register extends Activity implements OnClickListener{
	private ImageButton mBack;
	private ImageButton mRegister;
	private EditText mUserName,mEmail,mPassword,mPasswordAgain;
	private SoapManager mSoapManager;
	private CheckBox showPassword;
	private Dialog waitDialog;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		setContentView(R.layout.register);
		mSoapManager = SoapManager.getInstance();
		SoapManager.context = this;
		mBack = (ImageButton)findViewById(R.id.ib_register_back);
		mRegister = (ImageButton)findViewById(R.id.ib_register_ok);
		mUserName = (EditText)findViewById(R.id.et_register_account);
		mEmail = (EditText)findViewById(R.id.et_register_email);
		mPassword = (EditText)findViewById(R.id.et_register_password);
		mPasswordAgain = (EditText)findViewById(R.id.et_register_password_confirm);
		showPassword = (CheckBox)findViewById(R.id.cb_register_show_password);
		showPassword.setOnCheckedChangeListener(new OnCheckedChangeListener() {
			
			@Override
			public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
				// TODO Auto-generated method stub
				if(isChecked){
					mPassword.setInputType(InputType.TYPE_TEXT_VARIATION_VISIBLE_PASSWORD);
					mPasswordAgain.setInputType(InputType.TYPE_TEXT_VARIATION_VISIBLE_PASSWORD);
				}else{
					mPassword.setInputType(InputType.TYPE_CLASS_TEXT | InputType.TYPE_TEXT_VARIATION_PASSWORD);
					mPasswordAgain.setInputType(InputType.TYPE_CLASS_TEXT | InputType.TYPE_TEXT_VARIATION_PASSWORD);
				}
				
			}
		});
		
		mBack.setOnClickListener(this);
		mRegister.setOnClickListener(this);
	}

	@Override
	public void onClick(View view) {
		// TODO Auto-generated method stub
		switch (view.getId()) {
		case R.id.ib_register_back:
			finish();
			break;
			
		case R.id.ib_register_ok:
			//finish();
			final String password = mPassword.getText().toString();
			final String passwordAgain = mPasswordAgain.getText().toString();
			if(mUserName.getText().toString().equals("") || mPassword.getText().toString().equals("")
					|| mPasswordAgain.getText().toString().equals("")){
				MessageUtiles.postToast(Register.this, getResources().getString(R.string.register_activity_account_password_empty), 1000);
				return;
			}
			if(!password.equals(passwordAgain)){
				MessageUtiles.postToast(Register.this, getResources().getString(R.string.register_activity_password_differ), 1000);
				return ;
			}
			waitDialog = MessageUtiles.postWaitingDialog(Register.this);
			waitDialog.show();
			new AsyncTask<Void, Integer, Void>() {
				CreateAccountRes res = null;
				String account,encodedPassword,email;
				@Override
				protected void onPreExecute() {
					super.onPreExecute();
					account = mUserName.getText().toString();
					email = mEmail.getText().toString();
				}

				@Override
				protected Void doInBackground(Void... params) {
					// TODO Auto-generated method stub
					encodedPassword = DecodeUtils.getEncodedPassword(password);
					CreateAccountReq req = new CreateAccountReq(account,encodedPassword,email);
					res = mSoapManager.getCreateAccountRes(req);
					System.out.println(res.getResult());
					return null;
				}
				
				@Override
				protected void onPostExecute(Void result) {
					// TODO Auto-generated method stub
					super.onPostExecute(result);
					waitDialog.dismiss();
					if(res != null && res.getResult().equals("OK")){
						System.out.println("注册成功！");
						MessageUtiles.postToast(Register.this, getResources().getString(R.string.register_activity_success), 1000);
						finish();
					}else if(res != null && res.getResult().equals("AccountExist")){
						System.out.println("注册失败！AccountExist");
						MessageUtiles.postToast(Register.this, getResources().getString(R.string.register_activity_fail_account_exist), 1000);
					}else if(res != null && res.getResult().equals("EmailExist")){
						System.out.println("注册失败！EmailExist");
						MessageUtiles.postToast(Register.this, getResources().getString(R.string.register_activity_fail_email_exist), 1000);
					}else if(res != null && res.getResult().equals("AccountFormat")){
						System.out.println("注册失败！AccountFormat");
						MessageUtiles.postToast(Register.this, getResources().getString(R.string.register_activity_fail_account_format), 1000);
					}else {
						System.out.println("注册失败！");
						MessageUtiles.postToast(Register.this, getResources().getString(R.string.register_activity_fail), 1000);
					}
				}
				
			}.execute();
			break;

		default:
			break;
		}
	}
}
