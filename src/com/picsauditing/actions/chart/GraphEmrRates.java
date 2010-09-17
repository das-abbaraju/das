package com.picsauditing.actions.chart;

import java.util.List;

import com.picsauditing.access.OpPerms;
import com.picsauditing.jpa.entities.AuditQuestion;
import com.picsauditing.search.SelectSQL;
import com.picsauditing.util.PermissionQueryBuilder;
import com.picsauditing.util.Strings;
import com.picsauditing.util.chart.ChartMultiSeries;
import com.picsauditing.util.chart.ChartType;
import com.picsauditing.util.chart.DataRow;
import com.picsauditing.util.chart.FusionChart;
import com.picsauditing.util.chart.MultiSeriesConverterHistogram;

@SuppressWarnings("serial")
public class GraphEmrRates extends ChartMSAction {
	private ChartType chartType = ChartType.MSLine;
	private String flashChart;
	private String[] years = { "2008", "2009" };

	public String execute() throws Exception {
		if (!forceLogin())
			return LOGIN;
		permissions.tryPermission(OpPerms.EMRReport);
		super.execute();
		flashChart = FusionChart.createChart("charts/" + chartType.toString() + ".swf", "", output, chartType
				.toString(), 600, 500, false, false);
		return SUCCESS;
	}

	@Override
	public ChartMultiSeries buildChart() throws Exception {
		chart.setCaption("EMR Rates");
		chart.setXAxisName("EMR Rate");
		chart.setYAxisName("Contractors");

		SelectSQL sql = new SelectSQL("accounts a");
		sql.addJoin("JOIN contractor_audit pqf ON pqf.conID = a.id");
		sql.addJoin("JOIN pqfdata d ON d.auditID = pqf.id");
		sql.addJoin("JOIN contractor_audit_operator cao on cao.auditID = ca.id");
		sql.addField("pqf.auditFor as series");
		sql.addField("floor(d.answer*10)/10 as label");
		sql.addField("count(*) as value");
		sql.addWhere("d.answer > 0");
		sql.addWhere("cao.status = 'Complete' AND pqf.auditTypeID = 11");

		sql.addWhere("d.questionID = " + AuditQuestion.EMR);
		String auditFor = Strings.implodeForDB(getYears(), ",");
		if (!Strings.isEmpty(auditFor))
			sql.addWhere("pqf.auditFor IN (" + auditFor + ")");

		sql.addGroupBy("questionID, label");
		sql.addOrderBy("questionID DESC, label");

		PermissionQueryBuilder permQuery = new PermissionQueryBuilder(permissions);
		sql.addWhere("1 " + permQuery.toString());

		ChartDAO db = new ChartDAO();
		List<DataRow> data = db.select(sql.toString());
		for (DataRow row : data) {
			int year = Integer.parseInt(row.getSeries());
			row.setSeries(year + " EMR");

			float labelEnd = Float.parseFloat(row.getLabel()) + 0.1F;
			row.setLink("ReportEmrRates.action?year=" + year + "%26amp;filter.minEMR=" + row.getLabel() + 
					"%26amp;filter.maxEMR=" + labelEnd);
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

	public String[] getYears() {
		return years;
	}

	public void setYears(String[] years) {
		this.years = years;
	}

}
