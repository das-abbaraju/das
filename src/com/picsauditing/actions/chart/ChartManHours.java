package com.picsauditing.actions.chart;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

import com.picsauditing.search.SelectSQL;
import com.picsauditing.util.chart.ChartSingleSeries;
import com.picsauditing.util.chart.ChartType;
import com.picsauditing.util.chart.DataRow;
import com.picsauditing.util.chart.FusionChart;
import com.picsauditing.util.chart.Set;
import com.picsauditing.util.chart.TrendLine;

@SuppressWarnings("serial")
public class ChartManHours extends ChartSSAction {
	private float min = 0.8f;
	private float max = 1.2f;
	private float hours = 2080;
	private int conID;
	
	public String execute() throws Exception {
		if (!forceLogin())
			return LOGIN;
		
		super.execute();
		
		if (conID == 0) {
			addActionError("Missing contractors");
			return SUCCESS;
		}
		
		output = FusionChart.createChart("charts/" + ChartType.Column3D.toString() + ".swf", "", output, 
				ChartType.Column3D.toString(), 450, 300, false, false);
		
		return CHART_XML;
	}

	@Override
	public ChartSingleSeries buildChart() throws Exception {
		float lowTrend = min * hours;
		float highTrend = max * hours;
		
		chart.setCaption("Total Man Hours");
		
		chart.setXAxisName("Years");
		chart.setyAxisName("Hours");
		
		SelectSQL sql = new SelectSQL("osha_audit oa");
		sql.addField("ca.auditFor AS label");
		sql.addField("oa.manHours AS value");
		sql.addField("pqf.answer + pqf2.answer AS link");
		sql.addJoin("JOIN contractor_audit ca ON ca.id = oa.auditID");
		sql.addJoin("JOIN pqfdata pqf ON pqf.auditID = ca.id");
		sql.addJoin("JOIN pqfdata pqf2 ON pqf2.auditID = ca.id");
		sql.addWhere("ca.conID = " + conID);
		sql.addWhere("oa.SHAType = 'OSHA'");
		sql.addWhere("pqf.questionID = 2447");
		sql.addWhere("pqf2.questionID = 2448");
		
		Calendar cal = Calendar.getInstance();
		cal.setTime(new Date());
		cal.add(Calendar.YEAR, -7);
		
		sql.addWhere("ca.auditFor > " + cal.get(Calendar.YEAR));
		sql.addGroupBy("label");
		sql.addOrderBy("label");
			
		ChartDAO db = new ChartDAO();
		List<DataRow> data = db.select(sql.toString());
		List<Float> employees = new ArrayList<Float>();
		
		for (DataRow row : data) {
			employees.add(Float.parseFloat(row.getLink()));
			row.setLink(null);
			chart.addSet(new Set(row));
		}
		
		float avgEmployees = 0;
		for (Float f : employees)
			avgEmployees += f;
		
		avgEmployees = avgEmployees / employees.size();
		
		lowTrend = lowTrend * avgEmployees;
		highTrend = highTrend * avgEmployees;
		
		TrendLine low = new TrendLine(lowTrend, lowTrend, "Min (" + (int) lowTrend + " hours)");
		TrendLine high = new TrendLine(highTrend, highTrend, "Max (" + (int) highTrend + " hours)");
		
		low.setColor("990000");
		high.setColor("009900");
		
		chart.addTrendLine(low);
		chart.addTrendLine(high);
		
		return chart;
	}
	
	public void setConID(int conID) {
		this.conID = conID;
	}
	
	public float getMin() {
		return min;
	}
	
	public void setMin(float min) {
		this.min = min;
	}
	
	public float getMax() {
		return max;
	}
	
	public void setMax(float max) {
		this.max = max;
	}
	
	public float getHours() {
		return hours;
	}
	
	public void setHours(float hours) {
		this.hours = hours;
	}
}
