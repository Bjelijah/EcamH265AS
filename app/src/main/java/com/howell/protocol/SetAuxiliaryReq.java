package com.howell.protocol;

public class SetAuxiliaryReq {
	private String account;
	private String loginSession;
	private String devID;
	private String auxiliary;
	private String auxiliaryState;
	public SetAuxiliaryReq(String account, String loginSession, String devID,
			String auxiliary, String auxiliaryState) {
		super();
		this.account = account;
		this.loginSession = loginSession;
		this.devID = devID;
		this.auxiliary = auxiliary;
		this.auxiliaryState = auxiliaryState;
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
	public String getAuxiliaryState() {
		return auxiliaryState;
	}
	public void setAuxiliaryState(String auxiliaryState) {
		this.auxiliaryState = auxiliaryState;
	}

}
