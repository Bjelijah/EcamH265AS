package com.howell.protocol;

public class AddDeviceReq {
	private String account;
	private String loginSession;
	private String devID;
	private String devKey;
	private String devName;
	private boolean forcible;
	public AddDeviceReq(String account, String loginSession, String devID,
			String devKey, String devName, boolean forcible) {
		super();
		this.account = account;
		this.loginSession = loginSession;
		this.devID = devID;
		this.devKey = devKey;
		this.devName = devName;
		this.forcible = forcible;
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
	public String getDevName() {
		return devName;
	}
	public void setDevName(String devName) {
		this.devName = devName;
	}
	public boolean isForcible() {
		return forcible;
	}
	public void setForcible(boolean forcible) {
		this.forcible = forcible;
	}

}
