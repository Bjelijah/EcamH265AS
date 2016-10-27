package bean;

public class MyFingerprintBeans {
	int fpID;
	String name;
	public int getFpID() {
		return fpID;
	}
	public void setFpID(int fpID) {
		this.fpID = fpID;
	}
	public String getName() {
		return name;
	}
	public void setName(String name) {
		this.name = name;
	}
	@Override
	public String toString() {
		return "MyFingerprintBeans [fpID=" + fpID + ", name=" + name + "]";
	}
	public MyFingerprintBeans(int fpID, String name) {
		super();
		this.fpID = fpID;
		this.name = name;
	}
	public MyFingerprintBeans(int fpID) {
		super();
		this.fpID = fpID;
	}
	public MyFingerprintBeans(String name) {
		super();
		this.name = name;
	}
	public MyFingerprintBeans() {
		super();
	}
	
}
