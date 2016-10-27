package com.howell.utils;

import com.howell.protocol.QueryNoticesReq;
import com.howell.protocol.QueryNoticesRes;
import com.howell.protocol.SoapManager;

/**
 * @author 霍之昊 
 *
 * 类说明
 */
public class NoticePagingUtils {
	private int curPageNo;		//当前页数
	private int totalPageCount;	//总共页数
	
	private int pageSize = 20;	//每页获取个数
	
	private SoapManager mSoapManager ;
	
	public NoticePagingUtils() {
		// TODO Auto-generated constructor stub
		curPageNo = 1;
		totalPageCount = 1;
		mSoapManager = SoapManager.getInstance();
		
	}
	
	public void clearResource(){
		curPageNo = 1;
		totalPageCount = 1;
	}
	
	public QueryNoticesRes getQueryNotices(){
		if(curPageNo > totalPageCount){
			return null;
		}
		QueryNoticesRes res = mSoapManager.getQueryNoticesRes(new QueryNoticesReq(mSoapManager.getLoginResponse().getAccount(),mSoapManager.getLoginResponse().getLoginSession(),curPageNo,pageSize));
		if(curPageNo == 1 && res != null && res.getResult() != null && res.getResult().equals("OK")){
			totalPageCount = res.getPageCount();
			System.out.println("totalPageCount:"+totalPageCount);
		}
		curPageNo++;
		return res;
	}
	
	
}
