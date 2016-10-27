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
import com.howell.protocol.SoapManager;

public class SetWifiOrNot extends Activity implements OnClickListener{
	private ImageButton mSetWifi,mAddDevice;
	private ImageButton mBack;
	private Activities mActivities;
	private HomeKeyEventBroadCastReceiver receiver;
	private SoapManager mSoapManager;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		setContentView(R.layout.add_device_step_select);
		mActivities = Activities.getInstance();
        mActivities.addActivity("SetWifiOrNot",SetWifiOrNot.this);
        receiver = new HomeKeyEventBroadCastReceiver();
		registerReceiver(receiver, new IntentFilter(Intent.ACTION_CLOSE_SYSTEM_DIALOGS));
		
		mSoapManager = SoapManager.getInstance();
		SoapManager.context = this;
		mSetWifi = (ImageButton)findViewById(R.id.ib_set_device_yes);
		mAddDevice = (ImageButton)findViewById(R.id.ib_set_device_no);
		
		mBack = (ImageButton)findViewById(R.id.ib_add_device_back);
		
		mSetWifi.setOnClickListener(this);
		mAddDevice.setOnClickListener(this);
		mBack.setOnClickListener(this);
	}
	


	@Override
	public void onClick(View view) {
		// TODO Auto-generated method stub
		switch (view.getId()) {
		case R.id.ib_set_device_yes:
			Intent intent = new Intent(SetWifiOrNot.this,FlashLighting.class);
			startActivity(intent);
			break;

		case R.id.ib_set_device_no:
			intent = new Intent(SetWifiOrNot.this,AddCamera.class);
			startActivity(intent);
			break;
			
		case R.id.ib_add_device_back:
			finish();
			break;
		default:
			break;
		}
	}
	
    @Override
    protected void onDestroy() {
    	// TODO Auto-generated method stub
    	super.onDestroy();
    	mActivities.removeActivity("SetWifiOrNot");
    	unregisterReceiver(receiver);
    }
}
