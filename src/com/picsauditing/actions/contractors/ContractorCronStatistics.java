package com.picsauditing.actions.contractors;

import com.picsauditing.actions.PicsActionSupport;
import com.picsauditing.cron.CronMetricsAggregator;
import com.picsauditing.cron.CronReportingPeriod;

@SuppressWarnings("serial")
public class ContractorCronStatistics extends PicsActionSupport {
	CronMetricsAggregator metricsAggregator;

	public ContractorCronStatistics(CronMetricsAggregator metricsAggregator) {
		this.metricsAggregator = metricsAggregator;
	}

	public String execute() throws Exception {
		loadPermissions();
		if (!permissions.isLoggedIn())
			return LOGIN_AJAX;
		
		return SUCCESS;
	}

	public CronMetricsAggregator getMetricsAggregator() {
		return metricsAggregator;
	}

	public void setMetricsAggregator(CronMetricsAggregator metricsAggregator) {
		this.metricsAggregator = metricsAggregator;
	}
	
	public CronReportingPeriod[] getCronPeriods() {
		return CronReportingPeriod.values();
	}
	
}
