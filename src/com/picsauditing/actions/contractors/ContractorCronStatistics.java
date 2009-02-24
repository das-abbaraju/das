package com.picsauditing.actions.contractors;

import java.util.ArrayList;
import java.util.List;

import com.picsauditing.actions.PicsActionSupport;
import com.picsauditing.cron.CronMetrics;
import com.picsauditing.cron.CronMetricsAggregator;
import com.picsauditing.cron.CronReportingPeriod;

@SuppressWarnings("serial")
public class ContractorCronStatistics extends PicsActionSupport {
	CronMetricsAggregator metricsAggregator;
	List<CronMetrics> cronMList = new ArrayList<CronMetrics>(); 

	public ContractorCronStatistics(CronMetricsAggregator metricsAggregator) {
		this.metricsAggregator = metricsAggregator;
	}

	public String execute() throws Exception {
		loadPermissions();
		if (!permissions.isLoggedIn())
			return LOGIN_AJAX;
		
		cronMList.add(0, metricsAggregator.getMetrics(CronReportingPeriod.TenMinutes));
		cronMList.add(1, metricsAggregator.getMetrics(CronReportingPeriod.ThirtyMinutes));
		cronMList.add(2, metricsAggregator.getMetrics(CronReportingPeriod.OneHour));
		cronMList.add(3, metricsAggregator.getMetrics(CronReportingPeriod.TwoHours));
		cronMList.add(4, metricsAggregator.getMetrics(CronReportingPeriod.ThreeHours));
		cronMList.add(5, metricsAggregator.getMetrics(CronReportingPeriod.SixHours));
		cronMList.add(6, metricsAggregator.getMetrics(CronReportingPeriod.TwelveHours)); 		
		cronMList.add(7, metricsAggregator.getMetrics(CronReportingPeriod.TwentyFourHours)); 		
		
		return SUCCESS;
	}

	public List<CronMetrics> getCronMList() {
		return cronMList;
	}

	public void setCronMList(List<CronMetrics> cronMList) {
		this.cronMList = cronMList;
	}

}
