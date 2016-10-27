package com.howell.protocol;
/**
 * @author 霍之昊 
 *
 * 类说明
 */
public class GetPictureRes {
	private String result;
	private String pictureID;
	private String picture;
	public GetPictureRes(String result, String pictureID, String picture) {
		super();
		this.result = result;
		this.pictureID = pictureID;
		this.picture = picture;
	}
	public GetPictureRes() {
		super();
	}
	public String getResult() {
		return result;
	}
	public void setResult(String result) {
		this.result = result;
	}
	public String getPictureID() {
		return pictureID;
	}
	public void setPictureID(String pictureID) {
		this.pictureID = pictureID;
	}
	public String getPicture() {
		return picture;
	}
	public void setPicture(String picture) {
		this.picture = picture;
	}

}
