package com.howell.utils;

import android.content.Context;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.wifi.ScanResult;
import android.net.wifi.WifiConfiguration;
import android.net.wifi.WifiConfiguration.KeyMgmt;
import android.net.wifi.WifiInfo;
import android.net.wifi.WifiManager;
import android.net.wifi.WifiManager.WifiLock;
import android.util.Log;

import java.util.ArrayList;
import java.util.List;

public class NetWorkUtils {
	
	private final static String TAG = "NetWorkUtils";
	private StringBuffer mStringBuffer = new StringBuffer();
	private List<ScanResult> listResult;
	private ScanResult mScanResult;
	// 定义WifiManager对象
	private WifiManager mWifiManager;
	// 定义WifiInfo对象
	private WifiInfo mWifiInfo;
	// 网络连接列表
	private List<WifiConfiguration> mWifiConfiguration;
	// 定义一个WifiLock
	WifiLock mWifiLock;
		
	//定义几种加密方式，一种是WEP，一种是WPA，还有没有密码的情况  
	public enum WifiCipherType  
	{  
		WIFICIPHER_WEP,WIFICIPHER_WPA, WIFICIPHER_NOPASS, WIFICIPHER_INVALID  
	}  
	 
	/**
	 * 构造方法
	*/
	public NetWorkUtils(Context context) {
		mWifiManager = (WifiManager) context.getSystemService(Context.WIFI_SERVICE);
		mWifiInfo = mWifiManager.getConnectionInfo();
		mWifiConfiguration = mWifiManager.getConfiguredNetworks();
	}
	
	/**
	 * 判断是否连接网络
	*/
	public static boolean isNetworkConnected(Context context) {
        ConnectivityManager manager = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
        if (manager == null) {
            return false;
        }
        NetworkInfo networkinfo = manager.getActiveNetworkInfo();
        if (networkinfo == null || !networkinfo.isAvailable()) {
            return false;
        }
        return true;
    }
	
	/**
	* 打开Wifi网卡
	*/
	public void openNetCard() {
		if (!mWifiManager.isWifiEnabled()) {
			mWifiManager.setWifiEnabled(true);
		}
	}

	/**
	* 关闭Wifi网卡
	 */
	public void closeNetCard() {
		if (mWifiManager.isWifiEnabled()) {
			mWifiManager.setWifiEnabled(false);
		}
	}

	/**
	* 检查当前Wifi网卡状态
	*/
	public void checkNetCardState() {
		if (mWifiManager.getWifiState() == 0) {
			Log.i(TAG, "网卡正在关闭");
		} else if (mWifiManager.getWifiState() == 1) {
			Log.i(TAG, "网卡已经关闭");
		} else if (mWifiManager.getWifiState() == 2) {
			Log.i(TAG, "网卡正在打开");
		} else if (mWifiManager.getWifiState() == 3) {
			Log.i(TAG, "网卡已经打开");
		} else {
			Log.i(TAG, "---_---晕......没有获取到状态---_---");
		}
	}

	/**
	* 扫描周边网络
	*/
	public void scan() {
		mWifiManager.startScan();
		listResult = mWifiManager.getScanResults();
		System.out.println("listResult size:"+listResult.size());
		if (listResult != null) {
			Log.i(TAG, "当前区域存在无线网络，请查看扫描结果");
		} else {
			Log.i(TAG, "当前区域没有无线网络");
		}
	}

	/**
	* 得到扫描结果
	*/
	public String getScanResult() {
		// 每次点击扫描之前清空上一次的扫描结果
		if (mStringBuffer != null) {
			mStringBuffer = new StringBuffer();
		}
		// 开始扫描网络
		scan();
		//listResult = mWifiManager.getScanResults();
		if (listResult != null) {
			for (int i = 0; i < listResult.size(); i++) {
				mScanResult = listResult.get(i);
				mStringBuffer = mStringBuffer.append("NO.").append(i + 1)
					.append(" :").append(mScanResult.SSID).append("->")
					.append(mScanResult.BSSID).append("->")
					.append(mScanResult.capabilities).append("->")
					.append(mScanResult.frequency).append("->")
					.append(mScanResult.level).append("->")
					.append(mScanResult.describeContents()).append("\n\n");
			}
		}
		Log.i(TAG, mStringBuffer.toString());
		return mStringBuffer.toString();
	}

	public ArrayList<String> getSSIDResultList() {
		ArrayList<String> SSIDList = new ArrayList<String>();
		scan();
		//listResult = mWifiManager.getScanResults();
		//System.out.println("listResult size:"+listResult.size());
		if (listResult != null) {
			for (int i = 0; i < listResult.size(); i++) {
				mScanResult = listResult.get(i);
				System.out.println(mScanResult.SSID);
				SSIDList.add(mScanResult.SSID);
			}
		}
		return SSIDList;
	}
	
	/**
	* 连接指定网络
	*/
	public void connect() {
		mWifiInfo = mWifiManager.getConnectionInfo();
		
	}
	
	/**
	* 断开当前连接的网络
	*/
	public void disconnectWifi() {
		int netId = getNetworkId();
		mWifiManager.disableNetwork(netId);
		mWifiManager.disconnect();
		mWifiInfo = null;
	}
	
	/**
	* 检查当前网络状态
	* 
	* @return String
	*/
	public void checkNetWorkState() {
		if (mWifiInfo != null) {
			Log.i(TAG, "网络正常工作");
		} else {
			Log.i(TAG, "网络已断开");
		}
	}
	
	/**
	* 得到连接的ID
	*/
	public int getNetworkId() {
		return (mWifiInfo == null) ? 0 : mWifiInfo.getNetworkId();
	}
	
	/**
	* 得到IP地址
	*/
	public int getIPAddress() {
		//return (mWifiInfo == null) ? 0 : mWifiInfo.getIpAddress();
		connect();
		return (mWifiInfo.getSSID() == null) ? 0 : mWifiInfo.getIpAddress();
	}
	
	// 锁定WifiLock
	public void acquireWifiLock() {
		mWifiLock.acquire();
	}
	
	// 解锁WifiLock
	public void releaseWifiLock() {
	// 判断时候锁定
		if (mWifiLock.isHeld()) {
			mWifiLock.acquire();
		}
	}
	
	// 创建一个WifiLock
	public void creatWifiLock() {
		mWifiLock = mWifiManager.createWifiLock("Test");
	}
	
	// 得到配置好的网络
	public List<WifiConfiguration> getConfiguration() {
		return mWifiConfiguration;
	}
	
	// 指定配置好的网络进行连接
	public void connectConfiguration(int index) {
	// 索引大于配置好的网络索引返回
		if (index >= mWifiConfiguration.size()) {
			return;
		}
		// 连接配置好的指定ID的网络
		mWifiManager.enableNetwork(mWifiConfiguration.get(index).networkId,true);
	}
	
	// 得到MAC地址
	public String getMacAddress() {
	return (mWifiInfo == null) ? "NULL" : mWifiInfo.getMacAddress();
	}
	
	// 得到接入点的BSSID
	public String getBSSID() {
		return (mWifiInfo == null) ? "NULL" : mWifiInfo.getBSSID();
	}
	
	// 得到WifiInfo的所有信息包
	public String getWifiInfo() {
		return (mWifiInfo == null) ? "NULL" : mWifiInfo.toString();
	}
	
	// 得到WifiInfo的所有信息包
	public String getWifiSSID() {
		connect();
		return (mWifiInfo.getSSID() == null) ? "NULL" : mWifiInfo.getSSID().toString();
	}
	
	// 添加一个网络并连接
	public int addNetwork(String SSID, String Password, WifiCipherType Type) {
		WifiConfiguration config = IsExsits(SSID);
		
		int wcgID = -1;
		if(config == null){
			System.out.println("config == null "+config );
			config = CreateWifiInfo(SSID,Password,Type);
			wcgID = mWifiManager.addNetwork(config);
			mWifiManager.enableNetwork(wcgID, true);
		}else{
			System.out.println("config != null "+config.networkId+","+config);
			mWifiManager.enableNetwork(config.networkId, true);
			//mWifiManager.removeNetwork(config.networkId); 
		}
		return wcgID;
	}
	
	//查看以前是否也配置过这个网络  
	private WifiConfiguration IsExsits(String SSID)  
	{  
		List<WifiConfiguration> existingConfigs = mWifiManager.getConfiguredNetworks();  
		for (WifiConfiguration existingConfig : existingConfigs)   
		{  
			if (existingConfig.SSID.equals("\""+SSID+"\""))  
			{  
				return existingConfig;  
			}  
		}  
		return null;   
	}  
	 
	private WifiConfiguration CreateWifiInfo(String SSID, String Password, WifiCipherType Type)  
	{  
		WifiConfiguration config = new WifiConfiguration();    
		config.allowedAuthAlgorithms.clear();  
		config.allowedGroupCiphers.clear();  
		config.allowedKeyManagement.clear();  
		config.allowedPairwiseCiphers.clear();  
		config.allowedProtocols.clear();  
		config.SSID = "\"" + SSID + "\"";    
		if(Type == WifiCipherType.WIFICIPHER_NOPASS)  
		{  
			//config.wepKeys[0] = "";  
			//config.allowedKeyManagement.set(WifiConfiguration.KeyMgmt.NONE);  
			//config.wepTxKeyIndex = 0;  
			config.allowedKeyManagement.set(KeyMgmt.NONE); 
			config.status = WifiConfiguration.Status.ENABLED;
		}  
		else if(Type == WifiCipherType.WIFICIPHER_WEP)  
		{  
			config.preSharedKey = "\""+Password+"\"";   
			config.hiddenSSID = true;    
			config.allowedAuthAlgorithms.set(WifiConfiguration.AuthAlgorithm.SHARED);  
			config.allowedGroupCiphers.set(WifiConfiguration.GroupCipher.CCMP);  
			config.allowedGroupCiphers.set(WifiConfiguration.GroupCipher.TKIP);  
			config.allowedGroupCiphers.set(WifiConfiguration.GroupCipher.WEP40);  
			config.allowedGroupCiphers.set(WifiConfiguration.GroupCipher.WEP104);  
			config.allowedKeyManagement.set(KeyMgmt.NONE);
			config.wepTxKeyIndex = 0;  
		}  
		else if(Type == WifiCipherType.WIFICIPHER_WPA)  
		{  
			config.preSharedKey = "\""+Password+"\"";  
			config.hiddenSSID = true;    
			config.allowedAuthAlgorithms.set(WifiConfiguration.AuthAlgorithm.OPEN);    
			config.allowedGroupCiphers.set(WifiConfiguration.GroupCipher.TKIP);                          
			config.allowedKeyManagement.set(KeyMgmt.WPA_PSK);
			config.allowedPairwiseCiphers.set(WifiConfiguration.PairwiseCipher.TKIP);                     
			//config.allowedProtocols.set(WifiConfiguration.Protocol.WPA);  
			config.allowedGroupCiphers.set(WifiConfiguration.GroupCipher.CCMP); 
			config.allowedPairwiseCiphers.set(WifiConfiguration.PairwiseCipher.CCMP); 
			config.status = WifiConfiguration.Status.ENABLED;    
		}  
		else  
		{  
			return null;  
		}  
			return config;  
	}  
}
