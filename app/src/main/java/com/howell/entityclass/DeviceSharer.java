package com.howell.entityclass;

public class DeviceSharer {
	private String sharerAccount;
	private int sharingPriority;
	
	public DeviceSharer() {
		super();
	}
	public DeviceSharer(String sharerAccount, int sharingPriority) {
		super();
		this.sharerAccount = sharerAccount;
		this.sharingPriority = sharingPriority;
	}
	public String getSharerAccount() {
		return sharerAccount;
	}
	public void setSharerAccount(String sharerAccount) {
		this.sharerAccount = sharerAccount;
	}
	public int getSharingPriority() {
		return sharingPriority;
	}
	public void setSharingPriority(int sharingPriority) {
		this.sharingPriority = sharingPriority;
	}
	@Override
	public String toString() {
		return "DeviceSharer [" + sharerAccount + ","+ sharingPriority + "]";
	}
	

}
