package bean;

public class Subscribe {
	String sessionId;
	int dialogId;
	String deviceId;
	String mode;
	int is_sub;
	String startTime;
	String endTime;
	public Subscribe(){}
	
	public Subscribe(String sessionId,int dialogId,String deviceId,String mode,int is_sub){
		this.sessionId = sessionId;
		this.dialogId = dialogId;
		this.deviceId = deviceId;
		this.mode = mode;
		this.is_sub = is_sub;
	}
	
	
	
	public String getStartTime() {
		return startTime;
	}

	public void setStartTime(String startTime) {
		this.startTime = startTime;
	}

	public String getEndTime() {
		return endTime;
	}

	public void setEndTime(String endTime) {
		this.endTime = endTime;
	}

	public int getIs_sub() {
		return is_sub;
	}

	public void setIs_sub(int is_sub) {
		this.is_sub = is_sub;
	}

	public String getSessionId() {
		return sessionId;
	}
	public void setSessionId(String sessionId) {
		this.sessionId = sessionId;
	}
	public int getDialogId() {
		return dialogId;
	}
	public void setDialogId(int dialogId) {
		this.dialogId = dialogId;
	}
	public String getDeviceId() {
		return deviceId;
	}
	public void setDeviceId(String deviceId) {
		this.deviceId = deviceId;
	}
	public String getMode() {
		return mode;
	}
	public void setMode(String mode) {
		this.mode = mode;
	}
	
}
