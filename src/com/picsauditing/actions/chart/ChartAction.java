package com.picsauditing.actions.chart;

import com.picsauditing.actions.PicsActionSupport;
import com.picsauditing.util.chart.Chart;
import com.picsauditing.util.chart.Set;

public class ChartAction extends PicsActionSupport {
	protected Chart chart = new Chart();
	protected String FCTime;

	public Chart getChart() {
		return chart;
	}

	public void setChart(Chart chart) {
		this.chart = chart;
	}

	public String getFCTime() {
		return FCTime;
	}

	public void setFCTime(String time) {
		FCTime = time;
	}

	public String execute() {
		if (chart.getSets().size() == 0 && chart.getDataSets().size() == 0) {
			Set set = new Set();
			set.setLabel("No Data");
			set.setValue(0);
			chart.addSet(set);
		}
		message = chart.toString();
		
		return SUCCESS;
	}
}
