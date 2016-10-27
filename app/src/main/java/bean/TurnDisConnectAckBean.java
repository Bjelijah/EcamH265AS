package bean;

public class TurnDisConnectAckBean {
	int code;
	String detail;
	public int getCode() {
		return code;
	}
	public void setCode(int code) {
		this.code = code;
	}
	public String getDetail() {
		return detail;
	}
	public void setDetail(String detail) {
		this.detail = detail;
	}
	public TurnDisConnectAckBean(int code, String detail) {
		super();
		this.code = code;
		this.detail = detail;
	}
	public TurnDisConnectAckBean(int code) {
		super();
		this.code = code;
	}
	@Override
	public String toString() {
		return "TurnDisConnectAckBean [code=" + code + ", detail=" + detail + "]";
	}
	
}
