package bean;

import java.util.Arrays;

public class TurnGetRecordedFileAckBean {
	int code;
	String detail;
	String deviceID;
	int channel;
	int recordFileCount;
	RecordedFile [] recordedFiles;
	
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

	public String getDeviceID() {
		return deviceID;
	}

	public void setDeviceID(String deviceID) {
		this.deviceID = deviceID;
	}

	public int getChannel() {
		return channel;
	}

	public void setChannel(int channel) {
		this.channel = channel;
	}

	public int getRecordFileCount() {
		return recordFileCount;
	}

	public void setRecordFileCount(int recordFileCount) {
		this.recordFileCount = recordFileCount;
	}

	public RecordedFile[] getRecordedFiles() {
		return recordedFiles;
	}

	public void setRecordedFiles(RecordedFile[] recordedFiles) {
		this.recordedFiles = recordedFiles;
	}

	public TurnGetRecordedFileAckBean(int code, String detail, String deviceID, int channel, int recordFileCount,
			RecordedFile[] recordedFiles) {
		super();
		this.code = code;
		this.detail = detail;
		this.deviceID = deviceID;
		this.channel = channel;
		this.recordFileCount = recordFileCount;
		this.recordedFiles = recordedFiles;
	}

	@Override
	public String toString() {
		return "TurnGetRecordedFileAckBean [code=" + code + ", detail=" + detail + ", deviceID=" + deviceID
				+ ", channel=" + channel + ", recordFileCount=" + recordFileCount + ", recordedFiles="
				+ Arrays.toString(recordedFiles) + "]";
	}

	public static class RecordedFile{
		String beginTime;
		String endTime;
		public String getBeginTime() {
			return beginTime;
		}
		public void setBeginTime(String beginTime) {
			this.beginTime = beginTime;
		}
		public String getEndTime() {
			return endTime;
		}
		public void setEndTime(String endTime) {
			this.endTime = endTime;
		}
		public RecordedFile(String beginTime, String endTime) {
			super();
			this.beginTime = beginTime;
			this.endTime = endTime;
		}
		@Override
		public String toString() {
			return "RecordedFile [beginTime=" + beginTime + ", endTime=" + endTime + "]";
		}
	}
}
