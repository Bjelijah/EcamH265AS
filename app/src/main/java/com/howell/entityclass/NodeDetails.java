package com.howell.entityclass;

import java.io.Serializable;

public class NodeDetails implements Serializable {
	private String devID;
	private int channelNo;
	private String name;
	private boolean onLine;
	private boolean ptzFlag;
	private int securityArea;
	private boolean eStoreFlag;
	private String upnpIP;
	private int upnpPort;
	private String devVer;
	private int curVideoNum;
	private String lastUpdated;
    private int sMSSubscribedFlag;
    private int eMailSubscribedFlag;
    private int sharingFlag;
    private int applePushSubscribedFlag;
    private int androidPushSubscribedFlag;
    private int infraredFlag;//����
    private int wirelessFlag;//����
    private String wirelessType;
    private String sSID;
    private int intensity;
    
    private int methodType;
    private boolean hasUpdate;
    private String picturePath;
    
	public NodeDetails(String upnpIP, int upnpPort) {
		super();
		this.upnpIP = upnpIP;
		this.upnpPort = upnpPort;
		methodType = 0;
		hasUpdate = false;
		picturePath = "/sdcard/eCamera/cache/"+devID+".jpg";
	}
	public NodeDetails(String devID, int channelNo, String name,
			boolean onLine, boolean ptzFlag, int securityArea,
			boolean eStoreFlag, String upnpIP, int upnpPort, String devVer,
			int curVideoNum, String lastUpdated) {
		super();
		this.devID = devID;
		this.channelNo = channelNo;
		this.name = name;
		this.onLine = onLine;
		this.ptzFlag = ptzFlag;
		this.securityArea = securityArea;
		this.eStoreFlag = eStoreFlag;
		this.upnpIP = upnpIP;
		this.upnpPort = upnpPort;
		this.devVer = devVer;
		this.curVideoNum = curVideoNum;
		this.lastUpdated = lastUpdated;
		methodType = 0;
		hasUpdate = false;
		picturePath = "/sdcard/eCamera/cache/"+devID+".jpg";
	}
	
	public NodeDetails(String devID, int channelNo, String name,
			boolean onLine, boolean ptzFlag, int securityArea,
			boolean eStoreFlag, String upnpIP, int upnpPort, String devVer,
			int curVideoNum, String lastUpdated, int sMSSubscribedFlag,
			int eMailSubscribedFlag, int sharingFlag,
			int applePushSubscribedFlag, int androidPushSubscribedFlag) {
		super();
		this.devID = devID;
		this.channelNo = channelNo;
		this.name = name;
		this.onLine = onLine;
		this.ptzFlag = ptzFlag;
		this.securityArea = securityArea;
		this.eStoreFlag = eStoreFlag;
		this.upnpIP = upnpIP;
		this.upnpPort = upnpPort;
		this.devVer = devVer;
		this.curVideoNum = curVideoNum;
		this.lastUpdated = lastUpdated;
		this.sMSSubscribedFlag = sMSSubscribedFlag;
		this.eMailSubscribedFlag = eMailSubscribedFlag;
		this.sharingFlag = sharingFlag;
		this.applePushSubscribedFlag = applePushSubscribedFlag;
		this.androidPushSubscribedFlag = androidPushSubscribedFlag;
		methodType = 0;
		hasUpdate = false;
		picturePath = "/sdcard/eCamera/cache/"+devID+".jpg";
	}
	
	public NodeDetails(String devID, int channelNo, String name,
			boolean onLine, boolean ptzFlag, int securityArea,
			boolean eStoreFlag, String upnpIP, int upnpPort, String devVer,
			int curVideoNum, String lastUpdated, int sMSSubscribedFlag,
			int eMailSubscribedFlag, int sharingFlag,
			int applePushSubscribedFlag, int androidPushSubscribedFlag,
			int infraredFlag, int wirelessFlag) {
		super();
		this.devID = devID;
		this.channelNo = channelNo;
		this.name = name;
		this.onLine = onLine;
		this.ptzFlag = ptzFlag;
		this.securityArea = securityArea;
		this.eStoreFlag = eStoreFlag;
		this.upnpIP = upnpIP;
		this.upnpPort = upnpPort;
		this.devVer = devVer;
		this.curVideoNum = curVideoNum;
		this.lastUpdated = lastUpdated;
		this.sMSSubscribedFlag = sMSSubscribedFlag;
		this.eMailSubscribedFlag = eMailSubscribedFlag;
		this.sharingFlag = sharingFlag;
		this.applePushSubscribedFlag = applePushSubscribedFlag;
		this.androidPushSubscribedFlag = androidPushSubscribedFlag;
		this.infraredFlag = infraredFlag;
		this.wirelessFlag = wirelessFlag;
		this.methodType = 0;
		hasUpdate = false;
		picturePath = "/sdcard/eCamera/cache/"+devID+".jpg";
	}
	
	
	public String getPicturePath() {
		return picturePath;
	}
	public void setPicturePath(String picturePath) {
		this.picturePath = picturePath;
	}
	public boolean isHasUpdate() {
		return hasUpdate;
	}
	public void setHasUpdate(boolean hasUpdate) {
		this.hasUpdate = hasUpdate;
	}
	public int getMethodType() {
		return methodType;
	}
	public void setMethodType(int methodType) {
		this.methodType = methodType;
	}
	public int getInfraredFlag() {
		return infraredFlag;
	}
	public void setInfraredFlag(int infraredFlag) {
		this.infraredFlag = infraredFlag;
	}
	public int getWirelessFlag() {
		return wirelessFlag;
	}
	public void setWirelessFlag(int wirelessFlag) {
		this.wirelessFlag = wirelessFlag;
	}
	public String getWirelessType() {
		return wirelessType;
	}
	public void setWirelessType(String wirelessType) {
		this.wirelessType = wirelessType;
	}
	public String getsSID() {
		return sSID;
	}
	public void setsSID(String sSID) {
		this.sSID = sSID;
	}
	public int getIntensity() {
		return intensity;
	}
	public void setIntensity(int intensity) {
		this.intensity = intensity;
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
	
	@Override
	public String toString() {
		return "NodeDetails [devID=" + devID + ", channelNo=" + channelNo
				+ ", name=" + name + ", onLine=" + onLine + ", ptzFlag="
				+ ptzFlag + ", securityArea=" + securityArea + ", eStoreFlag="
				+ eStoreFlag + ", upnpIP=" + upnpIP + ", upnpPort=" + upnpPort
				+ ", devVer=" + devVer + ", curVideoNum=" + curVideoNum
				+ ", lastUpdated=" + lastUpdated + ", sMSSubscribedFlag="
				+ sMSSubscribedFlag + ", eMailSubscribedFlag="
				+ eMailSubscribedFlag + ", sharingFlag=" + sharingFlag
				+ ", applePushSubscribedFlag=" + applePushSubscribedFlag
				+ ", androidPushSubscribedFlag=" + androidPushSubscribedFlag
				+ ", infraredFlag=" + infraredFlag + ", wirelessFlag="
				+ wirelessFlag + ", wirelessType=" + wirelessType + ", sSID="
				+ sSID + ", intensity=" + intensity + ", methodType="
				+ methodType + "]";
	}
    
}
