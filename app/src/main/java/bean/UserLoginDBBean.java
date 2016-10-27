package bean;

public class UserLoginDBBean {

	int userNum;//当前用户 账号  
	String userName;
	String userPassword;
	
	
	public UserLoginDBBean(int userNum, String userName, String userPassword) {
		super();
		this.userNum = userNum;
		this.userName = userName;
		this.userPassword = userPassword;
	}
	public int getUserNum() {
		return userNum;
	}
	public void setUserNum(int userNum) {
		this.userNum = userNum;
	}
	public String getUserName() {
		return userName;
	}
	public void setUserName(String userName) {
		this.userName = userName;
	}
	public String getUserPassword() {
		return userPassword;
	}
	public void setUserPassword(String userPassword) {
		this.userPassword = userPassword;
	}
	@Override
	public String toString() {
		return "UserLoginDBBean [userNum=" + userNum + ", userName=" + userName + ", userPassword=" + userPassword
				+ "]";
	}
	
	
}
