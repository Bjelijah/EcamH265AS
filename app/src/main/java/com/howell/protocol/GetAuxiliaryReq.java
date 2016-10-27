package com.howell.protocol;

public class GetAuxiliaryReq {
	private String account;
	private String loginSession;
	private String devID;
	private String auxiliary;
	public GetAuxiliaryReq(String account, String loginSession, String devID,
			String auxiliary) {
		super();
		this.account = account;
		this.loginSession = loginSession;
		this.devID = devID;
		this.auxiliary = auxiliary;
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
	public String getAuxiliary() {
		return auxiliary;
	}
	public void setAuxiliary(String auxiliary) {
		this.auxiliary = auxiliary;
	}
	
}
