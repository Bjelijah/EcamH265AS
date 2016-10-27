package bean;

public class TurnSubScribe {
	String SessionId;
	String topic;
	media media;
	public String getSessionId() {
		return SessionId;
	}
	public void setSessionId(String sessionId) {
		SessionId = sessionId;
	}
	public String getTopic() {
		return topic;
	}
	public void setTopic(String topic) {
		this.topic = topic;
	}
	public media getMedia() {
		return media;
	}
	public void setMedia(media media) {
		this.media = media;
	}
	public TurnSubScribe(String sessionId, String topic, TurnSubScribe.media media) {
		super();
		SessionId = sessionId;
		this.topic = topic;
		this.media = media;
	}
	@Override
	public String toString() {
		return "TurnSubScribe [SessionId=" + SessionId + ", topic=" + topic + ", media=" + media + "]";
	}
	public static class media{
		int dialogId;
		meta meta;
		public int getDialogId() {
			return dialogId;
		}
		public void setDialogId(int dialogId) {
			this.dialogId = dialogId;
		}
		public meta getMeta() {
			return meta;
		}
		public void setMeta(meta meta) {
			this.meta = meta;
		}
		public media(int dialogId, TurnSubScribe.meta meta) {
			super();
			this.dialogId = dialogId;
			this.meta = meta;
		}
		@Override
		public String toString() {
			return "media [dialogId=" + dialogId + ", meta=" + meta + "]";
		}
	}
	
	public static class meta{
		String deviceId;
		String mode;
		int channel;
		int stream;
		String begin;
		String end;
		public String getDeviceId() {
			return deviceId;
		}
		public void setDeviceId(String deviceId) {
			this.deviceId = deviceId;
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
		public String getBegin() {
			return begin;
		}
		public void setBegin(String begin) {
			this.begin = begin;
		}
		public String getEnd() {
			return end;
		}
		public void setEnd(String end) {
			this.end = end;
		}
		public meta(String deviceId, String mode, int channel, int stream) {
			super();
			this.deviceId = deviceId;
			this.mode = mode;
			this.channel = channel;
			this.stream = stream;
		}
		public meta(String deviceId, String mode, int channel, int stream, String begin, String end) {
			super();
			this.deviceId = deviceId;
			this.mode = mode;
			this.channel = channel;
			this.stream = stream;
			this.begin = begin;
			this.end = end;
		}
		@Override
		public String toString() {
			return "meta [deviceId=" + deviceId + ", mode=" + mode + ", channel=" + channel + ", stream=" + stream
					+ ", begin=" + begin + ", end=" + end + "]";
		}
	}
}
