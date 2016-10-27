package com.howell.protocol;

import java.io.Serializable;

/**
 * @author 霍之昊 
 *
 * 类说明
 */
public class AddDeviceJsonString implements Serializable{
	String id;
	String key;
	String serial_no;
	String company;
	public AddDeviceJsonString(String id, String key, String serial_no,
			String company) {
		super();
		this.id = id;
		this.key = key;
		this.serial_no = serial_no;
		this.company = company;
	}
	public String getId() {
		return id;
	}
	public void setId(String id) {
		this.id = id;
	}
	public String getKey() {
		return key;
	}
	public void setKey(String key) {
		this.key = key;
	}
	public String getSerial_no() {
		return serial_no;
	}
	public void setSerial_no(String serial_no) {
		this.serial_no = serial_no;
	}
	public String getCompany() {
		return company;
	}
	public void setCompany(String company) {
		this.company = company;
	}
	@Override
	public String toString() {
		return "AddDeviceJsonString [id=" + id + ", key=" + key
				+ ", serial_no=" + serial_no + ", company=" + company + "]";
	}
	
	
}
