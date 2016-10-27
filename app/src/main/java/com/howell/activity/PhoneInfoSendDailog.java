package com.howell.activity;


import android.app.DialogFragment;
import android.content.Context;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnTouchListener;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.howell.ecamh265.R;
import com.howell.protocol.SoapManager;
import com.howell.protocol.UpdataDeviceAuthenticatedReq;
import com.howell.protocol.UpdataDeviceAuthenticatedRes;
import com.howell.utils.PhoneConfig;

public class PhoneInfoSendDailog extends DialogFragment implements OnTouchListener {
	Context mContext;
	TextView mCancel,mSend,mInfo1,mState;
	LinearLayout mll;
	RelativeLayout mrl;
	ProgressBar mpb;
	boolean mIsShow;
	SoapManager mSoapManager = SoapManager.getInstance();
	public boolean isShow(){
		return mIsShow;
	}
	
	@Override
	public void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		setRetainInstance(true);
		setStyle(DialogFragment.STYLE_NORMAL, android.R.style.Theme_Material_Light_Dialog);
		super.onCreate(savedInstanceState);
	}

	


	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
			return  null;
		}
		mContext = getContext();
		View v = inflater.inflate(R.layout.phone_info_send_dialog, container, false);

		getDialog().setTitle(getString(R.string.phone_info_send_title));
		mCancel = (TextView) v.findViewById(R.id.tv_phoneinfosend_cancel);
		mSend = (TextView) v.findViewById(R.id.tv_phoneinfosend_send);
		mInfo1 = (TextView) v.findViewById(R.id.phone_info_send_info1);
		mInfo1.setText("设备ID："+PhoneConfig.getPhoneDeveceID(mContext));
		mState = (TextView) v.findViewById(R.id.phone_info_tv_state);

		mll = (LinearLayout) v.findViewById(R.id.phone_info_ll_info);
		mll.setVisibility(View.VISIBLE);
		mrl = (RelativeLayout) v.findViewById(R.id.phone_info_ll_state);
		mrl.setVisibility(View.GONE);

		mpb = (ProgressBar) v.findViewById(R.id.phone_info_pb_wait);
		mCancel.setOnTouchListener(this);
		mSend.setOnTouchListener(this);
		Log.e("123", "on create view");
		mIsShow = true;
		getDialog().setCanceledOnTouchOutside(false);
		return v;

	}


	@Override
	public void onDestroyView() {
		Log.i("123", "now fragment is destroy view");
		mIsShow = false;
		super.onDestroyView();
	}





	@Override
	public boolean onTouch(View arg0, MotionEvent arg1) {
		switch (arg0.getId()) {
		case R.id.tv_phoneinfosend_cancel:
			if (arg1.getAction()==MotionEvent.ACTION_DOWN) {
				mCancel.postDelayed( new Runnable() {
					public void run() {
						dismiss();
					}
				}, 300);
			}
			break;
		case R.id.tv_phoneinfosend_send:

			mSend.postDelayed(new Runnable() {
				public void run() {
					register2Server();
				}
			}, 300);

			break;
		default:
			break;
		}
		return false;
	}

	private void register2Server(){
		mll.setVisibility(View.GONE);
		mrl.setVisibility(View.VISIBLE);
		mpb.setVisibility(View.VISIBLE);
		mState.setText(mContext.getString(R.string.phone_info_sending));
		if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
			mState.setTextColor(mContext.getColor(R.color.black));
		}
		new AsyncTask<Void, Void, Boolean>() {

			@Override
			protected Boolean doInBackground(Void... params) {
	
				boolean res = false;
				UpdataDeviceAuthenticatedReq req = new UpdataDeviceAuthenticatedReq(PhoneConfig.getPhoneDeveceID(mContext),
						PhoneConfig.getPhoneModel(),
						PhoneConfig.getOSVersion(),
						PhoneConfig.getPhoneManufactory(),
						PhoneConfig.getPhoneDeveceID(mContext));
				UpdataDeviceAuthenticatedRes resObj = null;
				try {
					Thread.sleep(1000);
				} catch (InterruptedException e1) {
					// TODO Auto-generated catch block
					e1.printStackTrace();
				}
				try {
					resObj = mSoapManager.getUpdataDeviceAuthenticatedRes(req);
					if (resObj.getResult().equals("OK")) {
						res = true;
					}
					
//					res = true;
//					Thread.sleep(1000);
					
					
				} catch (Exception e) {
					e.printStackTrace();
					return false;
				}
				return res;
			}

			protected void onPostExecute(Boolean result) {
				mpb.setVisibility(View.GONE);
				Window window = getDialog().getWindow();
				if (result) {
					window.setWindowAnimations(R.style.DialogSend);
					mState.setText(mContext.getString(R.string.phone_info_send_ok));
					mState.setTextColor(mContext.getResources().getColor(R.color.finger_green));
					mCancel.postDelayed(new Runnable() {
						public void run() {
							dismiss();
						}
					}, 300);
				}else{
					//TODO 添加动画
//					window.setWindowAnimations(R.style.DialogAnimationShake);
					

					mState.setText(mContext.getString(R.string.phone_info_send_fail));
					mState.setTextColor(mContext.getResources().getColor(R.color.finger_fail));
					mSend.setText(mContext.getString(R.string.phone_info_send_again));
				
					
					
				}
			};
		}.execute();
	}
}
