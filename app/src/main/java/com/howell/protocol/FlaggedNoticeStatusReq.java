package com.howell.protocol;

/**
 * @author 霍之昊 
 *
 * 类说明
 */
public class FlaggedNoticeStatusReq {
	private String account;
	private String loginSession;
	private String status;
	private String noticeID;
	public FlaggedNoticeStatusReq(String account, String loginSession,
			String status, String noticeID) {
		super();
		this.account = account;
		this.loginSession = loginSession;
		this.status = status;
		this.noticeID = noticeID;
	}
	public FlaggedNoticeStatusReq() {
		super();
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
	public String getStatus() {
		return status;
	}
	public void setStatus(String status) {
		this.status = status;
	}
	public String getNoticeID() {
		return noticeID;
	}
	public void setNoticeID(String noticeID) {
		this.noticeID = noticeID;
	}
	
}
