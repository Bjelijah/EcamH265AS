package com.howell.activity;

import android.app.Activity;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.view.KeyEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.WindowManager;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.howell.broadcastreceiver.HomeKeyEventBroadCastReceiver;
import com.howell.ecamh265.R;
import com.howell.utils.CameraUtils;

public class FlashLighting extends Activity implements OnClickListener{
	private TextView /*tips,*/btnTips;
	private ImageButton mBack,mFlashLight;
	//private ImageView mBackground;
	private LinearLayout mSucceedTips;
	private Activities mActivities;
	private HomeKeyEventBroadCastReceiver receiver;
	private CameraUtils c;
	private boolean isBtnClicked;
	private String wifi_ssid,wifi_password,device_name;
	
	private ImageView ivFlash;
	
	private static final int LIGHTON = 1;
	private static final int LIGHTOFF = 2;
	private FlashThread thread;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		setContentView(R.layout.flash_light);

		getWindow().addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);
		isBtnClicked = false;
		mActivities = Activities.getInstance();
        mActivities.addActivity("FlashLighting",FlashLighting.this);
        receiver = new HomeKeyEventBroadCastReceiver();
		registerReceiver(receiver, new IntentFilter(Intent.ACTION_CLOSE_SYSTEM_DIALOGS));
		
		Intent intent = getIntent();
		wifi_ssid = intent.getStringExtra("wifi_ssid");
		wifi_password = intent.getStringExtra("wifi_password");
		device_name = intent.getStringExtra("device_name");
		
		c = new CameraUtils();
		
		//tips = (TextView)findViewById(R.id.tv_flash_light_success);
		mBack = (ImageButton)findViewById(R.id.ib_flash_light_back);
		mFlashLight = (ImageButton)findViewById(R.id.ib_flash_light);
		btnTips = (TextView)findViewById(R.id.tv_flash_light);
		mSucceedTips = (LinearLayout)findViewById(R.id.ll_flash_light_success);
		//mBackground = (ImageView)findViewById(R.id.iv_flash_background2);
		//mFinish = (Button)findViewById(R.id.btn_flash_light_finish);
		ivFlash = (ImageView)findViewById(R.id.iv_flash_light_success);
		
		mBack.setOnClickListener(this);
		mFlashLight.setOnClickListener(this);
		
		if(thread == null){
	        thread = new FlashThread();
	        thread.start();
        }
		//mFinish.setOnClickListener(this);
		
	}

	@Override
	public void onClick(View v) {
		// TODO Auto-generated method stub
		switch (v.getId()) {
		case R.id.ib_flash_light:
			if(!isBtnClicked){
				c.twinkle();
				isBtnClicked = true;
				mFlashLight.setImageDrawable(getResources().getDrawable(R.drawable.ok_btn_red_selector));
				btnTips.setText(getResources().getString(R.string.flash_activity_turn_red_btn_name));
				btnTips.setTextColor(getResources().getColor(R.color.red));
				mSucceedTips.setVisibility(View.VISIBLE);
			}else{
				c.stopTwinkle();
				Intent intent = new Intent(FlashLighting.this,SendWifi.class);
				intent.putExtra("wifi_ssid", wifi_ssid);
				intent.putExtra("wifi_password", wifi_password);
				intent.putExtra("device_name", device_name);
				startActivity(intent);
			}
			break;
			
		case R.id.ib_flash_light_back:
			if(c.getCamera() != null){
				c.stopTwinkle();
			}
			finish();
			break;
		default:
			break;
		}
	}
	
	@Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        super.onKeyDown(keyCode, event);
        if (keyCode == KeyEvent.KEYCODE_BACK) {
        	if(c.getCamera() != null){
				c.stopTwinkle();
			}
			finish();
        }
        return false;
    }
	
	@Override
	protected void onStop() {
		// TODO Auto-generated method stub
		super.onStop();
		System.out.println("onStop");
		thread.setThreadExit(true);
    	thread = null;
		isBtnClicked = false;
		mFlashLight.setImageDrawable(getResources().getDrawable(R.drawable.flash_light_btn_selecor));
		btnTips.setText(getResources().getString(R.string.flash_lighting_btn_tips));
		btnTips.setTextColor(getResources().getColor(R.color.btn_blue_color));
		mSucceedTips.setVisibility(View.GONE);
	}
	
    @Override
    protected void onDestroy() {
    	// TODO Auto-generated method stub
    	super.onDestroy();
    	mActivities.removeActivity("FlashLighting");
    	unregisterReceiver(receiver);
    }
    
    @Override
    protected void onRestart() {
    	// TODO Auto-generated method stub
    	super.onRestart();
    	System.out.println("onRestart");
    	if(thread == null){
    		thread = new FlashThread();
    		thread.start();
    	}
    }
    
	private Handler handler = new Handler(){
		@Override
		public void handleMessage(Message msg) {
			// TODO Auto-generated method stub
			super.handleMessage(msg);
			switch (msg.what) {
			case LIGHTON:
				System.out.println("on");
				ivFlash.setVisibility(View.VISIBLE);
				break;
			case LIGHTOFF:
				System.out.println("off");
				ivFlash.setVisibility(View.GONE);
				break;
			default:
				break;
			}
		}
	};
    
	class FlashThread extends Thread{
		private boolean threadExit ;
		
		public FlashThread() {
			super();
			this.threadExit = false;
		}

		public boolean isThreadExit() {
			return threadExit;
		}

		public void setThreadExit(boolean threadExit) {
			this.threadExit = threadExit;
		}

		public void run() {
			while(!threadExit){
//				System.out.println("start thread");
				try {
					if(mSucceedTips.isShown()){
						handler.sendEmptyMessage(LIGHTON);
						Thread.sleep(500);
						handler.sendEmptyMessage(LIGHTOFF);
						Thread.sleep(500);
					}
				} catch (InterruptedException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			}
		};
	}

}
