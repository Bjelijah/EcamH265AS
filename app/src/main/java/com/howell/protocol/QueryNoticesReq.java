package com.howell.protocol;
/**
 * @author 霍之昊 
 *
 * 类说明
 */
public class QueryNoticesReq {
	private String account;
	private String loginSession;
	private int pageNo;
	private String searchID;
	private int pageSize;
	private String status;
	private String time;
	private String sender;
	public QueryNoticesReq(String account, String loginSession, int pageNo,
			String searchID, int pageSize, String status, String time,
			String sender) {
		super();
		this.account = account;
		this.loginSession = loginSession;
		this.pageNo = pageNo;
		this.searchID = searchID;
		this.pageSize = pageSize;
		this.status = status;
		this.time = time;
		this.sender = sender;
	}
	
	public QueryNoticesReq(String account, String loginSession, int pageNo,
			int pageSize) {
		super();
		this.account = account;
		this.loginSession = loginSession;
		this.pageNo = pageNo;
		this.pageSize = pageSize;
	}


	public QueryNoticesReq() {
		super();
	}
	
	public QueryNoticesReq(String account, String loginSession) {
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
	public int getPageNo() {
		return pageNo;
	}
	public void setPageNo(int pageNo) {
		this.pageNo = pageNo;
	}
	public String getSearchID() {
		return searchID;
	}
	public void setSearchID(String searchID) {
		this.searchID = searchID;
	}
	public int getPageSize() {
		return pageSize;
	}
	public void setPageSize(int pageSize) {
		this.pageSize = pageSize;
	}
	public String getStatus() {
		return status;
	}
	public void setStatus(String status) {
		this.status = status;
	}
	public String getTime() {
		return time;
	}
	public void setTime(String time) {
		this.time = time;
	}
	public String getSender() {
		return sender;
	}
	public void setSender(String sender) {
		this.sender = sender;
	}

}
