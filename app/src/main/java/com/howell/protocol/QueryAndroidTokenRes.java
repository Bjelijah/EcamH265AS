package com.howell.protocol;

public class QueryAndroidTokenRes {
	private String result;
	private String UDID;
	private String deviceToken;
	private boolean APNs;
	public String getResult() {
		return result;
	}
	public void setResult(String result) {
		this.result = result;
	}
	public String getUDID() {
		return UDID;
	}
	public void setUDID(String uDID) {
		UDID = uDID;
	}
	public String getDeviceToken() {
		return deviceToken;
	}
	public void setDeviceToken(String deviceToken) {
		this.deviceToken = deviceToken;
	}
	public boolean isAPNs() {
		return APNs;
	}
	public void setAPNs(boolean aPNs) {
		APNs = aPNs;
	}
	@Override
	public String toString() {
		return "QueryAndroidTokenRes [result=" + result + ", UDID=" + UDID
				+ ", deviceToken=" + deviceToken + ", APNs=" + APNs + "]";
	}
	
	
}
