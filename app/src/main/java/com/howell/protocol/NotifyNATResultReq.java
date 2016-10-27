package com.howell.protocol;

public class NotifyNATResultReq {
	private String account;
	private String loginSession;
	private String dialogID;
	private String NATType;
	
	public NotifyNATResultReq(String account, String loginSession,
			String dialogID, String nATType) {
		super();
		this.account = account;
		this.loginSession = loginSession;
		this.dialogID = dialogID;
		NATType = nATType;
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
	public String getDialogID() {
		return dialogID;
	}
	public void setDialogID(String dialogID) {
		this.dialogID = dialogID;
	}
	public String getNATType() {
		return NATType;
	}
	public void setNATType(String nATType) {
		NATType = nATType;
	}
	@Override
	public String toString() {
		return "NotifyNATResultReq [account=" + account + ", loginSession="
				+ loginSession + ", dialogID=" + dialogID + ", NATType="
				+ NATType + "]";
	}
	
	
}
