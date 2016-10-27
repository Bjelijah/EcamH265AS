package com.howell.protocol;

public class QueryClientVersionRes {
	private String result;
	private String version;
	private String downloadAddress;
	public String getResult() {
		return result;
	}
	public void setResult(String result) {
		this.result = result;
	}
	public String getVersion() {
		return version;
	}
	public void setVersion(String version) {
		this.version = version;
	}
	public String getDownloadAddress() {
		return downloadAddress;
	}
	public void setDownloadAddress(String downloadAddress) {
		this.downloadAddress = downloadAddress;
	}
	@Override
	public String toString() {
		return "QueryClientVersionRes [result=" + result + ", version="
				+ version + ", downloadAddress=" + downloadAddress + "]";
	}
	
}
