package com.howell.utils;

import java.util.ArrayList;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import com.howell.entityclass.VODRecord;

import android.util.Log;
import bean.TurnGetCamBean;
import bean.TurnGetRecordedFilesBean;
import bean.Subscribe;
import bean.TurnConnectAckBean;

public class JsonUtil {

	public static String subScribeJson(Subscribe subscribe){

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

	public static String getCamJson(TurnGetCamBean bean){

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

	public static String getRecordFilesJson(TurnGetRecordedFilesBean bean){

		JSONObject object = null;
		object = new JSONObject();

		try {
			object.put("device_id", bean.getDeviceId());
			object.put("channel", bean.getChannel());
			object.put("begin", bean.getBegin());
			object.put("end", bean.getEnd());
		} catch (JSONException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

		return object.toString();
	}

	public static ArrayList<VODRecord> parseRecordFileList(JSONObject obj) throws JSONException{
		ArrayList<VODRecord> list = new ArrayList<VODRecord>();
		int code = obj.getInt("code");
		String deviceId = obj.getString("device_id");
		int channel = obj.getInt("channel");
		int recordedfileCount = obj.getInt("recordedfile_count");
		JSONArray array = obj.getJSONArray("recordedfile");

		for (int i = 0; i < array.length(); i++) {
			JSONObject bar =  (JSONObject) array.get(i);
			String startTime = bar.getString("begin");
			String endTime = bar.getString("end");
			Log.i("123",i+ ": "+"startTime: "+startTime+" endTime:"+endTime);
			VODRecord vod = new VODRecord();
			vod.setTimeZoneStartTime(startTime);
			vod.setTimeZoneEndTime(endTime);
			list.add(vod);
		}
		
		return list;
	}
	


}
