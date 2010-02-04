package com.picsauditing.actions.chart;

import java.util.List;
import java.util.Map;

import com.picsauditing.jpa.entities.FlagColor;
import com.picsauditing.search.SelectSQL;
import com.picsauditing.util.Strings;
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
		chart.setyAxisMaxValue(100);
		chart.setNumberSuffix("%");
		chart.setShowValues(true);
		
		List<DataRow> dataDetailed;
		List<DataRow> dataAverage;
		List<DataRow> dataWorstCase;

		{
			SelectSQL sql = createBase();
			sql.addField("'Detailed' as label");
			sql.addField("count(*) as value");
			dataDetailed = db.select(sql.toString());
			normalize(dataDetailed);
		}
		{
			SelectSQL sql = createBase();
			sql.addField("a.id");
			sql.addField("CASE f.flag WHEN 'Red' THEN 1 WHEN 'Amber' THEN 2 WHEN 'Green' THEN 3 END as flag");
			sql.addGroupBy("a.id");
			
			SelectSQL sql2 = new SelectSQL("(" + sql.toString() + ") t");
			sql2.addField("id");
			sql2.addField("MIN(flag) flag");
			sql2.addGroupBy("id");
			
			SelectSQL sql3 = new SelectSQL("(" + sql2.toString() + ") t");
			sql3.addField("CASE flag WHEN 1 THEN 'Red' WHEN 2 THEN 'Amber' WHEN 3 THEN 'Green' END as series");
			sql3.addField("'Worst Case' as label");
			sql3.addField("count(*) as value");
			sql3.addGroupBy("flag");

			dataWorstCase = db.select(sql3.toString());
			normalize(dataWorstCase);
		}
		{
			SelectSQL sql = createBase();
			sql.addField("a.id");
			sql.addField("CASE f.flag WHEN 'Red' THEN 1 WHEN 'Amber' THEN 2 WHEN 'Green' THEN 3 END as flag");
			sql.addGroupBy("a.id");
			
			SelectSQL sql2 = new SelectSQL("(" + sql.toString() + ") t");
			sql2.addField("id");
			sql2.addField("ROUND(AVG(flag)) flag");
			sql2.addGroupBy("id");
			
			SelectSQL sql3 = new SelectSQL("(" + sql2.toString() + ") t");
			sql3.addField("CASE flag WHEN 1 THEN 'Red' WHEN 2 THEN 'Amber' WHEN 3 THEN 'Green' END as series");
			sql3.addField("'Average' as label");
			sql3.addField("count(*) as value");
			sql3.addGroupBy("flag");

			dataAverage = db.select(sql3.toString());
			normalize(dataAverage);
		}
		
		MultiSeriesConverter converter = new MultiSeriesConverter();
		converter.setChart(chart);
		converter.addData(dataDetailed);
		converter.addData(dataAverage);
		converter.addData(dataWorstCase);
		
		Map<String, DataSet> dataSet = converter.getChart().getDataSets();
		for (DataSet row : dataSet.values()) {
			row.setShowValues(true);
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
	 * WHERE a.status IN ('Active','Demo')
	 * AND gc.genID IN (SELECT opID from facilities where corporateID = ?)
	 * AND op.status IN ('Active','Demo')<br>
	 * GROUP BY f.flag
	 * @return
	 */
	private SelectSQL createBase() {
		SelectSQL sql = new SelectSQL("accounts a");
		sql.addWhere("a.status IN ('Active','Demo')");
		sql.addJoin("JOIN generalcontractors gc ON a.id = gc.subID");
		sql.addWhere("gc.genID IN (SELECT opID from facilities where corporateID = " + permissions.getAccountId()
				+ ")");
		sql.addJoin("JOIN flags f ON a.id = f.conID AND f.opID = gc.genID");
		sql.addJoin("JOIN accounts op ON op.id = gc.genID");
		sql.addWhere("op.status IN ('Active','Demo')");
		sql.addField("f.flag as series");
		sql.addGroupBy("f.flag");
		return sql;
	}
	
	private void normalize(List<DataRow> data) {
		float total = 0;
		for (DataRow dataRow : data) {
			total += dataRow.getValue();
		}
		
		for (DataRow dataRow : data) {
			String tooltip = Strings.formatShort(dataRow.getValue());
			String flag = dataRow.getSeries();
			String type = dataRow.getLabel();
			if (type.equals("Worst Case")) {
				if (flag.equals("Red"))
					tooltip += " contractors with at least one Red Flag";
				if (flag.equals("Amber"))
					tooltip += " contractors with at least one Amber Flag";
				if (flag.equals("Green"))
					tooltip += " contractors with all Green Flags";
			} else if (dataRow.getLabel().equals("Average")) {
				if (flag.equals("Red"))
					tooltip += " contractors with mostly Red Flags";
				if (flag.equals("Amber"))
					tooltip += " contractors with mixed flags";
				if (flag.equals("Green"))
					tooltip += " contractors with mostly Green Flags";
			} else if (dataRow.getLabel().equals("Detailed")) {
				tooltip += " " + flag + " Flags including duplicates";
			}
			dataRow.setToolText(tooltip);
			dataRow.setValue(Math.round(1000f * dataRow.getValue() / total)/10f);
			if (dataRow.getValue() < 3)
				dataRow.setShowValue(false);
		}
	}
}
