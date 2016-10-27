package com.howell.protocol;

import java.io.Serializable;
import java.util.ArrayList;

import com.howell.entityclass.Device;

@SuppressWarnings("serial")
public class LoginResponse implements Serializable{

    private String mResult;
    private String mLoginSession;
    private String mUsername;
    private String mAccount;
    
    public String getAccount() {
        return mAccount;
    }

    public void setAccount(String account) {
        mAccount = account;
    }

    private ArrayList<Device> nodeList;
    
    public void setLoginResponse(LoginResponse res){
    	this.mResult = res.getResult();
    	this.mLoginSession = res.getLoginSession();
    	this.mUsername = res.getUsername();
    	this.mAccount = res.getAccount();
    	this.nodeList = res.getNodeList();
    				
    }

    public String getResult() {
        return mResult;
    }

    public void setResult(String result) {
        mResult = result;
    }

    public String getLoginSession() {
        return mLoginSession;
    }

    public void setLoginSession(String loginSession) {
        mLoginSession = loginSession;
    }

    public ArrayList<Device> getNodeList() {
        return nodeList;
    }

    public void setNodeList(ArrayList<Device> nodeList) {
        this.nodeList = nodeList;
    }

    public String getUsername() {
        return mUsername;
    }

    public void setUsername(String username) {
        mUsername = username;
    }

	@Override
	public String toString() {
		return "LoginResponse [mResult=" + mResult + ", mLoginSession="
				+ mLoginSession + ", mUsername=" + mUsername + ", mAccount="
				+ mAccount + ", nodeList=" + nodeList + "]";
	}
    

}
