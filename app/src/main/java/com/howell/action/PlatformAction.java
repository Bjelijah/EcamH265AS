package com.howell.action;

import android.os.AsyncTask;
import android.os.Handler;
import android.util.Log;

import com.howell.entityclass.Device;
import com.howell.entityclass.NodeDetails;
import com.howell.protocol.GetNATServerReq;
import com.howell.protocol.GetNATServerRes;
import com.howell.protocol.LoginRequest;
import com.howell.protocol.LoginResponse;
import com.howell.protocol.SoapManager;
import com.howell.utils.DecodeUtils;
import com.howell.utils.IConst;

import java.util.List;

public class PlatformAction implements IConst{
	
	
	private static PlatformAction mInstance = null;
	private PlatformAction() {	}
	public static PlatformAction getInstance(){
		if(mInstance == null){
			mInstance = new PlatformAction();
		}
		return mInstance;
	}
	SoapManager mSoapManager = SoapManager.getInstance();
	
	private String turnServerIp = null;
	private int turnServerPort = -1;
	private String device_id = null;
	private String deviceID = null;
	private String account = null;
	private String password= null;
	private boolean isTest = false;//是用100868账号登入试用e看
	private NodeDetails curSelNode= null;
	List<Device> deviceList = null;
	
	public boolean isTest(){
		return isTest;
	}
	
	public void setIsTest(boolean isTest){
		this.isTest = isTest;
	}
	
	public void setCurSelNode(NodeDetails node){
		this.curSelNode = node;
	}
	public NodeDetails getCurSelNode(){
		return curSelNode;
	}
	
	public void setDeviceID(String deviceID){
		Log.e("123","~~~~~~~~ PlatformAction set dev id="+deviceID);
		this.deviceID = deviceID;
	}
	public String getDeviceID(){
		return this.deviceID;
	}
	
	public List<Device> getDeviceList() {
		return deviceList;
	}
	public void setDeviceList(List<Device> deviceList) {
		this.deviceList = deviceList;
	}
	public String getDevice_id() {
		return device_id;
	}
	
	public String getDevice_id(int index){
		if(deviceList==null)return null;
		if(index>deviceList.size())return null;
		
		return deviceList.get(index).getDeviceID();
	}
	
	public void setDevice_id(String device_id) {
		this.device_id = device_id;
	}
	public void setDevice_id(int index){
		if(deviceList==null)return;
		if(index>deviceList.size())return;
		this.device_id = deviceList.get(index).getDeviceID();
	}
	
	
	public String getAccount() {
		return account;
	}
	public void setAccount(String account) {
		this.account = account;
	}
	public String getPassword() {
		return password;
	}
	public void setPassword(String password) {
		this.password = password;
	}
	public String getDeviceId(){
//		return device_id;
		return this.deviceID;
	}
	
	public String getCurSelDeviceId(){
		return getDeviceId();
	}
	
	public void setCurSelDeviceId(String deviceId){
		setDevice_id(deviceId);
	}
	
	public void setTurnServerIP(String turnServerIp){
//		Log.i("123","~~~~~~turnServerIP="+turnServerIp);
		this.turnServerIp = turnServerIp;
	}
	
	public String getTurnServerIP(){
		return this.turnServerIp;
	}
	
	public void setTurnServerPort(int turnServerPort){
//		Log.i("123","~~~~~turnServerPort="+turnServerPort);
		this.turnServerPort = turnServerPort;
	}
	
	public int getTurnServerPort(){
		return turnServerPort;
	}
	
	Handler handler;
	public void setHandler(Handler handler){
		this.handler = handler;
	}
	

	
	
	/**
	 * @Deprecated H265 Turn SSL never used 
	 * 
	 * 
	 */
	@Deprecated
	public void loginPlatform(){
		
		new AsyncTask<Void, Void, Boolean>() {

			@Override
			protected Boolean doInBackground(Void... params) {
				String encodedPassword = DecodeUtils.getEncodedPassword(TEST_PASSWORD);
//				String imei = PhoneConfig.getPhoneDeveceID(PlatformAction.this);
				LoginRequest loginReq = new LoginRequest(TEST_ACCOUNT, "Common",encodedPassword, "1.0.0.1",null);
				LoginResponse loginRes = mSoapManager.getUserLoginRes(loginReq);
				if(loginRes.getResult().equals("OK")){
					List<Device> list = loginRes.getNodeList();
					if(!list.isEmpty()){
						device_id = list.get(0).getDeviceID();
					}else{
						device_id = null;
					}	
					GetNATServerRes res = mSoapManager.getGetNATServerRes(new GetNATServerReq(TEST_ACCOUNT, loginRes.getLoginSession()));
					Log.i("123", res.toString());
					if(res.getResult().equals("OK")){
						turnServerIp = res.getTURNServerAddress();
						turnServerPort = res.getTURNServerPort();
					}else{
						turnServerIp = null;
						turnServerPort = -1;
					}
					return true;
				}else{
					return false;
				}
			}
			
			protected void onPostExecute(Boolean result) {
				if(result){
					handler.sendEmptyMessage(MSG_LOGIN_OK);
				}else{
					handler.sendEmptyMessage(MSG_LOGIN_FAIL);
				}
			};
		}.execute();
	}
	
	
	
	
	
}
