package com.howell.utils;

import java.util.ArrayList;

import com.howell.entityclass.VODRecord;

public class H265PlaybackUtils extends PlaybackUtils{

	@Override
	public ArrayList<VODRecord> getMoreVideoList(InviteUtils client, String startTime, String endTime) {
		// TODO Auto-generated method stub
		ArrayList<VODRecord> mList = new ArrayList<VODRecord>();
		

		return super.getMoreVideoList(client, startTime, endTime);
	}

	@Override
	public ArrayList<VODRecord> getVideoList(InviteUtils client, String startTime, String endTime) {
		// TODO Auto-generated method stub
		ArrayList<VODRecord> mList = new ArrayList<VODRecord>();
		
		
		
		return super.getVideoList(client, startTime, endTime);
	}

	
	
	
	
}
