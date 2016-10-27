package com.howell.activity;


import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.util.Log;
import android.view.Gravity;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup.LayoutParams;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.PopupWindow;
import android.widget.SeekBar;
import android.widget.SeekBar.OnSeekBarChangeListener;
import android.widget.TextView;
import android.widget.Toast;


import com.howell.action.UserPowerAction;
import com.howell.broadcastreceiver.HomeKeyEventBroadCastReceiver;
import com.howell.ecamh265.R;
import com.howell.entityclass.NodeDetails;
import com.howell.utils.AlerDialogUtils;
import com.howell.utils.MessageUtiles;
import com.howell.utils.DeviceVersionUtils;
import com.howell.entityclass.VMDGrid;
import com.howell.protocol.CodingParamReq;
import com.howell.protocol.CodingParamRes;
import com.howell.protocol.GetAuxiliaryReq;
import com.howell.protocol.GetAuxiliaryRes;
import com.howell.protocol.GetDevVerReq;
import com.howell.protocol.GetDevVerRes;
import com.howell.protocol.GetVideoParamReq;
import com.howell.protocol.LoginResponse;
import com.howell.protocol.NullifyDeviceReq;
import com.howell.protocol.NullifyDeviceRes;
import com.howell.protocol.QueryDeviceReq;
import com.howell.protocol.QueryDeviceRes;
import com.howell.protocol.SetAuxiliaryReq;
import com.howell.protocol.SetAuxiliaryRes;
import com.howell.protocol.SetVideoParamReq;
import com.howell.protocol.SetVideoParamRes;
import com.howell.protocol.SoapManager;
import com.howell.protocol.SubscribeAndroidPushReq;
import com.howell.protocol.SubscribeAndroidPushRes;
import com.howell.protocol.VMDParamReq;
import com.howell.protocol.VMDParamRes;

public class DeviceSetActivity extends Activity implements
        OnSeekBarChangeListener ,OnClickListener{
	
    private TextView mTvDeviceName,mCameraUpdateStatus;
    private static SoapManager mSoapManager;
    private SeekBar mSeekBar_reso, mSeekBar_quality;
    private static LoginResponse mLoginResponse;
    private String[] mFrameSizeValues;
    private CodingParamRes mCodingParamRes;
    private TextView reso_text_,quality_text_;
    private CheckBox vmd_checkbox_,video_checkbox,power_led_checkbox;
    private VMDParamRes vmd_res_;
//    private static int backCount;
    public NodeDetails dev;
    private boolean isCrashed;
    private static int[][] reso_bitrate_map_ = {{96,128,196},{128,256,384},{1024,1536,2048}};
    private static String[] VMD_DEFAULT_GRIDS = {
    	"00000000000",
    	"00000000000",
    	"00011111000",
    	"00011111000",
    	"00011111000",
    	"00011111000",
    	"00011111000",
    	"00000000000",
    	"00000000000",
    };
    private static String[] VMD_ZERO_GRIDS = {
    	"00000000000",
    	"00000000000",
    	"00000000000",
    	"00000000000",
    	"00000000000",
    	"00000000000",
    	"00000000000",
    	"00000000000",
    	"00000000000",
    };
    
    private Activities mActivities;
    private HomeKeyEventBroadCastReceiver receiver;
    private ProgressDialog pd;
    private Button mUpdateButton;
    private LinearLayout ll_alarm_push,mShareDevice,mRemoveDevice;
    private CheckBox cb_alarm_notice;
    private TextView mCameraVersion;
    private TextView mTvTurnOver,mTvLightState;
    
    private static final int CRASH = 1;
    private static final int ALARMPUSHOFF = 2;
    private int gainedReso,gainedQuality;
    
	private PopupWindow popupWindow;
	private ImageButton mBack;
    
    @Override
    protected void onCreate(Bundle savedInstanceState) {
     
        super.onCreate(savedInstanceState);
        setContentView(R.layout.deviceset);
        
        mActivities = Activities.getInstance();
    	mActivities.addActivity("DeviceSetActivity",DeviceSetActivity.this);
    	receiver = new HomeKeyEventBroadCastReceiver();
		registerReceiver(receiver, new IntentFilter(Intent.ACTION_CLOSE_SYSTEM_DIALOGS));
		
//        backCount = 0;
        gainedReso = -1;
        gainedQuality = -1;
        mSoapManager = SoapManager.getInstance();
    	SoapManager.context = this;
        mTvDeviceName = (TextView) findViewById(R.id.tv_device_name);
        mSeekBar_reso = (SeekBar) findViewById(R.id.seekBar1);
        mSeekBar_reso.setMax(2);
//        mSeekBar_reso.setMax(1);
        mSeekBar_reso.setOnSeekBarChangeListener(this);
        //FIXME
        mSeekBar_reso.setEnabled(false);
        
        reso_text_ = (TextView) findViewById(R.id.resolutoin_str);
        reso_text_.setTextColor(getResources().getColor(R.color.gray));
        
        
        mSeekBar_quality = (SeekBar) findViewById(R.id.seekBar2);
        mSeekBar_quality.setMax(2);
        quality_text_ = (TextView) findViewById(R.id.quality_str);
        vmd_checkbox_ = (CheckBox)findViewById(R.id.vmd_enable);
        
//        vmd_checkbox_.setEnabled(false);//FIXME 
        
        ((TextView)findViewById(R.id.vmd_text)).setTextColor(getResources().getColor(R.color.gray));//FIXME
        
        
        video_checkbox = (CheckBox)findViewById(R.id.turn_over);
        
//        video_checkbox.setEnabled(false);//FIXME
        
        power_led_checkbox = (CheckBox)findViewById(R.id.power_led);
    
        mCameraUpdateStatus = (TextView)findViewById(R.id.camera_update_status);
//        power_led_checkbox.setEnabled(false);//FIXME
        mCameraUpdateStatus.setTextColor(getResources().getColor(R.color.gray));
        mUpdateButton = (Button)findViewById(R.id.setting_update_button);
        ll_alarm_push = (LinearLayout)findViewById(R.id.ll_alarm_push);
        mShareDevice = (LinearLayout)findViewById(R.id.ll_deviceset_share);
        mRemoveDevice = (LinearLayout)findViewById(R.id.ll_deviceset_remove);
        cb_alarm_notice = (CheckBox)findViewById(R.id.alarm_notice);
      
        
        
        mCameraVersion = (TextView)findViewById(R.id.tv_camera_version);
        mTvTurnOver = (TextView)findViewById(R.id.tv_device_set_picture_turn_over);
//        mTvTurnOver.setTextColor(getResources().getColor(R.color.gray));//FIXME
        mTvLightState = (TextView)findViewById(R.id.tv_device_set_light_state);
//        mTvLightState.setTextColor(getResources().getColor(R.color.gray));//FIXME
        mBack = (ImageButton)findViewById(R.id.ib_device_set_back);
        
        mSeekBar_reso.setOnSeekBarChangeListener(this);
        mSeekBar_quality.setOnSeekBarChangeListener(this);
        mShareDevice.setOnClickListener(this);
        mRemoveDevice.setOnClickListener(this);
        mBack.setOnClickListener(this);

        Intent intent = getIntent();
        dev = (NodeDetails) intent.getSerializableExtra("Device");
        mTvDeviceName.setText(dev.getName());

        mLoginResponse = mSoapManager.getLoginResponse();
        
        mUpdateButton.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				if(dev.getSharingFlag() == 1){
					MessageUtiles.postToast(DeviceSetActivity.this, DeviceSetActivity.this.getResources().getString(R.string.device_set_sharer_no_property), 1000);
					return ;
				}
				if(!dev.isHasUpdate())return;
				AlerDialogUtils.postDialog(DeviceSetActivity.this,dev);
			}
		});
        
        video_checkbox.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View arg0) {
				pd = new ProgressDialog(DeviceSetActivity.this);  
		        pd.setTitle(getResources().getString(R.string.save_set)+"...");   
		        pd.setMessage(getResources().getString(R.string.please_wait)+"..."); 
		        pd.setProgressStyle(ProgressDialog.STYLE_SPINNER); 
				pd.show();
				new AsyncTask<Void, Void, Void>() {
					boolean isTurnedOver;
					protected Void doInBackground(Void... params) {
						try{
							isTurnedOver = saveVideoParam();
						}catch (Exception e) {
						}
						return null;
					}

					@Override
					protected void onPostExecute(Void result) {
						try{
							pd.dismiss();
							if(isTurnedOver){
								mTvTurnOver.setText(getResources().getString(R.string.turn_over_180));
							}else{
								mTvTurnOver.setText(getResources().getString(R.string.turn_over_0));
							}
						}catch (Exception e) {
						}
					}
				}.execute();
			}
		});
        
        power_led_checkbox.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View arg0) {
				pd = new ProgressDialog(DeviceSetActivity.this);  
		        pd.setTitle(getResources().getString(R.string.save_set)+"...");   //���ñ���  
		        pd.setMessage(getResources().getString(R.string.please_wait)+"..."); //����body��Ϣ  
		        pd.setProgressStyle(ProgressDialog.STYLE_SPINNER); //���ý������ʽ�� ����� 
				pd.show();
				new AsyncTask<Void, Void, Void>() {
					boolean isLighted;
					protected Void doInBackground(Void... params) {
						try{
							isLighted = savePowerLedParam();
						}catch (Exception e) {
						}
						return null;
					}

					@Override
					protected void onPostExecute(Void result) {
						try{
							pd.dismiss();
							if(isLighted){
								mTvLightState.setText(getResources().getString(R.string.power_land_on));
							}else{
								mTvLightState.setText(getResources().getString(R.string.power_land_off));
							}
						}catch (Exception e) {
						}
					}
				}.execute();
			}
		});
        
        vmd_checkbox_.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View arg0) {
				pd = new ProgressDialog(DeviceSetActivity.this);  
		        pd.setTitle(getResources().getString(R.string.save_set)+"...");   //���ñ���  
		        pd.setMessage(getResources().getString(R.string.please_wait)+"..."); //����body��Ϣ  
		        pd.setProgressStyle(ProgressDialog.STYLE_SPINNER); //���ý������ʽ�� ����� 
				pd.show();
				new AsyncTask<Void, Void, Void>() {
					protected Void doInBackground(Void... params) {
						try{
							saveVMDParam();
						}catch (Exception e) {
						}
						return null;
					}

					@Override
					protected void onPostExecute(Void result) {
						pd.dismiss();
						if(vmd_checkbox_.isChecked()){
							ll_alarm_push.setVisibility(View.VISIBLE);
						}else{
							ll_alarm_push.setVisibility(View.GONE);
						}
					}
				}.execute();
			}
		});
        
        cb_alarm_notice.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View arg0) {
				pd = new ProgressDialog(DeviceSetActivity.this);  
		        pd.setTitle(getResources().getString(R.string.save_set)+"...");   //���ñ���  
		        pd.setMessage(getResources().getString(R.string.please_wait)+"..."); //����body��Ϣ  
		        pd.setProgressStyle(ProgressDialog.STYLE_SPINNER); //���ý������ʽ�� ����� 
				pd.show();
				new AsyncTask<Void, Void, Void>() {
					boolean alarmPush = false;
					@Override
					protected void onPreExecute() {
						super.onPreExecute();
						alarmPush = cb_alarm_notice.isChecked();
					}

					protected Void doInBackground(Void... params) {
						try{
							saveAlarmPushParam(alarmPush);
						}catch (Exception e) {
						}
						return null;
					}

					@Override
					protected void onPostExecute(Void result) {
						try{
							pd.dismiss();
						}catch (Exception e) {
						}
					}
				}.execute();
			}
		});
        
		if(dev.isOnLine()){
        pd = new ProgressDialog(DeviceSetActivity.this);  
        pd.setTitle(getResources().getString(R.string.gain_set)+"...");   
        pd.setMessage(getResources().getString(R.string.please_wait)+"...");  
        pd.setProgressStyle(ProgressDialog.STYLE_SPINNER); 
		pd.show();
		
		new AsyncTask<Void, Void, Void>() {
			int bitrate = 0;
	        String framesize=null;
	        int rotationDegree = 0;
	        int reso_idx=-1;
	        GetAuxiliaryRes getAuxiliaryRes = null;
	        GetDevVerRes res = null;
	        QueryDeviceRes queryDeviceRes = null;
			protected Void doInBackground(Void... params) {
		        System.out.println("111111111111111");
				 try{
				        if (mLoginResponse.getResult().toString().equals("OK")) {
				            String account = mLoginResponse.getAccount().toString();
				            String loginSession = mLoginResponse.getLoginSession().toString();

				            CodingParamReq req = new CodingParamReq(account, loginSession,
				                    dev.getDevID(), dev.getChannelNo(), "Sub");

				            mCodingParamRes = mSoapManager.getCodingParamRes(req);
				            framesize = mCodingParamRes.getFrameSize();
				            Log.v("dev","frame size is "+framesize);
				            bitrate = Integer.parseInt(mCodingParamRes.getBitRate());
				            Log.v("dev","image qualit is "+mCodingParamRes.getImageQuality());
				            Log.v("dev","image bitrate is "+bitrate);
				            VMDParamReq vmd_req = new VMDParamReq(account,loginSession,dev.getDevID(),dev.getChannelNo());
				            vmd_res_ = mSoapManager.getVMDParam(vmd_req);
				            Log.v("dev", "vmd enable: "+vmd_res_.getEnabled());
				            if(vmd_res_.getEnabled()){
				            	QueryDeviceReq queryDeviceReq = new QueryDeviceReq(mLoginResponse.getAccount(),mLoginResponse.getLoginSession(),dev.getDevID());
				            	queryDeviceRes = new QueryDeviceRes();
				            	queryDeviceRes = mSoapManager.getQueryDeviceRes(queryDeviceReq);
				            	System.out.println("is Push?"+queryDeviceRes.toString());
				            }
				        }
				        
				        System.out.println("2222222222222222");
				        mFrameSizeValues = getResources().getStringArray(R.array.FrameSize);
				        
				        for (int i=0; i<mFrameSizeValues.length; ++i) {
				        	if (mFrameSizeValues[i].equals(framesize)) {
				        		reso_idx = i;
				        		break;
				        	}
				        }
				        GetAuxiliaryReq getAuxiliaryReq = new GetAuxiliaryReq(mLoginResponse.getAccount(),mLoginResponse.getLoginSession(),dev.getDevID(),"SignalLamp");
				        getAuxiliaryRes = mSoapManager.getGetAuxiliaryRes(getAuxiliaryReq);
				        System.out.println("getAuxiliaryRes"+getAuxiliaryRes.getResult());
				        Log.d("aux", "aux: "+getAuxiliaryRes.getAuxiliaryState());
				        System.out.println("333333333333333");
				        GetVideoParamReq getVideoParamReq = new GetVideoParamReq(mLoginResponse.getAccount(),mLoginResponse.getLoginSession(),dev.getDevID(), dev.getChannelNo());
				    	rotationDegree = mSoapManager.getGetVideoParamRes(getVideoParamReq).getRotationDegree();
				    	System.out.println("rotationDegree:"+rotationDegree);
				    	
				    	 System.out.println("4444444444444444444");
				    	GetDevVerReq getDevVerReq = new GetDevVerReq(mLoginResponse.getAccount(),mLoginResponse.getLoginSession(),dev.getDevID());
				    	res = mSoapManager.getGetDevVerRes(getDevVerReq);
				    	Log.e("GetDevVerRes", res.toString());
				    	
				        }catch (Exception e) {
				        	System.out.println("crash!!!!!!");
				        	isCrashed = true;
				        	pd.dismiss();
				        	handler.sendEmptyMessage(CRASH);
						}
				 System.out.println("55555555555555");
				return null;
			}

			@Override
			protected void onPostExecute(Void result) {
				try{
					System.out.println("re11111111111");
					Log.e("123", "reso_idx="+reso_idx   +  "  bitrate="+bitrate);
					 if (reso_idx>=0) {
						 gainedReso = reso_idx;
				        	mSeekBar_reso.setProgress(reso_idx);
				        	refreshResolutionText(reso_idx);
				        	
				        	for (int i=0; i<reso_bitrate_map_[reso_idx].length; ++i) {
				        		if (reso_bitrate_map_[reso_idx][i]>=bitrate) {
				        			gainedQuality = i;
				        			mSeekBar_quality.setProgress(i);
				        			Log.e("123", "get  i="+i);
				        			refreshImageQualityText(i);
				        			break;
				        		}
				        	}
				        }
				        else {
				        }
					 
					vmd_checkbox_.setChecked(vmd_res_.getEnabled());
					if(vmd_checkbox_.isChecked()){
						ll_alarm_push.setVisibility(View.VISIBLE);
						if(queryDeviceRes.getAndroidPushSubscribedFlag() == 0){
							cb_alarm_notice.setChecked(false);
						}else if(queryDeviceRes.getAndroidPushSubscribedFlag() == 1){
							cb_alarm_notice.setChecked(true);
						}
					}else{
						ll_alarm_push.setVisibility(View.GONE);
						cb_alarm_notice.setChecked(false);
					}
					
					System.out.println("re222222222222");
					if(getAuxiliaryRes.getResult().equals("OK")){
						if(getAuxiliaryRes.getAuxiliaryState().equals("Inactive")){
							mTvLightState.setText(getResources().getString(R.string.power_land_off));
							power_led_checkbox.setChecked(false);
						}else if(getAuxiliaryRes.getAuxiliaryState().equals("Active")){
							mTvLightState.setText(getResources().getString(R.string.power_land_on));
							power_led_checkbox.setChecked(true);
						}
					}
					System.out.println("re333333333333");
					if(rotationDegree == 0){
						mTvTurnOver.setText(getResources().getString(R.string.turn_over_0));
			    		video_checkbox.setChecked(false);
			    	}else if(rotationDegree == 180){
			    		mTvTurnOver.setText(getResources().getString(R.string.turn_over_180));
			    		video_checkbox.setChecked(true);
			    	}
					System.out.println("re4444444444444");
					mCameraVersion.setText(getResources().getString(R.string.camera_version_title)+"(V"+res.getCurDevVer()+")");
					if(!DeviceVersionUtils.needToUpdate(res.getCurDevVer(), res.getNewDevVer())){
			    		mCameraUpdateStatus.setText(getResources().getString(R.string.camera_old_version1)+res.getCurDevVer()+getResources().getString(R.string.camera_old_version2));
			    		mUpdateButton.setVisibility(View.INVISIBLE);
			    	}else{
			    		mCameraUpdateStatus.setText(getResources().getString(R.string.camera_new_version)+res.getNewDevVer()/*+getResources().getString(R.string.client_new_version)*/);
			    		mUpdateButton.setVisibility(View.VISIBLE);
			    	}
					System.out.println("re5555555555555");
				}catch (Exception e) {
					System.out.println("exception");
				}
				pd.dismiss();
			}
		}.execute();
		
		}
//		else{
//			mUpdateButton.setVisibility(View.VISIBLE);
//		}
    }
    
    Handler handler = new Handler(){
    	@Override
    	public void handleMessage(Message msg) {
    		super.handleMessage(msg);
    		if(msg.what == CRASH){
    			MessageUtiles.postAlertDialog(DeviceSetActivity.this, getResources().getString(R.string.get_settings_fail), getResources().getString(R.string.set_fail), R.drawable.expander_ic_minimized
            			, null, getResources().getString(R.string.ok), null, null);
//    			MessageUtiles.postNewUIDialog(DeviceSetActivity.this, getResources().getString(R.string.set_fail), "OK", 0);
    		}
    		if(msg.what == ALARMPUSHOFF){
    			cb_alarm_notice.setChecked(false);
    		}
    	}
    };
    
//    public static void cameraUpdate(){
//    	Log.e("", "cameraUpdate");
//    	UpgradeDevVerReq req = new UpgradeDevVerReq(mLoginResponse.getAccount(),mLoginResponse.getLoginSession(),dev.getDevID());
//    	UpgradeDevVerRes res = mSoapManager.getUpgradeDevVerRes(req);
//    	Log.e("cameraUpdate", res.getResult());
//    }

    @Override
    public void onProgressChanged(SeekBar seekBar, int progress,
            boolean fromUser) {
    	if (seekBar == mSeekBar_reso) {
    		refreshResolutionText(progress);
//    		saveEncodingParam(true);
    	}
    	else {
    		refreshImageQualityText(progress);
    	}
    }

    @Override
    public void onStartTrackingTouch(SeekBar seekBar) {
    }

    @Override
    public void onStopTrackingTouch(SeekBar seekBar) {
    	/*
        if (seekBar == mSeekBar1) {
            if (mLoginResponse.getResult().toString().equals("OK")) {
                mParamRes.setFrameSize(mFrameSizeValues[seekBar.getProgress()]);
                mSoapManager.setCodingParamFrameSize(mParamRes);
            }
        } else if (seekBar == mSeekBar2) {
            if (mLoginResponse.getResult().toString().equals("OK")) {
                mParamRes
                        .setImageQuality(String.valueOf(seekBar.getProgress()));
                mSoapManager.setCodingParamImageQuality(mParamRes);
            }
        }
        */
    }
    
    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        super.onKeyDown(keyCode, event);
        if (keyCode == KeyEvent.KEYCODE_BACK) {
        	if(isCrashed || !dev.isOnLine()){
        		finish();
        		return false;
        	}
        	int reso_idx = mSeekBar_reso.getProgress();
        	int qual_idx = mSeekBar_quality.getProgress();
        	//���û����ֱ���˳�
        	if(gainedReso == reso_idx && gainedQuality == qual_idx){
        		finish();
        		return false;
        	}
        		pd = new ProgressDialog(DeviceSetActivity.this);  
		        pd.setTitle(getResources().getString(R.string.save_set)+"...");   //���ñ���  
		        pd.setMessage(getResources().getString(R.string.please_wait)+"..."); //����body��Ϣ  
		        pd.setProgressStyle(ProgressDialog.STYLE_SPINNER); //���ý������ʽ�� ����� 
				pd.show();
				new AsyncTask<Void, Void, Void>() {
					protected Void doInBackground(Void... params) {
						try{
						saveEncodingParam(false);
						}catch (Exception e) {
						}
						return null;
					}

					@Override
					protected void onPostExecute(Void result) {
						try{
							pd.dismiss();
							finish();
						}catch (Exception e) {
						}
					}
				}.execute();
        }
        return false;
    }
    
    private void refreshResolutionText(int idx) {
    	String[] s = getResources().getStringArray(R.array.ResolutionText);
    	if (idx<s.length) {
    		reso_text_.setText(s[idx]);
    	}
    }
    
    private void refreshImageQualityText(int idx) {
    	String[] s = getResources().getStringArray(R.array.ImageQualityText);
    	if (idx<s.length) {
    		quality_text_.setText(s[idx]);
    	}
    }

    private boolean saveEncodingParam(boolean bSaveByChange) {
    	int reso_idx = mSeekBar_reso.getProgress();
    	if (reso_idx==2) {
    		return true;
    	}
    	if (bSaveByChange) {
			if (reso_idx==1) {
				reso_idx = 0;
			}else{
				reso_idx = 1;
			}
		}
    	
    	
    	int qual_idx = mSeekBar_quality.getProgress();
    	
    	if(gainedReso == reso_idx && gainedQuality == qual_idx){
    		return false;
    	}
    	
    	int bitrate = reso_bitrate_map_[reso_idx][qual_idx];
    	Log.v("dev","save bitrate: "+bitrate);
    	String[] s = getResources().getStringArray(R.array.FrameSize);
    	//add by cbj FIXME
    	String stremtype = null;
    	if(reso_idx == 0){
    		stremtype = "Sub";
    	}else{
    		stremtype = "Main";
    	}
    	
    	mCodingParamRes.setStreamType(stremtype);
    	mCodingParamRes.setFrameSize(s[reso_idx]);
    	mCodingParamRes.setBitRate(String.valueOf(bitrate));
    	mSoapManager.setCodingParam(mCodingParamRes);
    	return true;
    }
    
    private boolean saveVMDParam() {
    	boolean use_vmd = vmd_checkbox_.isChecked();
    	vmd_res_.setEnabled(use_vmd);
    	vmd_res_.setSensitivity(40);
    	if (use_vmd) {
    		VMDGrid grids = new VMDGrid(VMD_DEFAULT_GRIDS);
    		vmd_res_.setGrids(grids);
    	}else{
    		VMDGrid grids = new VMDGrid(VMD_ZERO_GRIDS);
    		vmd_res_.setGrids(grids);
			handler.sendEmptyMessage(ALARMPUSHOFF);
			saveAlarmPushParam(false);
    	}
    	mSoapManager.setVMDParam(vmd_res_);
    	return true;
    }
    
    private boolean saveVideoParam(){
    	System.out.println(mLoginResponse.getAccount()+","+mLoginResponse.getLoginSession()+","+dev.getDevID()+","+dev.getChannelNo());
    	boolean isTurnOver = video_checkbox.isChecked();
    	SetVideoParamReq req_set = null;
    	SetVideoParamRes res = null;
    	if(isTurnOver){
    		req_set = new SetVideoParamReq(mLoginResponse.getAccount(),mLoginResponse.getLoginSession(),dev.getDevID(), dev.getChannelNo(),180);
			res = mSoapManager.getSetVideoParamRes(req_set);
			System.out.println("turn over:"+res.getResult());
			return true;
    	}else{
    		req_set = new SetVideoParamReq(mLoginResponse.getAccount(),mLoginResponse.getLoginSession(),dev.getDevID(), dev.getChannelNo(),0);
			res = mSoapManager.getSetVideoParamRes(req_set);
			System.out.println("turn over:"+res.getResult());
			return false;
    	}
    	
    }
    
    private boolean savePowerLedParam(){
    	System.out.println(mLoginResponse.getAccount()+","+mLoginResponse.getLoginSession()+","+dev.getDevID()+","+dev.getChannelNo());
    	boolean powerLed = power_led_checkbox.isChecked();
    	SetAuxiliaryReq req_set = null;
    	SetAuxiliaryRes res = null;
    	if(powerLed){
    		req_set = new SetAuxiliaryReq(mLoginResponse.getAccount(),mLoginResponse.getLoginSession(),dev.getDevID(), "SignalLamp","Active");
    		res = mSoapManager.getSetAuxiliaryRes(req_set);
    		System.out.println("power led:"+res.getResult());
        	return true;
    	}else{
    		req_set = new SetAuxiliaryReq(mLoginResponse.getAccount(),mLoginResponse.getLoginSession(),dev.getDevID(), "SignalLamp","Inactive");
    		res = mSoapManager.getSetAuxiliaryRes(req_set);
    		System.out.println("power led:"+res.getResult());
        	return false;
    	}
    	
    }
    
    private boolean saveAlarmPushParam(boolean alarmPush){
    	SubscribeAndroidPushReq req = null;
    	SubscribeAndroidPushRes res = null;
    	if(alarmPush){
    		req = new SubscribeAndroidPushReq(mLoginResponse.getAccount(),mLoginResponse.getLoginSession(),0x01,dev.getDevID(), dev.getChannelNo());
    		res = mSoapManager.getSubscribeAndroidPushRes(req);
    	}else{
    		req = new SubscribeAndroidPushReq(mLoginResponse.getAccount(),mLoginResponse.getLoginSession(),0x00,dev.getDevID(),dev.getChannelNo());
    		res = mSoapManager.getSubscribeAndroidPushRes(req);
    	}
    	System.out.println("alarm push:"+req.toString());
    	System.out.println("alarm push:"+res.getResult());
    	return true;
    }
    
    private void removeDevice(){
    	NullifyDeviceReq req = new NullifyDeviceReq(mSoapManager.getLoginResponse().getAccount(),mSoapManager.getLoginResponse().getLoginSession(),dev.getDevID(),dev.getDevID());
    	NullifyDeviceRes res = mSoapManager.getNullifyDeviceRes(req);
    	System.out.println("removeDevice:"+res.getResult());
    }
    
	private void showPopupWindow() {

		View view = (LinearLayout) LayoutInflater.from(DeviceSetActivity.this)
				.inflate(R.layout.popmenu, null);

		LinearLayout bt_clear = (LinearLayout) view.findViewById(R.id.bt_remove);
		LinearLayout bt_exit = (LinearLayout) view.findViewById(R.id.bt_exit);
		
//		TextView tv_clear = (TextView) view.findViewById(R.id.tv_remove);
//		TextView tv_exit = (TextView) view.findViewById(R.id.tv_exit);
//		TextPaint tp = tv_clear.getPaint();
//        tp.setFakeBoldText(true);
//        tp = tv_exit.getPaint();
//        tp.setFakeBoldText(true);

		bt_clear.setOnClickListener(this);
		bt_exit.setOnClickListener(this);

		if (popupWindow == null) {

			popupWindow = new PopupWindow(DeviceSetActivity.this);

//			popupWindow.setFocusable(true); // 设置PopupWindow可获得焦点
			popupWindow.setTouchable(true); // 设置PopupWindow可触摸
			popupWindow.setOutsideTouchable(true); // 设置非PopupWindow区域可触摸

			popupWindow.setContentView(view);
			
			popupWindow.setWidth(LayoutParams.MATCH_PARENT);
			popupWindow.setHeight(LayoutParams.WRAP_CONTENT);
			
			popupWindow.setAnimationStyle(R.style.popuStyle);	//设置 popupWindow 动画样式
		}

		popupWindow.showAtLocation(mRemoveDevice, Gravity.BOTTOM, 0, 0);

		popupWindow.update();

	}
	
    @Override
    protected void onStop() {
    	super.onStop();
    }
    @Override
    protected void onDestroy() {
    	super.onDestroy();
    	mActivities.removeActivity("DeviceSetActivity");
    	unregisterReceiver(receiver);
    }
    
    @Override
    protected void onPause() {
    	super.onPause();
    }

	@Override
	public void onClick(View v) {
		switch (v.getId()) {
		case R.id.ll_deviceset_share:
			//被分享帐号
			if(dev.getSharingFlag() == 1){
				MessageUtiles.postToast(this, getResources().getString(R.string.device_set_sharer_no_property), 1000);
				return;
			}
			Intent intent = new Intent(DeviceSetActivity.this,DeviceShareToOther.class);
			intent.putExtra("Device", dev);
			startActivity(intent);
			break;
		case R.id.ll_deviceset_remove:
			if (UserPowerAction.getInstance().getPower() == UserPowerAction.RIGHT_VISITOR ) {
				Toast.makeText(this, getResources().getString(R.string.device_set_no_permission), Toast.LENGTH_LONG).show();
				break;
			}
			showPopupWindow();
			break;
			
		case R.id.bt_exit:
			popupWindow.dismiss();
			break;
		case R.id.bt_remove:
			popupWindow.dismiss();
			System.out.println("remove");
			pd = new ProgressDialog(DeviceSetActivity.this);  
	        pd.setTitle(getResources().getString(R.string.save_set)+"...");   //���ñ���  
	        pd.setMessage(getResources().getString(R.string.please_wait)+"..."); //����body��Ϣ  
	        pd.setProgressStyle(ProgressDialog.STYLE_SPINNER); //���ý������ʽ�� ����� 
			pd.show();
			new AsyncTask<Void, Void, Void>() {
				protected Void doInBackground(Void... params) {
					try{
						removeDevice();
					}catch (Exception e) {
					}
					return null;
				}

				@Override
				protected void onPostExecute(Void result) {
					try{
						pd.dismiss();
						finish();
						if(mActivities.getmActivityList().containsKey("CamTabActivity")){
							mActivities.getmActivityList().get("CamTabActivity").finish();
						}
						Intent intent = new Intent(DeviceSetActivity.this,CamTabActivity.class);
						startActivity(intent);
					}catch (Exception e) {
					}
				}
			}.execute();
			break;
		case R.id.ib_device_set_back:
			if(isCrashed || !dev.isOnLine()){
        		finish();
        		return ;
        	}
        	int reso_idx = mSeekBar_reso.getProgress();
        	int qual_idx = mSeekBar_quality.getProgress();
        	//���û����ֱ���˳�
        	if(gainedReso == reso_idx && gainedQuality == qual_idx){
        		finish();
        		return ;
        	}
        		pd = new ProgressDialog(DeviceSetActivity.this);  
		        pd.setTitle(getResources().getString(R.string.save_set)+"...");   //���ñ���  
		        pd.setMessage(getResources().getString(R.string.please_wait)+"..."); //����body��Ϣ  
		        pd.setProgressStyle(ProgressDialog.STYLE_SPINNER); //���ý������ʽ�� ����� 
				pd.show();
				new AsyncTask<Void, Void, Void>() {
					protected Void doInBackground(Void... params) {
						try{
						saveEncodingParam(false);
						}catch (Exception e) {
						}
						return null;
					}

					@Override
					protected void onPostExecute(Void result) {
						try{
							pd.dismiss();
							finish();
						}catch (Exception e) {
						}
					}
				}.execute();
			break;
		default:
			break;
		}
	}
}
