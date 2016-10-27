package com.howell.protocol;

import java.util.ArrayList;

import com.howell.entityclass.DeviceSharer;

public class QueryDeviceSharerRes {
	private String result;
	private ArrayList<DeviceSharer> deviceSharerList;
	
	public QueryDeviceSharerRes() {
		super();
		deviceSharerList = new ArrayList<DeviceSharer>();
	}
	
	public void addDeviceSharer(DeviceSharer d){
		deviceSharerList.add(d);
	}
	
	public String getResult() {
		return result;
	}
	public void setResult(String result) {
		this.result = result;
	}
	public ArrayList<DeviceSharer> getDeviceSharerList() {
		return deviceSharerList;
	}
	public void setDeviceSharerList(ArrayList<DeviceSharer> deviceSharerList) {
		this.deviceSharerList = deviceSharerList;
	}
	
	
}
