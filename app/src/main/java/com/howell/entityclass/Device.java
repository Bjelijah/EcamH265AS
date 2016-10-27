package com.howell.entityclass;

import java.io.Serializable;

public class Device implements Serializable {
    /**
     * 
     */
    private static final long serialVersionUID = -7054945138110360538L;
    private String mDeviceID;
    private int mChannelNo;
    private String mName;
    private boolean mOnLine;
    private boolean mPtzFlag;
    private boolean hasUpdate;
    private int methodType;
    private int indensity;
    
    public Device() {
		super();
	}

	public Device(String devID, int channelNo, String name, boolean onLine,
            boolean ptzFlag) {
        mDeviceID = devID;
        mChannelNo = channelNo;
        mName = name;
        mOnLine = onLine;
        mPtzFlag = ptzFlag;
        hasUpdate = false;
        methodType = 0;
        indensity = -1;
    }

    public Device(String mDeviceID, int mChannelNo, String mName,
			boolean mOnLine, boolean mPtzFlag, boolean hasUpdate) {
		super();
		this.mDeviceID = mDeviceID;
		this.mChannelNo = mChannelNo;
		this.mName = mName;
		this.mOnLine = mOnLine;
		this.mPtzFlag = mPtzFlag;
		this.hasUpdate = hasUpdate;
		indensity = -1;
	}
    
	public int getIndensity() {
		return indensity;
	}

	public void setIndensity(int indensity) {
		this.indensity = indensity;
	}

	public boolean isHasUpdate() {
		return hasUpdate;
	}

	public int getMethodType() {
		return methodType;
	}

	public void setMethodType(int methodType) {
		this.methodType = methodType;
	}

	public void setHasUpdate(boolean hasUpdate) {
		this.hasUpdate = hasUpdate;
	}

	public String getDeviceID() {
        return mDeviceID;
    }

    public void setDeviceID(String deviceID) {
        mDeviceID = deviceID;
    }

    public int getChannelNo() {
        return mChannelNo;
    }

    public void setChannelNo(int cannelNo) {
        mChannelNo = cannelNo;
    }

    public String getName() {
        return mName;
    }

    public void setName(String name) {
        mName = name;
    }

    public boolean isOnLine() {
        return mOnLine;
    }

    public void setOnLine(boolean onLine) {
        mOnLine = onLine;
    }

    public boolean isPtzFlag() {
        return mPtzFlag;
    }

    public void setPtzFlag(boolean ptzFlag) {
        mPtzFlag = ptzFlag;
    }

	@Override
	public String toString() {
		return "Device [mDeviceID=" + mDeviceID + ", mChannelNo=" + mChannelNo
				+ ", mName=" + mName + ", mOnLine=" + mOnLine + ", mPtzFlag="
				+ mPtzFlag + ", hasUpdate=" + hasUpdate + ", methodType="
				+ methodType + ", indensity=" + indensity + "]";
	}

}
