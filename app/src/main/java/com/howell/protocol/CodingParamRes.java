package com.howell.protocol;

public class CodingParamRes {

    private String result;
    private String FrameSize;
    private String FrameRate;
    private String RateType;
    private String BitRate;
    private String ImageQuality;
    private String AudioInput;
    private String Account;
    private String LoginSession;
    private String DevID;
    private int ChannelNo;
    private String StreamType;

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

    public String getResult() {
        return result;
    }

    public void setResult(String result) {
        this.result = result;
    }

    public String getFrameSize() {
        return FrameSize;
    }

    public void setFrameSize(String frameSize) {
        FrameSize = frameSize;
    }

    public String getFrameRate() {
        return FrameRate;
    }

    public void setFrameRate(String frameRate) {
        FrameRate = frameRate;
    }

    public String getRateType() {
        return RateType;
    }

    public void setRateType(String rateType) {
        RateType = rateType;
    }

    public String getBitRate() {
        return BitRate;
    }

    public void setBitRate(String bitRate) {
        BitRate = bitRate;
    }

    public String getImageQuality() {
        return ImageQuality;
    }

    public void setImageQuality(String imageQuality) {
        ImageQuality = imageQuality;
    }

    public String getAudioInput() {
        return AudioInput;
    }

    public void setAudioInput(String audioInput) {
        AudioInput = audioInput;
    }

    @Override
    public String toString() {
        return "CodingParamRes [result=" + result + ", FrameSize=" + FrameSize
                + ", FrameRate=" + FrameRate + ", RateType=" + RateType
                + ", BitRate=" + BitRate + ", ImageQuality=" + ImageQuality
                + ", AudioInput=" + AudioInput + "]";
    }
}
