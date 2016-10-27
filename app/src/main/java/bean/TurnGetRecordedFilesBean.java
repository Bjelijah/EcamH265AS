package bean;

public class TurnGetRecordedFilesBean {
	String deviceId;
	int channel;
	String begin;
	String end;
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
	public String getBegin() {
		return begin;
	}
	public void setBegin(String begin) {
		this.begin = begin;
	}
	public String getEnd() {
		return end;
	}
	public void setEnd(String end) {
		this.end = end;
	}
	public TurnGetRecordedFilesBean(String deviceId, int channel, String begin, String end) {
		super();
		this.deviceId = deviceId;
		this.channel = channel;
		this.begin = begin;
		this.end = end;
	}
	public TurnGetRecordedFilesBean() {
		super();
	}
	
}
