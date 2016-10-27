package com.howell.activity;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.ImageButton;
import android.widget.TextView;

import com.howell.ecamh265.R;
import com.howell.entityclass.NodeDetails;


public class CameraProperty extends Activity implements OnClickListener{
	private TextView mDeviceName,mDeviceId,mWifiIntensity;
	private ImageButton mBack;
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		setContentView(R.layout.camera_property);

		Intent intent = getIntent();
		NodeDetails dev = (NodeDetails) intent.getSerializableExtra("Device");
		mDeviceName = (TextView)findViewById(R.id.device_name);
		mDeviceId = (TextView)findViewById(R.id.device_id);
		mBack = (ImageButton)findViewById(R.id.ib_camera_property_back);
		mBack.setOnClickListener(this);
		
		if(dev != null){
			mDeviceName.setText(dev.getName());
			mDeviceId.setText(dev.getDevID());
		}
		mWifiIntensity = (TextView)findViewById(R.id.wifi_intensity);
		mWifiIntensity.setText(dev.getIntensity()+"%");
		
	}
	@Override
	public void onClick(View v) {
		// TODO Auto-generated method stub
		switch (v.getId()) {
		case R.id.ib_camera_property_back:
			finish();
			break;

		default:
			break;
		}
	}
}
