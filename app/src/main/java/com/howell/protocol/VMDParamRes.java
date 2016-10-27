package com.howell.protocol;

import com.howell.entityclass.VMDGrid;

public class VMDParamRes {
	private String result_;
	private boolean enabled_;
	private int sensitivity_;
	private int start_trigger_time_;
	private int end_trigger_time_;
	private int rows_;
	private int cols_;
	private VMDGrid grids_;
	private String account_;
	private String login_session_;
	private String dev_id_;
	private int channel_no_;
	
	    public String getAccount() {
	        return account_;
	    }

	    public void setAccount(String account) {
	        account_ = account;
	    }

	    public String getLoginSession() {
	        return login_session_;
	    }

	    public void setLoginSession(String loginSession) {
	        login_session_ = loginSession;
	    }

	    public String getDevID() {
	        return dev_id_;
	    }

	    public void setDevID(String devID) {
	        dev_id_ = devID;
	    }

	    public int getChannelNo() {
	        return channel_no_;
	    }

	    public void setChannelNo(int channelNo) {
	        channel_no_ = channelNo;
	    }

	    public String getResult() {
	        return result_;
	    }

	    public void setResult(String result) {
	        this.result_ = result;
	    }

	    public boolean getEnabled() {
	        return enabled_;
	    }

	    public void setEnabled(boolean enabled) {
	        enabled_ = enabled;
	    }

	    public int getSensitivity() {
	        return sensitivity_;
	    }

	    public void setSensitivity(int sensi) {
	        sensitivity_ = sensi;
	    }

	    public int getStartTriggerTime() {
	        return start_trigger_time_;
	    }

	    public void setStartTriggerTime(int value) {
	        start_trigger_time_ =  value;
	    }
	    
	    public int getEndTriggerTime() {
	        return end_trigger_time_;
	    }

	    public void setEndTriggerTime(int value) {
	        end_trigger_time_ =  value;
	    }

	    public int getRows() {
	        return rows_;
	    }

	    public int getColumns() {
	    	return cols_;
	    }
	    
//	    public void setRows(int rows) {
//	    	if (rows_!=rows) {
//	    		rows_ = rows;
//	    		grids_ = new String[rows];
//	    	}
//	    }
//	    
//	    public void setColumns(int columns) {
//	    	cols_ = columns;
//	    }
//
//	    public void setRowColumn(int rows, int columns) {
//	    	if (rows_!=rows) {
//	    		grids_ = new String[rows];
//	    	}
//	    	rows_ = rows;
//	    	cols_ = columns;
//	    }
	    
	    public VMDGrid getGrids() {
	        return grids_;
	    }
	    
	    public void setGrids(VMDGrid grids) {
	    	grids_ = grids;
	    }
	    
	    @Override
	    public String toString() {
	        return "VMDParamRes [result=" + result_ + "]";
	    }

}
