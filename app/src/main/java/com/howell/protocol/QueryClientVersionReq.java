package com.howell.protocol;

public class QueryClientVersionReq {
	private String ClientType;

	public QueryClientVersionReq(String clientType) {
		super();
		ClientType = clientType;
	}

	public String getClientType() {
		return ClientType;
	}

	public void setClientType(String clientType) {
		ClientType = clientType;
	}
	
}
