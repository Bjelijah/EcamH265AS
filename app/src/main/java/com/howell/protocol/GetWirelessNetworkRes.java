package com.howell.protocol;

public class GetWirelessNetworkRes {
	private String result;
	private String wirelessType;
	private String sSID;
	private int intensity;
	public String getResult() {
		return result;
	}
	public void setResult(String result) {
		this.result = result;
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
	@Override
	public String toString() {
		return "GetWirelessNetworkRes [result=" + result + ", wirelessType="
				+ wirelessType + ", sSID=" + sSID + ", intensity=" + intensity
				+ "]";
	}
}
