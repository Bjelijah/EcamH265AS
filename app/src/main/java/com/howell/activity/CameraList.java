package com.howell.activity;

import android.app.ListActivity;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager.NameNotFoundException;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.provider.Settings.Secure;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.view.ViewGroup.LayoutParams;
import android.widget.BaseAdapter;
import android.widget.FrameLayout;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.PopupWindow;
import android.widget.TextView;

import com.howell.action.PlatformAction;
import com.howell.action.UserPowerAction;
import com.howell.ecamera.cameraupdatedetective.DeviceVersionDetective;
import com.howell.ecamera.cameraupdatedetective.Observer;
import com.howell.ecamh265.R;
import com.howell.ehlib.MyListView;
import com.howell.ehlib.MyListView.OnRefreshListener;
import com.howell.entityclass.NodeDetails;
import com.howell.protocol.GetDevVerReq;
import com.howell.protocol.GetDevVerRes;
import com.howell.protocol.LoginResponse;
import com.howell.protocol.QueryClientVersionReq;
import com.howell.protocol.QueryClientVersionRes;
import com.howell.protocol.QueryDeviceReq;
import com.howell.protocol.SoapManager;
import com.howell.protocol.UpdateAndroidTokenReq;
import com.howell.protocol.UpdateAndroidTokenRes;
import com.howell.utils.ClientUpdateUtils;
import com.howell.utils.DeviceVersionUtils;
import com.howell.utils.MessageUtiles;
import com.howell.utils.PhoneConfig;
import com.howell.utils.ScaleImageUtils;
import com.wyy.twodimcode.CaptureActivity;

import org.kobjects.base64.Base64;

import java.io.File;
import java.util.ArrayList;
//import android.widget.Button;

public class CameraList extends ListActivity implements Observer{

    private SoapManager mSoapManager;
    private LoginResponse mResponse;
    private MyListView listView;
    private CameraListAdapter adapter;
    private ArrayList<NodeDetails> list;
    private static final int onFirstRefresh = 1;
    private static final int postUpdateMessage = 2;
    private static final int refreshCameraList = 3;
    private static final int refreshDeviceUpdate = 4;
    
    private ImageButton mAddDevice;
    private ImageButton mBack;
    private LinearLayout noCameraImg;
    private ImageView ivNoCameraImg;
//    private TextView mTvAdd;
//    private ImageView mIvAdd;
    
    private String url;
    
    private Activities mActivities;
    private Bitmap bm;
    
    private DeviceVersionDetective detective;
//	private TitlePopup titlePopup;
    private int country;//中国 0 ，别的国家 1
    
	private PopupWindow mPopupWindow;  
	private LinearLayout listen,scan;
    
    //private Button test;
    
//    static {
//        System.loadLibrary("ffmpeg");
//    }
//	
//	public native void ffmpegtest();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.camera_list);

        Log.i("123", "onCreate");
        File eCameraDir = new File("/sdcard/eCamera");
		if (!eCameraDir.exists()) {
			eCameraDir.mkdirs();
		}
        try{
        	if (savedInstanceState != null) {
        		mSoapManager = (SoapManager) savedInstanceState.getSerializable("soap");
        		SoapManager.context = this;
        		Log.e("", mSoapManager.toString());
    		}else{
    			mSoapManager = SoapManager.getInstance();
    			SoapManager.context = this;
    			Log.e("", mSoapManager.toString());
    		}
        	
        	detective = DeviceVersionDetective.getInstance();
        	detective.attachObserver("CameraList", CameraList.this);
        	
        	if(getResources().getConfiguration().locale.getCountry().equals("CN")){
        		country = 0;
        	}else{
        		country = 1;
        	}
        	
        	noCameraImg = (LinearLayout)findViewById(R.id.ll_no_cameralist_default);
        	ivNoCameraImg = (ImageView)findViewById(R.id.iv_no_cameralist_default);
        	if(country == 0)
        		ivNoCameraImg.setImageResource(R.mipmap.img_no_device);
        	else
        		ivNoCameraImg.setImageResource(R.mipmap.img_no_device_eng);
        	mActivities = Activities.getInstance();
        	mActivities.addActivity("CameraList",CameraList.this);
        	
	        mResponse = mSoapManager.getLoginResponse();
	        
	        adapter = new CameraListAdapter(this);
            setListAdapter(adapter);
	        
        }catch (Exception e) {
			// TODO: handle exception
		}
        
//        mTvAdd = (TextView)findViewById(R.id.tv_add);
//        mIvAdd = (ImageView)findViewById(R.id.iv_add);
        mAddDevice = (ImageButton)findViewById(R.id.ib_add);
        mAddDevice.setOnClickListener(adapter.listener);
        
//        test = (Button)findViewById(R.id.camera_list_test);
//        test.setOnClickListener(adapter.listener);
//        mAddDevice.setOnTouchListener(new OnTouchListener() {
//			
//			@Override
//			public boolean onTouch(View arg0, MotionEvent event) {
//				// TODO Auto-generated method stub
//				if(event.getAction()==MotionEvent.ACTION_DOWN){  
//					System.out.println("ACTION DOWN");
//					mTvAdd.setTextColor(getResources().getColor(R.color.gray));
//					mIvAdd.setImageResource(R.drawable.arrow_right_gray);
//	            }
//				if(event.getAction()==MotionEvent.ACTION_UP){  
//	            	System.out.println("ACTION UP");
//	            	mTvAdd.setTextColor(getResources().getColor(R.color.white)); 
//	            	mIvAdd.setImageResource(R.drawable.arrow_right);
//	            	Intent intent = new Intent(CameraList.this, SetDeviceWifi.class);
//		            startActivity(intent);
//	            }  
//				return true;
//			}
//		});
        mBack = (ImageButton)findViewById(R.id.ib_camera_list_back);
        mBack.setOnClickListener(adapter.listener);
        
//        titlePopup = new TitlePopup(this, width,width * 3 / 4,R.layout.title_popup
//        		,this.getResources().getColor(android.R.color.black),Gravity.CENTER_VERTICAL);
//        titlePopup.setItemOnClickListener(this);
//        initTitlePopupData();
        
        //-----如果是演示帐号则去掉添加按钮，加上返回按钮
        if(mResponse.getAccount().equals("100868")){
        	UserPowerAction.getInstance().setPower(UserPowerAction.RIGHT_VISITOR);
        	mAddDevice.setVisibility(View.GONE);
        	mBack.setVisibility(View.VISIBLE);
        }else{
        	UserPowerAction.getInstance().setPower(UserPowerAction.RIGHT_USER);
        }
        
        listView = (MyListView)findViewById(android.R.id.list);
        listView.setonRefreshListener(new OnRefreshListener() {
			public void onRefresh() {
				listView.setEnabled(false);
				new AsyncTask<Void, Void, Void>() {
					protected Void doInBackground(Void... params) {
						try {
							mSoapManager.getQueryDeviceRes(new QueryDeviceReq(mResponse.getAccount(), mResponse.getLoginSession()));
							list = mSoapManager.getNodeDetails();
//							Log.i("123", "list size="+list.size());
					        sort(list);
//				        	for(NodeDetails d:list){
//				                GetDevVerReq getDevVerReq = new GetDevVerReq(mResponse.getAccount(),mResponse.getLoginSession(),d.getDevID());
//				                GetDevVerRes res = mSoapManager.getGetDevVerRes(getDevVerReq);
//				                Log.e("GetDevVerRes", res.toString());
//				                if(/*d.isOnLine() && */DeviceVersionUtils.needToUpdate(res.getCurDevVer(), res.getNewDevVer())){
//				                	System.out.println(res.getCurDevVer()+","+res.getNewDevVer());
//				                	d.setHasUpdate(true);
//				                }
//				            }
						} catch (Exception e) {
							e.printStackTrace();
						}
						
						for(NodeDetails d:list){
		                	System.out.println("aaaaaa");
		                	GetDevVerReq getDevVerReq = new GetDevVerReq(mResponse.getAccount(),mResponse.getLoginSession(),d.getDevID());
		                	GetDevVerRes getDevVerRes = mSoapManager.getGetDevVerRes(getDevVerReq);
		                	Log.e("GetDevVerRes", getDevVerRes.toString());
		                	if(d.isOnLine() && DeviceVersionUtils.needToUpdate(getDevVerRes.getCurDevVer(), getDevVerRes.getNewDevVer())){
		                	//if(!getDevVerRes.getCurDevVer().equals(getDevVerRes.getNewDevVer())){	
		                		System.out.println(getDevVerRes.getCurDevVer()+","+getDevVerRes.getNewDevVer());
		                		d.setHasUpdate(true);
		                	}
		                	System.out.println(d.getDevID()+":"+d.isHasUpdate());
		                	System.out.println("cur ver:"+getDevVerRes.getCurDevVer()+" new ver:"+getDevVerRes.getNewDevVer());
		                }
//					    myHandler.sendEmptyMessage(refreshDeviceUpdate);
						return null;
					}

					@Override
					protected void onPostExecute(Void result) {
						try{
//							adapter.notifyDataSetChanged();
//							listView.onRefreshComplete();
							listView.setEnabled(true);
							myHandler.sendEmptyMessage(refreshCameraList);
						}catch (Exception e) {
							// TODO: handle exception
						}
					}

				}.execute();
			}
			
			@Override
			public void onFirstRefresh() {
				// TODO Auto-generated method stub
				Log.e("CameraList", "onFirstRefresh");
				mSoapManager.getQueryDeviceRes(new QueryDeviceReq(mResponse.getAccount(), mResponse.getLoginSession()));
				list = mSoapManager.getNodeDetails();
		        sort(list);
		        myHandler.sendEmptyMessage(refreshCameraList);
//		        CamTabActivity.cameraVerThread = true;
				
			}
		});
		
        new Thread(){
        	@Override
        	public void run() {
        		// TODO Auto-generated method stub
        		super.run();
		        QueryClientVersionReq queryClientVersionReq = new QueryClientVersionReq("Android");
		        QueryClientVersionRes queryClientVersionRes = mSoapManager.getQueryClientVersionRes(queryClientVersionReq);
				System.out.println(queryClientVersionRes.toString());
				url = new String(Base64.decode(queryClientVersionRes.getDownloadAddress()));
				System.out.println("url:"+url);
				String version = getVersion();
				if(!version.equals(queryClientVersionRes.getVersion())){
					myHandler.sendEmptyMessage(postUpdateMessage);
				}
				
		        String UUID = Secure.getString(getContentResolver(), Secure.ANDROID_ID);
		        UpdateAndroidTokenReq updateAndroidTokenReq = new UpdateAndroidTokenReq(mResponse.getAccount(), mResponse.getLoginSession()
			    		, UUID,UUID, true);
			    System.out.println(updateAndroidTokenReq.toString());
			    UpdateAndroidTokenRes res = mSoapManager.GetUpdateAndroidTokenRes(updateAndroidTokenReq);
			    Log.e("savePushParam", res.getResult());
			    
			    
			    int _num = 10;
			    while(list == null){//FIXME   should use RxAndroid or Rxjava observer 
			    	try {
						sleep(1000);
					} catch (InterruptedException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					}
			    	_num--;
			    	if (_num < 0) {
						return;
					}
			    	
			    	
			    	
			    	Log.i("123", "CameraList test");
			    }
			    for(NodeDetails d:list){
                	System.out.println("aaaaaa");
                	GetDevVerReq getDevVerReq = new GetDevVerReq(mResponse.getAccount(),mResponse.getLoginSession(),d.getDevID());
                	GetDevVerRes getDevVerRes = mSoapManager.getGetDevVerRes(getDevVerReq);
                	Log.e("GetDevVerRes", getDevVerRes.toString());
                	if(d.isOnLine() && DeviceVersionUtils.needToUpdate(getDevVerRes.getCurDevVer(), getDevVerRes.getNewDevVer())){
                	//if(!getDevVerRes.getCurDevVer().equals(getDevVerRes.getNewDevVer())){	
                		System.out.println(getDevVerRes.getCurDevVer()+","+getDevVerRes.getNewDevVer());
                		d.setHasUpdate(true);
                	}
                	System.out.println(d.getDevID()+":"+d.isHasUpdate());
                	System.out.println("cur ver:"+getDevVerRes.getCurDevVer()+" new ver:"+getDevVerRes.getNewDevVer());
                }
			    myHandler.sendEmptyMessage(refreshDeviceUpdate);
        	}
        }.start();
    }
    
    /* 
     * 获取PopupWindow实例 
     */  
    private void getPopupWindowInstance() {  
        if (null != mPopupWindow) {  
            mPopupWindow.dismiss();  
            return;  
        } else {  
            initPopuptWindow();  
        }  
    } 
    
    /* 
     * 创建PopupWindow 
     */  
    private void initPopuptWindow() {  
        LayoutInflater layoutInflater = LayoutInflater.from(CameraList.this);  
        View popupWindow = layoutInflater.inflate(R.layout.title_popup, null);  
        listen = (LinearLayout)popupWindow.findViewById(R.id.camera_list_pop_layout_listen);
        scan = (LinearLayout)popupWindow.findViewById(R.id.camera_list_pop_layout_scan);
        listen.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View arg0) {
				// TODO Auto-generated method stub
				Intent intent = new Intent(CameraList.this, SetDeviceWifi.class);
	            startActivity(intent);
			}
		});
        scan.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View arg0) {
				// TODO Auto-generated method stub
				Intent it = new Intent(CameraList.this, CaptureActivity.class);
				startActivity(it);
			}
		});
        
        int width = PhoneConfig.getPhoneWidth(this)/4;
        mPopupWindow = new PopupWindow(popupWindow, LayoutParams.WRAP_CONTENT,LayoutParams.WRAP_CONTENT);  
        
        ColorDrawable dw = new ColorDrawable(0000000000);
		// ��back�������ط�ʹ����ʧ,������������ܴ���OnDismisslistener ����������ؼ��仯�Ȳ���
        mPopupWindow.setBackgroundDrawable(dw);
        mPopupWindow.setFocusable(true);  
        mPopupWindow.setOutsideTouchable(true);  
  
    }
    
    
    private String getVersion(){
        PackageInfo pkg;
        String versionName = "";
        try {
            pkg = getPackageManager().getPackageInfo(getApplication().getPackageName(), 0);
            versionName = pkg.versionName; 
            System.out.println("versionName:" + versionName);
        } catch (NameNotFoundException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        } 
        return versionName;
    }
    
    private Handler myHandler = new Handler(){
    	@Override
    	public void handleMessage(Message msg) {
    		// TODO Auto-generated method stub
    		super.handleMessage(msg);
    		if(msg.what == onFirstRefresh){
    			listView.onRefreshComplete();
    		}
    		if(msg.what == postUpdateMessage){
    			try{
    				ClientUpdateUtils.showUpdataDialog(CameraList.this,url);
    			}catch(Exception e){
    				
    			}
    		}
    		if(msg.what == refreshCameraList){
    			listView.onRefreshComplete();
    			adapter.notifyDataSetChanged();
    			System.out.println("list size:"+list.size());
				if(list.size() == 0){
					//listView.setVisibility(View.GONE);
					noCameraImg.setVisibility(View.VISIBLE);
				}else{
					//listView.setVisibility(View.VISIBLE);
					noCameraImg.setVisibility(View.GONE);
				}
    		}
    		if(msg.what == refreshDeviceUpdate){
    			System.out.println("refreshDeviceUpdate");
    			adapter.notifyDataSetChanged();
    			System.out.println("222");
    		}
    	}
    };
    
    private void sort(ArrayList<NodeDetails> list){
    	if(list != null){
	    	int length = list.size();
	    	for(int i = 0 ; i < length ; i++){
	    		System.out.println(i+":"+list.get(i).toString());
	    		if(list.get(i).isOnLine()){
	    			list.add(0, list.get(i));
	    			list.remove(i+1);
	    		}else{
	    			//System.out.println(i);
	    		}
	    	}
    	}
    }

    @Override
    public void onSaveInstanceState(Bundle savedInstanceState){
        super.onSaveInstanceState(savedInstanceState);
        Log.e("CameraList", "onSaveInstanceState");
        savedInstanceState.putSerializable("soap", mSoapManager);
    }
    
    @Override
    protected void onPause() {
    	// TODO Auto-generated method stub
    	super.onPause();
    	Log.e("CameraList", "onPause()");
    }
    
    @Override
    protected void onRestart() {
    	// TODO Auto-generated method stub
    	super.onRestart();
    	Log.e("CameraList", "onRestart()");
    	adapter.notifyDataSetChanged();
    }
    
    @Override
    protected void onDestroy() {
    	// TODO Auto-generated method stub
    	super.onDestroy();
    	Log.e("CameraList", "onDestroy()");
    	mActivities.removeActivity("CameraList");
    	if((bm!=null)&&(!bm.isRecycled())){
	    	bm.recycle();
	    	bm = null;
    	}
    }
    
    @Override
    protected void onStop() {
    	// TODO Auto-generated method stub
    	super.onStop();
    	Log.e("CameraList", "onStop()");
    }
    
    @Override
    protected void onResume() {
        // TODO Auto-generated method stub
        super.onResume();
        Log.e("CameraList", "onResume()");
    }

    public class CameraListAdapter extends BaseAdapter {

        private Context mContext;
        private int imageWidth;
        private int imageHeight;

        public CameraListAdapter(Context context) {
            mContext = context;
            imageWidth = PhoneConfig.getPhoneWidth(getApplicationContext())/2;
            imageHeight = imageWidth * 10 / 16;
        }
        
        @Override
        public int getCount() {
            // TODO Auto-generated method stub
            return list == null ? 0 : list.size();
        }

        @Override
        public Object getItem(int position) {
            // TODO Auto-generated method stub
            return list == null ? null : list.get(position);
        }

        @Override
        public long getItemId(int position) {
            // TODO Auto-generated method stub
            return position;
        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent) {
            // TODO Auto-generated method stub
        	System.out.println("getView:"+position);
        	ViewHolder holder = null;
            if (convertView == null) {
            	LayoutInflater layoutInflater = LayoutInflater.from(mContext);
				convertView = layoutInflater.inflate(R.layout.item, null);
				holder = new ViewHolder();
				
				holder.iv = (ImageView)convertView.findViewById(R.id.iv_picture);
				holder.playback = (ImageButton)convertView.findViewById(R.id.iv_playback);
				holder.iv_wifi = (ImageView)convertView.findViewById(R.id.iv_wifi_idensity);
				holder.set = (ImageButton)convertView.findViewById(R.id.iv_set);
				holder.about = (ImageButton)convertView.findViewById(R.id.iv_about);
				holder.tv = (TextView)convertView.findViewById(R.id.tv_name);
				holder.iv_offline = (ImageView)convertView.findViewById(R.id.iv_offline);
				holder.iv_badge = (ImageView)convertView.findViewById(R.id.iv_badge);
				holder.mPlay = (LinearLayout)convertView.findViewById(R.id.to_play);
				
				holder.tv.setTextColor(Color.BLACK);
                convertView.setTag(holder);
                
                holder.iv.setLayoutParams(new FrameLayout.LayoutParams(imageWidth, imageHeight));
		        
                holder.playback.setOnClickListener(listener);
                holder.set.setOnClickListener(listener);
                holder.about.setOnClickListener(listener);
                holder.mPlay.setOnClickListener(listener);
                
            }else{
            	holder = (ViewHolder)convertView.getTag();
            }
            
            holder.mPlay.setTag(position);
            holder.playback.setTag(position);
            holder.set.setTag(position);
            holder.about.setTag(position);
            
            NodeDetails camera = list.get(position);
            
            if(country == 0){
        		holder.playback.setImageResource(R.drawable.card_playback_selector);
        		holder.about.setImageResource(R.drawable.card_property_selector);
        		holder.set.setImageResource(R.drawable.card_setting_selector);
            }else{
        		holder.playback.setImageResource(R.drawable.card_playback_eng_selector);
        		holder.about.setImageResource(R.drawable.card_property_eng_selector);
        		holder.set.setImageResource(R.drawable.card_setting_eng_selector);
            }
            if(!camera.iseStoreFlag()){
            	if(country == 0)
            		holder.playback.setImageResource(R.mipmap.card_tab_playback_no_sdcard);
            	else
            		holder.playback.setImageResource(R.mipmap.card_tab_playback_no_sdcard_eng);
            }else{
            	if(country == 0)
            		holder.playback.setImageResource(R.mipmap.card_tab_playback);
            	else
            		holder.playback.setImageResource(R.mipmap.card_tab_playback_eng);
            }
            
            if(camera.getSharingFlag() == 1){
            	holder.tv.setText(camera.getName()+getResources().getString(R.string.camera_list_sharer_tip));
            }else{
            	holder.tv.setText(camera.getName());
            }
            
            if (camera.isOnLine()) {
            	if(country == 0)
            		holder.iv_offline.setImageResource(R.mipmap.card_online_image_blue);
            	else
            		holder.iv_offline.setImageResource(R.mipmap.card_online_image_blue_english);
//            	if(camera.getIntensity() >= 0 && camera.getIntensity() <= 33){
//            		holder.tv_wifi.setText("wifi强度:弱");
//            	}else if(camera.getIntensity() > 33 && camera.getIntensity() <= 66){
//            		holder.tv_wifi.setText("wifi强度:中");
//            	}else {
//            		holder.tv_wifi.setText("wifi强度:强");
//            	}
				if(camera.getIntensity() == 0){
                	holder.iv_wifi.setImageResource(R.mipmap.wifi_0);
                }else if((camera.getIntensity() > 0 && camera.getIntensity() <= 25)){
                	holder.iv_wifi.setImageResource(R.mipmap.wifi_1);
                }else if(camera.getIntensity() > 25 && camera.getIntensity() <= 50){
                	holder.iv_wifi.setImageResource(R.mipmap.wifi_2);
                }else if(camera.getIntensity() > 50 && camera.getIntensity() <= 75){
                	holder.iv_wifi.setImageResource(R.mipmap.wifi_3);
                }else{
					holder.iv_wifi.setImageResource(R.mipmap.wifi_4);
				}
			}else {
	        	if(country == 0)
	        		holder.iv_offline.setImageResource(R.mipmap.card_offline_image_gray);
	        	else 
	        		holder.iv_offline.setImageResource(R.mipmap.card_offline_image_gray_english);
	        	holder.iv_wifi.setImageResource(R.mipmap.wifi_0);
//	        	holder.tv_wifi.setText("");
	        }
            
            System.out.println(camera.getDevID()+": "+camera.isHasUpdate());
            if(camera.isHasUpdate()){
            	holder.iv_badge.setVisibility(View.VISIBLE);
            }else{
            	holder.iv_badge.setVisibility(View.GONE);
            }
            
            
            
            BitmapFactory.Options options = new BitmapFactory.Options();
	        options.inSampleSize = 2;
	        bm = ScaleImageUtils.decodeFile(imageWidth, imageHeight, new File(list.get(position).getPicturePath()));
	        if(bm == null){
	        	holder.iv.setImageResource(R.mipmap.card_camera_default_image);
	        }else{
	        	holder.iv.setImageBitmap(bm);
	        }
			return convertView;
        }

        private OnClickListener listener = new OnClickListener() {
			
			@Override
			public void onClick(View arg0) {
				// TODO Auto-generated method stub
				if(arg0.getId() == R.id.iv_playback){
					if(!list.get(Integer.valueOf(arg0.getTag().toString())).isOnLine()){
			    		MessageUtiles.postToast(getApplication(), getResources().getString(R.string.not_online_message),2000);
			    		return;
			    	}
					if(!list.get(Integer.valueOf(arg0.getTag().toString())).iseStoreFlag()){
			    		MessageUtiles.postToast(getApplication(), getResources().getString(R.string.no_estore),2000);
			    		return;
			    	}
					System.out.println("tag:"+arg0.getTag().toString());
					System.out.println(((NodeDetails) getItem(Integer.valueOf(arg0.getTag().toString()))).getName());
					
					int index = Integer.valueOf(arg0.getTag().toString());
					String deviceID = list.get(index).getDevID();
					NodeDetails node = (NodeDetails)getItem(index);
					PlatformAction.getInstance().setDeviceID(deviceID);
			        PlatformAction.getInstance().setDevice_id(index);
			        PlatformAction.getInstance().setCurSelNode(node);
					Intent intent = new Intent(CameraList.this, VideoList.class);
		            intent.putExtra("Device", ((NodeDetails) getItem(Integer.valueOf(arg0.getTag().toString()))));
		            startActivity(intent);
				}else if(arg0.getId() == R.id.iv_set){
					if(!list.get(Integer.valueOf(arg0.getTag().toString())).isOnLine()){
			    		MessageUtiles.postToast(getApplication(), getResources().getString(R.string.not_online_message),2000);
			    		return;
			    	}
					int index = Integer.valueOf(arg0.getTag().toString());
					String deviceID = list.get(index).getDevID();
					PlatformAction.getInstance().setDeviceID(deviceID);
			        PlatformAction.getInstance().setDevice_id(index);
					
					Intent intent = new Intent(CameraList.this,DeviceSetActivity.class);
					intent.putExtra("Device", (NodeDetails) getItem(Integer.valueOf(arg0.getTag().toString())));
					startActivity(intent);
					
//					Toast.makeText(CameraList.this, "无法设置", Toast.LENGTH_SHORT).show();
					
				}else if(arg0.getId() == R.id.iv_about){
					Intent intent = new Intent(CameraList.this,CameraProperty.class);
					intent.putExtra("Device", (NodeDetails) getItem(Integer.valueOf(arg0.getTag().toString())));
					startActivity(intent);
				}
//				else if(arg0.getId() == R.id.iv_picture){
//					System.out.println(getItem(Integer.valueOf(arg0.getTag().toString())).toString());
//					if (!((NodeDetails)getItem(Integer.valueOf(arg0.getTag().toString()))).isOnLine()) {
//			        	MessageUtiles.postToast(getApplicationContext(), getResources().getString(R.string.not_online_message),1000);
//			        } else {
//			            Intent intent = new Intent(CameraList.this, PlayerActivity.class);
//			            intent.putExtra("arg", ((NodeDetails) getItem(Integer.valueOf(arg0.getTag().toString()))));
//			            startActivity(intent);
//			        }
//				}
				else if(arg0.getId() == R.id.ib_add){
					//Intent intent = new Intent(CameraList.this, SetDeviceWifi.class);
		            //startActivity(intent);
//					titlePopup.show(arg0);
					getPopupWindowInstance();  
			        mPopupWindow.showAsDropDown(arg0);  
				}else if(arg0.getId() == R.id.to_play){
					System.out.println(getItem(Integer.valueOf(arg0.getTag().toString())).toString());
					if (!((NodeDetails)getItem(Integer.valueOf(arg0.getTag().toString()))).isOnLine()) {
			        	MessageUtiles.postToast(getApplicationContext(), getResources().getString(R.string.not_online_message),1000);
			        } else {
			            Intent intent = new Intent(CameraList.this, PlayerActivity.class);
			            int index = Integer.valueOf(arg0.getTag().toString());
			            NodeDetails node = (NodeDetails)getItem(index);
						String deviceID = list.get(index).getDevID();
						PlatformAction.getInstance().setDeviceID(deviceID);
			            PlatformAction.getInstance().setDevice_id(index);
			            PlatformAction.getInstance().setCurSelNode(node);
			            intent.putExtra("arg", ((NodeDetails) getItem(Integer.valueOf(arg0.getTag().toString()))));
			            intent.putExtra("bPlayBack", false);
			            startActivity(intent);
			        }
				}else if(arg0.getId() == R.id.ib_camera_list_back){
					finish();
				}
//				else if(arg0.getId() == R.id.camera_list_test){
//					ffmpegtest();
//				}
			}
		};

    }
    
	public static class ViewHolder {
		public ImageView iv,iv_play_icon,iv_offline/*,iv_wifi*/,iv_badge,iv_wifi;
	    public ImageButton about,set,playback;
	    public TextView tv;
	    public LinearLayout mPlay;
	}

	@Override
	public void update() {
		// TODO Auto-generated method stub
		System.out.println("test update!!");
		for(NodeDetails d: list){
			System.out.println("update "+d.getDevID()+":"+d.isHasUpdate());
		}
		myHandler.sendEmptyMessage(refreshDeviceUpdate);
	}

//	@Override
//	public void onItemClick(ActionItem item, int position) {
//		// TODO Auto-generated method stub
//		if(position == 0){
//			Intent intent = new Intent(CameraList.this, SetDeviceWifi.class);
//            startActivity(intent);
//		}else if(position == 1){
//			Intent it = new Intent(CameraList.this, CaptureActivity.class);
//			startActivity(it);
//		}
//	}

}
