package com.howell.protocol;

public class QueryDeviceRes {
	public String result;
	public String devID;
    public int channelNo;
    public String name;
    public boolean onLine;
    public boolean ptzFlag;
    public int securityArea;
    public boolean eStoreFlag;
    public String upnpIP;
    public int upnpPort;
    public String devVer;
    public int curVideoNum;
    public String lastUpdated;
    private int sMSSubscribedFlag;
    private int eMailSubscribedFlag;
    private int sharingFlag;
    private int applePushSubscribedFlag;
    private int androidPushSubscribedFlag;
	
	public QueryDeviceRes() {
		super();
	}
	public QueryDeviceRes(String result) {
		super();
		this.result = result;
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
	public String getName() {
		return name;
	}
	public void setName(String name) {
		this.name = name;
	}
	public boolean isOnLine() {
		return onLine;
	}
	public void setOnLine(boolean onLine) {
		this.onLine = onLine;
	}
	public boolean isPtzFlag() {
		return ptzFlag;
	}
	public void setPtzFlag(boolean ptzFlag) {
		this.ptzFlag = ptzFlag;
	}
	public int getSecurityArea() {
		return securityArea;
	}
	public void setSecurityArea(int securityArea) {
		this.securityArea = securityArea;
	}
	public boolean iseStoreFlag() {
		return eStoreFlag;
	}
	public void seteStoreFlag(boolean eStoreFlag) {
		this.eStoreFlag = eStoreFlag;
	}
	public String getUpnpIP() {
		return upnpIP;
	}
	public void setUpnpIP(String upnpIP) {
		this.upnpIP = upnpIP;
	}
	public int getUpnpPort() {
		return upnpPort;
	}
	public void setUpnpPort(int upnpPort) {
		this.upnpPort = upnpPort;
	}
	public String getDevVer() {
		return devVer;
	}
	public void setDevVer(String devVer) {
		this.devVer = devVer;
	}
	public int getCurVideoNum() {
		return curVideoNum;
	}
	public void setCurVideoNum(int curVideoNum) {
		this.curVideoNum = curVideoNum;
	}
	public String getLastUpdated() {
		return lastUpdated;
	}
	public void setLastUpdated(String lastUpdated) {
		this.lastUpdated = lastUpdated;
	}
	public int getsMSSubscribedFlag() {
		return sMSSubscribedFlag;
	}
	public void setsMSSubscribedFlag(int sMSSubscribedFlag) {
		this.sMSSubscribedFlag = sMSSubscribedFlag;
	}
	public int geteMailSubscribedFlag() {
		return eMailSubscribedFlag;
	}
	public void seteMailSubscribedFlag(int eMailSubscribedFlag) {
		this.eMailSubscribedFlag = eMailSubscribedFlag;
	}
	public int getSharingFlag() {
		return sharingFlag;
	}
	public void setSharingFlag(int sharingFlag) {
		this.sharingFlag = sharingFlag;
	}
	public int getApplePushSubscribedFlag() {
		return applePushSubscribedFlag;
	}
	public void setApplePushSubscribedFlag(int applePushSubscribedFlag) {
		this.applePushSubscribedFlag = applePushSubscribedFlag;
	}
	public int getAndroidPushSubscribedFlag() {
		return androidPushSubscribedFlag;
	}
	public void setAndroidPushSubscribedFlag(int androidPushSubscribedFlag) {
		this.androidPushSubscribedFlag = androidPushSubscribedFlag;
	}
	public String getResult() {
		return result;
	}
	public void setResult(String result) {
		this.result = result;
	}
	@Override
	public String toString() {
		return "QueryDeviceRes [result=" + result  + ", devID=" + devID + ", channelNo=" + channelNo
				+ ", name=" + name + ", onLine=" + onLine + ", ptzFlag="
				+ ptzFlag + ", securityArea=" + securityArea + ", eStoreFlag="
				+ eStoreFlag + ", upnpIP=" + upnpIP + ", upnpPort=" + upnpPort
				+ ", devVer=" + devVer + ", curVideoNum=" + curVideoNum
				+ ", lastUpdated=" + lastUpdated + ", sMSSubscribedFlag="
				+ sMSSubscribedFlag + ", eMailSubscribedFlag="
				+ eMailSubscribedFlag + ", sharingFlag=" + sharingFlag
				+ ", applePushSubscribedFlag=" + applePushSubscribedFlag
				+ ", androidPushSubscribedFlag=" + androidPushSubscribedFlag
				+ "]";
	}

	
	
}
