package com.howell.utils;

import android.media.AudioFormat;
import android.media.AudioRecord;
import android.media.MediaRecorder;
import android.util.Log;

/**
 * @author 霍之昊 
 *
 * 类说明
 */
public class TalkManager {
	//google录音类
	private AudioRecord audioRecord;  
	private int recBufSize;  
	private long handle;
	private int talkState;	//语音对讲当前状态
	public static final int TALKING = 1;
	public static final int STOP = 2;
	//语音对讲各个参数
	private static final int frequency = 8000;  
	private static final int channelConfiguration = AudioFormat.CHANNEL_CONFIGURATION_MONO;  
	private static final int audioEncoding = AudioFormat.ENCODING_PCM_16BIT;
	
	private native int setAudioData(long handle,byte[] buf ,int len);
	
	private RecordPlayThread thread;
	
	public TalkManager(long handle) {
		// TODO Auto-generated constructor stub
		initAudioRecord();
		this.handle = handle;
		this.talkState = STOP;
	}
	
	public int getTalkState() {
		return talkState;
	}

	//初始化语音对讲
	public void initAudioRecord(){
		recBufSize = AudioRecord.getMinBufferSize(frequency, channelConfiguration, audioEncoding);  
		audioRecord = new AudioRecord(MediaRecorder.AudioSource.MIC, frequency,channelConfiguration, audioEncoding, recBufSize);  
	}
	
	public synchronized void startTalk(){
		talkState = TALKING;
		thread = new RecordPlayThread();
		thread.start();
	}
	
	public synchronized void stopTalk(){
		talkState = STOP;
		try {
			thread.join();
			thread = null;
		} catch (InterruptedException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	
	public void release(){
		if(audioRecord != null){
			talkState = STOP;
			if(thread != null){
				try {
					thread.join();
					thread = null;
				} catch (InterruptedException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			}
			audioRecord.release();
		}
	}
	
	class RecordPlayThread extends Thread { 
		int realLen = 800;
		public void run() {  
			try {  
				byte[] buffer = new byte[recBufSize];  
				audioRecord.startRecording();//开始录制  
				while (talkState == TALKING) { 
		            //从MIC保存数据到缓冲区  
		            int bufferReadResult = audioRecord.read(buffer, 0, recBufSize);  
		            Log.e("","bufferReadResult:"+bufferReadResult);
		
		            int position = 0;
		            while(bufferReadResult > 0){
		            	int tmpBufLen = 0;
		            	if(bufferReadResult >= realLen){
		            		tmpBufLen = realLen;
		            	}else{
		            		tmpBufLen = bufferReadResult;
		            	}
		            	byte[] tmpBuf = new byte[tmpBufLen];  
		            	System.arraycopy(buffer, position, tmpBuf, 0, tmpBufLen);  
		            	//写入数据即播放  
			            int ret = setAudioData(handle,tmpBuf, tmpBufLen);
			            Log.e("","startTalk ret :"+ret);
			            position += realLen ;
			            bufferReadResult -= realLen;
			            Log.e("","send audio /position:"+position+" len:"+tmpBufLen +" bufferReadResult:"+bufferReadResult);
		            }
				}
			} catch (Throwable t) {  
				
      		}  
		}  
	}
}
