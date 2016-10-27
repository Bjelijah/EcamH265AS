package com.howell.utils;

import java.util.Random;
import org.kobjects.base64.Base64;
import android.util.Log;

import com.howell.entityclass.Crypto;
import com.howell.entityclass.NodeDetails;
import com.howell.entityclass.StreamReqContext;
import com.howell.entityclass.StreamReqIceOpt;
import com.howell.protocol.ByeRequest;
import com.howell.protocol.ByeResponse;
import com.howell.protocol.GetNATServerRes;
import com.howell.protocol.InviteRequest;
import com.howell.protocol.InviteResponse;
import com.howell.protocol.NotifyNATResultReq;
import com.howell.protocol.NotifyNATResultRes;
import com.howell.protocol.QueryDeviceReq;
import com.howell.protocol.QueryDeviceRes;
import com.howell.protocol.SoapManager;
import com.howell.protocol.VodSearchRes;
import com.howell.activity.DeviceManager;
import com.howell.activity.PlayerActivity;
import com.howell.activity.PlayerActivity.PlayerHandler;

public class InviteUtils {
    public static int REQ_TIMEOUT = 10000;
    // private String account;
    private Random random;
    private SoapManager mSoapManager;
    // private String passwd;
    private String account, loginSession, devID, streamType, dialogID,
            SDPMessage;
    private int channelNo;
    private String local_sdp, remote_sdp;
    private long handle;
    private QueryDeviceRes queryDeviceRes;
//    private NodeDetails nodeDetail;
    
    private PlayerHandler handler;
	public static final Integer POSTERROR = 0x0006;
	
	private String NATType;
	private int methodType;
	
	//private int EStoreFlag;
	
	private boolean isQuit;
	
	private boolean isStartFinish;
	private long beg,end;
	
	private NodeDetails dev;
	
//	public int quickQuit;
	
	private DeviceManager mDeviceManager;
    // private long context; //Í¨¹ýjni´«»Øecam_stream_req_contextµØÖ·
    // private long decoder_handle;
//    static {
////         //System.loadLibrary("SDL");
//       // System.loadLibrary("hwplay");
////        // System.loadLibrary("ecamstream");
////        //System.loadLibrary("sdlplayer_jni");
//        //System.loadLibrary("player_jni");
//    }

    public InviteUtils(NodeDetails dev) {
        super();
        this.dev = dev;
        beg = -1;
        end = -1;
        mDeviceManager = DeviceManager.getInstance();
        NATType = "";
        handle = -1;
        isQuit = false;
        isStartFinish = false;
        random = new Random();
        handler = PlayerActivity.getHandler();
        mSoapManager = SoapManager.getInstance();
        this.account = mSoapManager.getLoginResponse().getAccount();
        // System.out.println(account);
        this.loginSession = mSoapManager.getLoginResponse().getLoginSession();
        this.devID = dev.getDevID();
        this.channelNo = dev.getChannelNo();
        this.streamType = "Sub";
        this.dialogID = String.valueOf(random.nextInt());
        
//        ArrayList<NodeDetails> node = mSoapManager.getNodeDetails();
//        if(node != null){
//	        for(int i = 0;i < node.size() ; i++){
//	        	if(node.get(i).getDevID().equals(devID)){
//	        		nodeDetail =  node.get(i);
//	        		break;
//	        	}
//	        }
//        }
        Log.e("InviteUtils", "account:"+account+",loginSession:"+loginSession+",devID:"+devID);
        if(!mDeviceManager.getMap().containsKey(dev.getDevID())){
        	mDeviceManager.addMember(dev);
        }
        methodType = mDeviceManager.getMap().get(dev.getDevID()).getMethodType();
//        queryDeviceRes = mSoapManager.getQueryDeviceRes(new QueryDeviceReq(
//                account, loginSession, devID));
//        Log.e("InviteUtils", queryDeviceRes.toString());
//        if(queryDeviceRes == null){
//        	loginSession = mSoapManager.getLoginResponse().getLoginSession();
//        	queryDeviceRes = mSoapManager.getQueryDeviceRes(new QueryDeviceReq(
//                    account, loginSession, devID));
//        }
    }
    
    private void getQueryDevice(){
    	 queryDeviceRes = mSoapManager.getQueryDeviceRes(new QueryDeviceReq(
                 account, loginSession, devID));
         Log.e("getQueryDevice", "getQueryDevice:"+queryDeviceRes.toString());
         if(queryDeviceRes == null){
         	loginSession = mSoapManager.getLoginResponse().getLoginSession();
         	queryDeviceRes = mSoapManager.getQueryDeviceRes(new QueryDeviceReq(
                     account, loginSession, devID));
         }
    }
    
//    public QueryDeviceRes getQueryDeviceRes() {
//		return queryDeviceRes;
//	}

	public VodSearchRes getVodSearchReq(int pageNo,String startTime,String endTime,int pageSize) {
        return mSoapManager.getVodSearchReq(account, loginSession, devID,
                channelNo, streamType , pageNo,startTime,endTime,pageSize);
    }

    // srteam = 0 :main ,stream =1 :sub
    public boolean InviteLive(final int stream) {
    	System.out.println("start invitelive");
        if (stream == 0) {
            streamType = "Main";
        } else if (stream == 1) {
            streamType = "Sub";
        }
        handle = createHandle(account, 0);
        if(handle == -1){
        	return false;
        }
        System.out.println("Invite handle success!!");
        StreamReqContext context = fillStreamReqContext(0, 0, 0, 0, methodType,stream);
        if(context == null ){
        	Log.e("false", "postMessage111");
        	if(!isQuit){
        		Log.e("false", "postMessage222");
        		handler.sendEmptyMessage(POSTERROR);
        	}
        	isStartFinish = true;
        	return false;
        }
        if (!invite(context,false)) {
        	Log.e("false", "postMessage333");
        	if(!isQuit){
        		Log.e("false", "postMessage444");
        		handler.sendEmptyMessage(POSTERROR);
        	}
        	System.out.println("invite fail");
        	isStartFinish = true;
            return false;
        }
        System.out.println("start live start");
        int ret = start(handle, context, REQ_TIMEOUT);
        isStartFinish = true;
        if(ret != 0) {
        	Log.e("false", "postMessage555");
        	System.out.println("PlayerActivity.isQuit :"+isQuit);
        	if(!isQuit){
        		Log.e("false", "postMessage666");
        		handler.sendEmptyMessage(POSTERROR);
        	}
        	return false; 
        }
        System.out.println("finish start");
        System.out.println("isStartFinish:"+isStartFinish+","+this.toString());
        getNATResult();
        new Thread(){
        	@Override
        	public void run() {
        		// TODO Auto-generated method stub
        		super.run();
        		System.out.println("thread isQuit:"+isQuit);
        		if(NATType.equals("UPnP") && !isQuit){
        			System.out.println("thread start"+methodType);
	        		try {
						sleep(3000);
						System.out.println("thread isQuit2:"+isQuit);
						//������߳̿���֮ǰ�Ѿ��˳� ��ִ���߳�
						if(isQuit) return;
						int count = getStreamCount(handle);
						System.out.println("thread start111 count"+count);
						if(count < 25){
							isStartFinish = false;
							mDeviceManager.getMap().get(devID).setMethodType(2);
							methodType = 2/*mDeviceManager.getMap().get(devID).getMethodType()*/;
							dialogID = String.valueOf(random.nextInt());
							System.out.println("thread start2222"+methodType);
							joinThread(handle);
							freeHandle(getHandle());
							bye(account,loginSession,devID,channelNo,streamType,dialogID);	
							handle = createHandle(account, 0);
						    if(handle == -1){
						        return ;
						    }
						    System.out.println("Invite handle success!!");
						    StreamReqContext context = fillStreamReqContext(0, 0, 0, 0, methodType,stream);
						    if(context == null){
						        Log.e("false", "postMessage111");
						        if(!isQuit){
						        	Log.e("false", "postMessage222");
						        	handler.sendEmptyMessage(POSTERROR);
						        }
						        isStartFinish = true;
						        return ;
						    }
						    if (!invite(context,false)) {
						        Log.e("false", "postMessage333");
						        if(!isQuit){
						        	Log.e("false", "postMessage444");
						        	handler.sendEmptyMessage(POSTERROR);
						        }
						        System.out.println("invite fail");
						        isStartFinish = true;
						        return ;
						    }
						    System.out.println("start live start");
						    int ret = InviteUtils.this.start(handle, context, REQ_TIMEOUT);
						    isStartFinish = true;
						    if(ret != 0) {
						    	Log.e("false", "postMessage555");
						        System.out.println("PlayerActivity.isQuit :"+isQuit);
						        if(!isQuit){
						        	Log.e("false", "postMessage666");
						        	handler.sendEmptyMessage(POSTERROR);
						        }
						        return ; 
						    }
						    System.out.println("finish start");
						    getNATResult();
						}
					} catch (InterruptedException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					}
        		}
        	}
        }.start();
        //new MyGetNATResultTask().execute();
        return catchError(ret);
    }

    public boolean InvitePlayback(long beg, long end,int stream) {
        handle = createHandle(account, 1);
        if(handle == -1){
        	return false;
        }
        Log.e("----------->>>", "beg = " + beg);
        Log.e("----------->>>", "end = " + end);
        StreamReqContext context = fillStreamReqContext(1, beg, end, 0 , methodType,stream);
        if(context == null ){
        	if(!isQuit)
        		handler.sendEmptyMessage(POSTERROR);
        	isStartFinish = true;
        	return false;
        }
        if (!invite(context,true)) {
            System.out.println("invite error");
            if(!isQuit)
            	handler.sendEmptyMessage(POSTERROR);
            isStartFinish = true;
            return false;
        }
        int ret = start(handle, context, REQ_TIMEOUT);
        isStartFinish = true;
        if(ret != 0) {
        	if(!isQuit)
        		handler.sendEmptyMessage(POSTERROR);
        	return false; 
        }
        getNATResult();
        //new MyGetNATResultTask().execute();
        return catchError(ret);
    }

    public boolean Replay(int isPlayBack,long beg, long end,int stream) {
        prepareReplay(isPlayBack,handle);
        StreamReqContext context = fillStreamReqContext(isPlayBack, beg, end, 1 , methodType,stream);
        if(context == null){
        	if(!isQuit)
        		handler.sendEmptyMessage(POSTERROR);
        	isStartFinish = true;
        	return false;
        }
        int ret = start(handle, context, REQ_TIMEOUT);
        isStartFinish = true;
        if(ret != 0) {
        	if(!isQuit)
        		handler.sendEmptyMessage(POSTERROR);
        	return false; 
        }
        return catchError(ret);
        // return false;
    }

    private StreamReqContext fillStreamReqContext(int isPlayBack, long beg,
            long end, int re_invite , int methodType,int stream) {
//        StreamReqIceOpt opt = new StreamReqIceOpt(1, "180.166.7.214", 34780,
//                "180.166.7.214", 34780, 0, "100", "100");
    	System.out.println("methodType:"+methodType);
    	String UpnpIP = "";
    	int UpnpPort = 0;
    	System.out.println("fillStreamReqContext11111111111");
    	if(dev == null){
    		Log.e("fillStreamReqContext", "nodeDetail == null");
    		getQueryDevice();
    		UpnpIP = queryDeviceRes.getUpnpIP();
	        UpnpPort = queryDeviceRes.getUpnpPort();
    	}else{
    		Log.e("fillStreamReqContext", "nodeDetail != null");
    		UpnpIP = dev.getUpnpIP();
	        UpnpPort = dev.getUpnpPort();
    	}
    	StreamReqContext streamReqContext = null;
    	GetNATServerRes res = mSoapManager.getLocalGetNATServerRes();
    	if(res == null){
    		Log.e("InviteUtils", "res == null");
    	}else{
    		Log.e("InviteUtils", res.toString());
    	}
    	try{
	        StreamReqIceOpt opt = new StreamReqIceOpt(1, res.getSTUNServerAddress(), res.getSTUNServerPort(),
	        		res.getTURNServerAddress(), res.getTURNServerPort(), 
	        		0, res.getTURNServerUserName(), res.getTURNServerPassword());
	        Log.e("InviteUtils", "1");
//    		StreamReqIceOpt opt = new StreamReqIceOpt(1, "222.191.251.186", 34780,
//                  "222.191.251.186", 34780, 0, "100", "100");
	        Crypto crypto = new Crypto(1);
	        Log.e("InviteUtils", "3");
	        if(methodType == 0){
//	        	streamReqContext = new StreamReqContext(isPlayBack,
//		                beg, end, re_invite, 1 << 1 | 1 << 2 ,UpnpIP , UpnpPort, opt);
	        	streamReqContext = new StreamReqContext(isPlayBack,
		                beg, end, re_invite, 1 << 1 | 1 << 2 ,UpnpIP , UpnpPort, opt,crypto,0,stream);
	        	Log.e("streamReqContext", "java stream:"+stream);
	        	Log.e("streamReqContext", "UpnpIP:"+UpnpIP+"UpnpPort:"+UpnpPort);
	        }else if(methodType == 2){
//	        	streamReqContext = new StreamReqContext(isPlayBack,
//		                beg, end, re_invite, 1 << 2 ,UpnpIP , UpnpPort, opt);
	        	streamReqContext = new StreamReqContext(isPlayBack,
		                beg, end, re_invite, 1 << 2 ,UpnpIP , UpnpPort, opt,crypto,0,stream);
	        	Log.e("streamReqContext", "java stream:"+stream);
	    		Log.e("streamReqContext", "UpnpIP:"+UpnpIP+"UpnpPort:"+UpnpPort);
	        }
    		
    	}catch (Exception e) {
			// TODO: handle exception
    		Log.e("", "fillStreamReqContext fail");
    		if(!isQuit){
        		handler.sendEmptyMessage(POSTERROR);
        	}
		}
    	System.out.println("fillStreamReqContext2222222222222");
        return streamReqContext;
    }
    
    private boolean catchError(int ret){
    	if(ret == 0 ){
        	return true;
        }else {
        	Log.e("false", "postMessage777");
        	if(!isQuit){
        		Log.e("false", "postMessage888");
        		handler.sendEmptyMessage(POSTERROR);
        	}
        	return false;
        }
    }
    // byeReq
    @SuppressWarnings("unused")
	public boolean bye(String account, String loginSession, String devID,
            int channelNo,String streamType,String dialogID) {
    	System.out.println("--------------bye1");
        try{
		ByeResponse byeRes = mSoapManager.getByeRes(new ByeRequest(account, loginSession, devID,
                channelNo, streamType, dialogID));
        System.out.println("--------------bye2");
        }catch(Exception e){
        	System.out.println("bye fail");
        }
//        if(byeRes == null){
//        	loginSession = mSoapManager.getLoginResponse().getLoginSession();
//        	byeRes = mSoapManager.getByeRes(new ByeRequest(account, loginSession, devID,
//                    channelNo, streamType, dialogID));
//        }
        return false;
    }

    private boolean invite(StreamReqContext streamReqContext ,boolean isPlayback) {
        System.out.println("start invite");
        Log.e("------------>>>>", "start invite");
        Log.e("1", streamReqContext.toString());
        local_sdp = prepareSDP(handle, streamReqContext);
        SDPMessage = Base64.encode(local_sdp.getBytes());
        System.out.println("SDPMessage:"+SDPMessage);
        // System.out.println(account+"\n"+loginSession+"\n"+devID+"\n"+channelNo+"\n"+streamType+"\n"+dialogID+"\n"+SDPMessage);
        try{
        	Log.e("---------->>>", "00000000000:"+dialogID);
	        InviteResponse inviteRes = mSoapManager.getIviteRes(new InviteRequest(account, loginSession,
	                devID, channelNo, streamType, dialogID, SDPMessage));
	        Log.e("---------->>>", "aaaaaaaaaaaaaa");
	        if(inviteRes == null){
	        	loginSession = mSoapManager.getLoginResponse().getLoginSession();
	        	inviteRes = mSoapManager.getIviteRes(new InviteRequest(account, loginSession,
	                    devID, channelNo, streamType, dialogID, SDPMessage));
	        }
	        Log.e("---------->>>", "bbbbbbbbbbbbbb");
	        if(!inviteRes.getResult().equals("OK")){
	        	return false;
	        }
	        // System.out.println(inviteRes.getResult());
	        remote_sdp = new String(Base64.decode(inviteRes.getSDPMessage()));
	        Log.e("---------->>>", "ccccccccccccc");
	        System.out.println(remote_sdp);
	        int ret = handleRemoteSDP(handle, streamReqContext, dialogID, remote_sdp);
	        if(ret == -1) return false;
	        if(isPlayback){
		        getSdpTime(handle);
		        beg = getBegSdpTime(handle);
		        end = getEndSdpTime(handle);
		        Log.e("---------->>>", beg+","+end);
	        }
	        Log.e("---------->>>", "ddddddddddddd ");
        }catch (Exception e) {
			// TODO: handle exception
        	Log.e("invite", "invite catch");
        	//return false;
		}
        // destoryLocalSDP(local_sdp);
        return true;
    }
    
    private void getNATResult(){
    	int method = getMethod(handle);
    	//String NATType = "";
    	switch(method){
	    	case 0:NATType = "Other";break;
	    	case 1:NATType = "TURN";break;
	    	case 2:NATType = "STUN";break;
	    	case 3:NATType = "UPnP";break;
    		default:NATType = "";break;
    	}
    	Log.e("thread getNATResult", account+","+loginSession+","+dialogID+","+NATType);
    	NotifyNATResultReq req = new NotifyNATResultReq(account,loginSession,dialogID,NATType);
    	try{
	    	NotifyNATResultRes res = mSoapManager.getNotifyNATResultRes(req);
			Log.e("getNATResult", res.getResult());
    	}catch(Exception e){
    		Log.e("","error");
    	}
    }
    
//    public class MyGetNATResultTask extends AsyncTask<Void, Integer, Void> {
//
//        @Override
//        protected Void doInBackground(Void... params) {
//            // TODO Auto-generated method stub
//        	getNATResult();
//            return null;
//        }
//    }
    
    
    public long getHandle(){
    	return handle;
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

	public boolean isStartFinish() {
		return isStartFinish;
	}

	public void setStartFinish(boolean isStartFinish) {
		this.isStartFinish = isStartFinish;
	}

	public String getNATType() {
		return NATType;
	}

	public void setNATType(String nATType) {
		NATType = nATType;
	}

	public String getAccount() {
		return account;
	}

	public void setAccount(String account) {
		this.account = account;
	}

	public String getDevID() {
		return devID;
	}

	public void setDevID(String devID) {
		this.devID = devID;
	}

	public String getStreamType() {
		return streamType;
	}

	public String getLoginSession() {
		return loginSession;
	}

	public void setLoginSession(String loginSession) {
		this.loginSession = loginSession;
	}

	public void setStreamType(String streamType) {
		this.streamType = streamType;
	}

	public String getDialogID() {
		return dialogID;
	}

	public void setDialogID(String dialogID) {
		this.dialogID = dialogID;
	}

	public int getChannelNo() {
		return channelNo;
	}

	public void setChannelNo(int channelNo) {
		this.channelNo = channelNo;
	}

	public boolean isQuit() {
		return isQuit;
	}

	public void setQuit(boolean isQuit) {
		this.isQuit = isQuit;
	}

//	public void setQuickQuit(int quickQuit){
//    	this.quickQuit = quickQuit;
//    }
    
	public void getStreamLen(int streamLen){
		PlayerActivity.showStreamLen(streamLen/1024*8);
		//Log.e("getStreamLen", (streamLen/1024*8)+"");
	}
	
	public native void playbackPause(long handle,boolean bPause);
	public native int getStreamCount(long handle);
	public native void joinThread(long handle);
    public native long createHandle(String account, int is_palyback);

    public native String prepareSDP(long handle,
            StreamReqContext streamReqContext);

    public native int handleRemoteSDP(long handle,
            StreamReqContext streamReqContext, String dialog_id,
            String remote_sdp);

    public native int start(long handle, StreamReqContext streamReqContext,
            int timeout_ms);

    public native void freeHandle(long handle);

    public native void prepareReplay(int isPlayBack,long handle);
    
    public native int getMethod(long handle);
    
    public native int getSdpTime(long handle);
    public native int getBegSdpTime(long handle);
    public native int getEndSdpTime(long handle);
    public native int setCatchPictureFlag(long handle,String path,int length);
    
    public native void testMainJni();
    
    /////////////////////////////////////////////////////////////
    /////////////////////////////////////////////////////////////
    /////////////////////////////////////////////////////////////
    

    
}
