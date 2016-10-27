package com.howell.entityclass;

import java.util.Arrays;

public class StreamReqContext {
    private int playback;
    private long beg = 10000;
    private long end = 10001;

    private int re_invite;
    private int method_bitmap;
    private String udp_addr;
    private int udp_port;
    private StreamReqIceOpt ice_opt;
    //private long[] times = new long[2];
    private Crypto crypto;
    private int channel;
    private int stream;//0注码流 1次码流
    
    public StreamReqContext() {
		super();
	}

	public StreamReqContext(int playback, long beg, long end, int re_invite,
            int method_bitmap, String udp_addr, int udp_port,
            StreamReqIceOpt ice_opt) {
        super();
        this.playback = playback;
        this.beg = beg;
        this.end = end;
        //times[0] = beg;
        //times[1] = end;
        this.re_invite = re_invite;
        this.method_bitmap = method_bitmap;
        this.udp_addr = udp_addr;
        this.udp_port = udp_port;
        this.ice_opt = ice_opt;
    }
	
    public StreamReqContext(int playback, long beg, long end, int re_invite,
			int method_bitmap, String udp_addr, int udp_port,
			StreamReqIceOpt ice_opt, Crypto crypto, int channel, int stream) {
		super();
		this.playback = playback;
		this.beg = beg;
		this.end = end;
		this.re_invite = re_invite;
		this.method_bitmap = method_bitmap;
		this.udp_addr = udp_addr;
		this.udp_port = udp_port;
		this.ice_opt = ice_opt;
		this.crypto = crypto;
		this.channel = channel;
		this.stream = stream;
	}

	public int getPlayback() {
        return playback;
    }

    public void setPlayback(int playback) {
        this.playback = playback;
    }

    public int getRe_invite() {
        return re_invite;
    }

    public void setRe_invite(int re_invite) {
        this.re_invite = re_invite;
    }

    public long getBeg() {
        return beg;
    }

    public void setBeg(long beg) {
        this.beg = beg;
    }

    public long getEnd() {
        return end;
    }

    public void setEnd(long end) {
        this.end = end;
    }

    public int getMethod_bitmap() {
        return method_bitmap;
    }

    public void setMethod_bitmap(int method_bitmap) {
        this.method_bitmap = method_bitmap;
    }

    public String getUdp_addr() {
        return udp_addr;
    }

    public void setUdp_addr(String udp_addr) {
        this.udp_addr = udp_addr;
    }

    public int getUdp_port() {
        return udp_port;
    }

    public void setUdp_port(int udp_port) {
        this.udp_port = udp_port;
    }

    public StreamReqIceOpt getIce_opt() {
        return ice_opt;
    }

    public void setIce_opt(StreamReqIceOpt ice_opt) {
        this.ice_opt = ice_opt;
    }

	@Override
	public String toString() {
		return "StreamReqContext [playback=" + playback + ", beg=" + beg
				+ ", end=" + end + ", re_invite=" + re_invite
				+ ", method_bitmap=" + method_bitmap + ", udp_addr=" + udp_addr
				+ ", udp_port=" + udp_port + ", ice_opt=" + ice_opt
				+ ", crypto=" + crypto + ", channel=" + channel + ", stream="
				+ stream + "]";
	}

}
