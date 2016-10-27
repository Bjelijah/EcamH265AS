package com.howell.activity;

import android.app.Activity;
import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.AsyncTask;
import android.os.Bundle;
import android.text.TextPaint;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.view.ViewGroup.LayoutParams;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.BaseAdapter;
import android.widget.FrameLayout;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.PopupWindow;
import android.widget.TextView;

import com.howell.broadcastreceiver.HomeKeyEventBroadCastReceiver;
import com.howell.ecamh265.R;
import com.howell.entityclass.DeviceSharer;
import com.howell.entityclass.NodeDetails;
import com.howell.protocol.NullifyDeviceSharerReq;
import com.howell.protocol.NullifyDeviceSharerRes;
import com.howell.protocol.QueryDeviceSharerReq;
import com.howell.protocol.QueryDeviceSharerRes;
import com.howell.protocol.SoapManager;
import com.howell.utils.MessageUtiles;

import java.util.ArrayList;

public class DeviceShareToOther extends Activity implements OnClickListener,OnItemClickListener{
    private NodeDetails dev;
    private ListView deviceSharerListView;
    private CameraListAdapter adapter;
    private ArrayList<DeviceSharer> list;
    private SoapManager mSoapManager;
	private Activities mActivities;
	private HomeKeyEventBroadCastReceiver receiver;
	
	private PopupWindow popupWindow;
	private int deviceSharerNum;
    
    private ImageButton mBack;
    private FrameLayout mAddAccount;
    
	private Dialog waitDialog;
    
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		setContentView(R.layout.device_share_to_other);

		Intent intent = getIntent();
        dev = (NodeDetails) intent.getSerializableExtra("Device");
        mSoapManager = SoapManager.getInstance();
    	SoapManager.context = this;
		mActivities = Activities.getInstance();
        mActivities.addActivity("DeviceShareToOther",DeviceShareToOther.this);
        receiver = new HomeKeyEventBroadCastReceiver();
		registerReceiver(receiver, new IntentFilter(Intent.ACTION_CLOSE_SYSTEM_DIALOGS));
        
        deviceSharerListView = (ListView)findViewById(R.id.lv_device_share_to_other);
        mBack = (ImageButton)findViewById(R.id.ib_device_share_to_other_back);
        mAddAccount = (FrameLayout)findViewById(R.id.fl_device_share_to_other_add_account);
        mBack.setOnClickListener(this);
        mAddAccount.setOnClickListener(this);
        
        waitDialog = MessageUtiles.postWaitingDialog(DeviceShareToOther.this);
		waitDialog.show();
		new AsyncTask<Void, Integer, Void>() {
			QueryDeviceSharerRes res = null;
			@Override
			protected Void doInBackground(Void... params) {
				// TODO Auto-generated method stub
				QueryDeviceSharerReq req = new QueryDeviceSharerReq(mSoapManager.getLoginResponse().getAccount(),mSoapManager.getLoginResponse().getLoginSession(),dev.getDevID(),0);
		        res = mSoapManager.getQueryDeviceSharerRes(req);
				return null;
			}
			
			@Override
			protected void onPostExecute(Void result) {
				// TODO Auto-generated method stub
				super.onPostExecute(result);
				waitDialog.dismiss();
				System.out.println("QueryDeviceSharer:"+res.getResult());
				if(res.getResult().equals("OK")){
			        list = res.getDeviceSharerList();
			        adapter = new CameraListAdapter(DeviceShareToOther.this);
			        deviceSharerListView.setAdapter(adapter);
			        deviceSharerListView.setOnItemClickListener(DeviceShareToOther.this);
				}
			}
			
		}.execute();
        
//        QueryDeviceSharerReq req = new QueryDeviceSharerReq(mSoapManager.getLoginResponse().getAccount(),mSoapManager.getLoginResponse().getLoginSession(),dev.getDevID(),0);
//        QueryDeviceSharerRes res = mSoapManager.getQueryDeviceSharerRes(req);
        
	}
	
	@Override
	protected void onRestart() {
		// TODO Auto-generated method stub
		super.onRestart();
		
		
	}
	
	@Override
	protected void onDestroy() {
		// TODO Auto-generated method stub
		super.onDestroy();
    	mActivities.removeActivity("DeviceShareToOther");
    	unregisterReceiver(receiver);
	}
	
    public class CameraListAdapter extends BaseAdapter {

        private Context mContext;

        public CameraListAdapter(Context context) {
            mContext = context;
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
				convertView = layoutInflater.inflate(R.layout.device_sharer_list_item, null);
				holder = new ViewHolder();
				
				holder.tv = (TextView)convertView.findViewById(R.id.tv_device_sharer_account);
				
                convertView.setTag(holder);
            }else{
            	holder = (ViewHolder)convertView.getTag();
            }
            
            DeviceSharer deviceSharer = list.get(position);
            
            holder.tv.setText(deviceSharer.getSharerAccount());
            
            
			return convertView;
        }
    }
    
	public static class ViewHolder {
	    public TextView tv;
	}

	@Override
	public void onClick(View v) {
		// TODO Auto-generated method stub
		switch (v.getId()) {
		case R.id.ib_device_share_to_other_back:
			finish();
			break;
		case R.id.fl_device_share_to_other_add_account:
			Intent intent = new Intent(DeviceShareToOther.this,AddSharerAccount.class);
			intent.putExtra("Device", dev);
			startActivity(intent);
			break;
		case R.id.bt_exit:
			popupWindow.dismiss();
			break;
		case R.id.bt_remove:
			popupWindow.dismiss();
			waitDialog = MessageUtiles.postWaitingDialog(DeviceShareToOther.this);
			waitDialog.show();
			new AsyncTask<Void, Integer, Void>() {
				NullifyDeviceSharerRes res = null;
				@Override
				protected Void doInBackground(Void... params) {
					// TODO Auto-generated method stub
					NullifyDeviceSharerReq req = new NullifyDeviceSharerReq(mSoapManager.getLoginResponse().getAccount(),mSoapManager.getLoginResponse().getLoginSession(),dev.getDevID(),0,list.get(deviceSharerNum).getSharerAccount());
					res = mSoapManager.getNullifyDeviceSharerRes(req);
					System.out.println("NullifyDeviceSharer result:" + res.getResult());
					return null;
				}
				
				@Override
				protected void onPostExecute(Void result) {
					// TODO Auto-generated method stub
					super.onPostExecute(result);
					waitDialog.dismiss();
					if(res.getResult().equals("OK")){
						list.remove(deviceSharerNum);
						adapter.notifyDataSetChanged();
					}
				}
				
			}.execute();
			break;
		default:
			break;
		}
	}

	@Override
	public void onItemClick(AdapterView<?> arg0, View arg1, int arg2, long arg3) {
		// TODO Auto-generated method stub
		deviceSharerNum = (int)arg3;
		System.out.println(deviceSharerNum);
		showPopupWindow();
	}
	
	private void showPopupWindow() {

		View view = (LinearLayout) LayoutInflater.from(DeviceShareToOther.this)
				.inflate(R.layout.popmenu, null);

		LinearLayout bt_clear = (LinearLayout) view.findViewById(R.id.bt_remove);
		LinearLayout bt_exit = (LinearLayout) view.findViewById(R.id.bt_exit);
		
		TextView tv_clear = (TextView) view.findViewById(R.id.tv_remove);
		TextView tv_exit = (TextView) view.findViewById(R.id.tv_exit);
		TextPaint tp = tv_clear.getPaint();
        tp.setFakeBoldText(true);
        tp = tv_exit.getPaint();
        tp.setFakeBoldText(true);

		bt_clear.setOnClickListener(this);
		bt_exit.setOnClickListener(this);

		if (popupWindow == null) {

			popupWindow = new PopupWindow(DeviceShareToOther.this);
			//popupWindow.setBackgroundDrawable(new BitmapDrawable());

//			popupWindow.setFocusable(true); // 设置PopupWindow可获得焦点
			popupWindow.setTouchable(true); // 设置PopupWindow可触摸
			popupWindow.setOutsideTouchable(true); // 设置非PopupWindow区域可触摸

			popupWindow.setContentView(view);
			
			popupWindow.setWidth(LayoutParams.MATCH_PARENT);
			popupWindow.setHeight(LayoutParams.WRAP_CONTENT);
			
			popupWindow.setAnimationStyle(R.style.popuStyle);	//设置 popupWindow 动画样式
		}

		popupWindow.showAtLocation(deviceSharerListView, Gravity.BOTTOM, 0, 0);

		popupWindow.update();

	}

}
