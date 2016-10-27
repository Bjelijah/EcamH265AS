package com.howell.utils;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;

import android.util.Log;

import com.howell.entityclass.VODRecord;
import com.howell.protocol.VodSearchRes;

public class PlaybackUtils {
	private int nowPageCount;
	
	private int totalPageForNewVer;//一共有多少页
	private int nowPageCountForNewVer;//当前是多少页
	
	public PlaybackUtils(){
		nowPageCount = 0;
		totalPageForNewVer = 1;
		nowPageCountForNewVer = 1;
	}
	
	public ArrayList<VODRecord> getMoreVideoList(InviteUtils client,String startTime,String endTime){
		ArrayList<VODRecord> mList = new ArrayList<VODRecord>();
		System.out.println("nowPage:"+nowPageCount);
		if(nowPageCount > 0){
			VodSearchRes vodSearchRes = client.getVodSearchReq(nowPageCount,startTime,endTime,0);
			nowPageCount = nowPageCount - 1;
			mList = vodSearchRes.getRecord();
			sort(mList);
			addTitleFlag(mList);
		}
		return mList;
	}
	
	public ArrayList<VODRecord> getVideoList(InviteUtils client,String startTime,String endTime){
		ArrayList<VODRecord> mList = new ArrayList<VODRecord>();
    	VodSearchRes vodSearchRes = client.getVodSearchReq(1,startTime,endTime,0);
    	int pageCount = vodSearchRes.getPageCount();
    	nowPageCount = pageCount;
    	if(pageCount == 0){
    		return mList;
    	}
    	//int recordCount = vodSearchRes.getRecordCount();
        mList = client.getVodSearchReq(pageCount, startTime, endTime,0).getRecord();
        if(pageCount <= 1){
        	nowPageCount = 0;
        }else if(pageCount > 1){
        	if(mList.size() < 12){
        		mList.addAll(client.getVodSearchReq(pageCount - 1, startTime, endTime,0).getRecord());
        		nowPageCount = nowPageCount - 2;
        	}else{
        		nowPageCount = nowPageCount - 1;
        	}
        }
        sort(mList);
        addTitleFlag(mList);
        return mList;
    }
	
	public ArrayList<VODRecord> getNewVerVideoList(InviteUtils client,String startTime,String endTime){
		ArrayList<VODRecord> mList = new ArrayList<VODRecord>();
		if(nowPageCountForNewVer <= totalPageForNewVer){
			VodSearchRes vodSearchRes = client.getVodSearchReq(nowPageCountForNewVer,startTime,endTime,20);
			int pageCount = vodSearchRes.getPageCount();
	    	totalPageForNewVer = pageCount;
	    	mList.addAll(vodSearchRes.getRecord());
	    	nowPageCountForNewVer++;
		}
		addTitleFlag(mList);
		return mList;
	}
	
	public void clearResource(){
		nowPageCountForNewVer = 1;
		totalPageForNewVer = 1;
	}
	
	@SuppressWarnings({ "unchecked", "rawtypes" })
	private void sort(ArrayList<VODRecord> arrayList){
		Collections.sort(arrayList, new Comparator() {
            @Override
            	public int compare(Object o1, Object o2) {
            		VODRecord v1 = (VODRecord)o1;
            		VODRecord v2 = (VODRecord)o2;
            		return v2.getStartTime().compareTo(v1.getStartTime());
                }
        });
	}
	
	public void addTitleFlag(ArrayList<VODRecord> mList){
		 for(int i = 1 ; i < mList.size() ; i++){
//	        	System.out.println("A:"+mList.get(i).getTimeZoneStartTime());
//	        	System.out.println("B"+mList.get(i-1).getTimeZoneStartTime());
	        	if(!mList.get(i).getTimeZoneStartTime().substring(0, 10).equals(mList.get(i-1).getTimeZoneStartTime().substring(0, 10))){
//	        		System.out.println("A:"+i+","+mList.get(i).getTimeZoneStartTime());
//	            	System.out.println("B"+i+","+mList.get(i-1).getTimeZoneStartTime());
	        		mList.get(i).setHasTitle(true);
	        	}
	        }
	}
	public int getNowPageCount(){
		return nowPageCount;
	}
}
