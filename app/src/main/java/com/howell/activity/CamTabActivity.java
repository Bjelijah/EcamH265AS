package com.howell.activity;

import android.app.TabActivity;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.util.Log;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.RadioGroup.OnCheckedChangeListener;
import android.widget.TabHost;


import com.howell.broadcastreceiver.HomeKeyEventBroadCastReceiver;
import com.howell.ecamh265.R;
//import com.howell.ehlib.MyListView.OnRefreshListener;
//import com.howell.entityclass.NodeDetails;
//import com.howell.utils.DeviceVersionUtils;
//import com.howell.protocol.LoginResponse;

@SuppressWarnings("deprecation")
public class CamTabActivity extends TabActivity implements
        OnCheckedChangeListener {

    private TabHost mHost;
    private RadioGroup mGroup;
    private RadioButton mCameraList,mLocalFiles,mSettings,mNotices;
    
    private Activities mActivities;
    private HomeKeyEventBroadCastReceiver receiver;
    
//    private SoapManager mSoapManager;
    //static int updateNum;
    
//    static boolean cameraVerThread;
    
    //private static BadgeView badge;
//    ArrayList<NodeDetails> list;
//    LoginResponse mResponse;
//    private static final int TOGGLEON = 1;
//    private static final int TOGGLEOFF = 2;
//    private static boolean hasToggled;
    
//    private DeviceVersionDetective detective;
    /*static Handler handler = new Handler(){
    	@Override
    	public void handleMessage(Message msg) {
    		// TODO Auto-generated method stub
    		super.handleMessage(msg);
    		if(msg.what == TOGGLEON){
    			badge.toggle(1);
    		}
    		if(msg.what == TOGGLEOFF){
    			badge.toggle(0);
    		}
    	}
    };*/
    
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        // TODO Auto-generated method stub
        super.onCreate(savedInstanceState);
        setContentView(R.layout.cam_tab);

        Log.e("CamTabActivity", "onCreate");
        //updateNum = 0;
//        hasToggled = false;
//        cameraVerThread = false;
        mActivities = Activities.getInstance();
        mActivities.addActivity("CamTabActivity",CamTabActivity.this);
        
        receiver = new HomeKeyEventBroadCastReceiver();
		registerReceiver(receiver, new IntentFilter(Intent.ACTION_CLOSE_SYSTEM_DIALOGS));

//		detective = DeviceVersionDetective.getInstance();
        mGroup = (RadioGroup) findViewById(R.id.radio_group);
        mGroup.setOnCheckedChangeListener(this);
        mCameraList = (RadioButton)findViewById(R.id.rb_camera_list);
        mLocalFiles = (RadioButton)findViewById(R.id.rb_local_files);
        mSettings = (RadioButton)findViewById(R.id.rb_settings);
        mNotices = (RadioButton)findViewById(R.id.rb_notices);

        mHost = getTabHost();
        mHost.addTab(mHost
                .newTabSpec("cameralist")
                .setIndicator(getResources().getString(R.string.camera_list),
                        getResources().getDrawable(R.mipmap.camera))
                .setContent(new Intent(this, CameraList.class)));
        mHost.addTab(mHost
                .newTabSpec("localfiles")
                .setIndicator(getResources().getString(R.string.local_files),
                        getResources().getDrawable(R.drawable.tab_camera_selector))
                .setContent(new Intent(this, LocalFilesActivity.class)));
        mHost.addTab(mHost
                .newTabSpec("notices")
                .setIndicator(getResources().getString(R.string.notice),
                        getResources().getDrawable(R.drawable.tab_notice_selector))
                .setContent(new Intent(this, NoticeActivity.class)));

        mHost.addTab(mHost
                .newTabSpec("settings")
                .setIndicator(getResources().getString(R.string.settings),
                        getResources().getDrawable(R.mipmap.setting))
                .setContent(new Intent(this, Settings.class)));
        mHost.setCurrentTab(0);  
        
       /* badge = new BadgeView(this, mGroup);*/
        
//        mSoapManager = SoapManager.getInstance();
//        mResponse = mSoapManager.getLoginResponse();
        
//        list = mSoapManager.getNodeDetails();
        
//        new Thread (){
//        	@Override
//        	public void run() {
//        		// TODO Auto-generated method stub
//        		super.run();
//        		while(true){
//        			if(cameraVerThread == true){
//        				break;
//        			}
//        		}
//        		try{
//	        		for(NodeDetails d:list){
//	                	System.out.println("aaaaaa");
//	                	GetDevVerReq getDevVerReq = new GetDevVerReq(mResponse.getAccount(),mResponse.getLoginSession(),d.getDevID());
//	                	GetDevVerRes res = mSoapManager.getGetDevVerRes(getDevVerReq);
//	                	Log.e("GetDevVerRes", res.toString());
//	                	//if(/*d.isOnLine() && */DeviceVersionUtils.needToUpdate(res.getCurDevVer(), res.getNewDevVer())){
//	                	if(!res.getCurDevVer().equals(res.getNewDevVer())){	
//	                		System.out.println(res.getCurDevVer()+","+res.getNewDevVer());
//	                		d.setHasUpdate(true);
//	                	}
//	                	System.out.println(d.getDevID()+":"+d.isHasUpdate());
//	                	System.out.println("cur ver:"+res.getCurDevVer()+" new ver:"+res.getNewDevVer());
//	                }
//	        		System.out.println("start notify");
//	        		detective.notifyObserver("CameraList");
//        		}catch(Exception e){
//                	System.out.println("getDevVerReq crash");
//                }
//        	}
//        }.start();
        
    }
    
	@Override
    public void onCheckedChanged(RadioGroup group, int checkedId) {
        // TODO Auto-generated method stub
        switch (checkedId) {
        case R.id.rb_camera_list:
            mHost.setCurrentTabByTag("cameralist");
            mCameraList.setTextColor(getResources().getColor(R.color.blue));
            mLocalFiles.setTextColor(getResources().getColor(R.color.light_gray));
            mSettings.setTextColor(getResources().getColor(R.color.light_gray));
            mNotices.setTextColor(getResources().getColor(R.color.light_gray));
            break;
        case R.id.rb_local_files:
            mHost.setCurrentTabByTag("localfiles");
            mLocalFiles.setTextColor(getResources().getColor(R.color.blue));
            mCameraList.setTextColor(getResources().getColor(R.color.light_gray));
            mSettings.setTextColor(getResources().getColor(R.color.light_gray));
            mNotices.setTextColor(getResources().getColor(R.color.light_gray));
            break;
        case R.id.rb_notices:
            mHost.setCurrentTabByTag("notices");
            mNotices.setTextColor(getResources().getColor(R.color.blue));
            mLocalFiles.setTextColor(getResources().getColor(R.color.light_gray));
            mCameraList.setTextColor(getResources().getColor(R.color.light_gray));
            mSettings.setTextColor(getResources().getColor(R.color.light_gray));
            break;
        case R.id.rb_settings:
            mHost.setCurrentTabByTag("settings");
            mSettings.setTextColor(getResources().getColor(R.color.blue));
            mLocalFiles.setTextColor(getResources().getColor(R.color.light_gray));
            mCameraList.setTextColor(getResources().getColor(R.color.light_gray));
            mNotices.setTextColor(getResources().getColor(R.color.light_gray));
            break;
        default:
            break;
        }
    }
	
    
    @Override
    protected void onPause() {
    	// TODO Auto-generated method stub
    	super.onPause();
    	Log.e("CamTab","onPause");
//    	for(Activity a:mActivities.getmActivityList()){
//    		a.finish();
//    	}
    }
    
    @Override
    protected void onRestart() {
    	// TODO Auto-generated method stub
    	super.onRestart();
//    	Log.e("CamTab","onRestart:"+hasToggled);
    	/*if(updateNum == 0){
    		if(!hasToggled){
    			System.out.println("toggle");
    			handler.sendEmptyMessage(TOGGLEOFF);
    			hasToggled = true;
    		}
    	}*/
    }
    @Override
    protected void onResume() {
    	// TODO Auto-generated method stub
    	super.onResume();
    	Log.e("CamTab","onResume");
    }
    @Override
    protected void onStop() {
    	// TODO Auto-generated method stub
    	Log.e("CamTab", "onStop");
    	super.onStop();
    }
    
    @Override
    protected void onDestroy() {
    	// TODO Auto-generated method stub
    	Log.e("CamTab", "onDestroy");
    	super.onDestroy();
    	mActivities.removeActivity("CamTabActivity");
    	unregisterReceiver(receiver);
    }
}
