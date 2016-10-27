package com.howell.protocol;

public class SubscribeAndroidPushReq {
	private String account;
	private String loginSession;
	private int subscribedFlag;
	private String devID;
	private int channelNo;
	
	public SubscribeAndroidPushReq(String account, String loginSession,
			int subscribedFlag, String devID, int channelNo) {
		super();
		this.account = account;
		this.loginSession = loginSession;
		this.subscribedFlag = subscribedFlag;
		this.devID = devID;
		this.channelNo = channelNo;
	}
	public String getAccount() {
		return account;
	}
	public void setAccount(String account) {
		this.account = account;
	}
	public String getLoginSession() {
		return loginSession;
	}
	public void setLoginSession(String loginSession) {
		this.loginSession = loginSession;
	}
	public int getSubscribedFlag() {
		return subscribedFlag;
	}
	public void setSubscribedFlag(int subscribedFlag) {
		this.subscribedFlag = subscribedFlag;
	}
	public String getDevID() {
		return devID;
	}
	public void setDevID(String devID) {
		this.devID = devID;
	}
	public int getChannelNo() {
		return channelNo;
	}
	public void setChannelNo(int channelNo) {
		this.channelNo = channelNo;
	}
	@Override
	public String toString() {
		return "SubscribeAndroidPushReq [account=" + account
				+ ", loginSession=" + loginSession + ", subscribedFlag="
				+ subscribedFlag + ", devID=" + devID + ", channelNo="
				+ channelNo + "]";
	}

	
}
