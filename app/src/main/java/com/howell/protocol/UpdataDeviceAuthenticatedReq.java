package com.howell.protocol;

public class UpdataDeviceAuthenticatedReq {
	private String UUID;
	private String Model;
	private String Type;
	private String OSType;
	private String OSVersion;
	private String Manufactory;
	private String IMEI;
	public String getUUID() {
		return UUID;
	}
	public void setUUID(String uUID) {
		UUID = uUID;
	}
	public String getModel() {
		return Model;
	}
	public void setModel(String model) {
		Model = model;
	}
	public String getType() {
		return Type;
	}
	public void setType(String type) {
		Type = type;
	}
	public String getOSType() {
		return OSType;
	}
	public void setOSType(String oSType) {
		OSType = oSType;
	}
	public String getOSVersion() {
		return OSVersion;
	}
	public void setOSVersion(String oSVersion) {
		OSVersion = oSVersion;
	}
	public String getManufactory() {
		return Manufactory;
	}
	public void setManufactory(String manufactory) {
		Manufactory = manufactory;
	}
	public String getIMEI() {
		return IMEI;
	}
	public void setIMEI(String iMEI) {
		IMEI = iMEI;
	}
	public UpdataDeviceAuthenticatedReq() {
		super();
	}
	public UpdataDeviceAuthenticatedReq(String uUID, String model, String type, String oSType, String oSVersion,
			String manufactory, String iMEI) {
		super();
		UUID = uUID;
		Model = model;
		Type = type;
		OSType = oSType;
		OSVersion = oSVersion;
		Manufactory = manufactory;
		IMEI = iMEI;
	}
	public UpdataDeviceAuthenticatedReq(String uUID, String  model) {
		super();
		UUID = uUID;
		Model = model;
		IMEI = uUID;
		Type = "CellPhone";
		OSType = "Android";
	}
	
	public UpdataDeviceAuthenticatedReq(String uUID, String model, String oSVersion, String manufactory, String iMEI) {
		super();
		UUID = uUID;
		Model = model;
		OSVersion = oSVersion;
		Manufactory = manufactory;
		IMEI = iMEI;
		Type = "CellPhone";
		OSType = "Android";
	}
	@Override
	public String toString() {
		return "UpdataDeviceAuthenticatedReq [UUID=" + UUID + ", Model=" + Model + ", Type=" + Type + ", OSType="
				+ OSType + ", OSVersion=" + OSVersion + ", Manufactory=" + Manufactory + ", IMEI=" + IMEI + "]";
	}
	
}
