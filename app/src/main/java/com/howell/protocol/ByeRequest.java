package com.howell.protocol;

public class ByeRequest {
    private String account;
    private String loginSession;
    private String devID;
    private int channelNo;
    private String streamType;
    private String dialogID;

    public ByeRequest(String account, String loginSession, String devID,
            int channelNo, String streamType, String dialogID) {
        super();
        this.account = account;
        this.loginSession = loginSession;
        this.devID = devID;
        this.channelNo = channelNo;
        this.streamType = streamType;
        this.dialogID = dialogID;
    }

    public String getAccount() {
        return account;
    }

    public void setAccount(String account) {
        this.account = account;
    }

    public String getLoginSession() {
        return loginSession;
    }

    public void setLoginSession(String loginSession) {
        this.loginSession = loginSession;
    }

    public String getDevID() {
        return devID;
    }

    public void setDevID(String devID) {
        this.devID = devID;
    }

    public int getChannelNo() {
        return channelNo;
    }

    public void setChannelNo(int channelNo) {
        this.channelNo = channelNo;
    }

    public String getStreamType() {
        return streamType;
    }

    public void setStreamType(String streamType) {
        this.streamType = streamType;
    }

    public String getDialogID() {
        return dialogID;
    }

    public void setDialogID(String dialogID) {
        this.dialogID = dialogID;
    }

}
