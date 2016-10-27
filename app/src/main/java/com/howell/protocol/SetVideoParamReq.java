package com.howell.protocol;

public class SetVideoParamReq {
	private String account;
	private String loginSession;
	private String devID;
	private int channelNo;
	private int rotationDegree;
	public SetVideoParamReq(String account, String loginSession, String devID,
			int channelNo, int rotationDegree) {
		super();
		this.account = account;
		this.loginSession = loginSession;
		this.devID = devID;
		this.channelNo = channelNo;
		this.rotationDegree = rotationDegree;
	}
	public SetVideoParamReq() {
		super();
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
	public int getRotationDegree() {
		return rotationDegree;
	}
	public void setRotationDegree(int rotationDegree) {
		this.rotationDegree = rotationDegree;
	}
	
}
