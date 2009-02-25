package com.picsauditing.cron;

import java.util.Date;

public class CronMetrics {
	private int cronJobs = 0;
	private long totalCronTime = 0;
	private long averageCronTime = 0;
	private Date endTime = null;
	
	private int totalContractors = 0;
	private long minContractorTime = 0;
	private long maxContractorTime = 0;
	private long averageContractorTime = 0L;
	
	public int getCronJobs() {
		return cronJobs;
	}
	public void setCronJobs(int cronJobs) {
		this.cronJobs = cronJobs;
	}
	public long getTotalCronTime() {
		return totalCronTime;
	}
	public void setTotalCronTime(long totalCronTime) {
		this.totalCronTime = totalCronTime;
	}
	public long getAverageCronTime() {
		return averageCronTime;
	}
	public void setAverageCronTime(long averageCronTime) {
		this.averageCronTime = averageCronTime;
	}
	public int getTotalContractors() {
		return totalContractors;
	}
	public void setTotalContractors(int totalContractors) {
		this.totalContractors = totalContractors;
	}
	public long getMinContractorTime() {
		return minContractorTime;
	}
	public void setMinContractorTime(long minContractorTime) {
		this.minContractorTime = minContractorTime;
	}
	public long getMaxContractorTime() {
		return maxContractorTime;
	}
	public void setMaxContractorTime(long maxContractorTime) {
		this.maxContractorTime = maxContractorTime;
	}
	public long getAverageContractorTime() {
		return averageContractorTime;
	}
	public void setAverageContractorTime(long averageContractorTime) {
		this.averageContractorTime = averageContractorTime;
	}
	
	public Date getEndTime() {
		return endTime;
	}
	public void setEndTime(Date endTime) {
		this.endTime = endTime;
	}

	public void addContractor( long time ) {
		totalContractors++;
		
		if( minContractorTime == 0 ) {
			minContractorTime = time;
		}
		else if( time < minContractorTime ) {
			minContractorTime = time;
		}

		if( time > maxContractorTime ) {
			maxContractorTime = time;
		}
	}
	
	public void add( CronMetrics other ) {

		cronJobs += other.getCronJobs();
		totalCronTime += other.getTotalCronTime();
		totalContractors += other.getTotalContractors();
		
		if( minContractorTime == 0 ) {
			minContractorTime = other.minContractorTime;
		}
		else if( other.getMinContractorTime() < minContractorTime ) {
			minContractorTime = other.getMinContractorTime();
		}

		if( other.getMaxContractorTime() > maxContractorTime ) {
			maxContractorTime = other.getMaxContractorTime();
		}

		averageCronTime = totalCronTime / cronJobs;
		
		if( totalContractors != 0 ) {
			averageContractorTime = totalCronTime / totalContractors;
		}
	}
}
