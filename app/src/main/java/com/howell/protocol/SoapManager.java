package com.howell.protocol;

import java.io.Serializable;
import java.net.SocketTimeoutException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;

import org.ksoap2.SoapEnvelope;
import org.ksoap2.serialization.SoapObject;
import org.ksoap2.serialization.SoapSerializationEnvelope;

import org.ksoap2.transport.HttpTransportSE;

import com.howell.action.PlatformAction;
import com.howell.entityclass.Device;
import com.howell.entityclass.DeviceSharer;
import com.howell.entityclass.NodeDetails;
import com.howell.entityclass.VODRecord;
import com.howell.utils.AnalyzingDoNetOutput;
import com.howell.utils.IConst;
import com.howell.utils.PhoneConfig;
import com.howell.utils.SSLConection;
import com.howell.utils.SharedPreferencesUtil;

import android.content.Context;
import android.util.Log;

@SuppressWarnings("serial")
public class SoapManager implements Serializable ,IConst{

    private static String sNameSpace = "http://www.haoweis.com/HomeServices/MCU/";

    //https://www.haoweis.com:8807/HomeService/HomeMCUService.svc 
    //http://www.haoweis.com:8800/HomeService/HomeMCUService.svc?xsd=xsd0
//    private static String sEndPoint = "http://www.haoweis.com:8800/HomeService/HomeMCUService.svc?wsdl";
 
//    private static String sEndPoint = WSDL_URL;//FIXME
    private static String sEndPoint = null;//FIXME
    
    //private static String sSoapAction = null;

    private static SoapManager sInstance = new SoapManager();
    public static Context context;
    private LoginRequest mLoginRequest;
    private LoginResponse mLoginResponse;
    private GetNATServerRes mGetNATServerRes;

    private GetDeviceMatchingCodeRes mGetDeviceMatchingCodeRes;

    private ArrayList<NodeDetails> nodeDetails = new ArrayList<NodeDetails>();
    private static final int REPLAYTIME = 10 * 24 * 60 * 60 * 1000;

    private SoapManager() {

    }


	public static void initUrl(Context context){
    	
    	if (PlatformAction.getInstance().isTest()) {
			sEndPoint = WSDL_URL;
		}else{
			String ip = SharedPreferencesUtil.getLoginServiceIP(context);
	    	int port = SharedPreferencesUtil.getLoginServicePort(context);
			if (USING_WSDL_ENCRYPTION) {
				sEndPoint = "https://" + ip + ":" + port + "/HomeService/HomeMCUService.svc?wsdl";
			}else {
				sEndPoint = "http://" + ip + ":" + port + "/HomeService/HomeMCUService.svc?wsdl";
			}
		}
    	
    }
    
    public static SoapManager getInstance() {
        return sInstance;
    }

    public GetDeviceMatchingCodeRes getmGetDeviceMatchingCodeRes() {
		return mGetDeviceMatchingCodeRes;
	}

	public void setmGetDeviceMatchingCodeRes(
			GetDeviceMatchingCodeRes mGetDeviceMatchingCodeRes) {
		this.mGetDeviceMatchingCodeRes = mGetDeviceMatchingCodeRes;
	}

	public LoginRequest getLoginRequest() {
        return mLoginRequest;
    }
//
    public void setLoginRequest(LoginRequest loginRequest) {
        mLoginRequest = loginRequest;
    }

    public LoginResponse getLoginResponse() {
        return mLoginResponse;
    }

    public void setLoginResponse(LoginResponse loginResponse) {
        mLoginResponse = loginResponse;
    }

    public GetNATServerRes getLocalGetNATServerRes() {
		return mGetNATServerRes;
	}

	public void setGetNATServerRes(GetNATServerRes mGetNATServerRes) {
		this.mGetNATServerRes = mGetNATServerRes;
	}
	
	public ArrayList<NodeDetails> getNodeDetails() {
		return nodeDetails;
	}

	public SoapObject initEnvelopAndTransport(SoapObject rpc , String sSoapAction) {

        SoapSerializationEnvelope envelope = new SoapSerializationEnvelope(
                SoapEnvelope.VER10);

        envelope.bodyOut = rpc;
        envelope.dotNet = true;
        envelope.encodingStyle = "UTF-8";
        envelope.setOutputSoapObject(rpc);
       // com.howell.activity.FakeX509TrustManager.allowAllSSL();
		if(USING_WSDL_ENCRYPTION){
			SSLConection.allowAllSSL(context);
		}
        HttpTransportSE transport;
        Log.i("123", "sEndPoint="+sEndPoint);
		transport = new HttpTransportSE(sEndPoint);
		
		transport.debug = true;
		try {
		    transport.call(sSoapAction, envelope);
		} catch (SocketTimeoutException e) {
			Log.e("", "SocketTimeoutException");
		    e.printStackTrace();
		} catch (Exception e) {
			Log.e("", "Exception");
		    e.printStackTrace();
		}

        SoapObject soapObject = (SoapObject) envelope.bodyIn;
        return soapObject;
    }

//	MCU登录
    public LoginResponse getUserLoginRes(LoginRequest loginRequest) {
    	Log.e("SoapManager", "getUserLoginRes");
        setLoginRequest(loginRequest);
        mLoginResponse = new LoginResponse();
        SoapObject rpc = new SoapObject(sNameSpace, "userLoginReq");

        rpc.addProperty("Account", loginRequest.getAccount());
        rpc.addProperty("PwdType", loginRequest.getPwdType());
        rpc.addProperty("Password", loginRequest.getPassword());

        rpc.addProperty("Version", loginRequest.getVersion());
        
        SoapObject rpcChild = new SoapObject(sNameSpace, "MCUDev");
        
        rpcChild.addProperty("UUID",loginRequest.getIEMI());
        rpcChild.addProperty("Model",PhoneConfig.getPhoneModel());
        rpcChild.addProperty("NetworkOperator",PhoneConfig.getPhoneOperator(context));
        rpcChild.addProperty("NetType","Wifi");
        rpcChild.addProperty("Type", "CellPhone");
        rpcChild.addProperty("OSType", "Android");
        rpcChild.addProperty("OSVersion",PhoneConfig.getOSVersion());
        rpcChild.addProperty("Manufactory",PhoneConfig.getPhoneManufactory());
        rpcChild.addProperty("IEMI",loginRequest.getIEMI());
 
        rpc.addProperty("MCUDev",rpcChild);
        
        Log.e("123", "login UUID="+loginRequest.getIEMI());
        
//        Log.i("123", "rpc="+rpc.toString());
        
        SoapObject object = initEnvelopAndTransport(rpc,"http://www.haoweis.com/HomeServices/MCU/userLogin");

//        if (object == null) {
//            return null;
//        }
        try{
	        Object result = object.getProperty("result");
	        mLoginResponse.setResult(result.toString());
	        System.out.println("loginRes :"+result);
        }catch (Exception e) {
			// TODO: handle exception
		}
	    if (mLoginResponse.getResult().toString().equals("OK")) {
	    	try{
		        Object session = object.getProperty("LoginSession");
		        mLoginResponse.setLoginSession(session.toString());
		        System.out.println("login session:"+session);
	    	}catch (Exception e) {
				// TODO: handle exception
	    		mLoginResponse.setLoginSession("");
			}
	        try{
			    Object nodeList = object.getProperty("NodeList");
			    mLoginResponse.setNodeList(AnalyzingDoNetOutput.analyzing(nodeList.toString()));
	        }catch (Exception e) {
					// TODO: handle exception
	        	ArrayList<Device> list = new ArrayList<Device>();
	            mLoginResponse.setNodeList(list);
	        }
	        try{
		        Object username = object.getProperty("Username");
	            mLoginResponse.setUsername(username.toString());
	        }catch (Exception e) {
					// TODO: handle exception
	        	mLoginResponse.setUsername("");
			}
	        try{
	            Object Account = object.getProperty("Account");
	            mLoginResponse.setAccount(Account.toString());
	        }catch (Exception e) {
					// TODO: handle exception
	        	mLoginResponse.setAccount("");
	        }
	    }
	    System.out.println("LoginResponse.toString:"+mLoginResponse.toString());
        return mLoginResponse;
    }

//    public boolean reLogin(){
//    	System.out.println("relogin");
//    	System.out.println(mLoginRequest.toString());
//    	getUserLoginRes(mLoginRequest);
//    	return mLoginResponse.getResult().equals("OK") ? true : false;
//    } 
    
    
    // 设备状态查询
    public QueryDeviceRes getQueryDeviceRes(QueryDeviceReq req) {
    	Log.e("SoapManager", "getQueryDeviceRes");
        QueryDeviceRes queryDeviceRes = new QueryDeviceRes();
        SoapObject rpc = new SoapObject(sNameSpace, "queryDeviceReq");
        rpc.addProperty("Account", req.getAccount());
        rpc.addProperty("LoginSession", req.getLoginSession());
        rpc.addProperty("DevID", req.getDevID());
        SoapObject object = initEnvelopAndTransport(rpc,"http://www.haoweis.com/HomeServices/MCU/queryDevice");
        try{
	        Object result = object.getProperty("result");
	        
	        if(result.toString().equals("SessionExpired")){
	        	mLoginResponse = getUserLoginRes(mLoginRequest);
	        	req.setLoginSession(mLoginResponse.getLoginSession());
	        	return getQueryDeviceRes(req);
	        }
	        
	        queryDeviceRes.setResult(result.toString());
	        //System.out.println("queryDeviceRes:"+queryDeviceRes.getResult());
	        SoapObject NodeList = (SoapObject)object.getProperty("NodeList");
	        System.out.println("queryDevice NodeList:"+NodeList.toString());
	        //System.out.println(req.getDevID().toString());
	        if(req.getDevID() != null){
		        SoapObject NodeDetails = (SoapObject)NodeList.getProperty("NodeDetails");
		        System.out.println("NodeDetails:"+NodeDetails.toString());
		        Object DevID = NodeDetails.getProperty("DevID");
		        queryDeviceRes.setDevID(DevID.toString());
		        
		        Object ChannelNo = NodeDetails.getProperty("ChannelNo");
		        queryDeviceRes.setChannelNo(Integer.valueOf(ChannelNo.toString()));
		       
		        Object Name = NodeDetails.getProperty("Name");
		        queryDeviceRes.setName(Name.toString());
		        
		        Object OnLine = NodeDetails.getProperty("OnLine");
		        queryDeviceRes.setOnLine(Boolean.parseBoolean(OnLine.toString()));
		        
		        Object PtzFlag = NodeDetails.getProperty("PtzFlag");
		        queryDeviceRes.setPtzFlag(Boolean.parseBoolean(PtzFlag.toString()));
		        
		        Object SecurityArea = NodeDetails.getProperty("SecurityArea");
		        queryDeviceRes.setSecurityArea(Integer.valueOf(SecurityArea.toString()));
		        
		        Object EStoreFlag = NodeDetails.getProperty("EStoreFlag");
		        queryDeviceRes.seteStoreFlag(Boolean.parseBoolean(EStoreFlag.toString()));
		       
		        Object UpnpIP = NodeDetails.getProperty("UpnpIP");
		        queryDeviceRes.setUpnpIP(UpnpIP.toString());
		        
		        Object UpnpPort = NodeDetails.getProperty("UpnpPort");
		        queryDeviceRes.setUpnpPort(Integer.valueOf(UpnpPort.toString()));
		        
		        Object DevVer = NodeDetails.getProperty("DevVer");
		        queryDeviceRes.setDevVer(DevVer.toString());
		        
		        Object CurVideoNum = NodeDetails.getProperty("CurVideoNum");
		        queryDeviceRes.setCurVideoNum(Integer.valueOf(CurVideoNum.toString()));
		       
		        Object LastUpdated = NodeDetails.getProperty("LastUpdated");
		        queryDeviceRes.setLastUpdated(LastUpdated.toString());
		        
		        Object SMSSubscribedFlag = NodeDetails.getProperty("SMSSubscribedFlag");
		        queryDeviceRes.setsMSSubscribedFlag(Integer.valueOf(SMSSubscribedFlag.toString()));
		        
		        Object EMailSubscribedFlag = NodeDetails.getProperty("EMailSubscribedFlag");
		        queryDeviceRes.seteMailSubscribedFlag(Integer.valueOf(EMailSubscribedFlag.toString()));
		       
		        Object SharingFlag = NodeDetails.getProperty("SharingFlag");
		        queryDeviceRes.setSharingFlag(Integer.valueOf(SharingFlag.toString()));
		        
		        Object ApplePushSubscribedFlag = NodeDetails.getProperty("ApplePushSubscribedFlag");
		        queryDeviceRes.setApplePushSubscribedFlag(Integer.valueOf(ApplePushSubscribedFlag.toString()));
		        
		        Object AndroidPushSubscribedFlag = NodeDetails.getProperty("AndroidPushSubscribedFlag");
		        queryDeviceRes.setAndroidPushSubscribedFlag(Integer.valueOf(AndroidPushSubscribedFlag.toString()));
	        }else{
	        	if(nodeDetails.size() > 0)
	        		nodeDetails.clear();
	        	for(int i = 0 ;i<NodeList.getPropertyCount();i++){
	        		//System.out.println(NodeList.getProperty(i).toString());
	        		SoapObject NodeDetails = (SoapObject)NodeList.getProperty(i);
	        		//System.out.println(NodeDetails.toString());
	        		NodeDetails node = new NodeDetails(NodeDetails.getProperty("DevID").toString(),Integer.valueOf(NodeDetails.getProperty("ChannelNo").toString())
	        				,NodeDetails.getProperty("Name").toString(),Boolean.parseBoolean(NodeDetails.getProperty("OnLine").toString()),Boolean.parseBoolean(NodeDetails.getProperty("PtzFlag").toString())
	        				,Integer.valueOf(NodeDetails.getProperty("SecurityArea").toString()),Boolean.parseBoolean(NodeDetails.getProperty("EStoreFlag").toString()),NodeDetails.getProperty("UpnpIP").toString()
	        				,Integer.valueOf(NodeDetails.getProperty("UpnpPort").toString()),NodeDetails.getProperty("DevVer").toString(),Integer.valueOf(NodeDetails.getProperty("CurVideoNum").toString())
	        				,NodeDetails.getProperty("LastUpdated").toString(),Integer.valueOf(NodeDetails.getProperty("SMSSubscribedFlag").toString()),Integer.valueOf(NodeDetails.getProperty("EMailSubscribedFlag").toString())
	        				,Integer.valueOf(NodeDetails.getProperty("SharingFlag").toString()),Integer.valueOf(NodeDetails.getProperty("ApplePushSubscribedFlag").toString()),Integer.valueOf(NodeDetails.getProperty("AndroidPushSubscribedFlag").toString())
	        				,Integer.valueOf(NodeDetails.getProperty("InfraredFlag").toString()),Integer.valueOf(NodeDetails.getProperty("WirelessFlag").toString()));
	        		//System.out.println("node : "+i+" :"+node.toString());
	        		if(node.getWirelessFlag() == 0){
	        			node.setIntensity(0);
	        		}else if(node.getWirelessFlag() == 1){
	        			node.setIntensity(0);
	        		}else{
		        		SoapObject wirelessNetwork = (SoapObject)NodeDetails.getProperty("WirelessNetwork");
		        		System.out.println(wirelessNetwork.toString());
		        		Object intensity = wirelessNetwork.getProperty("Intensity");
		        		System.out.println(intensity.toString());
		        		node.setIntensity(Integer.valueOf(intensity.toString()));
	        		}
	        		nodeDetails.add(node);
	        	}
	        }
//	        Object NodeList = object.getProperty("NodeList");
//	        queryDeviceRes.setNodeDetails(new NodeDetails(AnalyzingDoNetOutput
//	                .analyzingIPandPort(NodeList.toString())[0].substring(7),
//	                Integer.parseInt(AnalyzingDoNetOutput
//	                        .analyzingIPandPort(NodeList.toString())[1]
//	                        .substring(9))));
	        //System.out.println("queryDevice:"+queryDeviceRes.toString());
        }catch (Exception e) {
			// TODO: handle exception
        	System.out.println("queryDevice:Crash");
		}
        return queryDeviceRes;
    }

    // 请求视频流
    public InviteResponse getIviteRes(InviteRequest req) {
    	Log.e("SoapManager", "getIviteRes");
        InviteResponse inviteRes = new InviteResponse();
        SoapObject rpc = new SoapObject(sNameSpace, "inviteReq");
        rpc.addProperty("Account", req.getAccount());
        rpc.addProperty("LoginSession", req.getLoginSession());
        rpc.addProperty("DevID", req.getDevID());
        rpc.addProperty("ChannelNo", req.getChannelNo());
        rpc.addProperty("StreamType", req.getStreamType());
        rpc.addProperty("DialogID", req.getDialogID());
        rpc.addProperty("SDPMessage", req.getSDPMessage());
        SoapObject object = initEnvelopAndTransport(rpc,"http://www.haoweis.com/HomeServices/MCU/invite");
        try{
	        Object result = object.getProperty("result");
	        if(result.toString().equals("SessionExpired")){
	        	mLoginResponse = getUserLoginRes(mLoginRequest);
	        	req.setLoginSession(mLoginResponse.getLoginSession());
	        	return getIviteRes(req);
	        }
	        inviteRes.setResult(result.toString());
	        Object dialogID = object.getProperty("DialogID");
	        inviteRes.setDialogID(dialogID.toString());
	        System.out.println("dialogID.toString():"+dialogID.toString());
	        Object SDPMessage = object.getProperty("SDPMessage");
	        inviteRes.setSDPMessage(SDPMessage.toString());
        }catch (Exception e) {
			// TODO: handle exception
		}
        return inviteRes;
    }
    
//    视频流断开
    public ByeResponse getByeRes(ByeRequest req) {
    	Log.e("SoapManager", "getByeRes");
        ByeResponse byeRes = new ByeResponse();
        SoapObject rpc = new SoapObject(sNameSpace, "byeReq");
        rpc.addProperty("Account", req.getAccount());
        rpc.addProperty("LoginSession", req.getLoginSession());
        rpc.addProperty("DevID", req.getDevID());
        rpc.addProperty("ChannelNo", req.getChannelNo());
        rpc.addProperty("StreamType", req.getStreamType());
        rpc.addProperty("DialogID", req.getDialogID());
        SoapObject object = initEnvelopAndTransport(rpc,"http://www.haoweis.com/HomeServices/MCU/bye");
        try{
	        Object result = object.getProperty("result");
	        if(result.toString().equals("SessionExpired")){
	        	mLoginResponse = getUserLoginRes(mLoginRequest);
	        	req.setLoginSession(mLoginResponse.getLoginSession());
	        	return getByeRes(req);
	        }
	        byeRes.setResult(result.toString());
        }catch (Exception e) {
			// TODO: handle exception
		}
        return byeRes;
    }

//    查询账户信息
    public AccountResponse getAccountRes(AccountRequest req) {
    	Log.e("SoapManager", "getAccountRes");
        AccountResponse accountRes = new AccountResponse();
        SoapObject rpc = new SoapObject(sNameSpace, "getAccountReq");
        rpc.addProperty("Account", req.getAccount());
        rpc.addProperty("LoginSession", req.getLoginSession());

        SoapObject object = initEnvelopAndTransport(rpc,"http://www.haoweis.com/HomeServices/MCU/getAccount");
        try{
	        Log.e("-------------->>>>>", "object = " + object.toString());
	        
	        Object result = object.getProperty("result");
	        if(result.toString().equals("SessionExpired")){
	        	mLoginResponse = getUserLoginRes(mLoginRequest);
	        	req.setLoginSession(mLoginResponse.getLoginSession());
	        	return getAccountRes(req);
	        }
	        accountRes.setResult(result.toString());
        }catch (Exception e) {
			// TODO: handle exception
        	accountRes.setResult("");
		}
        try{
	        Object Username = object.getProperty("Username");
	        accountRes.setUsername(Username.toString());
        }catch (Exception e) {
			// TODO: handle exception
        	accountRes.setUsername("");
		}
        try{
	        Object Email = object.getProperty("Email");
	        accountRes.setEmail(Email.toString());
        }catch (Exception e) {
			// TODO: handle exception
        	accountRes.setEmail("");
		}
        try{
	        Object MobileTel = object.getProperty("MobileTel");
	        accountRes.setMobileTel(MobileTel.toString());
        }catch (Exception e) {
			// TODO: handle exception
        	accountRes.setMobileTel("");
		}
        try{
	        Object Account = object.getProperty("Account");
	        accountRes.setAccount(Account.toString());
        }catch (Exception e) {
			// TODO: handle exception
        	accountRes.setAccount("");
		}
        return accountRes;
    }

//    摄像头音视频流编码参数查询
    public CodingParamRes getCodingParamRes(CodingParamReq req) {
    	Log.e("SoapManager", "getCodingParamRes");
        CodingParamRes res = new CodingParamRes();
        SoapObject rpc = new SoapObject(sNameSpace, "getCodingParamReq");
        rpc.addProperty("Account", req.getAccount());
        rpc.addProperty("LoginSession", req.getLoginSession());
        rpc.addProperty("DevID", req.getDevID());
        rpc.addProperty("ChannelNo", req.getChannelNo());
        rpc.addProperty("StreamType", req.getStreamType());

        res.setAccount(req.getAccount());
        res.setLoginSession(req.getLoginSession());
        res.setDevID(req.getDevID());
        res.setChannelNo(req.getChannelNo());
        res.setStreamType(req.getStreamType());

        SoapObject object = initEnvelopAndTransport(rpc,"http://www.haoweis.com/HomeServices/MCU/getCodingParam");
        try{
	        Object result = object.getProperty("result");
	        if(result.toString().equals("SessionExpired")){
	        	mLoginResponse = getUserLoginRes(mLoginRequest);
	        	req.setLoginSession(mLoginResponse.getLoginSession());
	        	return getCodingParamRes(req);
	        }
	        res.setResult(result.toString());
	        System.out.println("getCodingParamRes:"+result.toString());
	        Object FrameSize = object.getProperty("FrameSize");
	        res.setFrameSize(FrameSize.toString());
	
	        Object FrameRate = object.getProperty("FrameRate");
	        res.setFrameRate(FrameRate.toString());
	
	        Object RateType = object.getProperty("RateType");
	        res.setRateType(RateType.toString());
	
	        Object BitRate = object.getProperty("BitRate");
	        res.setBitRate(BitRate.toString());
	
	        Object ImageQuality = object.getProperty("ImageQuality");
	        res.setImageQuality(ImageQuality.toString());
	
	        Object AudioInput = object.getProperty("AudioInput");
	        res.setAudioInput(AudioInput.toString());
        }catch (Exception e) {
			// TODO: handle exception
		}
        return res;
    }

//    public void setCodingParamFrameSize(CodingParamRes res) {
//        SoapObject rpc = new SoapObject(sNameSpace, "setCodingParamReq");
//        rpc.addProperty("Account", res.getAccount());
//        rpc.addProperty("LoginSession", res.getLoginSession());
//        rpc.addProperty("DevID", res.getDevID());
//        rpc.addProperty("ChannelNo", res.getChannelNo());
//        rpc.addProperty("StreamType", res.getStreamType());
//        rpc.addProperty("FrameSize", res.getFrameSize());
//        rpc.addProperty("FrameRate", res.getFrameRate());
//        rpc.addProperty("RateType", res.getRateType());
//        rpc.addProperty("BitRate", res.getBitRate());
//        rpc.addProperty("ImageQuality", res.getImageQuality());
//        rpc.addProperty("AudioInput", res.getAudioInput());
//
//        SoapObject object = initEnvelopAndTransport(rpc);
//        Object result = object.getProperty("result");
//        Log.e("-----111----->>>>", "result = " + result.toString());
//    }
//
//    public void setCodingParamImageQuality(CodingParamRes res) {
//        SoapObject rpc = new SoapObject(sNameSpace, "setCodingParamReq");
//        rpc.addProperty("Account", res.getAccount());
//        rpc.addProperty("LoginSession", res.getLoginSession());
//        rpc.addProperty("DevID", res.getDevID());
//        rpc.addProperty("ChannelNo", res.getChannelNo());
//        rpc.addProperty("StreamType", res.getStreamType());
//        rpc.addProperty("FrameSize", res.getFrameSize());
//        rpc.addProperty("FrameRate", res.getFrameRate());
//        rpc.addProperty("RateType", res.getRateType());
//        rpc.addProperty("BitRate", res.getBitRate());
//        rpc.addProperty("ImageQuality", res.getImageQuality());
//        rpc.addProperty("AudioInput", res.getAudioInput());
//        SoapObject object = initEnvelopAndTransport(rpc);
//        Object result = object.getProperty("result");
//        Log.e("-----222----->>>>", "result = " + result.toString());
//    }

//		摄像头音视频编码参数设置 
    public void setCodingParam(CodingParamRes res) {
    	Log.e("SoapManager", "setCodingParam");
        SoapObject rpc = new SoapObject(sNameSpace, "setCodingParamReq");
        rpc.addProperty("Account", res.getAccount());
        rpc.addProperty("LoginSession", res.getLoginSession());
        rpc.addProperty("DevID", res.getDevID());
        rpc.addProperty("ChannelNo", res.getChannelNo());
        rpc.addProperty("StreamType", res.getStreamType());
        rpc.addProperty("FrameSize", res.getFrameSize());
        rpc.addProperty("FrameRate", res.getFrameRate());
        rpc.addProperty("RateType", res.getRateType());
        rpc.addProperty("BitRate", res.getBitRate());
        rpc.addProperty("ImageQuality", res.getImageQuality());
        rpc.addProperty("AudioInput", res.getAudioInput());

        
        Log.e("123", "set coding param   rpc="+rpc.toString());
        
        
        SoapObject object = initEnvelopAndTransport(rpc,"http://www.haoweis.com/HomeServices/MCU/setCodingParam");
        try{
	        Object result = object.getProperty("result");
//	        if(result.toString().equals("SessionExpired")){
//	        	reLogin();
//	        }
	        Log.e("-----111----->>>>", "result = " + result.toString());
        }catch (Exception e) {
			// TODO: handle exception
		}
    }
    
//    运动侦测参数查询
    public VMDParamRes getVMDParam(VMDParamReq req) {
    	Log.e("SoapManager", "getVMDParam");
    	VMDParamRes res = new VMDParamRes();
        SoapObject rpc = new SoapObject(sNameSpace, "getVMDParamReq");
        rpc.addProperty("Account", req.getAccount());
        rpc.addProperty("LoginSession", req.getLoginSession());
        rpc.addProperty("DevID", req.getDevID());
        rpc.addProperty("ChannelNo", req.getChannelNo());

        res.setAccount(req.getAccount());
        res.setLoginSession(req.getLoginSession());
        res.setDevID(req.getDevID());
        res.setChannelNo(req.getChannelNo());

        SoapObject object = initEnvelopAndTransport(rpc,"http://www.haoweis.com/HomeServices/MCU/getVMDParam");
        try{
        	Log.v("sopa","object: "+object);
	        Object result = object.getProperty("result");
	        if(result.toString().equals("SessionExpired")){
	        	mLoginResponse = getUserLoginRes(mLoginRequest);
	        	req.setLogin_session_(mLoginResponse.getLoginSession());
	        	return getVMDParam(req);
	        }
	        res.setResult(result.toString());
	
	        Object v = object.getProperty("Enabled");
	        res.setEnabled(Boolean.parseBoolean(v.toString()));
	        
	        v = object.getProperty("Sensitivity");
	        if (v!=null) {
	        	res.setSensitivity(Integer.valueOf(v.toString()));
	        }
	        Object v1 = object.getProperty("RowGranularity");
	        v = object.getProperty("ColumnGranularity");
	//        if (v!=null && v1!=null) {
	//        	int row = Integer.valueOf(v1.toString());
	//        	int col = Integer.valueOf(v.toString());
	//        	res.setRowColumn(row,col);
	//        }
	        /*
	        res.setStartTriggerTime(Integer.valueOf(object.getProperty("StartTriggerTime").toString()));
	        res.setEndTriggerTime(Integer.valueOf(object.getProperty("EndTriggerTime").toString()));
	         */
	        Object grids = object.getProperty("Grid");
	        Log.v("soap","vmd get grids: "+grids);
	        //String[] s = new String[res.getRows()];
	        //analyzingGrids(grids.toString(),s);
	        //res.setGrids(s);
        }catch (Exception e) {
			// TODO: handle exception
		}
        
        return res;
    }
    
//    运动侦测参数设置
    public void setVMDParam(VMDParamRes res) {
    	//TODO
    	
    	SoapObject rpc = new SoapObject(sNameSpace, "setVMDParamReq");
        rpc.addProperty("Account", res.getAccount());
        rpc.addProperty("LoginSession", res.getLoginSession());
        rpc.addProperty("DevID", res.getDevID());
        rpc.addProperty("ChannelNo", res.getChannelNo());
        rpc.addProperty("Enabled",res.getEnabled());
        rpc.addProperty("Sensitivity",res.getSensitivity());
        rpc.addProperty("StartTriggerTime",res.getStartTriggerTime());
        rpc.addProperty("EndTriggerTime",res.getEndTriggerTime());

        SoapObject so = new SoapObject(sNameSpace,"VMDGrid");
        for (String s: res.getGrids().getRows()) {
        	so.addProperty("Row", s);
        }
        rpc.addProperty("Grid", so);
    	
    	SoapObject object = initEnvelopAndTransport(rpc,"http://www.haoweis.com/HomeServices/MCU/setVMDParam");
    	try{
	        Object result = object.getProperty("result");
//    	 	if(result.toString().equals("SessionExpired")){
//	        	Log.d("------------->>>>", "result = " + result.toString());
//	        	reLogin();
//	        }
	        Log.d("-----222----->>>>", "result = " + result.toString());
    	}catch (Exception e) {
			// TODO: handle exception
		}
    }

//		设备视频调节参数设置
    public SetVideoParamRes getSetVideoParamRes(SetVideoParamReq req){
    	SetVideoParamRes res = new SetVideoParamRes();
    	SoapObject rpc = new SoapObject(sNameSpace, "setVideoParamReq");
    	rpc.addProperty("Account", req.getAccount());
        rpc.addProperty("LoginSession", req.getLoginSession());
        rpc.addProperty("DevID", req.getDevID());
        rpc.addProperty("ChannelNo", req.getChannelNo());
        rpc.addProperty("RotationDegree", req.getRotationDegree());
        SoapObject object = initEnvelopAndTransport(rpc,"http://www.haoweis.com/HomeServices/MCU/setVideoParam");
        try{
	        Object result = object.getProperty("result");
	        res.setResult(result.toString());
	        
	        if(result.toString().equals("SessionExpired")){
	        	mLoginResponse = getUserLoginRes(mLoginRequest);
	        	req.setLoginSession(mLoginResponse.getLoginSession());
	        	return getSetVideoParamRes(req);
	        }
        }catch (Exception e) {
			// TODO: handle exception
		}
    	return res;
    }
    
//    设备视频调节参数查询 （旋转 亮度等）
    public GetVideoParamRes getGetVideoParamRes(GetVideoParamReq req){
    	GetVideoParamRes res = new GetVideoParamRes();
    	SoapObject rpc = new SoapObject(sNameSpace, "getVideoParamReq");
    	rpc.addProperty("Account", req.getAccount());
        rpc.addProperty("LoginSession", req.getLoginSession());
        rpc.addProperty("DevID", req.getDevID());
        rpc.addProperty("ChannelNo", req.getChannelNo());
        SoapObject object = initEnvelopAndTransport(rpc,"http://www.haoweis.com/HomeServices/MCU/getVideoParam");
        try{
	        Object result = object.getProperty("result");
	        res.setResult(result.toString());
	        
	        if(result.toString().equals("SessionExpired")){
	        	mLoginResponse = getUserLoginRes(mLoginRequest);
	        	req.setLoginSession(mLoginResponse.getLoginSession());
	        	return getGetVideoParamRes(req);
	        }
    	 	
	        Object rotationDegree = object.getProperty("RotationDegree");
	        Log.e("RotationDegree", rotationDegree.toString());
	        res.setRotationDegree(Integer.valueOf(rotationDegree.toString()));
        }catch (Exception e) {
			// TODO: handle exception
		}
        return res;
    }
    
//    查询视频存储记录
    public VodSearchRes getVodSearchReq(String account, String loginSession,
            String devID, int channelNo, String streamType,int pageNo,String startTime,String endTime,int pageSize) {
        SoapObject rpc = new SoapObject(sNameSpace, "vodSearchReq");
        rpc.addProperty("Account", account);
        rpc.addProperty("LoginSession", loginSession);
        rpc.addProperty("DevID", devID);
        rpc.addProperty("ChannelNo", channelNo);
        rpc.addProperty("StreamType", streamType);
        try {
            SimpleDateFormat bar = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss");
            if(endTime.equals("") || startTime.equals("")){
            	Date endDate = new Date();
                Date startDate = new Date(System.currentTimeMillis() - REPLAYTIME);
                endTime = bar.format(endDate);
                startTime = bar.format(startDate);
            }
            Log.e("", startTime+","+endTime);
            rpc.addProperty("StartTime", startTime);
            rpc.addProperty("EndTime", endTime);
        } catch (Exception e) {
            // TODO Auto-generated catch block
        	Log.e("", "SimpleDateFormat fail");
            e.printStackTrace();
        }

        rpc.addProperty("PageNo", pageNo);
//        rpc.addProperty("SearchID", "");
        if(pageSize != 0){
        	rpc.addProperty("PageSize", pageSize);
        }

        VodSearchRes res = new VodSearchRes();
        SoapObject object = initEnvelopAndTransport(rpc,"http://www.haoweis.com/HomeServices/MCU/vodSearch");
        try{
	        Object result = object.getProperty("result");
	        System.out.println("result:"+result.toString());
	        if(result.toString().equals("SessionExpired")){
	        	mLoginResponse = getUserLoginRes(mLoginRequest);
	        	return getVodSearchReq(account,mLoginResponse.getLoginSession(),
	                    devID, channelNo, streamType,pageNo, startTime, endTime, pageSize);
	        }
	        res.setResult(result.toString());
        }catch (Exception e) {
			// TODO: handle exception
		}
        try{
	        Object PageNo = object.getProperty("PageNo");
	        res.setPageNo(Integer.valueOf(PageNo.toString()));
	        System.out.println("pageNO:"+PageNo.toString());
	        Object PageCount = object.getProperty("PageCount");
	        res.setPageCount(Integer.valueOf(PageCount.toString()));
	        System.out.println("PageCount:"+PageCount.toString());
	        Object RecordCount = object.getProperty("RecordCount");
	        res.setRecordCount(Integer.valueOf(RecordCount.toString()));
	        System.out.println("RecordCount:"+RecordCount.toString());
	        
	        int count = object.getPropertyCount();
	        System.out.println(count);
	        ArrayList<VODRecord> list = new ArrayList<VODRecord>();
	        for (int i = 4; i < count ; i++) {
	          Object o = object.getProperty(i);
	          System.out.println("vodrecord:"+o.toString());
	          AnalyzingDoNetOutput.analyzingVODRecord(o.toString(), list);
	        }
	        res.setRecord(list);
	        System.out.println("list:"+list.size());
        }catch (Exception e) {
			// TODO: handle exception
        	ArrayList<VODRecord> list = new ArrayList<VODRecord>();
        	res.setRecord(list);
        	Log.e("", "SoapObject fail");
		}
        return res;
    }
    
//    递交NAT结果（Other-其他 TURN-转发 STUN-穿透 UPnP-直连）
    public NotifyNATResultRes getNotifyNATResultRes(NotifyNATResultReq req){
    	Log.e("SoapManager", "getNotifyNATResultRes");
    	NotifyNATResultRes res = new NotifyNATResultRes();
    	SoapObject rpc = new SoapObject(sNameSpace, "notifyNATResultReq");
    	rpc.addProperty("Account", req.getAccount());
    	rpc.addProperty("LoginSession", req.getLoginSession());
    	rpc.addProperty("DialogID", req.getDialogID());
    	rpc.addProperty("NATType", req.getNATType());
    	SoapObject object = initEnvelopAndTransport(rpc,"http://www.haoweis.com/HomeServices/MCU/notifyNATResult");
    	try{
 	       	Object result = object.getProperty("result");
 	        res.setResult(result.toString());
 	        
 	       if(result.toString().equals("SessionExpired")){
	        	mLoginResponse = getUserLoginRes(mLoginRequest);
	        	req.setLoginSession(mLoginResponse.getLoginSession());
	        	return getNotifyNATResultRes(req);
	        }
        }catch (Exception e) {
 			// TODO: handle exception
        	Log.e("getNotifyNATResultRes", "error");
 		}
    	return res;
    }
    
//    NAT服务器查询（STUN TURN server）
    public GetNATServerRes getGetNATServerRes(GetNATServerReq req){
    	Log.e("SoapManager", "getGetNATServerRes");
//    	GetNATServerRes res = new GetNATServerRes();
    	mGetNATServerRes = new GetNATServerRes();
    	SoapObject rpc = new SoapObject(sNameSpace, "getNATServerReq");
    	rpc.addProperty("Account", req.getAccount());
    	rpc.addProperty("LoginSession", req.getLoginSession());
    	SoapObject object = initEnvelopAndTransport(rpc,"http://www.haoweis.com/HomeServices/MCU/getNATServer");
    	try{
 	       	Object result = object.getProperty("result");
 	       	mGetNATServerRes.setResult(result.toString());
 	       	
 	       if(result.toString().equals("SessionExpired")){
	        	mLoginResponse = getUserLoginRes(mLoginRequest);
	        	req.setLoginSession(mLoginResponse.getLoginSession());
	        	return getGetNATServerRes(req);
	        }
    	 	
 	        SoapObject STUNServerList = (SoapObject) object.getProperty("STUNServerList");
 	        SoapObject STUNServer = (SoapObject) STUNServerList.getProperty("STUNServer");
 	        Object STUNIPv4Address = STUNServer.getProperty("IPv4Address");
 	        mGetNATServerRes.setSTUNServerAddress(STUNIPv4Address.toString());
 	        Object STUNPort = STUNServer.getProperty("Port");
 	        mGetNATServerRes.setSTUNServerPort(Integer.valueOf(STUNPort.toString()));
 	        
	        SoapObject TURNServerList = (SoapObject)object.getProperty("TURNServerList");
	        SoapObject TURNServer = (SoapObject) TURNServerList.getProperty("TURNServer");
 	        Object TURNIPv4Address = TURNServer.getProperty("IPv4Address");
 	        mGetNATServerRes.setTURNServerAddress(TURNIPv4Address.toString());
	        Object TURNPort = TURNServer.getProperty("Port");
	        mGetNATServerRes.setTURNServerPort(Integer.valueOf(TURNPort.toString()));
	        Object userName = TURNServer.getProperty("Username");
	        mGetNATServerRes.setTURNServerUserName(userName.toString());
	        Object password = TURNServer.getProperty("Password");
	        mGetNATServerRes.setTURNServerPassword(password.toString());
	        Log.e("SoapManager", mGetNATServerRes.toString());
        }catch (Exception e) {
 			// TODO: handle exception
        	Log.e("getGetNATServerRes", "error");
 		}
    	return mGetNATServerRes;
    }
    
//    Android推送服务注册
    public UpdateAndroidTokenRes GetUpdateAndroidTokenRes(UpdateAndroidTokenReq req){
    	UpdateAndroidTokenRes res = new UpdateAndroidTokenRes();
    	SoapObject rpc = new SoapObject(sNameSpace, "updateAndroidTokenReq");
    	rpc.addProperty("Account", req.getAccount());
    	rpc.addProperty("LoginSession", req.getLoginSession());
    	rpc.addProperty("UDID", req.getUDID());
    	rpc.addProperty("DeviceToken", req.getDeviceToken());
    	rpc.addProperty("APNs", req.isAPNs());
    	//rpc.addProperty("AndroidOS", "Android");
    	SoapObject object = initEnvelopAndTransport(rpc,"http://www.haoweis.com/HomeServices/MCU/updateAndroidToken");
    	try{
 	       	Object result = object.getProperty("result");
 	       	res.setResult(result.toString());
 	       	if(result.toString().equals("SessionExpired")){
	        	mLoginResponse = getUserLoginRes(mLoginRequest);
	        	req.setLoginSession(mLoginResponse.getLoginSession());
	        	return GetUpdateAndroidTokenRes(req);
	        }
    	}catch (Exception e) {
				// TODO: handle exception
		}
    	return res;
    }
    
//    查询Android推送服务
    public QueryAndroidTokenRes GetQueryAndroidTokenRes(QueryAndroidTokenReq req){
    	QueryAndroidTokenRes res = new QueryAndroidTokenRes();
    	SoapObject rpc = new SoapObject(sNameSpace, "queryAndroidTokenReq");
    	rpc.addProperty("Account", req.getAccount());
    	rpc.addProperty("LoginSession", req.getLoginSession());
    	rpc.addProperty("UDID", req.getUDID());
    	SoapObject object = initEnvelopAndTransport(rpc,"http://www.haoweis.com/HomeServices/MCU/queryAndroidToken");
    	try{
 	       	Object result = object.getProperty("result");
 	       	res.setResult(result.toString());
 	       	if(result.toString().equals("SessionExpired")){
	        	mLoginResponse = getUserLoginRes(mLoginRequest);
	        	req.setLoginSession(mLoginResponse.getLoginSession());
	        	return GetQueryAndroidTokenRes(req);
	        }
 	       	Object UDID = object.getProperty("UDID");
	       	res.setUDID(UDID.toString());
	       	Object DeviceToken = object.getProperty("DeviceToken");
 	       	res.setDeviceToken(DeviceToken.toString());
 	       	Object APNs = object.getProperty("APNs");
	       	res.setAPNs(Boolean.parseBoolean(APNs.toString()));
    	}catch (Exception e) {
				// TODO: handle exception
		}
    	return res;
    }
    
//    MCU云台控制
    public PtzControlRes GetPtzControlRes(PtzControlReq req){
    	PtzControlRes res = new PtzControlRes();
    	SoapObject rpc = new SoapObject(sNameSpace, "ptzControlReq");
    	rpc.addProperty("Account", req.getAccount());
    	rpc.addProperty("LoginSession", req.getLoginSession());
    	rpc.addProperty("DevID", req.getDevID());
    	rpc.addProperty("ChannelNo", req.getChannelNo());
    	rpc.addProperty("PtzDirection", req.getPtzDirection());
    	SoapObject object = initEnvelopAndTransport(rpc,"http://www.haoweis.com/HomeServices/MCU/ptzControl");
    	try{
    		Object result = object.getProperty("result");
 	       	res.setResult(result.toString());
 	       	if(result.toString().equals("SessionExpired")){
	        	mLoginResponse = getUserLoginRes(mLoginRequest);
	        	req.setLoginSession(mLoginResponse.getLoginSession());
	        	return GetPtzControlRes(req);
	        }
    	}catch (Exception e) {
				// TODO: handle exception
		}
    	return res;
    }
    
//    查询客户端软件的最新版本号
    public QueryClientVersionRes getQueryClientVersionRes(QueryClientVersionReq req){
    	QueryClientVersionRes res = new QueryClientVersionRes();
    	SoapObject rpc = new SoapObject(sNameSpace, "queryClientVersionReq");
    	rpc.addProperty("ClientType", req.getClientType());
    	SoapObject object = initEnvelopAndTransport(rpc,"http://www.haoweis.com/HomeServices/MCU/queryClientVersion");
    	
 	    Object result = object.getProperty("result");
 	    res.setResult(result.toString());
 	    if(result.toString().equals("SessionExpired")){
 	    	mLoginResponse = getUserLoginRes(mLoginRequest);
 	    	return getQueryClientVersionRes(req);
 	    }
    	 	
    	try{
 	       	Object version = object.getProperty("Version");
	       	res.setVersion(version.toString());
    	}catch (NullPointerException e) {
			// TODO: handle exception
    		res.setVersion("");
    	}catch (RuntimeException e) {
			// TODO: handle exception
    		res.setVersion("");
		}catch (Exception e) {
			// TODO: handle exception
			res.setVersion("");
		}
	    try{
	       	Object downloadAddress = object.getProperty("DownloadAddress");
 	       	res.setDownloadAddress(downloadAddress.toString());
    	}catch (NullPointerException e) {
				// TODO: handle exception
    		res.setDownloadAddress("");
		}catch (RuntimeException e) {
			// TODO: handle exception
			res.setDownloadAddress("");
		}catch (Exception e) {
			// TODO: handle exception
			res.setDownloadAddress("");
		}
    	return res;
    }
    
//    设备固件版本查询
    public GetDevVerRes getGetDevVerRes(GetDevVerReq req){
    	GetDevVerRes res = new GetDevVerRes();
    	SoapObject rpc = new SoapObject(sNameSpace, "getDevVerReq");
    	rpc.addProperty("Account", req.getAccount());
    	rpc.addProperty("LoginSession", req.getLoginSession());
    	rpc.addProperty("DevID", req.getDevID());
    	SoapObject object = initEnvelopAndTransport(rpc,"http://www.haoweis.com/HomeServices/MCU/getDevVer");
    	try{
 	       	Object result = object.getProperty("result");
 	       	res.setResult(result.toString());
 	       	
 	       	if(result.toString().equals("SessionExpired")){
	        	mLoginResponse = getUserLoginRes(mLoginRequest);
	        	req.setLoginSession(mLoginResponse.getLoginSession());
	        	return getGetDevVerRes(req);
	        }
    	 	
 	       	Object CurDevVer = object.getProperty("CurDevVer");
	       	res.setCurDevVer(CurDevVer.toString());
	       	Log.e("getGetDevVerRes", "CurDevVer = " + CurDevVer.toString());
	       	Object NewDevVer = object.getProperty("NewDevVer");
 	       	res.setNewDevVer(NewDevVer.toString());
    	}catch (Exception e) {
				// TODO: handle exception
		}
    	return res;
    }
    
//    远程升级通知
    public UpgradeDevVerRes getUpgradeDevVerRes(UpgradeDevVerReq req){
    	UpgradeDevVerRes res = new UpgradeDevVerRes();
    	SoapObject rpc = new SoapObject(sNameSpace, "upgradeDevVerReq");
    	rpc.addProperty("Account", req.getAccount());
    	rpc.addProperty("LoginSession", req.getLoginSession());
    	rpc.addProperty("DevID", req.getDevID());
    	SoapObject object = initEnvelopAndTransport(rpc,"http://www.haoweis.com/HomeServices/MCU/upgradeDevVer");
    	try{
 	       	Object result = object.getProperty("result");
 	       	res.setResult(result.toString());
 	       	
 	       	if(result.toString().equals("SessionExpired")){
	        	mLoginResponse = getUserLoginRes(mLoginRequest);
	        	req.setLoginSession(mLoginResponse.getLoginSession());
	        	return getUpgradeDevVerRes(req);
	        }
    	}catch (Exception e) {
				// TODO: handle exception
		}
    	return res;
    }
    
//    查询摄像机辅助器状态
    public GetAuxiliaryRes getGetAuxiliaryRes(GetAuxiliaryReq req){
    	GetAuxiliaryRes res = new GetAuxiliaryRes();
    	SoapObject rpc = new SoapObject(sNameSpace, "getAuxiliaryReq");
    	rpc.addProperty("Account", req.getAccount());
    	rpc.addProperty("LoginSession", req.getLoginSession());
    	rpc.addProperty("DevID", req.getDevID());
    	rpc.addProperty("Auxiliary", req.getAuxiliary());
    	SoapObject object = initEnvelopAndTransport(rpc,"http://www.haoweis.com/HomeServices/MCU/getAuxiliary");
    	try{
 	       	Object result = object.getProperty("result");
 	       	res.setResult(result.toString());
 	       	
 	       	if(result.toString().equals("SessionExpired")){
	        	mLoginResponse = getUserLoginRes(mLoginRequest);
	        	req.setLoginSession(mLoginResponse.getLoginSession());
	        	return getGetAuxiliaryRes(req);
	        }
 	       	Log.i("aux", "res="+res.getResult());
 	       	Object auxiliaryState = object.getProperty("AuxiliaryState");
	       	res.setAuxiliaryState(auxiliaryState.toString());
    	}catch (Exception e) {
				// TODO: handle exception
		}
    	return res;
    }
    
//    设置摄像机辅助器状态 （辅助照明 信号灯）
    public SetAuxiliaryRes getSetAuxiliaryRes(SetAuxiliaryReq req){
    	SetAuxiliaryRes res = new SetAuxiliaryRes();
    	SoapObject rpc = new SoapObject(sNameSpace, "setAuxiliaryReq");
    	rpc.addProperty("Account", req.getAccount());
    	rpc.addProperty("LoginSession", req.getLoginSession());
    	rpc.addProperty("DevID", req.getDevID());
    	rpc.addProperty("Auxiliary", req.getAuxiliary());
    	rpc.addProperty("AuxiliaryState", req.getAuxiliaryState());
    	SoapObject object = initEnvelopAndTransport(rpc,"http://www.haoweis.com/HomeServices/MCU/setAuxiliary");
    	try{
 	       	Object result = object.getProperty("result");
 	       	res.setResult(result.toString());
 	       	
 	       	if(result.toString().equals("SessionExpired")){
	        	mLoginResponse = getUserLoginRes(mLoginRequest);
	        	req.setLoginSession(mLoginResponse.getLoginSession());
	        	return getSetAuxiliaryRes(req);
	        }
    	}catch (Exception e) {
				// TODO: handle exception
		}
    	return res;
    }

//    开通Android推送服务
    public SubscribeAndroidPushRes getSubscribeAndroidPushRes(SubscribeAndroidPushReq req){
    	SubscribeAndroidPushRes res = new SubscribeAndroidPushRes();
    	SoapObject rpc = new SoapObject(sNameSpace, "subscribeAndroidPushReq");
    	rpc.addProperty("Account", req.getAccount());
    	rpc.addProperty("LoginSession", req.getLoginSession());
    	rpc.addProperty("SubscribedFlag", req.getSubscribedFlag());
    	rpc.addProperty("DevID", req.getDevID());
    	rpc.addProperty("ChannelNo", req.getChannelNo());
    	SoapObject object = initEnvelopAndTransport(rpc,"http://www.haoweis.com/HomeServices/MCU/subscribeAndroidPush");
    	try{
 	       	Object result = object.getProperty("result");
 	       	res.setResult(result.toString());
 	       	
    	 	if(result.toString().equals("SessionExpired")){
	        	mLoginResponse = getUserLoginRes(mLoginRequest);
	        	req.setLoginSession(mLoginResponse.getLoginSession());
	        	return getSubscribeAndroidPushRes(req);
	        }
    	}catch (Exception e) {
				// TODO: handle exception
		}
    	return res;
    }
    
//    查询无线网络状态
    public GetWirelessNetworkRes getGetWirelessNetworkRes(GetWirelessNetworkReq req){
    	GetWirelessNetworkRes res = new GetWirelessNetworkRes();
    	SoapObject rpc = new SoapObject(sNameSpace, "getWirelessNetworkReq");
    	rpc.addProperty("Account", req.getAccount());
    	rpc.addProperty("LoginSession", req.getLoginSession());
    	rpc.addProperty("DevID", req.getDevID());
    	SoapObject object = initEnvelopAndTransport(rpc,"http://www.haoweis.com/HomeServices/MCU/getWirelessNetwork");
    	try{
 	       	Object result = object.getProperty("result");
 	       	res.setResult(result.toString());
 	       	
 	       	if(result.toString().equals("SessionExpired")){
	        	mLoginResponse = getUserLoginRes(mLoginRequest);
	        	req.setLoginSession(mLoginResponse.getLoginSession());
	        	return getGetWirelessNetworkRes(req);
	        }
 	        Object wirelessType = object.getProperty("WirelessType");
	       	res.setWirelessType(wirelessType.toString());
	       	Object sSID = object.getProperty("SSID");
 	       	res.setsSID(sSID.toString());
 	        Object intensity = object.getProperty("Intensity");
	       	res.setIntensity(Integer.valueOf(intensity.toString()));
    	}catch (Exception e) {
				// TODO: handle exception
		}
    	return res;
    }
    
//    修改账户密码
    public UpdatePasswordRes getUpdatePasswordRes(UpdatePasswordReq req){
    	UpdatePasswordRes res = new UpdatePasswordRes();
    	SoapObject rpc = new SoapObject(sNameSpace, "updatePasswordReq");
    	rpc.addProperty("Account", req.getAccount());
    	rpc.addProperty("LoginSession", req.getLoginSession());
    	rpc.addProperty("Password", req.getPassword());
    	rpc.addProperty("NewPassword", req.getNewPassword());
    	SoapObject object = initEnvelopAndTransport(rpc,"http://www.haoweis.com/HomeServices/MCU/updatePassword");
    	try{
 	       	Object result = object.getProperty("result");
 	       	res.setResult(result.toString());
 	       	
 	       	if(result.toString().equals("SessionExpired")){
	        	mLoginResponse = getUserLoginRes(mLoginRequest);
	        	req.setLoginSession(mLoginResponse.getLoginSession());
	        	return getUpdatePasswordRes(req);
	        }
    	}catch (Exception e) {
				// TODO: handle exception
		}
    	return res;
    }
    
//    修改账户信息
    public UpdateAccountRes getUpdateAccountRes(UpdateAccountReq req){
    	UpdateAccountRes res = new UpdateAccountRes();
    	SoapObject rpc = new SoapObject(sNameSpace, "updateAccountReq");
    	rpc.addProperty("Account", req.getAccount());
    	rpc.addProperty("LoginSession", req.getLoginSession());
    	//rpc.addProperty("Username", "");
    	rpc.addProperty("MobileTel", req.getMobileTel());
    	SoapObject object = initEnvelopAndTransport(rpc,"http://www.haoweis.com/HomeServices/MCU/updateAccount");
    	try{
 	       	Object result = object.getProperty("result");
 	       	res.setResult(result.toString());
 	       	
 	       	if(result.toString().equals("SessionExpired")){
	        	mLoginResponse = getUserLoginRes(mLoginRequest);
	        	req.setLoginSession(mLoginResponse.getLoginSession());
	        	return getUpdateAccountRes(req);
	        }
    	}catch (Exception e) {
				// TODO: handle exception
		}
    	return res;
    }
    
//    创建账户
    public CreateAccountRes getCreateAccountRes(CreateAccountReq req){
    	CreateAccountRes res = new CreateAccountRes();
    	SoapObject rpc = new SoapObject(sNameSpace, "createAccountReq");
    	rpc.addProperty("Account", req.getAccount());
    	rpc.addProperty("Username", " ");
    	rpc.addProperty("Password", req.getPassword());
    	rpc.addProperty("Email", req.getEmail());
    	//rpc.addProperty("MobileTel", " ");
    	SoapObject object = initEnvelopAndTransport(rpc,"http://www.haoweis.com/HomeServices/MCU/createAccount");
    	try{
    		Object result = object.getProperty("result");
    	 	res.setResult(result.toString());
    	 	
    	}catch (Exception e) {
    		// TODO: handle exception
    	}
    	   	return res;
    }
    
//    用户绑定设备
    public AddDeviceRes getAddDeviceRes(AddDeviceReq req){
    	AddDeviceRes res = new AddDeviceRes();
    	SoapObject rpc = new SoapObject(sNameSpace, "addDeviceReq");
    	rpc.addProperty("Account", req.getAccount());
    	System.out.println(req.getAccount());
    	rpc.addProperty("LoginSession", req.getLoginSession());
    	
    	SoapObject so = new SoapObject(sNameSpace,"Device");
        so.addProperty("DevID", req.getDevID());
        so.addProperty("DevKey", req.getDevKey());
        System.out.println("devName:"+req.getDevName());
        so.addProperty("DevName", req.getDevName());
        
        SoapObject so2 = new SoapObject(sNameSpace,"ArrayOfDevice");
        so2.addProperty("Device", so);
        rpc.addProperty("DeviceAll", so2);
    	
    	rpc.addProperty("Forcible", req.isForcible());
    	SoapObject object = initEnvelopAndTransport(rpc,"http://www.haoweis.com/HomeServices/MCU/addDevice");
    	try{
    	 	Object result = object.getProperty("result");
    	 	res.setResult(result.toString());
    	 	System.out.println("getAddDeviceRes:"+result.toString());
    	 	
    	 	if(result.toString().equals("SessionExpired")){
	        	mLoginResponse = getUserLoginRes(mLoginRequest);
	        	req.setLoginSession(mLoginResponse.getLoginSession());
	        	return getAddDeviceRes(req);
	        }
    	}catch (Exception e) {
    		// TODO: handle exception
    	}
    	    return res;
    }
    
    //修改设备名
    public UpdateChannelNameRes getUpdateChannelNameRes(UpdateChannelNameReq req){
    	UpdateChannelNameRes res = new UpdateChannelNameRes();
    	SoapObject rpc = new SoapObject(sNameSpace, "updateChannelNameReq");
    	rpc.addProperty("Account", req.getAccount());
    	rpc.addProperty("LoginSession", req.getLoginSession());
    	rpc.addProperty("DevID", req.getDevID());
    	rpc.addProperty("ChannelNo", 0);
    	rpc.addProperty("ChannelName", req.getChannelName());
    	SoapObject object = initEnvelopAndTransport(rpc,"http://www.haoweis.com/HomeServices/MCU/updateChannelName");
    	try{
    		Object result = object.getProperty("result");
    	 	res.setResult(result.toString());
    	 	System.out.println("UpdateChannelNameRes res:"+result.toString());
    	 	if(result.toString().equals("SessionExpired")){
	        	mLoginResponse = getUserLoginRes(mLoginRequest);
	        	req.setLoginSession(mLoginResponse.getLoginSession());
	        	return getUpdateChannelNameRes(req);
	        }
    	}catch (Exception e) {
    		// TODO: handle exception
    	}
    	   	return res;
    }
    
    //获取账户摄像机匹配码
    public GetDeviceMatchingCodeRes getGetDeviceMatchingCodeRes(GetDeviceMatchingCodeReq req){
    	setmGetDeviceMatchingCodeRes(null);
    	GetDeviceMatchingCodeRes res = new GetDeviceMatchingCodeRes();
    	SoapObject rpc = new SoapObject(sNameSpace, "getDeviceMatchingCodeReq");
    	rpc.addProperty("Account", req.getAccount());
    	rpc.addProperty("LoginSession", req.getLoginSession());
    	SoapObject object = initEnvelopAndTransport(rpc,"http://www.haoweis.com/HomeServices/MCU/getDeviceMatchingCode");
    	try{
    		Object result = object.getProperty("result");
    	 	res.setResult(result.toString());
    	 	
    	 	if(result.toString().equals("SessionExpired")){
	        	mLoginResponse = getUserLoginRes(mLoginRequest);
	        	req.setLoginSession(mLoginResponse.getLoginSession());
	        	return getGetDeviceMatchingCodeRes(req);
	        }
    	 	
    	 	Object matchingCode = object.getProperty("MatchingCode");
    	 	res.setMatchingCode(matchingCode.toString());
    	 	
    	 	setmGetDeviceMatchingCodeRes(res);
    	}catch (Exception e) {
    		// TODO: handle exception
    	}
    	   	return res;
    }
    
    //查询摄像机匹配结果
    public GetDeviceMatchingResultRes getGetDeviceMatchingResultRes(GetDeviceMatchingResultReq req){
    	GetDeviceMatchingResultRes res = new GetDeviceMatchingResultRes();
    	SoapObject rpc = new SoapObject(sNameSpace, "getDeviceMatchingResultReq");
    	rpc.addProperty("Account", req.getAccount());
    	rpc.addProperty("LoginSession", req.getLoginSession());
    	rpc.addProperty("MatchingCode", req.getMatchingCode());
    	SoapObject object = initEnvelopAndTransport(rpc,"http://www.haoweis.com/HomeServices/MCU/getDeviceMatchingResult");
    	try{
    		Object result = object.getProperty("result");
    	 	res.setResult(result.toString());
    	 	
    	 	if(result.toString().equals("SessionExpired")){
	        	mLoginResponse = getUserLoginRes(mLoginRequest);
	        	req.setLoginSession(mLoginResponse.getLoginSession());
	        	return getGetDeviceMatchingResultRes(req);
	        }
    	 	
    	 	Object devid = object.getProperty("DevID");
    	 	res.setDevID(devid.toString());
    	 	
    	}catch (Exception e) {
    		// TODO: handle exception
    	}
    	   	return res;
    }
    
    //添加设备视频分享 
    public AddDeviceSharerRes getAddDeviceSharerRes(AddDeviceSharerReq req){
    	AddDeviceSharerRes res = new AddDeviceSharerRes();
    	SoapObject rpc = new SoapObject(sNameSpace, "addDeviceSharerReq");
    	rpc.addProperty("Account", req.getAccount());
    	rpc.addProperty("LoginSession", req.getLoginSession());
    	rpc.addProperty("DevID", req.getDevID());
    	rpc.addProperty("ChannelNo", req.getChannelNo());
    	rpc.addProperty("SharerAccount", req.getSharerAccount());
    	rpc.addProperty("SharingPriority", req.getSharingPriority());
    	SoapObject object = initEnvelopAndTransport(rpc,"http://www.haoweis.com/HomeServices/MCU/addDeviceSharer");
    	try{
    		Object result = object.getProperty("result");
    	 	res.setResult(result.toString());
    	 	if(result.toString().equals("SessionExpired")){
	        	mLoginResponse = getUserLoginRes(mLoginRequest);
	        	req.setLoginSession(mLoginResponse.getLoginSession());
	        	return getAddDeviceSharerRes(req);
	        }
    	}catch (Exception e) {
    		// TODO: handle exception
    	}
    	   	return res;
    }
    
    //删除设备视频分享
    public NullifyDeviceSharerRes getNullifyDeviceSharerRes(NullifyDeviceSharerReq req){
    	NullifyDeviceSharerRes res = new NullifyDeviceSharerRes();
    	SoapObject rpc = new SoapObject(sNameSpace, "nullifyDeviceSharerReq");
    	rpc.addProperty("Account", req.getAccount());
    	rpc.addProperty("LoginSession", req.getLoginSession());
    	rpc.addProperty("DevID", req.getDevID());
    	rpc.addProperty("ChannelNo", req.getChannelNo());
    	rpc.addProperty("SharerAccount", req.getSharerAccount());
    	SoapObject object = initEnvelopAndTransport(rpc,"http://www.haoweis.com/HomeServices/MCU/nullifyDeviceSharer");
    	try{
    		Object result = object.getProperty("result");
    	 	res.setResult(result.toString());
    	 	if(result.toString().equals("SessionExpired")){
	        	mLoginResponse = getUserLoginRes(mLoginRequest);
	        	req.setLoginSession(mLoginResponse.getLoginSession());
	        	return getNullifyDeviceSharerRes(req);
	        }
    	}catch (Exception e) {
    		// TODO: handle exception
    	}
    	   	return res;
    }
    
    //用户移除设备
    public NullifyDeviceRes getNullifyDeviceRes(NullifyDeviceReq req){
    	NullifyDeviceRes res = new NullifyDeviceRes();
    	SoapObject rpc = new SoapObject(sNameSpace, "nullifyDeviceReq");
    	rpc.addProperty("Account", req.getAccount());
    	rpc.addProperty("LoginSession", req.getLoginSession());
    	rpc.addProperty("DevID", req.getDevID());
    	rpc.addProperty("DevKey", req.getDevKey());
    	SoapObject object = initEnvelopAndTransport(rpc,"http://www.haoweis.com/HomeServices/MCU/nullifyDevice");
    	try{
    		Object result = object.getProperty("result");
    	 	res.setResult(result.toString());
    	 	if(result.toString().equals("SessionExpired")){
	        	mLoginResponse = getUserLoginRes(mLoginRequest);
	        	req.setLoginSession(mLoginResponse.getLoginSession());
	        	return getNullifyDeviceRes(req);
	        }
    	}catch (Exception e) {
    		// TODO: handle exception
    	}
    	   	return res;
    }
    
    //查询设备视频分享的用户
    public QueryDeviceSharerRes getQueryDeviceSharerRes(QueryDeviceSharerReq req){
    	QueryDeviceSharerRes res = new QueryDeviceSharerRes();
    	SoapObject rpc = new SoapObject(sNameSpace, "queryDeviceSharerReq");
    	rpc.addProperty("Account", req.getAccount());
    	rpc.addProperty("LoginSession", req.getLoginSession());
    	rpc.addProperty("DevID", req.getDevID());
    	rpc.addProperty("ChannelNo", req.getChannelNo());
    	SoapObject object = initEnvelopAndTransport(rpc,"http://www.haoweis.com/HomeServices/MCU/queryDeviceSharer");
    	try{
    		Object result = object.getProperty("result");
    	 	res.setResult(result.toString());
    	 	System.out.println(result);
    	 	
    	 	if(result.toString().equals("SessionExpired")){
	        	mLoginResponse = getUserLoginRes(mLoginRequest);
	        	req.setLoginSession(mLoginResponse.getLoginSession());
	        	return getQueryDeviceSharerRes(req);
	        }
    	 	
    	 	SoapObject sharerList = (SoapObject)object.getProperty("SharerList");
	        System.out.println("QueryDeviceSharerRes SharerList:"+sharerList.toString());
	        
	        for(int i = 0 ;i<sharerList.getPropertyCount();i++){
	        	DeviceSharer d = new DeviceSharer();
	        	
		        SoapObject deviceSharer = (SoapObject)sharerList.getProperty(i);
		        System.out.println("DeviceSharer:"+deviceSharer.toString());
		        
		        Object account = deviceSharer.getProperty("Account");
		        d.setSharerAccount(account.toString());
		        
		        Object sharingPriority = deviceSharer.getProperty("SharingPriority");
		        d.setSharingPriority(Integer.valueOf(sharingPriority.toString()));
		        
		        System.out.println(d.toString());
		        res.addDeviceSharer(d);
	        }
    	 	
    	}catch (Exception e) {
    		// TODO: handle exception
    	}
    	   	return res;
    }
    
    // 获取系统通知列表
    public QueryNoticesRes getQueryNoticesRes(QueryNoticesReq req){
    	System.out.println("QueryNotices");
    	QueryNoticesRes res = new QueryNoticesRes();
    	SoapObject rpc = new SoapObject(sNameSpace, "queryNoticesReq");
    	rpc.addProperty("Account", req.getAccount());
    	rpc.addProperty("LoginSession", req.getLoginSession());
    	if(req.getPageNo() != 0)
    		rpc.addProperty("PageNo", req.getPageNo());
    	if(req.getPageSize() != 0)
    		rpc.addProperty("PageSize", req.getPageSize());
    	SoapObject object = initEnvelopAndTransport(rpc,"http://www.haoweis.com/HomeServices/MCU/queryNotices");
    	try{
    		Object result = object.getProperty("result");
    		if(result.toString().equals("SessionExpired")){
	        	mLoginResponse = getUserLoginRes(mLoginRequest);
	        	req.setLoginSession(mLoginResponse.getLoginSession());
	        	return getQueryNoticesRes(req);
	        }
    	 	res.setResult(result.toString());
    	 	System.out.println(result);
    	 	
    	 	Object pageNo = object.getProperty("PageNo");
    	 	res.setPageNo(Integer.valueOf(pageNo.toString()));
    	 	
    	 	Object pageCount = object.getProperty("PageCount");
    	 	res.setPageCount(Integer.valueOf(pageCount.toString()));
    	 	
    	 	Object recordCount = object.getProperty("RecordCount");
    	 	res.setRecordCount(Integer.valueOf(recordCount.toString()));
    	 	
    	 	SoapObject noticeList = (SoapObject)object.getProperty("Notice");
	        Log.e("","QueryNoticesRes nodeList:"+noticeList.toString());
	        ArrayList<NoticeList> list = new ArrayList<NoticeList>();
	        for(int i = 0 ;i<noticeList.getPropertyCount();i++){
	        	NoticeList n = new NoticeList();
	        	
		        SoapObject notice = (SoapObject)noticeList.getProperty(i);
//		        System.out.println("NoticeList:"+notice.toString());
		        
		        Object id = notice.getProperty("ID");
		        n.setiD(id.toString());
		        System.out.println("id:"+id);
		        Object message = notice.getProperty("Message");
		        n.setMessage(message.toString());
		        System.out.println("message:"+message);
		        Object classification = notice.getProperty("Classification");
		        n.setClassification(classification.toString());
		        System.out.println("classification:"+classification);
		        Object time = notice.getProperty("Time");
		        n.setTime(time.toString());
		        System.out.println("time:"+time);
		        Object status = notice.getProperty("Status");
		        n.setStatus(status.toString());
		        System.out.println("status:"+status);
		        Object devID = notice.getProperty("DevID");
		        n.setDevID(devID.toString());
		        System.out.println("devID:"+devID);
		        Object channelNo = notice.getProperty("ChannelNo");
		        n.setChannelNo(Integer.valueOf(channelNo.toString()));
		        System.out.println("channelNo:"+channelNo);
		        try{
			        Object name = notice.getProperty("Name");
			        n.setName(name.toString());
			        //System.out.println("name:"+name);
		        }catch(Exception e){
		        	System.out.println("name is null");
		        	n.setName("");
		        }
		        try{
		        	SoapObject pictureIDList = (SoapObject)notice.getProperty("PictureID");
		        	//System.out.println("pictureIDList:"+pictureIDList);
		        	ArrayList<String> pictureIdList = new ArrayList<String>();
		        	for(int j = 0 ; j < pictureIDList.getPropertyCount() ; j++){
		        		Object pictureID = pictureIDList.getProperty(j);
		        		//System.out.println("pictureID:"+pictureID);
		        		pictureIdList.add(pictureID.toString());
		        	}
			        n.setPictureID(pictureIdList);
			        
		        }catch(Exception e){
		        	//System.out.println("pictureID is null");
		        	n.setPictureID(new ArrayList<String>());
		        }
		        list.add(n);
	        }
    	 	res.setNoticeList(list);
    	}catch (Exception e) {
    		// TODO: handle exception
    		System.out.println("QueryNoticesRes crash");
    	}
    	return res;
    }
    
    // 标记通知状态
    public FlaggedNoticeStatusRes getFlaggedNoticeStatusRes(FlaggedNoticeStatusReq req){
    	FlaggedNoticeStatusRes res = new FlaggedNoticeStatusRes();
    	SoapObject rpc = new SoapObject(sNameSpace, "FlaggedNoticeStatusReq");
    	rpc.addProperty("Account", req.getAccount());
    	rpc.addProperty("LoginSession", req.getLoginSession());
    	rpc.addProperty("Status", req.getStatus());
    	rpc.addProperty("NoticeID", req.getNoticeID());
    	SoapObject object = initEnvelopAndTransport(rpc,"http://www.haoweis.com/HomeServices/MCU/flaggedNoticeStatus");
    	try{
    		Object result = object.getProperty("result");
    		if(result.toString().equals("SessionExpired")){
	        	mLoginResponse = getUserLoginRes(mLoginRequest);
	        	req.setLoginSession(mLoginResponse.getLoginSession());
	        	return getFlaggedNoticeStatusRes(req);
	        }
    	 	res.setResult(result.toString());
    	 	System.out.println(result);
    	}catch (Exception e) {
    		// TODO: handle exception
    	}
    	return res;
    }
    
    // 获取图片信息
    public GetPictureRes getGetPictureRes(GetPictureReq req){
    	System.out.println("GetPictureRes");
    	GetPictureRes res = new GetPictureRes();
    	SoapObject rpc = new SoapObject(sNameSpace, "getPictureReq");
    	rpc.addProperty("Account", req.getAccount());
    	rpc.addProperty("LoginSession", req.getLoginSession());
    	rpc.addProperty("PictureID", req.getPictureID());
    	SoapObject object = initEnvelopAndTransport(rpc,"http://www.haoweis.com/HomeServices/MCU/getPicture");
    	try{
    		Object result = object.getProperty("result");
    		if(result.toString().equals("SessionExpired")){
	        	mLoginResponse = getUserLoginRes(mLoginRequest);
	        	req.setLoginSession(mLoginResponse.getLoginSession());
	        	return getGetPictureRes(req);
	        }
    	 	res.setResult(result.toString());
    	 	System.out.println("GetPictureRes result"+result);
    	 	
    	 	Object pictureID = object.getProperty("PictureID");
    	 	res.setPictureID(pictureID.toString());
    	 	System.out.println("pictureID:"+pictureID);
    	 	
    	 	Object picture = object.getProperty("Picture");
    	 	res.setPicture(picture.toString());
    	}catch (Exception e) {
    		// TODO: handle exception
    		System.out.println("GetPictureRes crash");
    	}
    	return res;
    }
    
    public LensControlRes getLensControlRes(LensControlReq req){
    	LensControlRes res = new LensControlRes();
    	SoapObject rpc = new SoapObject(sNameSpace, "lensControlReq");
    	rpc.addProperty("Account", req.getAccount());
    	rpc.addProperty("LoginSession", req.getLoginSession());
    	rpc.addProperty("DevID", req.getDevID());
    	rpc.addProperty("ChannelNo", req.getChannelNo());
    	rpc.addProperty("PtzLens", req.getPtzLens());
    	
    	SoapObject object = initEnvelopAndTransport(rpc,"http://www.haoweis.com/HomeServices/MCU/lensControl");
    	try{
    		Object result = object.getProperty("result");
    		Log.e("","LensControlRes result"+result);
    		if(result.toString().equals("SessionExpired")){
	        	mLoginResponse = getUserLoginRes(mLoginRequest);
	        	req.setLoginSession(mLoginResponse.getLoginSession());
	        	return getLensControlRes(req);
	        }
    	 	res.setResult(result.toString());
    	 	
    	}catch (Exception e) {
    		// TODO: handle exception
    		System.out.println("LensControlRes crash");
    	}
    	return res;
    }
    
	@Override
	public String toString() {
		return "SoapManager [mLoginRequest=" + mLoginRequest
				+ ", mLoginResponse=" + mLoginResponse + ", mGetNATServerRes="
				+ mGetNATServerRes + "]";
	}
    
	
	
	public QueryDeviceAuthenticatedRes getDeviceAuthenticatedRes(QueryDeviceAuthenticatedReq req){
		QueryDeviceAuthenticatedRes  res = new QueryDeviceAuthenticatedRes();
	
		SoapObject rpc = new SoapObject(sNameSpace, "queryMCUDeviceAuthenticatedReq");
		rpc.addProperty("UUID", req.getUUID());
		Log.e("123", "UUID="+req.getUUID());
	 	SoapObject object = initEnvelopAndTransport(rpc,"http://www.haoweis.com/HomeServices/MCU/queryMCUDeviceAuthenticated");

		try {
			Object result = object.getProperty("result");
			Log.i("123", "queryMCUDeviceAuthenticated result="+result.toString());
			res.setResult(result.toString());
			Object Authenticated = object.getProperty("Authenticated");
			Log.i("123", "queryMCUDeviceAuthenticated Authenticated ="+Authenticated.toString());
			if (Authenticated.toString().equalsIgnoreCase("True")) {
				res.setAuthenticated(true);
			}else{
				res.setAuthenticated(false);
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
		
		return res;
	}
	
	public UpdataDeviceAuthenticatedRes getUpdataDeviceAuthenticatedRes(UpdataDeviceAuthenticatedReq req){
		UpdataDeviceAuthenticatedRes  res = new UpdataDeviceAuthenticatedRes();
		SoapObject rpc = new SoapObject(sNameSpace, "uploadMCUDeviceReq");
		rpc.addProperty("UUID",req.getUUID());
		rpc.addProperty("Model",req.getModel());
		rpc.addProperty("Type",req.getType());
		rpc.addProperty("OSType",req.getOSType());
		rpc.addProperty("OSVersion", req.getOSVersion());
		rpc.addProperty("Manufactory",req.getManufactory());
		rpc.addProperty("IEMI",req.getIMEI());
		SoapObject object = initEnvelopAndTransport(rpc,"http://www.haoweis.com/HomeServices/MCU/uploadMCUDevice");
		
		try {
			Object result = object.getProperty("result") ;
			Log.i("123", "UpdataDeviceAuthenticatedRes result="+result.toString());
			res.setResult(result.toString());
		} catch (Exception e) {
			e.printStackTrace();
		}
		return res;
	}
	
	
}
