package com.howell.protocol;

public class GetDeviceMatchingResultReq {
	private String account;
	private String loginSession;
	private String matchingCode;
	public GetDeviceMatchingResultReq(String account, String loginSession,
			String matchingCode) {
		super();
		this.account = account;
		this.loginSession = loginSession;
		this.matchingCode = matchingCode;
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
	public String getMatchingCode() {
		return matchingCode;
	}
	public void setMatchingCode(String matchingCode) {
		this.matchingCode = matchingCode;
	}
	
}
