package com.howell.protocol;

public class UpdateAndroidTokenReq {
	private String account;
    private String loginSession;
    private String UDID;
    private String deviceToken;
    private boolean APNs;
    private String AndroidOS;
	public UpdateAndroidTokenReq(String account, String loginSession,
			String UDID, String deviceToken, boolean aPNs) {
		super();
		this.account = account;
		this.loginSession = loginSession;
		this.UDID = UDID;
		this.deviceToken = deviceToken;
		APNs = aPNs;
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
	public String getUDID() {
		return UDID;
	}
	public void setUDID(String UDID) {
		this.UDID = UDID;
	}
	public String getDeviceToken() {
		return deviceToken;
	}
	public void setDeviceToken(String deviceToken) {
		this.deviceToken = deviceToken;
	}
	public boolean isAPNs() {
		return APNs;
	}
	public void setAPNs(boolean aPNs) {
		APNs = aPNs;
	}
	public String getAndroidOS() {
		return AndroidOS;
	}
	public void setAndroidOS(String androidOS) {
		AndroidOS = androidOS;
	}
	@Override
	public String toString() {
		return "UpdateAndroidTokenReq [account=" + account + ", loginSession="
				+ loginSession + ", UDID=" + UDID + ", deviceToken="
				+ deviceToken + ", APNs=" + APNs + ", AndroidOS=" + AndroidOS
				+ "]";
	}

    
}
