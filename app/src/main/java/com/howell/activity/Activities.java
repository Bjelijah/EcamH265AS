package com.howell.activity;

import java.util.HashMap;
import java.util.Map;

import android.app.Activity;

public class Activities {
	
    public Map<String,Activity> mActivityList = new HashMap<String,Activity>();
    
    private static Activities sInstance = new Activities();

    public static Activities getInstance() {
        return sInstance;
    }

	public Map<String,Activity> getmActivityList() {
		return mActivityList;
	}
	
	public void clearActivities(){
		mActivityList.clear();
	}
	
	public void addActivity(String name,Activity a){
		mActivityList.put(name, a);
	}
	
	public void removeActivity(String name){
		mActivityList.remove(name);
	}

//	@Override
//	public String toString() {
//		for(Activity a:mActivityList){
//			System.out.println(a.getLocalClassName());
//		}
//		return null;
//	}

//	public static void setmActivityList(ArrayList<Activity> mActivityList) {
//		Activitys.mActivityList = mActivityList;
//	}
    
}
