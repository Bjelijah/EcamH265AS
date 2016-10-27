package bean;

public class TurnConnectAckBean {
	int code;
	String sessionId;
	String detail;
	public String getDetail() {
		return detail;
	}
	public void setDetail(String detail) {
		this.detail = detail;
	}
	public int getCode() {
		return code;
	}
	public void setCode(int code) {
		this.code = code;
	}
	public String getSessionId() {
		return sessionId;
	}
	public void setSessionId(String sessionId) {
		this.sessionId = sessionId;
	}
	public TurnConnectAckBean(int code, String sessionId) {
		super();
		this.code = code;
		this.sessionId = sessionId;
	}
	public TurnConnectAckBean(int code, String sessionId, String detail) {
		super();
		this.code = code;
		this.sessionId = sessionId;
		this.detail = detail;
	}
	
}
