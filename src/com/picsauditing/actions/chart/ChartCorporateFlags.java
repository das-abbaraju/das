package com.picsauditing.actions.chart;

import java.util.List;
import java.util.Map;

import com.picsauditing.jpa.entities.FlagColor;
import com.picsauditing.search.SelectSQL;
import com.picsauditing.util.chart.ChartMultiSeries;
import com.picsauditing.util.chart.DataRow;
import com.picsauditing.util.chart.DataSet;
import com.picsauditing.util.chart.MultiSeriesConverter;
import com.picsauditing.util.chart.Set;

@SuppressWarnings("serial")
public class ChartCorporateFlags extends ChartMSAction {

	public ChartMultiSeries buildChart() throws Exception {
		ChartDAO db = new ChartDAO();

		chart.setShowLegend(false);
		// chart.setShowValues(false);
		
		List<DataRow> dataDetailed;
		List<DataRow> dataAverage;
		List<DataRow> dataWorstCase;

		{
			SelectSQL sql = createBase();
			sql.addField("flag as series");
			sql.addField("'Detailed' as label");
			sql.addField("count(*) as value");
			sql.addGroupBy("series");
			sql.addOrderBy("series");
			sql.addJoin("JOIN generalcontractors gc ON a.id = gc.subID");
			sql.addJoin("JOIN flags f ON a.id = f.conID AND f.opID = gc.genID");
			sql.addJoin("JOIN accounts op ON op.id = gc.genID");
			sql.addWhere("a.active = 'Y'");
			sql.addWhere("op.active = 'Y'");
			sql.addWhere("gc.genID IN (SELECT opID from facilities where corporateID = " + permissions.getAccountId()
					+ ")");

			dataDetailed = db.select(sql.toString());
		}
		{
			SelectSQL sql = createBase();
			sql.addField("a.id");
			sql.addField("CASE f.flag WHEN 'Red' THEN 1 WHEN 'Amber' THEN 2 WHEN 'Green' THEN 3 END flag");
			sql.addJoin("JOIN generalcontractors gc ON a.id = gc.subID");
			sql.addJoin("JOIN flags f ON a.id = f.conID AND f.opID = gc.genID");
			sql.addJoin("JOIN accounts op ON op.id = gc.genID");
			sql.addWhere("a.active = 'Y'");
			sql.addWhere("op.active = 'Y'");
			sql.addWhere("gc.genID IN (SELECT opID from facilities where corporateID = " + permissions.getAccountId()
					+ ")");
			sql.addGroupBy("a.id");
			sql.addGroupBy("f.flag");
			
			SelectSQL sql2 = new SelectSQL("(" + sql.toString() + ") t");
			
			SelectSQL sql3 = new SelectSQL("(" + sql2.toString() + ") t");
			sql3.addField("flag as series");
			sql3.addField("'Worst Case' as label");
			sql3.addField("count(*) as value");

			dataWorstCase = db.select(sql3.toString());
		}
		{
			SelectSQL sql = createBase();
			sql.addField("flag as series");
			sql.addField("count(*) as value");
			sql.addField("'Average' as label");
			sql.addGroupBy("series");
			sql.addOrderBy("series");
			sql.addJoin("JOIN generalcontractors gc ON a.id = gc.subID");
			sql.addJoin("JOIN flags f ON a.id = f.conID AND f.opID = gc.genID");
			sql.addJoin("JOIN accounts op ON op.id = gc.genID");
			sql.addWhere("a.active = 'Y'");
			sql.addWhere("op.active = 'Y'");
			sql.addWhere("gc.genID IN (SELECT opID from facilities where corporateID = " + permissions.getAccountId()
					+ ")");

			dataAverage = db.select(sql.toString());
		}
		
		MultiSeriesConverter converter = new MultiSeriesConverter();
		converter.setChart(chart);
		converter.addData(dataDetailed);
		converter.addData(dataAverage);
		converter.addData(dataWorstCase);
		
		Map<String, DataSet> dataSet = converter.getChart().getDataSets();
		for (DataSet row : dataSet.values()) {
			row.setShowValues(false);
			for (Set set : row.getSets().values()) {
				set.setColor(FlagColor.valueOf(row.getSeriesName()).getHex());
			}
		}

		return chart;
	}
	/**
	 * SELECT f.flag as series FROM accounts a<br>
	 * JOIN generalcontractors gc ON a.id = gc.subID<br>
	 * JOIN flags f ON a.id = f.conID AND f.opID = gc.genID<br>
	 * JOIN accounts op ON op.id = gc.genID<br>
	 * WHERE a.active = 'Y'
	 * AND gc.genID IN (SELECT opID from facilities where corporateID = ?)
	 * AND op.active = 'Y'<br>
	 * GROUP BY f.flag
	 * @return
	 */
	private SelectSQL createBase() {
		SelectSQL sql = new SelectSQL("accounts a");
		sql.addWhere("a.active = 'Y'");
		sql.addJoin("JOIN generalcontractors gc ON a.id = gc.subID");
		sql.addWhere("gc.genID IN (SELECT opID from facilities where corporateID = " + permissions.getAccountId()
				+ ")");
		sql.addJoin("JOIN flags f ON a.id = f.conID AND f.opID = gc.genID");
		sql.addJoin("JOIN accounts op ON op.id = gc.genID");
		sql.addWhere("op.active = 'Y'");
		sql.addField("f.flag as series");
		sql.addGroupBy("f.flag");
		return sql;
	}
}
