package bean;

public class TurnDisConnectBean {
	String sessionId;

	public String getSessionId() {
		return sessionId;
	}

	public void setSessionId(String sessionId) {
		this.sessionId = sessionId;
	}

	public TurnDisConnectBean(String sessionId) {
		super();
		this.sessionId = sessionId;
	}

	@Override
	public String toString() {
		return "TurnDisConnectBean [sessionId=" + sessionId + "]";
	}
	
}
