package com.howell.protocol;

public class InviteResponse {
    private String result;
    private String dialogID;
    private String SDPMessage;

    public String getResult() {
        return result;
    }

    public void setResult(String result) {
        this.result = result;
    }

    public String getDialogID() {
        return dialogID;
    }

    public void setDialogID(String dialogID) {
        this.dialogID = dialogID;
    }

    public String getSDPMessage() {
        return SDPMessage;
    }

    public void setSDPMessage(String sDPMessage) {
        SDPMessage = sDPMessage;
    }

}
