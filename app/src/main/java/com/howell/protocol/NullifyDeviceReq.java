package com.howell.protocol;

public class NullifyDeviceReq {
	private String account;
	private String loginSession;
	private String devID;
	private String devKey;
	public NullifyDeviceReq(String account, String loginSession, String devID,
			String devKey) {
		super();
		this.account = account;
		this.loginSession = loginSession;
		this.devID = devID;
		this.devKey = devKey;
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
	public String getDevKey() {
		return devKey;
	}
	public void setDevKey(String devKey) {
		this.devKey = devKey;
	}
	

}
