package com.picsauditing.actions.chart;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;
import java.util.Map;

import com.picsauditing.search.SelectSQL;
import com.picsauditing.util.chart.Category;
import com.picsauditing.util.chart.ChartMultiSeries;
import com.picsauditing.util.chart.ChartType;
import com.picsauditing.util.chart.DataRow;
import com.picsauditing.util.chart.DataSet;
import com.picsauditing.util.chart.FusionChart;
import com.picsauditing.util.chart.MultiSeriesConverterHistogram;
import com.picsauditing.util.chart.Set;

@SuppressWarnings("serial")
public class ChartEmrTrir extends ChartMSAction {
	private int conID;
	
	public String execute() throws Exception {
		if (!forceLogin())
			return LOGIN;
		
		super.execute();
		
		output = FusionChart.createChart("charts/" + ChartType.MSCombiDY2D.toString() + ".swf", "", output, 
				ChartType.MSCombiDY2D.toString(), 450, 300, false, false);
		
		return CHART_XML;
	}

	@Override
	public ChartMultiSeries buildChart() throws Exception {
		chart.setCaption("Average EMR and TRIR Rates");
		chart.setXAxisName("Years");
		chart.setPYAxisName("TRIR");
		chart.setSYAxisName("EMR");
		
		Calendar cal = Calendar.getInstance();
		cal.add(Calendar.YEAR, -7);
		
		SelectSQL sql = new SelectSQL();
		sql.setFullClause("(SELECT CONCAT('20',RIGHT(fc.label,2)) AS label, 'TRIR' AS series, " +
				"AVG(FLOOR(fcc.answer*10)/10) AS value FROM flag_criteria fc " +
				"JOIN flag_criteria_contractor fcc ON fcc.criteriaID = fc.id " +
				"WHERE fc.oshaRateType = 'TrirAbsolute' AND fcc.conID = " + conID +
				" AND fc.multiYearScope NOT LIKE '%average%' AND RIGHT(fc.label, 2) > RIGHT(" +
				cal.get(Calendar.YEAR) + ",2) GROUP BY RIGHT(fc.label,2)) " +
				"UNION (SELECT CONCAT('20',RIGHT(pqf.auditFor, 2)) AS label, 'EMR' AS series, " +
				"AVG(FLOOR(d.answer*10)/10) AS value FROM contractor_audit pqf " +
				"JOIN pqfdata d ON d.auditID = pqf.id WHERE d.answer > 0 " +
				"AND pqf.auditTypeID = 11 AND d.questionID = 2034 AND pqf.conID = " + conID +
				" AND pqf.auditFor > " + cal.get(Calendar.YEAR) +
				" GROUP BY pqf.auditFor) ORDER BY series, label;");
			
		ChartDAO db = new ChartDAO();
		List<DataRow> data = db.select(sql.toString());
		List<String> years = new ArrayList<String>();
		
		for (DataRow row : data) {
			if (!years.contains(row.getLabel()))
				years.add(row.getLabel());
		}
		
		MultiSeriesConverterHistogram converter = new MultiSeriesConverterHistogram();

		converter.setUseDecimal(false);
		converter.setMinCategory(Float.parseFloat(years.get(0)));
		converter.setMaxCategory(Float.parseFloat(years.get(years.size() - 1)));
		converter.setCategoryDifference(1);
		converter.setChart(chart);
		converter.addData(data);
		
		Map<String, DataSet> map = chart.getDataSets();
		for (String key : map.keySet()) {
			DataSet dataset = map.get(key);
			if (key.equals("TRIR")) {
				dataset.setParentYAxis("P");
				dataset.setRenderAs("LINE");
			}
			else
				dataset.setParentYAxis("S");
			
			Map<String, Set> setMap = dataset.getSets();
			for (String setString : setMap.keySet()) {
				Set set = setMap.get(setString);
				String label = set.getLabel();
				label = label.substring(0, label.indexOf("."));
				set.setLabel(label);
			}
		}
		
		Map<String, Category> catMap = chart.getCategories();
		for (String catString : catMap.keySet()) {
			Category cat = catMap.get(catString);
			String label = cat.getLabel();
			label = label.substring(0, label.indexOf("."));
			cat.setLabel(label);
		}
		
		return chart;
	}
	
	public void setConID(int conID) {
		this.conID = conID;
	}
}
