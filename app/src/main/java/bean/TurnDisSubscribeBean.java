package bean;

public class TurnDisSubscribeBean {
	String sessionId;
	String subScribeId;
	public String getSessionId() {
		return sessionId;
	}
	public void setSessionId(String sessionId) {
		this.sessionId = sessionId;
	}
	public String getSubScribeId() {
		return subScribeId;
	}
	public void setSubScribeId(String subScribeId) {
		this.subScribeId = subScribeId;
	}
	public TurnDisSubscribeBean(String sessionId, String subScribeId) {
		super();
		this.sessionId = sessionId;
		this.subScribeId = subScribeId;
	}
	@Override
	public String toString() {
		return "TurnDisSubscribeBean [sessionId=" + sessionId + ", subScribeId=" + subScribeId + "]";
	}
	
}
