package com.howell.protocol;

public class CodingParamReq {

    private String Account;
    private String LoginSession;
    private String DevID;
    private int ChannelNo;
    private String StreamType;

    public CodingParamReq(String account, String loginSession, String devID,
            int channelNo, String streamType) {
        super();
        Account = account;
        LoginSession = loginSession;
        DevID = devID;
        ChannelNo = channelNo;
        StreamType = streamType;
    }

    public String getAccount() {
        return Account;
    }

    public void setAccount(String account) {
        Account = account;
    }

    public String getLoginSession() {
        return LoginSession;
    }

    public void setLoginSession(String loginSession) {
        LoginSession = loginSession;
    }

    public String getDevID() {
        return DevID;
    }

    public void setDevID(String devID) {
        DevID = devID;
    }

    public int getChannelNo() {
        return ChannelNo;
    }

    public void setChannelNo(int channelNo) {
        ChannelNo = channelNo;
    }

    public String getStreamType() {
        return StreamType;
    }

    public void setStreamType(String streamType) {
        StreamType = streamType;
    }
}
