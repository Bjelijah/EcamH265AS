package com.howell.entityclass;
/**
 * @author 霍之昊 
 *
 * 类说明
 */
public class VMDGrid
{
	private String [] rows_;
	public VMDGrid(String[] rows_) {
		super();
		this.rows_ = rows_;
	}

	public String[] getRows() {
		return rows_;
	}
	
	public void setRows(String[] rows) {
		rows_ = rows;
	}
}