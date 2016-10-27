package bean;

public class TurnSubScribeAckBean {
	int code;
	String subscribeId;
	String detail;
	
	public int getCode() {
		return code;
	}
	public void setCode(int code) {
		this.code = code;
	}
	public String getSubscribeId() {
		return subscribeId;
	}
	public void setSubscribeId(String subscribeId) {
		this.subscribeId = subscribeId;
	}
	public String getDetail() {
		return detail;
	}
	public void setDetail(String detail) {
		this.detail = detail;
	}
	public TurnSubScribeAckBean(int code, String subscribeId, String detail) {
		super();
		this.code = code;
		this.subscribeId = subscribeId;
		this.detail = detail;
	}
	@Override
	public String toString() {
		return "TurnSubScribeAckBean [code=" + code + ", subscribeId=" + subscribeId + ", detail=" + detail + "]";
	}
	
}
