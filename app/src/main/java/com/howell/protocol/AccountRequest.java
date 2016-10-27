package com.howell.protocol;

public class AccountRequest {

    private String Account;
    private String LoginSession;

    public AccountRequest(String account, String loginSession) {
        super();
        Account = account;
        LoginSession = loginSession;
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
}
