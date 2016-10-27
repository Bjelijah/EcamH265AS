package com.howell.action;

public class UserPowerAction{
	public final static int RIGHT_VISITOR    = 1;
	public final static int RIGHT_USER       = 2;
	public final static int RIGHT_ADMIN	   	 = 3;
	private static UserPowerAction mInstance = null;
	public static UserPowerAction getInstance(){
		if (mInstance==null) {
			mInstance = new UserPowerAction();
		}
		return mInstance;
	}
	private UserPowerAction() {}
	
	private int power = 0;
	public int getPower() {
		return power;
	}
	public void setPower(int power) {
		this.power = power;
	}
	
}
