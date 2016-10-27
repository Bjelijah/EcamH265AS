package com.howell.protocol;

import java.io.Serializable;


@SuppressWarnings("serial")
public class LoginRequest implements Serializable{
    private String mAccount;
    private String mPwdType;
    private String mPassword;
    private String mVersion;
    private String mIMEI;
    
    
    public LoginRequest(String account, String pwdType, String password,
            String version,String IEMI) {
        mAccount = account;
        mPwdType = pwdType;
        mPassword = password;
        mVersion = version;
        this.mIMEI = IEMI;
    }

    public String getIEMI() {
		return mIMEI;
	}

	public void setIEMI(String mIEMI) {
		this.mIMEI = mIEMI;
	}

	public String getAccount() {
        return mAccount;
    }

    public void setAccount(String account) {
        mAccount = account;
    }

    public String getPwdType() {
        return mPwdType;
    }

    public void setPwdType(String pwdType) {
        mPwdType = pwdType;
    }

    public String getPassword() {
        return mPassword;
    }

    public void setPassword(String password) {
        mPassword = password;
    }

    public String getVersion() {
        return mVersion;
    }

    public void setVersion(String version) {
        mVersion = version;
    }

    @Override
    public String toString() {
        return "LoginRequest [account=" + mAccount + ", pwdType=" + mPwdType
                + ", password=" + mPassword + ", version=" + mVersion + "]";
    }

}
