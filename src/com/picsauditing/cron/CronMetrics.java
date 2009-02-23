package com.picsauditing.cron;

public class CronMetrics {
	private int cronJobs = 0;
	private int totalCronTime = 0;
	private int averageCronTime = 0;
	
	private int totalContractors = 0;
	private long minContractorTime = 0;
	private long maxContractorTime = 0;
	private long averageContractorTime = 0L;
	private long medianContractorTime = 0L;
	public int getCronJobs() {
		return cronJobs;
	}
	public void setCronJobs(int cronJobs) {
		this.cronJobs = cronJobs;
	}
	public int getTotalCronTime() {
		return totalCronTime;
	}
	public void setTotalCronTime(int totalCronTime) {
		this.totalCronTime = totalCronTime;
	}
	public int getAverageCronTime() {
		return averageCronTime;
	}
	public void setAverageCronTime(int averageCronTime) {
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
	public long getMedianContractorTime() {
		return medianContractorTime;
	}
	public void setMedianContractorTime(long medianContractorTime) {
		this.medianContractorTime = medianContractorTime;
	}
}
