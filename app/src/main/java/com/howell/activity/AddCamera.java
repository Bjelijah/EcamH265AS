package com.howell.activity;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;


import com.howell.broadcastreceiver.HomeKeyEventBroadCastReceiver;
import com.howell.ecamh265.R;
import com.howell.utils.MessageUtiles;
import com.howell.protocol.AddDeviceReq;
import com.howell.protocol.AddDeviceRes;
import com.howell.protocol.LoginResponse;
import com.howell.protocol.SoapManager;

public class AddCamera extends Activity implements OnClickListener{
	private ImageButton mBack;
	private Button ok,scan,search;
	private EditText devId,devKey;
	
	private Dialog waitDialog;
	private SoapManager mSoapManager;
	
	private Activities mActivities;
	private HomeKeyEventBroadCastReceiver receiver;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		setContentView(R.layout.add_camera);
		mActivities = Activities.getInstance();
        mActivities.addActivity("AddCamera",AddCamera.this);
        receiver = new HomeKeyEventBroadCastReceiver();
		registerReceiver(receiver, new IntentFilter(Intent.ACTION_CLOSE_SYSTEM_DIALOGS));
		mSoapManager = SoapManager.getInstance();
		SoapManager.context = this;
		mBack = (ImageButton)findViewById(R.id.ib_add_camera_back);
		ok = (Button)findViewById(R.id.btn_add_camera_ok);
//		scan = (Button)findViewById(R.id.btn_add_camera_scan);
		search = (Button)findViewById(R.id.btn_add_camera_search);
		devId = (EditText)findViewById(R.id.et_device_id);
		devKey = (EditText)findViewById(R.id.et_device_key);
		
		mBack.setOnClickListener(this);
		ok.setOnClickListener(this);
		scan.setOnClickListener(this);
		search.setOnClickListener(this);
	}
	
    @Override
    protected void onDestroy() {
    	// TODO Auto-generated method stub
    	super.onDestroy();
    	mActivities.removeActivity("AddCamera");
    	unregisterReceiver(receiver);
    }
    
	@Override
	public void onClick(View v) {
		// TODO Auto-generated method stub
		switch (v.getId()) {
		case R.id.ib_add_camera_back:
			finish();
			break;
		case R.id.btn_add_camera_ok:
			
			if(devId.getText().toString().equals("") || devKey.getText().toString().equals("")){
				Dialog alertDialog = new AlertDialog.Builder(this).   
			            setTitle("错误").   
			            setMessage("设备id或设备key不能为空").   
			            setIcon(R.drawable.expander_ic_minimized).   
			            setPositiveButton("确定", new DialogInterface.OnClickListener() {   

			                @Override   
			                public void onClick(DialogInterface dialog, int which) {   
			                    // TODO Auto-generated method stub    
			                	
			                }   
			            }).   
			    create();   
				alertDialog.show();   
				return;
			}
			waitDialog = MessageUtiles.postWaitingDialog(AddCamera.this);
			waitDialog.show();
			
			new AsyncTask<Void, Integer, Void>() {
				
				private AddDeviceRes res;
				private String deviceId;
				private String deviceKey;

				@Override
				protected void onPreExecute() {
					super.onPreExecute();
					deviceId = devId.getText().toString();
					deviceKey = devKey.getText().toString();
				}

				@Override
				protected Void doInBackground(Void... params) {

					
					LoginResponse mLoginResponse = mSoapManager.getLoginResponse();
					
					AddDeviceReq req = new AddDeviceReq(mLoginResponse.getAccount(),mLoginResponse.getLoginSession(),deviceId,deviceKey," ",true);
					res = mSoapManager.getAddDeviceRes(req);
					System.out.println("AddDevice res:"+res.getResult());
					return null;
				}
				
				@Override
				protected void onPostExecute(Void result) {
					// TODO Auto-generated method stub
					super.onPostExecute(result);
					waitDialog.dismiss();
					if(res!= null && res.getResult().equals("OK")){
						Intent intent = new Intent(AddCamera.this,ChangeDeviceName.class);
						intent.putExtra("devid", devId.getText().toString());
						startActivity(intent);
					}else if (res!= null && res.getResult().equals("DeviceNotExist")){
						Dialog alertDialog = new AlertDialog.Builder(AddCamera.this).   
					            setTitle("错误").   
					            setMessage("添加失败，设备id或设备key错误").   
					            setIcon(R.drawable.expander_ic_minimized).   
					            setPositiveButton("确定", new DialogInterface.OnClickListener() {   

					                @Override   
					                public void onClick(DialogInterface dialog, int which) {   
					                    // TODO Auto-generated method stub    
					                	
					                }   
					            }).   
					    create();   
						alertDialog.show(); 
					}else{
						Dialog alertDialog = new AlertDialog.Builder(AddCamera.this).   
					            setTitle("错误").   
					            setMessage("添加失败,请重新添加").   
					            setIcon(R.drawable.expander_ic_minimized).   
					            setPositiveButton("确定", new DialogInterface.OnClickListener() {   

					                @Override   
					                public void onClick(DialogInterface dialog, int which) {   
					                    // TODO Auto-generated method stub    
					                	
					                }   
					            }).   
					    create();   
						alertDialog.show(); 
					}
				}
				
			}.execute();
			break;
//		case R.id.btn_add_camera_scan:
//			Intent it = new Intent(AddCamera.this, CaptureActivity.class);
//			startActivityForResult(it, 1);
//			break;
		case R.id.btn_add_camera_search:
			
			break;
		default:
			break;
		}
	}
	
	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		// TODO Auto-generated method stub
		switch (requestCode) {
		case 1:
			if(data != null){
				String result = data.getStringExtra("result");
				if(result != null)
					//tv.setText(result);
					System.out.println(result);
				Uri uri = Uri.parse(result);  
				Intent it = new Intent(Intent.ACTION_VIEW, uri);  
				startActivity(it);
			}
			break;

		default:
			break;
		}
		
		super.onActivityResult(requestCode, resultCode, data);
	}
}
