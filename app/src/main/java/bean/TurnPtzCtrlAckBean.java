package bean;

public class TurnPtzCtrlAckBean {
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
	public TurnPtzCtrlAckBean(int code, String detail) {
		super();
		this.code = code;
		this.detail = detail;
	}
	@Override
	public String toString() {
		return "TurnPtzCtrlAckBean [code=" + code + ", detail=" + detail + "]";
	}
	
}
