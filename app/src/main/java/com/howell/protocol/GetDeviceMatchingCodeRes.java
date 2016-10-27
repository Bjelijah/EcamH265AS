package com.howell.protocol;

import java.io.Serializable;

public class GetDeviceMatchingCodeRes  implements Serializable{
	private String result;
	private String matchingCode;
	public String getResult() {
		return result;
	}
	public void setResult(String result) {
		this.result = result;
	}
	public String getMatchingCode() {
		return matchingCode;
	}
	public void setMatchingCode(String matchingCode) {
		this.matchingCode = matchingCode;
	}

}
