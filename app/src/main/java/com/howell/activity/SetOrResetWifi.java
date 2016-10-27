package com.howell.activity;

import android.app.Activity;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.text.TextPaint;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.FrameLayout;
import android.widget.ImageButton;
import android.widget.TextView;

import com.howell.broadcastreceiver.HomeKeyEventBroadCastReceiver;
import com.howell.ecamh265.R;

public class SetOrResetWifi extends Activity implements OnClickListener{
	private FrameLayout setWifi,resetWifi;
	private ImageButton mBack;
	private TextView greenLightTips,redLightTips;
	private Activities mActivities;
	private HomeKeyEventBroadCastReceiver receiver;
	private static final int LIGHTON = 1;
	private static final int LIGHTOFF = 2;
	private FlashThread thread;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		setContentView(R.layout.set_wifi_or_reset_wifi);
		mActivities = Activities.getInstance();
        mActivities.addActivity("SetOrResetWifi",SetOrResetWifi.this);
        receiver = new HomeKeyEventBroadCastReceiver();
		registerReceiver(receiver, new IntentFilter(Intent.ACTION_CLOSE_SYSTEM_DIALOGS));
		
		setWifi = (FrameLayout)findViewById(R.id.fl_set_wifi);
		resetWifi = (FrameLayout)findViewById(R.id.fl_reset_wifi);
		mBack = (ImageButton)findViewById(R.id.ib_reset_wifi_back);
		greenLightTips = (TextView)findViewById(R.id.tv_green_light_tips);
		redLightTips = (TextView)findViewById(R.id.tv_red_light_tips);
		
		TextPaint tp = greenLightTips.getPaint();
        tp.setFakeBoldText(true);
        
        tp = redLightTips.getPaint();
        tp.setFakeBoldText(true);
        
        if(thread == null){
	        thread = new FlashThread();
	        thread.start();
        }
        
		setWifi.setOnClickListener(this);
		resetWifi.setOnClickListener(this);
		mBack.setOnClickListener(this);
	}
	
	private Handler handler = new Handler(){
		@Override
		public void handleMessage(Message msg) {
			// TODO Auto-generated method stub
			super.handleMessage(msg);
			switch (msg.what) {
			case LIGHTON:
				System.out.println("on");
				setWifi.setBackgroundResource(R.mipmap.cam_set_image_red);
				break;
			case LIGHTOFF:
				System.out.println("off");
				setWifi.setBackgroundResource(R.mipmap.cam_set_image_default);
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
				try {
					handler.sendEmptyMessage(LIGHTON);
					Thread.sleep(500);
					handler.sendEmptyMessage(LIGHTOFF);
					Thread.sleep(500);
				} catch (InterruptedException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			}
		};
	}
	
	@Override
	public void onClick(View v) {
		// TODO Auto-generated method stub
		switch (v.getId()) {
		case R.id.fl_set_wifi:
			Intent intent = new Intent(SetOrResetWifi.this,SetDeviceWifi.class);
			startActivity(intent);
			break;
		case R.id.fl_reset_wifi:
			intent = new Intent(SetOrResetWifi.this,FlashLighting.class);
			startActivity(intent);
			break;
		case R.id.ib_reset_wifi_back:
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
    	mActivities.removeActivity("SetOrResetWifi");
    	unregisterReceiver(receiver);
    }
    
    @Override
    protected void onStop() {
    	// TODO Auto-generated method stub
    	super.onStop();
    	thread.setThreadExit(true);
    	thread = null;
    	
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
    
}
