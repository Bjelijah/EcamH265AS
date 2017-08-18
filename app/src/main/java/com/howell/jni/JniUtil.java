package com.howell.jni;

public class JniUtil {
	static{
		System.loadLibrary("jpush");
		System.loadLibrary("hwtrans");
		System.loadLibrary("hwplay");
		System.loadLibrary("player_jni");
	}
	
	//yuv
	public static native void logEnable(boolean enable);

	public static native void YUVInit();			//初始化
	public static native void YUVDeinit();			//释放内存
	public static native void YUVSetCallbackObject(Object callbackObject,int flag);
	public static native void YUVSetCallbackMethodName(String methodStr,int flag);
	public static native void YUVLock();
	public static native void YUVUnlock();
	public static native void YUVSetEnable();//开始显示YUV数据
	public static native void YUVRenderY();			//渲染Y数据
	public static native void YUVRenderU();			//渲染U数据
	public static native void YUVRenderV();			//渲染V数据

	public static native void YUVsetData(byte [] data,int len,int w,int h);
	public static native void setH264Data(byte [] data,int len,int w,int h,int isI);
	public static native void setHWData(byte [] data,int len);
	public static native byte [] H264toHWStream(byte [] inH264,int inLen,int isI);


	public static native void nativeAudioInit();
	public static native void nativeAudioDeinit();
    public static native void nativeAudioSetCallbackObject(Object o,int flag);
    public static native void nativeAudioSetCallbackMethodName(String str,int flag);
    public static native void nativeAudioBPlayable();
    public static native void nativeAudioStop();		
   
	
	//test
	
	//net
	public static native void netInit();
	public static native void netDeinit();
	public static native boolean login(String ip);//no using
	public static native boolean loginOut();//no using
	public static native void setCallBackObj(Object o);
	public static native boolean readyPlayLive();
	public static native boolean readyPlayTurnLive(Object bean,int isPlayback);
	public static native boolean readyPlayPlayback();//fixme same to readyPlayLive
	public static native void playView();
	public static native void pauseAndPlayView();
	public static native int isPause();
	public static native void stopView();
	public static native long getFirstTimeStamp();
	public static native long getTimeStamp();
	public static native void getHI265Version();

	//transmission
	
	public static native void transInit();
	public static native void transSetCallBackObj(Object o,int flag);
	public static native void transSetCallbackMethodName(String methodName,int flag);
	public static native void transDeinit();
	public static native void transConnect(String ip,int port,boolean isUseSSL,int type,String id,String name,String pwd);
	public static native void transDisconnect();
	public static native void transSubscribe(String jsonStr,int jsonLen);
	public static native void transUnsubscribe();
	public static native void catchPic(String path);
	public static native int transGetStreamLenSomeTime();
	public static native void transGetCam(String jsonStr,int jsonLen);
	public static native void transGetRecordFiles(String jsonStr,int jsonLen);
	public static native void transSetCrt(String ca,String client,String key);
	public static native void transSetCrtPaht(String caPath,String clientPath,String keyPath);
	public static native void transPTZControl(String jsonStr,int jsonLen);

	//turn
	public static native void turnInputViewData(byte [] data,int len);
	



}
