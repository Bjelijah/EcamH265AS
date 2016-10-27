package com.howell.protocol;

public class UpdataDeviceAuthenticatedRes {
	String Result;

	public String getResult() {
		return Result;
	}

	public void setResult(String result) {
		Result = result;
	}

	public UpdataDeviceAuthenticatedRes(String result) {
		super();
		Result = result;
	}

	public UpdataDeviceAuthenticatedRes() {
		super();
	}

	@Override
	public String toString() {
		return "UpdataDeviceAuthenticatedRes [Result=" + Result + "]";
	}
	
}
