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
		
		SelectSQL sql = new SelectSQL();
		sql.setFullClause("SELECT series, label, value FROM\n" +
				"(SELECT fc.oshaType AS series, FLOOR(fcc.answer*2)/2 AS label, COUNT(*) AS value\n" +
				"FROM flag_criteria_contractor fcc\n" +
				"JOIN accounts a ON a.id = fcc.conID\n" +
				"JOIN flag_criteria fc ON fc.id = fcc.criteriaID\n" +
				(permissions.isOperatorCorporate() ? 
						"JOIN generalcontractors gc ON gc.subID = fcc.conID AND gc.genID = " + 
						permissions.getAccountId() + "\n" : "") +
				"WHERE 1 AND (FLOOR(fcc.answer*2)/2 >= 0)\n" +
				"AND (FLOOR(fcc.answer*2)/2 <= 5.0)\n" +
				"AND (fc.multiYearScope = 'ThreeYearAverage')\n" +
				"AND (fc.oshaRateType = 'TrirAbsolute')\n" +
				"GROUP BY fc.oshaType, FLOOR(fcc.answer*2)/2\n" +
				"UNION SELECT fc.oshaType AS series, '5.5' AS label, COUNT(*) AS value\n" +
				"FROM flag_criteria_contractor fcc\n" +
				"JOIN accounts a ON a.id = fcc.conID\n" +
				"JOIN flag_criteria fc ON fc.id = fcc.criteriaID\n" +
				(permissions.isOperatorCorporate() ? 
						"JOIN generalcontractors gc ON gc.subID = fcc.conID AND gc.genID = " + 
						permissions.getAccountId() + "\n" : "") +
				"WHERE 1 AND (FLOOR(fcc.answer*2)/2 > 5.0)\n" +
				"AND (fc.multiYearScope = 'ThreeYearAverage')\n" +
				"AND (fc.oshaRateType = 'TrirAbsolute')\n" +
				"GROUP BY fc.oshaType) t\n" + 
				(shaType != null ? "WHERE t.series = '" + shaType + "'\n" : "") +
				"ORDER BY series, label;");
		
		ChartDAO db = new ChartDAO();
		List<DataRow> data = db.select(sql.toString());
		for (DataRow row : data) {
			float labelEnd = 0f;
			if (!row.getLabel().equals("5.5"))
				labelEnd = Float.parseFloat(row.getLabel()) + 0.5F;
			else
				labelEnd = 210000f;
			
			row.setLink("ReportTrirRates.action?filter.shaTypeFlagCriteria=" + row.getSeries() + 
					"%26amp;filter.minTRIR=" + row.getLabel() + "%26amp;filter.maxTRIR=" + labelEnd);
		}
		
		MultiSeriesConverterHistogram converter = new MultiSeriesConverterHistogram();

		converter.setMaxCategory(5.5f);
		converter.setMinCategory(0);
		converter.setCategoryDifference(0.5F);
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
