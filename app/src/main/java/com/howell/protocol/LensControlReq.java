package com.howell.protocol;
/**
 * @author 霍之昊 
 *
 * 类说明
 */
public class LensControlReq {
	private String account;
	private String loginSession;
	private String devID;
	private int channelNo;
	private String ptzLens;
	public LensControlReq(String account, String loginSession, String devID,
			int channelNo, String ptzLens) {
		super();
		this.account = account;
		this.loginSession = loginSession;
		this.devID = devID;
		this.channelNo = channelNo;
		this.ptzLens = ptzLens;
	}
	public LensControlReq() {
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
	public String getDevID() {
		return devID;
	}
	public void setDevID(String devID) {
		this.devID = devID;
	}
	public int getChannelNo() {
		return channelNo;
	}
	public void setChannelNo(int channelNo) {
		this.channelNo = channelNo;
	}
	public String getPtzLens() {
		return ptzLens;
	}
	public void setPtzLens(String ptzLens) {
		this.ptzLens = ptzLens;
	}
	
	

}
