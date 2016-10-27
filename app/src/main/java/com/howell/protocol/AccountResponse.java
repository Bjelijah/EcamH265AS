package com.howell.protocol;

public class AccountResponse {

    private String result;
    private String Username;
    private String Email;
    private String MobileTel;
    private String IDCard;
    private String Account;

    public String getResult() {
        return result;
    }

    public void setResult(String result) {
        this.result = result;
    }

    public String getUsername() {
        return Username;
    }

    public void setUsername(String username) {
        Username = username;
    }

    public String getEmail() {
        return Email;
    }

    public void setEmail(String email) {
        Email = email;
    }

    public String getMobileTel() {
        return MobileTel;
    }

    public void setMobileTel(String mobileTel) {
        MobileTel = mobileTel;
    }

    public String getIDCard() {
        return IDCard;
    }

    public void setIDCard(String iDCard) {
        IDCard = iDCard;
    }

    public String getAccount() {
        return Account;
    }

    public void setAccount(String account) {
        Account = account;
    }

    @Override
    public String toString() {
        return "AccountResponse [result=" + result + ", Username=" + Username
                + ", Email=" + Email + ", MobileTel=" + MobileTel + ", IDCard="
                + IDCard + ", Account=" + Account + "]";
    }

}
