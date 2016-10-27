package com.howell.protocol;

import java.io.Serializable;

@SuppressWarnings("serial")
public class GetNATServerRes implements Serializable{
	private String result;
	private String STUNServerAddress;
	private int STUNServerPort;
	private String TURNServerAddress;
	private int TURNServerPort;
	private String TURNServerUserName;
	private String TURNServerPassword;
	public GetNATServerRes(String result, String sTUNServerAddress,
			int sTUNServerPort, String tURNServerAddress, int tURNServerPort,
			String tURNServerUserName, String tURNServerPassword) {
		super();
		this.result = result;
		STUNServerAddress = sTUNServerAddress;
		STUNServerPort = sTUNServerPort;
		TURNServerAddress = tURNServerAddress;
		TURNServerPort = tURNServerPort;
		TURNServerUserName = tURNServerUserName;
		TURNServerPassword = tURNServerPassword;
	}
	public GetNATServerRes() {
		super();
	}
	public String getResult() {
		return result;
	}
	public void setResult(String result) {
		this.result = result;
	}
	public String getSTUNServerAddress() {
		return STUNServerAddress;
	}
	public void setSTUNServerAddress(String sTUNServerAddress) {
		STUNServerAddress = sTUNServerAddress;
	}
	public int getSTUNServerPort() {
		return STUNServerPort;
	}
	public void setSTUNServerPort(int sTUNServerPort) {
		STUNServerPort = sTUNServerPort;
	}
	public String getTURNServerAddress() {
		return TURNServerAddress;
	}
	public void setTURNServerAddress(String tURNServerAddress) {
		TURNServerAddress = tURNServerAddress;
	}
	public int getTURNServerPort() {
		return TURNServerPort;
	}
	public void setTURNServerPort(int tURNServerPort) {
		TURNServerPort = tURNServerPort;
	}
	public String getTURNServerUserName() {
		return TURNServerUserName;
	}
	public void setTURNServerUserName(String tURNServerUserName) {
		TURNServerUserName = tURNServerUserName;
	}
	public String getTURNServerPassword() {
		return TURNServerPassword;
	}
	public void setTURNServerPassword(String tURNServerPassword) {
		TURNServerPassword = tURNServerPassword;
	}
	@Override
	public String toString() {
		return "GetNATServerRes [result=" + result + ", STUNServerAddress="
				+ STUNServerAddress + ", STUNServerPort=" + STUNServerPort
				+ ", TURNServerAddress=" + TURNServerAddress
				+ ", TURNServerPort=" + TURNServerPort
				+ ", TURNServerUserName=" + TURNServerUserName
				+ ", TURNServerPassword=" + TURNServerPassword + "]";
	}
	
}
