package com.picsauditing.actions.report;

import java.util.List;

import org.apache.commons.beanutils.BasicDynaBean;

import com.picsauditing.actions.chart.ChartAction;
import com.picsauditing.jpa.entities.AuditQuestion;
import com.picsauditing.search.Database;
import com.picsauditing.search.SelectSQL;
import com.picsauditing.util.PermissionQueryBuilder;
import com.picsauditing.util.chart.ChartType;
import com.picsauditing.util.chart.DataSet;
import com.picsauditing.util.chart.FusionChart;
import com.picsauditing.util.chart.Set;

public class GraphEmrRates extends ChartAction {
	private ChartType chartType = ChartType.MSLine;
	private String flashChart;
	
	private boolean show07 = true;
	private boolean show06 = true;
	private boolean show05 = true;
	private boolean show04 = true;
	
	public String execute() {
		try {
			loadPermissions();
			if (!permissions.isLoggedIn())
				throw new Exception();
		} catch (Exception e) {
			Set set = new Set();
			set.setLabel("Authentication ERROR");
			set.setValue(1);
			chart.getSets().add(set);
			return SUCCESS;
		}

		SelectSQL sql = new SelectSQL("accounts a");
		sql.addJoin("JOIN pqfdata d ON a.id = d.conID");
		sql.addField("d.questionID");
		sql.addField("floor(d.verifiedAnswer*10)/10 as label");
		sql.addField("count(*) as value");
		sql.addWhere("d.verifiedAnswer > 0");
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

		try {
			Database db = new Database();
			List<BasicDynaBean> data = db.select(sql.toString(), false);
			chart.setMultiSeries(true);
			chart.setMaxCategory(2);
			chart.setMinCategory(0);
			chart.setCategoryDifference(0.1F);
			chart.setCaption("EMR Rates");
			chart.setXAxisName("EMR Rate");
			chart.setYAxisName("Contractors");

			int id = 0;
			int year = 0;
			DataSet dataSet = new DataSet();
			for (BasicDynaBean row : data) {
				int questionID = (Integer)row.get("questionID");
				if (questionID != id) {
					dataSet = new DataSet();
					chart.getDataSets().add(dataSet);
					switch (questionID) {
					case AuditQuestion.EMR07:
						year = 2007;
						break;
					case AuditQuestion.EMR06:
						year = 2006;
						break;
					case AuditQuestion.EMR05:
						year = 2005;
						break;
					case AuditQuestion.EMR04:
						year = 2004;
						break;
					}
					String seriesName = year + " EMR";
					dataSet.setSeriesName(seriesName);
					id = questionID;
					dataSet.setShowValues(false);
				}

				Float category = Float.parseFloat(row.get("label").toString());
				Float value = Float.parseFloat(row.get("value").toString());
				
				if (category < chart.getMinCategory())
					dataSet.addToMin(value);
				else if (category > chart.getMaxCategory())
					dataSet.getSets().get(category);
				else {
					Set set = new Set();
					set.setValue(value);
					set.setLink("ReportEmrRates.action?year="+year+"%26amp;minRate="+category+"%26amp;maxRate="+(category + chart.getCategoryDifference()));
					int index = Math.round(category / chart.getCategoryDifference());
					dataSet.addSet(index, set);
				}
			}
			
		} catch (Exception e) {
			chart.setMultiSeries(false);
			Set set = new Set();
			set.setLabel("ERROR");
			set.setValue(1);
			chart.getSets().add(set);
		}

		String xmlData = chart.toString();
		flashChart = FusionChart.createChart("charts/" + chartType.toString() + ".swf", "", xmlData, chartType.toString(), 600, 500, false, false);
		return SUCCESS;
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
