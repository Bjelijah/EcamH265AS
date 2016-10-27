package com.howell.ecamera.cameraupdatedetective;

import com.howell.activity.CameraList;

public class DeviceVersionDetective extends Observable{
    private static DeviceVersionDetective sInstance = new DeviceVersionDetective();
	
    public static DeviceVersionDetective getInstance() {
        return sInstance;
    }
	
	@Override
	public void notifyObserver(String name) {
		// TODO Auto-generated method stub
		if(map.containsKey(name)){
			((CameraList) map.get(name)).update();
		}
	}
    
}
