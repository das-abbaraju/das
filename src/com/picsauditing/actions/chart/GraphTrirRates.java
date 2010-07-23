package com.picsauditing.actions.chart;

import java.util.Arrays;
import java.util.List;

import com.picsauditing.access.OpPerms;
import com.picsauditing.jpa.entities.OshaType;
import com.picsauditing.search.SelectSQL;
import com.picsauditing.util.Strings;
import com.picsauditing.util.chart.ChartMultiSeries;
import com.picsauditing.util.chart.ChartType;
import com.picsauditing.util.chart.DataRow;
import com.picsauditing.util.chart.FusionChart;
import com.picsauditing.util.chart.MultiSeriesConverterHistogram;

@SuppressWarnings("serial")
public class GraphTrirRates extends ChartMSAction {
	private ChartType chartType = ChartType.MSLine;
	private String flashChart;
	private OshaType[] shaType = new OshaType[] { OshaType.OSHA };
	private int[] years = new int[] { 2009 };

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
		chart.setCaption("Incidence Rates");
		chart.setXAxisName("Incidence Rate");
		chart.setYAxisName("Contractors");
		
		SelectSQL part1 = setup();
		part1.addField("FLOOR((os.recordableTotal*200000/os.manHours)*2)/2 AS label");
		part1.addWhere("os.recordableTotal*200000/os.manHours > -1.0");
		part1.addWhere("os.recordableTotal*200000/os.manHours <= 5.5");
		
		SelectSQL part2 = setup();
		part2.addField("5.5 AS label");
		part2.addWhere("os.recordableTotal*200000/os.manHours > 5.5");
		
		SelectSQL sql = new SelectSQL();
		sql.setFullClause("(" + part1.toString() + ")\nUNION\n(" + part2.toString() + 
				")\nORDER BY series, label;");
		
		ChartDAO db = new ChartDAO();
		List<DataRow> data = db.select(sql.toString());
		for (DataRow row : data) {
			Float max = Float.parseFloat(row.getLabel()) + 0.5f;
			
			String link = "ReportIncidenceRate.action?filter.auditFor=" + 
					Strings.implode(years, "%26amp;filter.auditFor=") + "%26amp;filter.shaType=" + 
					Strings.implode(Arrays.asList(shaType), "%26amp;filter.shaType=") +
					"%26amp;filter.shaLocation=Corporate%26amp;filter.incidenceRate=" + row.getLabel() +
					"%26amp;filter.incidenceRateMax=" + max;

			row.setLink(link);
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
	
	public OshaType[] getShaType() {
		return shaType;
	}
	
	public void setShaType(OshaType[] shaType) {
		this.shaType = shaType;
	}
	
	public OshaType[] getShaTypes() {
		return OshaType.values();
	}
	
	public int[] getYears() {
		return years;
	}
	
	public void setYears(int[] years) {
		this.years = years;
	}
	
	public int[] getAllYears() {
		return new int[] { 2005, 2006, 2007, 2008, 2009 };
	}
	
	private SelectSQL setup() {
		SelectSQL sql = new SelectSQL("accounts a");
		sql.addJoin("JOIN contractor_info c ON a.id = c.id");
		sql.addJoin("JOIN users contact ON contact.id = a.contactID");
		sql.addJoin("JOIN contractor_audit ca ON ca.conID = a.id");
		sql.addJoin("JOIN osha_audit os ON os.auditID = ca.id");
		sql.addJoin("JOIN naics n ON n.code = a.naics");
		sql.addField("CONCAT(os.SHAType, ' ', ca.auditFor) AS series");
		sql.addField("COUNT(*) AS value");
		sql.addWhere("a.type='Contractor'");
		sql.addWhere("ca.auditTypeID = 11");
		sql.addWhere("verifiedDate IS NOT NULL");
		sql.addWhere("ca.auditFor IN (" + Strings.implode(years) + ")");
		sql.addWhere("os.SHAType IN (" + Strings.implodeForDB(shaType, ",") + ")");
		sql.addGroupBy("series, label");
		
		if (permissions.isOperatorCorporate()) {
			sql.addJoin("JOIN generalcontractors gc ON gc.subID = a.id");
			
			if (permissions.isOperator())
				sql.addWhere("gc.genID = " + permissions.getAccountId());
			
			if (permissions.isCorporate()) {
				sql.addWhere("gc.genID IN (SELECT id FROM operators WHERE parentID = " + 
						permissions.getAccountId() + ")");
			}
		}
		
		return sql;
	}
}
