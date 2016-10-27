package com.howell.entityclass;

import java.io.Serializable;

public class VODRecord implements Serializable {

    /**
     * 
     */
    private static final long serialVersionUID = 4722894254660276688L;

    private String StartTime;
    private String EndTime;
    private long FileSize;
    private String Desc;
    private boolean isWatched;
    private boolean hasTitle;
    private String TimeZoneStartTime;
    private String TimeZoneEndTime;

    public VODRecord(String startTime, String endTime, long fileSize,
            String desc) {
        super();
        StartTime = startTime;
        EndTime = endTime;
        FileSize = fileSize;
        Desc = desc;
        isWatched = false;
        hasTitle = false;
    }
    
    
    public VODRecord() {
		super();
	}


	public String getTimeZoneStartTime() {
		return TimeZoneStartTime;
	}


	public void setTimeZoneStartTime(String timeZoneStartTime) {
		TimeZoneStartTime = timeZoneStartTime;
	}


	public String getTimeZoneEndTime() {
		return TimeZoneEndTime;
	}


	public void setTimeZoneEndTime(String timeZoneEndTime) {
		TimeZoneEndTime = timeZoneEndTime;
	}


	public boolean hasTitle() {
		return hasTitle;
	}

	public void setHasTitle(boolean hasTitle) {
		this.hasTitle = hasTitle;
	}

	public boolean isWatched() {
		return isWatched;
	}


	public void setWatched(boolean isWatched) {
		this.isWatched = isWatched;
	}

    public String getStartTime() {
        return StartTime;
    }

    public void setStartTime(String startTime) {
        StartTime = startTime;
    }

    public String getEndTime() {
        return EndTime;
    }

    public void setEndTime(String endTime) {
        EndTime = endTime;
    }

    public long getFileSize() {
        return FileSize;
    }

    public void setFileSize(long fileSize) {
        FileSize = fileSize;
    }

    public String getDesc() {
        return Desc;
    }

    public void setDesc(String desc) {
        Desc = desc;
    }


	@Override
	public String toString() {
		return "VODRecord [StartTime=" + StartTime + ", EndTime=" + EndTime
				+ ", FileSize=" + FileSize + ", Desc=" + Desc + ", isWatched=" + isWatched + ", hasTitle="
				+ hasTitle + ", TimeZoneStartTime=" + TimeZoneStartTime
				+ ", TimeZoneEndTime=" + TimeZoneEndTime + "]";
	}

}
