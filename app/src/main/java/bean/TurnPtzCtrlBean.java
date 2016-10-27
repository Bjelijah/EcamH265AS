package bean;

public class TurnPtzCtrlBean {
	String sessionId;
	String deviceId;
	int channel;
	int ptzCmd;
	int speed;
	int presetNo;
	public String getSessionId() {
		return sessionId;
	}
	public void setSessionId(String sessionId) {
		this.sessionId = sessionId;
	}
	public String getDeviceId() {
		return deviceId;
	}
	public void setDeviceId(String deviceId) {
		this.deviceId = deviceId;
	}
	public int getChannel() {
		return channel;
	}
	public void setChannel(int channel) {
		this.channel = channel;
	}
	public int getPtzCmd() {
		return ptzCmd;
	}
	public void setPtzCmd(int ptzCmd) {
		this.ptzCmd = ptzCmd;
	}
	public int getSpeed() {
		return speed;
	}
	public void setSpeed(int speed) {
		this.speed = speed;
	}
	public int getPresetNo() {
		return presetNo;
	}
	public void setPresetNo(int presetNo) {
		this.presetNo = presetNo;
	}
	public TurnPtzCtrlBean(String sessionId, String deviceId, int channel, int ptzCmd, int speed, int presetNo) {
		super();
		this.sessionId = sessionId;
		this.deviceId = deviceId;
		this.channel = channel;
		this.ptzCmd = ptzCmd;
		this.speed = speed;
		this.presetNo = presetNo;
	}
	@Override
	public String toString() {
		return "TurnPtzCtrlBean [sessionId=" + sessionId + ", deviceId=" + deviceId + ", channel=" + channel
				+ ", ptzCmd=" + ptzCmd + ", speed=" + speed + ", presetNo=" + presetNo + "]";
	}
}
