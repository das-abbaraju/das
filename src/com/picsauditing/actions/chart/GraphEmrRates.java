package com.picsauditing.actions.chart;

import java.util.List;

import org.apache.commons.beanutils.LazyDynaBean;

import com.picsauditing.jpa.entities.AuditQuestion;
import com.picsauditing.search.Database;
import com.picsauditing.search.SelectSQL;
import com.picsauditing.util.PermissionQueryBuilder;
import com.picsauditing.util.chart.ChartMultiSeries;
import com.picsauditing.util.chart.ChartType;
import com.picsauditing.util.chart.DataRow;
import com.picsauditing.util.chart.FusionChart;
import com.picsauditing.util.chart.MultiSeriesConverterHistogram;
import com.picsauditing.util.chart.Set;

public class GraphEmrRates extends ChartMSAction {
	private ChartType chartType = ChartType.MSLine;
	private String flashChart;
	
	private boolean show07 = true;
	private boolean show06 = true;
	private boolean show05 = true;
	private boolean show04 = true;
	
	public String execute() {
		super.execute();
		flashChart = FusionChart.createChart("charts/" + chartType.toString() + 
				".swf", "", message, chartType.toString(), 
				600, 500, false, false);
		return SUCCESS;
	}

	@Override
	public ChartMultiSeries buildChart() throws Exception {
		chart.setCaption("EMR Rates");
		chart.setXAxisName("EMR Rate");
		chart.setYAxisName("Contractors");

		SelectSQL sql = new SelectSQL("accounts a");
		sql.addJoin("JOIN contractor_audit pqf ON pqf.conID = a.id");
		sql.addJoin("JOIN pqfdata d ON d.auditID = pqf.auditID");
		sql.addField("d.questionID as series");
		sql.addField("floor(d.verifiedAnswer*10)/10 as label");
		sql.addField("count(*) as value");
		sql.addWhere("d.verifiedAnswer > 0");
		sql.addWhere("pqf.auditStatus = 'Active' AND pqf.auditTypeID = 1");
		{
			String questionList = "0";
			if (show04) questionList += ","+AuditQuestion.EMR04;
			if (show05) questionList += ","+AuditQuestion.EMR05;
			if (show06) questionList += ","+AuditQuestion.EMR06;
			if (show07) questionList += ","+AuditQuestion.EMR07;
			sql.addWhere("d.questionID IN ("+questionList+")");
		}
		sql.addGroupBy("questionID, label");
		sql.addOrderBy("questionID DESC, label");
		
		PermissionQueryBuilder permQuery = new PermissionQueryBuilder(permissions);
		sql.addWhere("1 " +permQuery.toString());

		ChartDAO db = new ChartDAO();
		List<DataRow> data = db.select(sql.toString());
		for (DataRow row : data) {
			int questionID = Integer.parseInt(row.getSeries());
			int year = AuditQuestion.getEmrYear(questionID);
			row.setSeries(year + " EMR");
			
			float labelEnd = Float.parseFloat(row.getLabel()) + 0.1F;
			row.setLink("ReportEmrRates.action?year="+year+"%26amp;minRate="+row.getLabel()+"%26amp;maxRate="+labelEnd);
		}

		MultiSeriesConverterHistogram converter = new MultiSeriesConverterHistogram();
		
		converter.setMaxCategory(2);
		converter.setMinCategory(0);
		converter.setCategoryDifference(0.1F);
		converter.setChart(chart);

		converter.addData(data);
		return chart;
	}
	
	static public ChartType[] getChartTypeList() {
		ChartType[] charts = {
				ChartType.MSArea, 
				ChartType.MSBar2D,
				ChartType.MSBar3D,
				ChartType.MSColumn2D,
				ChartType.MSColumn3D,
				ChartType.MSLine};
		return charts;
	}

	public String getFlashChart() {
		return flashChart;
	}

	public void setFlashChart(String flashChart) {
		this.flashChart = flashChart;
	}

	public ChartType getChartType() {
		return chartType;
	}

	public void setChartType(ChartType chartType) {
		this.chartType = chartType;
	}

	public boolean isShow07() {
		return show07;
	}

	public void setShow07(boolean show07) {
		this.show07 = show07;
	}

	public boolean isShow06() {
		return show06;
	}

	public void setShow06(boolean show06) {
		this.show06 = show06;
	}

	public boolean isShow05() {
		return show05;
	}

	public void setShow05(boolean show05) {
		this.show05 = show05;
	}

	public boolean isShow04() {
		return show04;
	}

	public void setShow04(boolean show04) {
		this.show04 = show04;
	}
	
	
}
