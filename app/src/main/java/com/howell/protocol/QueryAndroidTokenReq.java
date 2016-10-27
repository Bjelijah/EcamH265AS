package com.howell.protocol;

public class QueryAndroidTokenReq {
	private String account;
	private String loginSession;
	private String UDID;
	public QueryAndroidTokenReq(String account, String loginSession, String uDID) {
		super();
		this.account = account;
		this.loginSession = loginSession;
		UDID = uDID;
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
	public String getUDID() {
		return UDID;
	}
	public void setUDID(String uDID) {
		UDID = uDID;
	}
	
}
