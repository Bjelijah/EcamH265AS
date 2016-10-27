package com.howell.protocol;

public class QueryDeviceReq {
	public String account;
	public String loginSession;
	public String devID;
	public QueryDeviceReq(String account, String loginSession, String devID) {
		super();
		this.account = account;
		this.loginSession = loginSession;
		this.devID = devID;
	}
	
	
	public QueryDeviceReq(String account, String loginSession) {
		super();
		this.account = account;
		this.loginSession = loginSession;
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
	
	
}
