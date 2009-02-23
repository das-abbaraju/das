package com.picsauditing.cron;

public interface CronMetricsAggregator {

	public boolean startJob();
	public void stopJob();

	public void addContractor( int id, long time);
	public CronMetrics getMetrics( CronReportingPeriod timeFrame );
}
