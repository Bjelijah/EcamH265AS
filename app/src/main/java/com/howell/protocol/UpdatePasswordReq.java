package com.howell.protocol;

public class UpdatePasswordReq {
	private String account;
    private String loginSession;
    private String password;
    private String newPassword;
	public UpdatePasswordReq(String account, String loginSession,
			String password, String newPassword) {
		super();
		this.account = account;
		this.loginSession = loginSession;
		this.password = password;
		this.newPassword = newPassword;
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
	public String getPassword() {
		return password;
	}
	public void setPassword(String password) {
		this.password = password;
	}
	public String getNewPassword() {
		return newPassword;
	}
	public void setNewPassword(String newPassword) {
		this.newPassword = newPassword;
	}
    
}
