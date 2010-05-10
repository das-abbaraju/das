package com.picsauditing.actions.chart;

import java.util.List;

import com.picsauditing.access.OpPerms;
import com.picsauditing.jpa.entities.OshaType;
import com.picsauditing.search.SelectSQL;
import com.picsauditing.util.chart.ChartMultiSeries;
import com.picsauditing.util.chart.ChartType;
import com.picsauditing.util.chart.DataRow;
import com.picsauditing.util.chart.FusionChart;
import com.picsauditing.util.chart.MultiSeriesConverterHistogram;

@SuppressWarnings("serial")
public class GraphTrirRates extends ChartMSAction {
	private ChartType chartType = ChartType.MSLine;
	private String flashChart;
	private OshaType shaType;

	public String execute() throws Exception {
		if (!forceLogin())
			return LOGIN;
		permissions.tryPermission(OpPerms.TRIRReport);
		super.execute();
		flashChart = FusionChart.createChart("charts/" + chartType.toString() + ".swf", "", output, chartType
				.toString(), 600, 500, false, false);
		return SUCCESS;
	}

	@Override
	public ChartMultiSeries buildChart() throws Exception {
		chart.setCaption("TRIR Rates");
		chart.setXAxisName("TRIR Rate");
		chart.setYAxisName("Contractors");
		
		SelectSQL sql = new SelectSQL("flag_criteria_contractor fcc");
		sql.addJoin("JOIN accounts a ON a.id = fcc.conID");
		sql.addJoin("JOIN flag_criteria fc ON fc.id = fcc.criteriaID");
		sql.addField("fc.oshaType AS series");
		sql.addField("FLOOR(fcc.answer*10)/10 AS label");
		sql.addField("count(*) AS value");
		sql.addWhere("FLOOR(fcc.answer*10)/10 > 0");
		sql.addWhere("fc.multiYearScope = 'ThreeYearAverage'");
		sql.addWhere("fc.oshaRateType = 'TrirAbsolute'");
		sql.addGroupBy("fc.oshaType, FLOOR(fcc.answer*10)/10");
		sql.addOrderBy("fc.oshaType DESC, FLOOR(fcc.answer*10)/10");
		
		if (shaType != null)
			sql.addWhere("fc.oshaType = '" + shaType + "'");
			
		if (permissions.isOperatorCorporate())
			sql.addJoin("JOIN generalcontractors gc ON gc.subID = fcc.conID AND gc.genID = " + permissions.getAccountId());

		ChartDAO db = new ChartDAO();
		List<DataRow> data = db.select(sql.toString());
		for (DataRow row : data) {
			float labelEnd = Float.parseFloat(row.getLabel()) + 0.1F;
			row.setLink("ReportTrirRates.action?filter.shaTypeFlagCriteria=" + row.getSeries() + 
					"%26amp;filter.minTRIR=" + row.getLabel() + "%26amp;filter.maxTRIR=" + labelEnd);
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
		ChartType[] charts = { ChartType.MSArea, ChartType.MSBar2D, ChartType.MSBar3D, ChartType.MSColumn2D,
				ChartType.MSColumn3D, ChartType.MSLine };
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
	
	public OshaType getShaType() {
		return shaType;
	}
	
	public void setShaType(OshaType shaType) {
		this.shaType = shaType;
	}
	
	public OshaType[] getShaTypes() {
		return OshaType.values();
	}
}
