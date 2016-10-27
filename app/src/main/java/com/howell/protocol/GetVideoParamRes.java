package com.howell.protocol;

public class GetVideoParamRes {
	private String result;
	private String videoStandard;
	private int rotationDegree;
	private int brightness;
    private int contrast;
    private int saturation;
    private int hue;
    private boolean infrared;
	public String getResult() {
		return result;
	}
	public void setResult(String result) {
		this.result = result;
	}
	public String getVideoStandard() {
		return videoStandard;
	}
	public void setVideoStandard(String videoStandard) {
		this.videoStandard = videoStandard;
	}
	public int getRotationDegree() {
		return rotationDegree;
	}
	public void setRotationDegree(int rotationDegree) {
		this.rotationDegree = rotationDegree;
	}
	public int getBrightness() {
		return brightness;
	}
	public void setBrightness(int brightness) {
		this.brightness = brightness;
	}
	public int getContrast() {
		return contrast;
	}
	public void setContrast(int contrast) {
		this.contrast = contrast;
	}
	public int getSaturation() {
		return saturation;
	}
	public void setSaturation(int saturation) {
		this.saturation = saturation;
	}
	public int getHue() {
		return hue;
	}
	public void setHue(int hue) {
		this.hue = hue;
	}
	public boolean isInfrared() {
		return infrared;
	}
	public void setInfrared(boolean infrared) {
		this.infrared = infrared;
	}
    
}
