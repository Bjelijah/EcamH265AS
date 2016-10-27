package com.howell.protocol;

public class UpdateAccountReq {
	private String account;
	private String loginSession;
	private String username;
	private String mobileTel;
	
	public UpdateAccountReq(String account, String loginSession,
			String mobileTel) {
		super();
		this.account = account;
		this.loginSession = loginSession;
		this.mobileTel = mobileTel;
	}
	public UpdateAccountReq(String account, String loginSession,
			String username, String mobileTel) {
		super();
		this.account = account;
		this.loginSession = loginSession;
		this.username = username;
		this.mobileTel = mobileTel;
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
	public String getUsername() {
		return username;
	}
	public void setUsername(String username) {
		this.username = username;
	}
	public String getMobileTel() {
		return mobileTel;
	}
	public void setMobileTel(String mobileTel) {
		this.mobileTel = mobileTel;
	}
	
	
}
