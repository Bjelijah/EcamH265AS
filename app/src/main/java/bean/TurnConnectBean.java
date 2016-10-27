package bean;

public class TurnConnectBean {
	int type;
	String deviceId;
	String userName;
	String passWord;
	public int getType() {
		return type;
	}
	public void setType(int type) {
		this.type = type;
	}
	public String getDeviceId() {
		return deviceId;
	}
	public void setDeviceId(String deviceId) {
		this.deviceId = deviceId;
	}
	public String getUserName() {
		return userName;
	}
	public void setUserName(String userName) {
		this.userName = userName;
	}
	public String getPassWord() {
		return passWord;
	}
	public void setPassWord(String passWord) {
		this.passWord = passWord;
	}
	public TurnConnectBean(int type, String deviceId, String userName, String passWord) {
		super();
		this.type = type;
		this.deviceId = deviceId;
		this.userName = userName;
		this.passWord = passWord;
	}
	public TurnConnectBean() {
		super();
	}
	@Override
	public String toString() {
		return "TurnConnectBean [type=" + type + ", deviceId=" + deviceId + ", userName=" + userName + ", passWord="
				+ passWord + "]";
	}
	
}
