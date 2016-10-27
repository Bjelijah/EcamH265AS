package com.howell.protocol;

import java.util.ArrayList;

import com.howell.entityclass.VODRecord;

public class VodSearchRes {

    private String result;
    private int PageNo;
    private int PageCount;
    private int RecordCount;
    private ArrayList<VODRecord> Records;

    public String getResult() {
        return result;
    }

    public void setResult(String result) {
        this.result = result;
    }

    public int getPageNo() {
        return PageNo;
    }

    public void setPageNo(int pageNo) {
        PageNo = pageNo;
    }

    public int getPageCount() {
        return PageCount;
    }

    public void setPageCount(int pageCount) {
        PageCount = pageCount;
    }

    public int getRecordCount() {
        return RecordCount;
    }

    public void setRecordCount(int recordCount) {
        RecordCount = recordCount;
    }

    public ArrayList<VODRecord> getRecord() {
        return Records;
    }

    public void setRecord(ArrayList<VODRecord> records) {
        Records = records;
    }

	@Override
	public String toString() {
		return "VodSearchRes [result=" + result + ", PageNo=" + PageNo
				+ ", PageCount=" + PageCount + ", RecordCount=" + RecordCount
				+ "]";
	}
    
}
