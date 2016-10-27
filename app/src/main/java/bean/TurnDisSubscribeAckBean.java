package bean;

public class TurnDisSubscribeAckBean {
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
	public TurnDisSubscribeAckBean(int code, String detail) {
		super();
		this.code = code;
		this.detail = detail;
	}
	@Override
	public String toString() {
		return "TurnDisSubscribeAckBean [code=" + code + ", detail=" + detail + "]";
	}
	
}
