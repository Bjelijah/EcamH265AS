package com.howell.ecamera.cameraupdatedetective;

import java.util.HashMap;

import android.app.Activity;

/**
 * @author 霍之昊 
 *
 * 类说明：被观察者
 */
public abstract class Observable {
	
	public HashMap<String, Activity> map = new HashMap<String, Activity>();
	
	public void attachObserver(String name , Activity activity){
		if(!map.containsKey(name)){
			map.put(name, activity);
		}
	}
	
	public void detachObserver(String name){
		if(map.containsKey(name)){
			map.clear();
		}
	}
	
	public abstract void notifyObserver(String name);
}
