package com.howell.activity;

import android.app.Activity;
import android.app.Dialog;
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
import com.howell.entityclass.NodeDetails;
import com.howell.utils.MessageUtiles;
import com.howell.protocol.AddDeviceSharerReq;
import com.howell.protocol.AddDeviceSharerRes;
import com.howell.protocol.SoapManager;

public class AddSharerAccount extends Activity implements OnClickListener{
	private ImageButton mBack;
	private EditText mSharerAccount;
	private ImageButton mOk;
	private SoapManager mSoapManager;
	private NodeDetails dev;
	
	private Activities mActivities;
	private HomeKeyEventBroadCastReceiver receiver;
	private Dialog waitDialog;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		setContentView(R.layout.add_sharer_account);

		Intent intent = getIntent();
        dev = (NodeDetails) intent.getSerializableExtra("Device");
		mSoapManager = SoapManager.getInstance();
		SoapManager.context = this;
		mActivities = Activities.getInstance();
        mActivities.addActivity("AddSharerAccount",AddSharerAccount.this);
        receiver = new HomeKeyEventBroadCastReceiver();
		registerReceiver(receiver, new IntentFilter(Intent.ACTION_CLOSE_SYSTEM_DIALOGS));
		mBack = (ImageButton)findViewById(R.id.ib_add_sharer_account_back);
		mSharerAccount = (EditText)findViewById(R.id.et_add_sharer_account);
		mOk = (ImageButton)findViewById(R.id.btn_add_sharer_account_ok);
		mBack.setOnClickListener(this);
		mOk.setOnClickListener(this);
	}
	
    @Override
    protected void onDestroy() {
    	// TODO Auto-generated method stub
    	super.onDestroy();
    	mActivities.removeActivity("AddSharerAccount");
    	unregisterReceiver(receiver);
    }

	@Override
	public void onClick(View v) {
		// TODO Auto-generated method stub
		switch (v.getId()) {
		case R.id.ib_add_sharer_account_back:
			finish();
			break;
		case R.id.btn_add_sharer_account_ok:
			waitDialog = MessageUtiles.postWaitingDialog(AddSharerAccount.this);
			waitDialog.show();
			new AsyncTask<Void, Integer, Void>() {
				AddDeviceSharerRes res = null;
				String sharerAccount = null;

				@Override
				protected void onPreExecute() {
					super.onPreExecute();
					sharerAccount =  mSharerAccount.getText().toString();
				}

				@Override
				protected Void doInBackground(Void... params) {


					AddDeviceSharerReq req = new AddDeviceSharerReq(mSoapManager.getLoginResponse().getAccount(), mSoapManager.getLoginResponse().getLoginSession(), dev.getDevID(), 0, sharerAccount);
					res = mSoapManager.getAddDeviceSharerRes(req);
					System.out.println("AddDeviceSharer:"+res.getResult());
					return null;
				}
				
				@Override
				protected void onPostExecute(Void result) {
					// TODO Auto-generated method stub
					super.onPostExecute(result);
					waitDialog.dismiss();
					if(res.getResult().equals("OK")){
						MessageUtiles.postToast(AddSharerAccount.this, AddSharerAccount.this.getResources().getString(R.string.share_device_succeess), 1000);
						AddSharerAccount.this.finish();
						if(mActivities.getmActivityList().containsKey("DeviceShareToOther")){
							mActivities.getmActivityList().get("DeviceShareToOther").finish();
						}
						Intent intent = new Intent(AddSharerAccount.this,DeviceShareToOther.class);
						intent.putExtra("Device", dev);
						startActivity(intent);
					}else if(res.getResult().equals("AlreadySucceed") || res.getResult().equals("AlreadySucceed")){
						MessageUtiles.postToast(AddSharerAccount.this, AddSharerAccount.this.getResources().getString(R.string.share_device_fail_already_succeed), 1000);
					}else if(res.getResult().equals("AccountNotExist")){
						MessageUtiles.postToast(AddSharerAccount.this, AddSharerAccount.this.getResources().getString(R.string.share_device_fail_account_not_exist), 1000);
					}else {
						MessageUtiles.postToast(AddSharerAccount.this, AddSharerAccount.this.getResources().getString(R.string.share_device_fail), 1000);
					}
				}
				
			}.execute();
			break;
		default:
			break;
		}
	}

}
