package com.howell.protocol;

public class VodSearchReq {

    private String Account;
    private String LoginSession;
    private String DevID;
    private int ChannelNo;
    private String StreamType;
    private String StartTime;
    private String EndTime;
    private String PageNo;
    private String SearchID;
    private String PageSize;

    public VodSearchReq(String account, String loginSession, String devID,
            int channelNo, String streamType, String startTime, String endTime,
            String pageNo, String searchID, String pageSize) {
        super();
        Account = account;
        LoginSession = loginSession;
        DevID = devID;
        ChannelNo = channelNo;
        StreamType = streamType;
        StartTime = startTime;
        EndTime = endTime;
        PageNo = pageNo;
        SearchID = searchID;
        PageSize = pageSize;
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

    public String getStartTime() {
        return StartTime;
    }

    public void setStartTime(String startTime) {
        StartTime = startTime;
    }

    public String getEndTime() {
        return EndTime;
    }

    public void setEndTime(String endTime) {
        EndTime = endTime;
    }

    public String getPageNo() {
        return PageNo;
    }

    public void setPageNo(String pageNo) {
        PageNo = pageNo;
    }

    public String getSearchID() {
        return SearchID;
    }

    public void setSearchID(String searchID) {
        SearchID = searchID;
    }

    public String getPageSize() {
        return PageSize;
    }

    public void setPageSize(String pageSize) {
        PageSize = pageSize;
    }

}
