package com.howell.protocol;

import java.util.ArrayList;

/**
 * @author 霍之昊 
 *
 * 类说明
 */
public class QueryNoticesRes {
	private String result;
	private int pageNo;
	private int pageCount;
	private int recordCount;
	private ArrayList<NoticeList> noticeList;
	public QueryNoticesRes(String result, int pageNo, int pageCount,
			int recordCount, ArrayList<NoticeList> noticeList) {
		super();
		this.result = result;
		this.pageNo = pageNo;
		this.pageCount = pageCount;
		this.recordCount = recordCount;
		this.noticeList = noticeList;
	}
	public QueryNoticesRes() {
		super();
	}
	public String getResult() {
		return result;
	}
	public void setResult(String result) {
		this.result = result;
	}
	public int getPageNo() {
		return pageNo;
	}
	public void setPageNo(int pageNo) {
		this.pageNo = pageNo;
	}
	public int getPageCount() {
		return pageCount;
	}
	public void setPageCount(int pageCount) {
		this.pageCount = pageCount;
	}
	public int getRecordCount() {
		return recordCount;
	}
	public void setRecordCount(int recordCount) {
		this.recordCount = recordCount;
	}
	public ArrayList<NoticeList> getNodeList() {
		return noticeList;
	}
	public void setNoticeList(ArrayList<NoticeList> noticeList) {
		this.noticeList = noticeList;
	}
	
}
