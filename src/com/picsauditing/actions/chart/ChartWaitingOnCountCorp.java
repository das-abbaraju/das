package com.picsauditing.actions.chart;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.picsauditing.search.SelectSQL;
import com.picsauditing.util.chart.ChartMultiSeries;
import com.picsauditing.util.chart.DataRow;
import com.picsauditing.util.chart.DataSet;
import com.picsauditing.util.chart.MultiSeriesConverter;
import com.picsauditing.util.chart.Set;

@SuppressWarnings("serial")
public class ChartWaitingOnCountCorp extends ChartMSAction {

	public ChartMultiSeries buildChart() throws Exception {
		Map<String, String> colors = new HashMap<String, String>();
		colors.put("None", "#339900"); // Green
		colors.put("Contractor", "#CC0000"); // Red
		colors.put("PICS", "#FFCC33"); // Amber
		colors.put("Operator", "#0000FF"); // Blue
		chart.setShowLegend(false);
		chart.setShowValues(false);
		chart.setAnimation(false);

		SelectSQL sql = new SelectSQL("accounts a");
		sql
				.addField("CASE waitingOn WHEN 0 THEN 'None' WHEN 1 THEN 'Contractor' WHEN 2 THEN 'PICS' WHEN 3 THEN 'Operator' END as series");
		sql.addField("op.name as label");
		sql.addField("count(*) as value");
		sql
				.addField("CONCAT('ReportContractorOperatorFlag.action?button=Search&filter.waitingOn=',f.waitingOn,'&filter.operator=',op.id) as link");
		sql.addGroupBy("series, label");
		sql.addOrderBy("series, label");
		sql.addJoin("JOIN generalcontractors gc ON a.id = gc.subID");
		sql.addJoin("JOIN flags f ON a.id = f.conID AND f.opID = gc.genID");
		sql.addJoin("JOIN accounts op ON op.id = gc.genID");
		sql.addWhere("a.active = 'Y'");
		sql.addWhere("op.active = 'Y'");
		sql.addWhere("gc.genID IN (SELECT fac.opID from facilities fac where fac.corporateID = "
				+ permissions.getAccountId() + ")");

		ChartDAO db = new ChartDAO();
		List<DataRow> data = db.select(sql.toString());

		MultiSeriesConverter converter = new MultiSeriesConverter();
		converter.setChart(chart);
		converter.addData(data);
		Map<String, DataSet> dataSet = converter.getChart().getDataSets();
		for (DataSet row : dataSet.values()) {
			row.setShowValues(false);
			for (Set set : row.getSets().values()) {
				set.setColor(colors.get(row.getSeriesName()));
			}
		}

		return chart;
	}
}
