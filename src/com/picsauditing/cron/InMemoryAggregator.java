package com.picsauditing.cron;

import java.util.Iterator;
import java.util.List;
import java.util.Vector;

public class InMemoryAggregator implements CronMetricsAggregator {

	private static boolean running  = false;
	private static List<CronMetrics> stats = new Vector<CronMetrics>();

	
	private CronMetrics currentMetric = null;
	private long startTime = 0L;
	
	
	@Override
	public void addContractor(int id, long time) {
		currentMetric.addContractor(time);
	}

	@Override
	public CronMetrics getMetrics(CronReportingPeriod timeFrame) {
		
		CronMetrics summary = new CronMetrics();
		
		for( Iterator<CronMetrics> iterator = stats.iterator(); iterator.hasNext();) {
			CronMetrics metric = iterator.next();

			if( ( System.currentTimeMillis() - metric.getEndTime().getTime() ) > CronReportingPeriod.TwentyFourHours.getPeriod() ) {
				iterator.remove();
				break;
			}
			
			summary.add(metric);
		}
		
		return summary;
	}

	@Override
	public boolean startJob() {
		if( !startJobSync() )
			return false;
		
		startTime = System.currentTimeMillis();
		currentMetric = new CronMetrics();
		currentMetric.setCronJobs(1);
		return true;
	}

	@Override
	public void stopJob() {
		currentMetric.setTotalCronTime(System.currentTimeMillis() - startTime);
		stopJobSync(currentMetric);
	}

	
	private synchronized static boolean startJobSync() {
		if( running ) return false;
		running = true;
		return true;
	}
	private synchronized static void stopJobSync(CronMetrics current) {
		stats.add(current);		
		running = false;
	}

}
