package com.howell.activity;

import java.util.HashMap;
import java.util.Map;

import com.howell.entityclass.NodeDetails;

public class DeviceManager {
	private static DeviceManager sInstance = new DeviceManager();
	private Map<String, NodeDetails> map = new HashMap<String, NodeDetails>();
	public static DeviceManager getInstance() {
	    return sInstance;
	}
	 
	public void addMember(NodeDetails device){
		map.put(device.getDevID(), device);
	}
	
	public void clearMember(){
		map.clear();
	}

	public Map<String, NodeDetails> getMap() {
		return map;
	}

}
