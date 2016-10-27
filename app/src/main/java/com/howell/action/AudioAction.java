package com.howell.action;


import android.content.Context;
import android.media.AudioFormat;
import android.media.AudioManager;
import android.media.AudioTrack;
import android.util.Log;

import com.howell.jni.JniUtil;

public class AudioAction {
	private static AudioAction mInstance = null;
	private AudioAction(){}
	public static AudioAction getInstance(){
		if(mInstance==null){
			mInstance = new AudioAction();
		}
		return mInstance;
	}

	private AudioTrack mAudioTrack;//FIXME
	private byte[] mAudioData;
	private int mAudioDataLength;
	
	private AudioManager mAudioManager = null;
	
	public void initAudio(){
		int buffer_size = AudioTrack.getMinBufferSize(8000, AudioFormat.CHANNEL_OUT_MONO, AudioFormat.ENCODING_PCM_16BIT);
		mAudioTrack = new AudioTrack(AudioManager.STREAM_MUSIC, 8000, AudioFormat.CHANNEL_OUT_MONO, AudioFormat.ENCODING_PCM_16BIT, buffer_size*8, AudioTrack.MODE_STREAM);
		mAudioData = new byte[buffer_size*8];
	
		
		JniUtil.nativeAudioInit();
		JniUtil.nativeAudioSetCallbackObject(this, 0);
		JniUtil.nativeAudioSetCallbackMethodName("mAudioDataLength", 0);
		JniUtil.nativeAudioSetCallbackMethodName("mAudioData", 1);
		JniUtil.nativeAudioBPlayable();
	}

	public void playAudio(){
		if(mAudioTrack==null)return;		
		if(mAudioTrack.getPlayState() != AudioTrack.PLAYSTATE_PLAYING){	
			mAudioTrack.play();
		}
		JniUtil.nativeAudioBPlayable();
	}
	
	public void stopAudio(){
		Log.e("123", "~~~~~~~~~~~~~~audio stop");
		JniUtil.nativeAudioStop();
		if(mAudioTrack!=null){
			mAudioTrack.stop();	
		}
	}
	
	public void deInitAudio(){
	
		if(mAudioTrack != null){
			mAudioTrack.stop();
			mAudioTrack.release();
			mAudioTrack = null;
		}
		JniUtil.nativeAudioDeinit();
	}

	
	public void audioWrite() {
		Log.e("123", "audio write  mAudioDataLength="+mAudioDataLength+" dataLen="+mAudioData.length);
		
		Log.i("123","play state="+	mAudioTrack.getPlayState()+"     1:stop 2:pause 3:play");//1 stop 2 pause 3 play
		mAudioTrack.write(mAudioData,0,mAudioDataLength);
	}    
	
	public void audioSoundMute(Context context,boolean bMute){
		if (mAudioManager == null) {//should just do once otherwise it will never be Unmuted
			mAudioManager =  (AudioManager) context.getSystemService(Context.AUDIO_SERVICE);
		}
		mAudioManager.setStreamMute(AudioManager.STREAM_MUSIC, bMute);
	}
	
	
	
	
}
