package bean;

import java.util.Arrays;

public class TurnSubScribeAckBean {
	int code;
	String subscribeId;
	String detail;

	int mediaDialogID;
	String deviceID;
	String mode;
	int channel;
	int stream;
	int [] videoResolution = new int[2];
	int videoFrameRate;
	String videoBitctrl;
	int videoBitrate;
	int videoGop;
	int videoCodec;  //0:VDEC_H264(264不加密) 1:VDEC_H264_ENCRYPT (264加密)  2:VDEC_HIS_H265(h265) 3:VDEC_HISH265_ENCRYPT(H265加密)

	int audioSamples;
	int audioChannels;
	int audioBitwidth;
	int audioCodec;//0:ADEC_AAC  1:ADEC_G711U

	boolean keyFrameIndex;



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

	public int getMediaDialogID() {
		return mediaDialogID;
	}

	public void setMediaDialogID(int mediaDialogID) {
		this.mediaDialogID = mediaDialogID;
	}

	public String getDeviceID() {
		return deviceID;
	}

	public void setDeviceID(String deviceID) {
		this.deviceID = deviceID;
	}

	public String getMode() {
		return mode;
	}

	public void setMode(String mode) {
		this.mode = mode;
	}

	public int getChannel() {
		return channel;
	}

	public void setChannel(int channel) {
		this.channel = channel;
	}

	public int getStream() {
		return stream;
	}

	public void setStream(int stream) {
		this.stream = stream;
	}

	public int[] getVideoResolution() {
		return videoResolution;
	}

	public void setVideoResolution(int[] videoResolution) {
		this.videoResolution = videoResolution;
	}

	public int getVideoFrameRate() {
		return videoFrameRate;
	}

	public void setVideoFrameRate(int videoFrameRate) {
		this.videoFrameRate = videoFrameRate;
	}

	public String getVideoBitctrl() {
		return videoBitctrl;
	}

	public void setVideoBitctrl(String videoBitctrl) {
		this.videoBitctrl = videoBitctrl;
	}

	public int getVideoBitrate() {
		return videoBitrate;
	}

	public void setVideoBitrate(int videoBitrate) {
		this.videoBitrate = videoBitrate;
	}

	public int getVideoGop() {
		return videoGop;
	}

	public void setVideoGop(int videoGop) {
		this.videoGop = videoGop;
	}

	public int getVideoCodec() {
		return videoCodec;
	}

	public void setVideoCodec(int videoCodec) {
		this.videoCodec = videoCodec;
	}

	public int getAudioSamples() {
		return audioSamples;
	}

	public void setAudioSamples(int audioSamples) {
		this.audioSamples = audioSamples;
	}

	public int getAudioChannels() {
		return audioChannels;
	}

	public void setAudioChannels(int audioChannels) {
		this.audioChannels = audioChannels;
	}

	public int getAudioBitwidth() {
		return audioBitwidth;
	}

	public void setAudioBitwidth(int audioBitwidth) {
		this.audioBitwidth = audioBitwidth;
	}

	public int getAudioCodec() {
		return audioCodec;
	}

	public void setAudioCodec(int audioCodec) {
		this.audioCodec = audioCodec;
	}

	public boolean isKeyFrameIndex() {
		return keyFrameIndex;
	}

	public void setKeyFrameIndex(boolean keyFrameIndex) {
		this.keyFrameIndex = keyFrameIndex;
	}

	public TurnSubScribeAckBean(int code, String subscribeId, String detail) {
		super();
		this.code = code;
		this.subscribeId = subscribeId;
		this.detail = detail;
	}

	public TurnSubScribeAckBean(){
		super();
	}


	@Override
	public String toString() {
		return "TurnSubScribeAckBean{" +
				"code=" + code +
				", subscribeId='" + subscribeId + '\'' +
				", detail='" + detail + '\'' +
				", mediaDialogID=" + mediaDialogID +
				", deviceID='" + deviceID + '\'' +
				", mode='" + mode + '\'' +
				", channel=" + channel +
				", stream=" + stream +
				", videoResolution=" + Arrays.toString(videoResolution) +
				", videoFrameRate=" + videoFrameRate +
				", videoBitctrl='" + videoBitctrl + '\'' +
				", videoBitrate=" + videoBitrate +
				", videoGop=" + videoGop +
				", videoCodec='" + videoCodec + '\'' +
				", audioSamples=" + audioSamples +
				", audioChannels=" + audioChannels +
				", audioBitwidth=" + audioBitwidth +
				", audioCodec='" + audioCodec + '\'' +
				", keyFrameIndex=" + keyFrameIndex +
				'}';
	}
}
