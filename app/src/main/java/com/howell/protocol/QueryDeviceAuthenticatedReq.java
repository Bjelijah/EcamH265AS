package com.howell.protocol;

public class QueryDeviceAuthenticatedReq {
	String UUID;

	public String getUUID() {
		return UUID;
	}

	public void setUUID(String uUID) {
		UUID = uUID;
	}

	public QueryDeviceAuthenticatedReq(String uUID) {
		super();
		UUID = uUID;
	}

	@Override
	public String toString() {
		return "QueryDeviceAuthenticatedReq [UUID=" + UUID + "]";
	}
	
}
