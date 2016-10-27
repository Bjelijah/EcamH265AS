package com.howell.protocol;

public class GetAuxiliaryRes {
	private String result;
	private String auxiliaryState;
	public String getResult() {
		return result;
	}
	public void setResult(String result) {
		this.result = result;
	}
	public String getAuxiliaryState() {
		return auxiliaryState;
	}
	public void setAuxiliaryState(String auxiliaryState) {
		this.auxiliaryState = auxiliaryState;
	}
	@Override
	public String toString() {
		return "GetAuxiliaryRes [result=" + result + ", auxiliaryState="
				+ auxiliaryState + "]";
	}
	

}
