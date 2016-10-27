package com.howell.utils;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import bean.TurnGetRecordedFilesBean;
import bean.TurnPtzCtrlAckBean;
import bean.TurnPtzCtrlBean;
import bean.Subscribe;
import bean.TurnConnectAckBean;
import bean.TurnConnectBean;
import bean.TurnDisConnectAckBean;
import bean.TurnDisConnectBean;
import bean.TurnDisSubscribeAckBean;
import bean.TurnDisSubscribeBean;
import bean.TurnGetCamAckBean;
import bean.TurnGetCamBean;
import bean.TurnGetRecordedFileAckBean;
import bean.TurnSubScribe;
import bean.TurnSubScribeAckBean;

public class TurnJsonUtil {
	public static String getTurnConnectJsonStr(TurnConnectBean bean){
		JSONObject object = null;
		
		object = new JSONObject();
		
		try {
			object.put("type", bean.getType());
			object.put("device_id", bean.getDeviceId());
			object.put("username", bean.getUserName());
			object.put("password", bean.getPassWord());
		} catch (JSONException e) {
			e.printStackTrace();
		}
		return object.toString();
	}
	
	public static String getTurnSubScribe(TurnSubScribe bean){
		JSONObject object = null;
		object = new JSONObject();
		try {
			object.put("session_id", bean.getSessionId());
			object.put("topic", bean.getTopic());
			JSONObject media = new JSONObject();
			media.put("dialog_id", bean.getMedia().getDialogId());
			JSONObject meta = new JSONObject();
			meta.put("device_id", bean.getMedia().getMeta().getDeviceId());
			String mode =  bean.getMedia().getMeta().getMode();
			meta.put("mode",mode);
			meta.put("channel", bean.getMedia().getMeta().getChannel());
			meta.put("stream", bean.getMedia().getMeta().getStream());
			if ("playback".equals(mode)) {
				meta.put("begin", bean.getMedia().getMeta().getBegin());
				meta.put("end", bean.getMedia().getMeta().getEnd());
			}
			media.put("meta", meta);
			object.put("media", media);
		} catch (JSONException e) {
			e.printStackTrace();
		}
		return object.toString();
	}
	
	public static TurnConnectAckBean getTurnConnectAckFromJsonStr(String jsonStr){
		JSONObject obj = null;
		int code = 0;
		String sessionId = null;
		String detail = null;
		try {
			obj = new JSONObject(jsonStr);
			code = obj.getInt("code");
			if (code!=200) {
				detail = obj.getString("detail");
			}else{
				sessionId = obj.getString("session_id");
			}
		} catch (JSONException e) {
			e.printStackTrace();
			return null;
		}
		return new TurnConnectAckBean(code, sessionId,detail);
	}
	
	public static String getTurnDisconnectJsonStr(TurnDisConnectBean bean){
		JSONObject object = null;
		object = new JSONObject();
		try {
			object.put("session_id", bean.getSessionId());
		} catch (JSONException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return object.toString();
	}
	
	public static TurnDisConnectAckBean getTurnDisconnectAckFromJsonStr(String jsonStr){
		int code = 0;
		String detail = null;
		JSONObject obj = null;
		
		try {
			obj = new JSONObject(jsonStr);
			code = obj.getInt("code");
			if (code!=200) {
				detail = obj.getString("detail");
			}
		} catch (JSONException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return new TurnDisConnectAckBean(code, detail);
	}
	
	public static String getSubScribeJsonStr(Subscribe subscribe){

		JSONObject object = null;

		object = new JSONObject();
		try {
			object.put("session_id", subscribe.getSessionId());
			object.put("topic", "media");

			JSONObject childchild = null;
			childchild = new JSONObject();
			childchild.put("device_id", subscribe.getDeviceId());
			childchild.put("mode", subscribe.getMode());
			childchild.put("channel", 0);
			childchild.put("stream", subscribe.getIs_sub());
			if(subscribe.getStartTime()!=null && subscribe.getEndTime()!=null){
				childchild.put("begin", subscribe.getStartTime());
				childchild.put("end", subscribe.getEndTime());
			}

			JSONObject child = null;
			child = new JSONObject();
			child.put("dialog_id", subscribe.getDialogId());
			child.put("meta", childchild);

			object.put("media", child);
		} catch (JSONException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return object.toString();
	}
	
	public static TurnSubScribeAckBean getTurnSubscribeAckFromJsonStr(String jsonStr){
		int code = 0;
		String subscribeId = null;
		String detail = null;
		JSONObject obj = null;
		try {
			obj = new JSONObject(jsonStr);
			code = obj.getInt("code");
			if (code!=200) {
				detail = obj.getString("detail");
			}else{
				subscribeId = obj.getString("subscribe_id");
			}
		} catch (JSONException e) {
			e.printStackTrace();
		}
		return new TurnSubScribeAckBean(code, subscribeId, detail);
	}
	
	public static String getTurnDisSubscribeJsonStr(TurnDisSubscribeBean bean){
		JSONObject obj = new JSONObject();
		try {
			obj.put("session_id", bean.getSessionId());
			obj.put("subscribe_id", bean.getSubScribeId());
		} catch (JSONException e) {
			e.printStackTrace();
		}
		return obj.toString();
		
	}
	
	public static TurnDisSubscribeAckBean getTurnDisSubscribeAckFromJsonStr(String jsonStr){
		int code = 0;
		String detail = null;
		JSONObject obj = null;
		try {
			obj = new JSONObject(jsonStr);
			code = obj.getInt("code");
			if (code!=200) {
				detail = obj.getString("detail");
			}
		} catch (JSONException e) {
			e.printStackTrace();
		}
		return new TurnDisSubscribeAckBean(code, detail);
	}
	
	public static String getTurnCamJsonStr(TurnGetCamBean bean){

		JSONObject object = null;

		object = new JSONObject();
		try {
			object.put("username", bean.getUserName());
			object.put("password", bean.getPassword());
		} catch (JSONException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

		return object.toString();
	}
	
	public static TurnGetCamAckBean getTurnCamFromJsonStr(String jsonStr){
		JSONObject obj = null;
		int code = 0;
		String detail = null;
		int cameraCount = 0;
		TurnGetCamAckBean.Camera [] cameras = null;
		try {
			obj = new JSONObject(jsonStr);
			code = obj.getInt("code");
			if (code!=200) {
				detail = obj.getString("detail");
			}else{
				cameraCount = obj.getInt("camera_count");
				if (cameraCount!=0) {
					cameras = new TurnGetCamAckBean.Camera [cameraCount];
				}
				JSONArray array =  obj.getJSONArray("camera");
				for(int i=0;i<cameraCount;i++){
					JSONObject child = array.getJSONObject(i);
					String deviceId = child.getString("device_id");
					int channel = child.getInt("channel");
					String name = child.getString("name");
					cameras[i].setDeviceID(deviceId);
					cameras[i].setChannel(channel);
					cameras[i].setName(name);
				}
			}
		} catch (JSONException e) {
			e.printStackTrace();
		}
		return new TurnGetCamAckBean(code, cameraCount, detail, cameras);
	}
	
	public static String getTurnRecordFilesJsonStr(TurnGetRecordedFilesBean bean){

		JSONObject object = null;
		object = new JSONObject();

		try {
			object.put("device_id", bean.getDeviceId());
			object.put("channel", bean.getChannel());
			object.put("begin", bean.getBegin());
			object.put("end", bean.getEnd());
		} catch (JSONException e) {
			e.printStackTrace();
		}

		return object.toString();
	}
	
	
	public static TurnGetRecordedFileAckBean getTurnRecordAckFromJsonStr(String jsonStr){
		JSONObject obj = null;
		int code = 0;
		String detail = null;
		String deviceId = null;
		int channel = 0;
		int recordedFileCount = 0;
		TurnGetRecordedFileAckBean.RecordedFile [] recordedFiles = null;
		try {
			obj = new JSONObject(jsonStr);
			code = obj.getInt("code");
			if (code!=200) {
				detail = obj.getString("detial");
			}else{
				deviceId = obj.getString("device_id");
				channel = obj.getInt("channel");
				recordedFileCount = obj.getInt("recordedfile_count");
				if (recordedFileCount!=0) {
					recordedFiles = new TurnGetRecordedFileAckBean.RecordedFile [recordedFileCount];
				}
				JSONArray array = obj.getJSONArray("recordedfile");
				for(int i=0;i<recordedFileCount;i++){
					JSONObject child = array.getJSONObject(i);
					String begin = child.getString("begin");
					String end = child.getString("end");
					recordedFiles[i].setBeginTime(begin);
					recordedFiles[i].setEndTime(end);
				}
			}
		} catch (JSONException e) {
			e.printStackTrace();
		}
		return new TurnGetRecordedFileAckBean(code, detail, deviceId, channel, recordedFileCount, recordedFiles);
	}
	
	public static String getTurnPtzJsonStr(TurnPtzCtrlBean bean){
		JSONObject object = null;
		object = new JSONObject();
		try {
			object.put("session_id", bean.getSessionId());
			object.put("device_id", bean.getDeviceId());
			object.put("channel", bean.getChannel());
			object.put("ptz_command", bean.getPtzCmd());
			object.put("speed", bean.getSpeed());
			
			if (bean.getPresetNo()!=-1) {
				object.put("preset_no",bean.getPresetNo());
			}
		} catch (JSONException e) {
			e.printStackTrace();
		}
		return object.toString();
	}
	
	public static TurnPtzCtrlAckBean getTurnPtzAckFromJsonStr(String jsonStr){
		int code = 0;
		String detail = null;
		try {
			JSONObject obj = new JSONObject(jsonStr);
			code = obj.getInt("code");
			if (code!=200) {
				detail = obj.getString("detail");
			}
		} catch (JSONException e) {
			e.printStackTrace();
		}
		return new TurnPtzCtrlAckBean(code, detail);
	}
	
}
