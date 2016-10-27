package com.howell.activity;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.AsyncTask;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.EditText;
import android.widget.ImageButton;

import com.howell.broadcastreceiver.HomeKeyEventBroadCastReceiver;
import com.howell.ecamh265.R;
import com.howell.protocol.LoginResponse;
import com.howell.protocol.SoapManager;
import com.howell.protocol.UpdateAccountReq;
import com.howell.protocol.UpdateAccountRes;
import com.howell.utils.MessageUtiles;

public class ModifyPhoneNum extends Activity implements OnClickListener{
	private ImageButton mBack;
	private ImageButton mOk;
	private EditText mPhoneNum;
	private Activities mActivities;
	private SoapManager mSoapManager;
	private HomeKeyEventBroadCastReceiver receiver;
	private UpdateAccountRes res;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		setContentView(R.layout.modify_phone_num);
		mSoapManager = SoapManager.getInstance();
		SoapManager.context = this;
		mActivities = Activities.getInstance();
        mActivities.addActivity("ModifyPhoneNum",ModifyPhoneNum.this);
        receiver = new HomeKeyEventBroadCastReceiver();
		registerReceiver(receiver, new IntentFilter(Intent.ACTION_CLOSE_SYSTEM_DIALOGS));
		mPhoneNum = (EditText)findViewById(R.id.et_modify_phone_num);
		mBack = (ImageButton)findViewById(R.id.ib_modify_phone_num_back);
		mOk = (ImageButton)findViewById(R.id.ib_modify_phone_num_ok);
		mBack.setOnClickListener(this);
		mOk.setOnClickListener(this);
	}
	
    @Override
    protected void onDestroy() {
    	// TODO Auto-generated method stub
    	super.onDestroy();
    	mActivities.removeActivity("ModifyPhoneNum");
    	unregisterReceiver(receiver);
    }
	
	@Override
	public void onClick(View view) {
		// TODO Auto-generated method stub
		final ProgressDialog pd;
		switch (view.getId()) {
		case R.id.ib_modify_phone_num_back:
			finish();
			break;
		case R.id.ib_modify_phone_num_ok:
			pd = new ProgressDialog(ModifyPhoneNum.this);  
	        pd.setTitle(getResources().getString(R.string.set_new_phone_num)+"...");   //���ñ���  
	        pd.setMessage(getResources().getString(R.string.please_wait)+"..."); //����body��Ϣ  
	        pd.setProgressStyle(ProgressDialog.STYLE_SPINNER); //���ý������ʽ�� ����� 
			pd.show();
			new AsyncTask<Void, Void, Void>() {
				String phoneNum;
				@Override
				protected void onPreExecute() {
					super.onPreExecute();
					phoneNum = mPhoneNum.getText().toString();
				}

				protected Void doInBackground(Void... params) {

					try{
						LoginResponse loginRes = mSoapManager.getLoginResponse();
						UpdateAccountReq req = new UpdateAccountReq(loginRes.getAccount(),loginRes.getLoginSession(),phoneNum);
						res = mSoapManager.getUpdateAccountRes(req);
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
							MessageUtiles.postToast(ModifyPhoneNum.this.getApplicationContext(), getResources().getString(R.string.modify_cellphonenum_activity_success_toast), 1000);
							ModifyPhoneNum.this.finish();
						}else {
							MessageUtiles.postToast(ModifyPhoneNum.this.getApplicationContext(), getResources().getString(R.string.modify_cellphonenum_activity_fail_toast), 1000);
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
