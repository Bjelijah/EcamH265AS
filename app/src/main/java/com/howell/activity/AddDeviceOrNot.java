package com.howell.activity;

import android.app.Activity;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.ImageButton;

import com.howell.broadcastreceiver.HomeKeyEventBroadCastReceiver;
import com.howell.ecamh265.R;


public class AddDeviceOrNot extends Activity implements OnClickListener{
	private ImageButton mBack;
	private ImageButton mYes,mNo;
	
	private Activities mActivities;
	private HomeKeyEventBroadCastReceiver receiver;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		setContentView(R.layout.add_device_or_not);
		
		mActivities = Activities.getInstance();
        mActivities.addActivity("AddDeviceOrNot",AddDeviceOrNot.this);
        receiver = new HomeKeyEventBroadCastReceiver();
		registerReceiver(receiver, new IntentFilter(Intent.ACTION_CLOSE_SYSTEM_DIALOGS));
		
		mBack = (ImageButton)findViewById(R.id.ib_add_device_or_not_back);
		mYes = (ImageButton)findViewById(R.id.ib_add_device_yes);
		mNo = (ImageButton)findViewById(R.id.ib_add_device_no);
		
		mBack.setOnClickListener(this);
		mYes.setOnClickListener(this);
		mNo.setOnClickListener(this);
	}
	
    @Override
    protected void onDestroy() {
    	// TODO Auto-generated method stub
    	super.onDestroy();
    	mActivities.removeActivity("AddDeviceOrNot");
    	unregisterReceiver(receiver);
    }
	
	@Override
	public void onClick(View v) {
		// TODO Auto-generated method stub
		switch (v.getId()) {
		case R.id.ib_add_device_or_not_back:
			finish();
			break;
		case R.id.ib_add_device_yes:
			Intent intent = new Intent(AddDeviceOrNot.this,AddCamera.class);
			startActivity(intent);
			break;
		case R.id.ib_add_device_no:
			mActivities.getmActivityList().get("CamTabActivity").finish();
			mActivities.getmActivityList().get("SetWifiOrNot").finish();
			mActivities.getmActivityList().get("SetDeviceWifi").finish();
			mActivities.getmActivityList().get("SendWifi").finish();
			finish();
			intent = new Intent(AddDeviceOrNot.this,CamTabActivity.class);
			startActivity(intent);
			break;
		default:
			break;
		}
	}
}
