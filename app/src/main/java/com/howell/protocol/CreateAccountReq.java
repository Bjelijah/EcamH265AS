package com.howell.protocol;

public class CreateAccountReq {
	private String account;
	private String username;
	private String password;
	private String email;
	private String mobileTel;
	private int securityQuestion;
	private String securityAnswer;
	private String country;
	private String countryTelCode;
	private String iDCard;
	public CreateAccountReq(String account, String username, String password,
			String email, String mobileTel) {
		super();
		this.account = account;
		this.username = username;
		this.password = password;
		this.email = email;
		this.mobileTel = mobileTel;
	}
	
	public CreateAccountReq(String account, String username, String password,
			String email) {
		super();
		this.account = account;
		this.username = username;
		this.password = password;
		this.email = email;
	}
	
	public CreateAccountReq(String account, String password,String email) {
		super();
		this.account = account;
		this.password = password;
		this.email = email;
	}
	
	public String getAccount() {
		return account;
	}
	public void setAccount(String account) {
		this.account = account;
	}
	public String getUsername() {
		return username;
	}
	public void setUsername(String username) {
		this.username = username;
	}
	public String getPassword() {
		return password;
	}
	public void setPassword(String password) {
		this.password = password;
	}
	public String getEmail() {
		return email;
	}
	public void setEmail(String email) {
		this.email = email;
	}
	public String getMobileTel() {
		return mobileTel;
	}
	public void setMobileTel(String mobileTel) {
		this.mobileTel = mobileTel;
	}
	public int getSecurityQuestion() {
		return securityQuestion;
	}
	public void setSecurityQuestion(int securityQuestion) {
		this.securityQuestion = securityQuestion;
	}
	public String getSecurityAnswer() {
		return securityAnswer;
	}
	public void setSecurityAnswer(String securityAnswer) {
		this.securityAnswer = securityAnswer;
	}
	public String getCountry() {
		return country;
	}
	public void setCountry(String country) {
		this.country = country;
	}
	public String getCountryTelCode() {
		return countryTelCode;
	}
	public void setCountryTelCode(String countryTelCode) {
		this.countryTelCode = countryTelCode;
	}
	public String getiDCard() {
		return iDCard;
	}
	public void setiDCard(String iDCard) {
		this.iDCard = iDCard;
	}
	@Override
	public String toString() {
		return "CreateAccountReq [account=" + account + ", username="
				+ username + ", password=" + password + ", email=" + email
				+ ", mobileTel=" + mobileTel + ", securityQuestion="
				+ securityQuestion + ", securityAnswer=" + securityAnswer
				+ ", country=" + country + ", countryTelCode=" + countryTelCode
				+ ", iDCard=" + iDCard + "]";
	}
	
}
