package com.howell.protocol;

public class UpdateChannelNameReq {
	private String account;
	private String loginSession;
	private String devID;
	private int channelNo;
	private String channelName;
	
	public UpdateChannelNameReq(String account, String loginSession,
			String devID, int channelNo, String channelName) {
		super();
		this.account = account;
		this.loginSession = loginSession;
		this.devID = devID;
		this.channelNo = channelNo;
		this.channelName = channelName;
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
	public String getChannelName() {
		return channelName;
	}
	public void setChannelName(String channelName) {
		this.channelName = channelName;
	}

}
