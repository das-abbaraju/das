package com.picsauditing.actions.chart;

import com.picsauditing.actions.PicsActionSupport;
import com.picsauditing.util.chart.Chart;
import com.picsauditing.util.chart.ChartSingleSeries;
import com.picsauditing.util.chart.Set;

@SuppressWarnings("serial")
public class ChartAction extends PicsActionSupport {
	protected String FCTime;

	public String getFCTime() {
		return FCTime;
	}

	public void setFCTime(String time) {
		FCTime = time;
	}

	public String execute() throws Exception {
		loadPermissions();
		if (!permissions.isLoggedIn()) {
			error("Not Logged In");
			return CHART_XML;
		}
		Chart chart;
		try {
			chart = buildChart();
		} catch (Exception e) {
			System.out.println(e.getMessage());
			error("Error Getting Data");
			return CHART_XML;
		}
		
		if (chart.hasData()) {
			output = chart.toString();
			return CHART_XML;
		} else {
			error("No Data");
			return CHART_XML;
		}
	}
	
	public Chart buildChart() throws Exception {
		return new ChartSingleSeries();
	}
	
	public void error(String error) {
		ChartSingleSeries chart = new ChartSingleSeries();
		Set set = new Set();
		set.setLabel(error);
		set.setValue(1);
		chart.getSets().add(set);
		output = chart.toString();
	}
}
