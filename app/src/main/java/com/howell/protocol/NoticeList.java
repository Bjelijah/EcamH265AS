package com.howell.protocol;

import java.util.ArrayList;

/**
 * @author 霍之昊 
 *
 * 类说明
 */
public class NoticeList {
	private String iD;
	private String message;
	private String classification;
	private String time;
	private String status;
	private String sender;
	private String devID;
	private int channelNo;
	private String name;
	private ArrayList<String> pictureID;
	public NoticeList(String iD, String message, String classification,
			String time, String status, String sender, String devID,
			int channelNo, String name, ArrayList<String> pictureID) {
		super();
		this.iD = iD;
		this.message = message;
		this.classification = classification;
		this.time = time;
		this.status = status;
		this.sender = sender;
		this.devID = devID;
		this.channelNo = channelNo;
		this.name = name;
		this.pictureID = pictureID;
	}
	public NoticeList() {
		super();
	}
	public String getiD() {
		return iD;
	}
	public void setiD(String iD) {
		this.iD = iD;
	}
	public String getMessage() {
		return message;
	}
	public void setMessage(String message) {
		this.message = message;
	}
	public String getClassification() {
		return classification;
	}
	public void setClassification(String classification) {
		this.classification = classification;
	}
	public String getTime() {
		return time;
	}
	public void setTime(String time) {
		this.time = time;
	}
	public String getStatus() {
		return status;
	}
	public void setStatus(String status) {
		this.status = status;
	}
	public String getSender() {
		return sender;
	}
	public void setSender(String sender) {
		this.sender = sender;
	}
	public String getDevID() {
		return devID;
	}
	public void setDevID(String devID) {
		this.devID = devID;
	}
	public int getChannelNo() {
		return channelNo;
	}
	public void setChannelNo(int channelNo) {
		this.channelNo = channelNo;
	}
	public String getName() {
		return name;
	}
	public void setName(String name) {
		this.name = name;
	}
	public ArrayList<String> getPictureID() {
		return pictureID;
	}
	public void setPictureID(ArrayList<String> pictureID) {
		this.pictureID = pictureID;
	}
	@Override
	public String toString() {
		return "NoticeList [iD=" + iD + ", message=" + message
				+ ", classification=" + classification + ", time=" + time
				+ ", status=" + status + ", sender=" + sender + ", devID="
				+ devID + ", channelNo=" + channelNo + ", name=" + name + "]";
	}

}
