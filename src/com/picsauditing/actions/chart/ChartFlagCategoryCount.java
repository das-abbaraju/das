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

/**
 * Get a count of each flag color by the flag category
 * 
 */
@SuppressWarnings("serial")
public class ChartFlagCategoryCount extends ChartMSAction {

	@Override
	public ChartMultiSeries buildChart() throws Exception {
		chart.setShowLegend(false);
		chart.setShowValues(false);
		chart.setAnimation(false);

		SelectSQL sql = new SelectSQL("accounts a");
		sql.addField("fd.flag as series");
		sql.addField("fc.category as label");
		sql.addField("count(*) as value");
		sql.addGroupBy("label,series");
		sql.addOrderBy("label,series");

		sql.addJoin("JOIN contractor_info c ON a.id = c.id");
		sql.addWhere("a.status IN ('Active','Demo')");
		sql.addWhere("c.welcomeAuditor_id = "+ permissions.getUserId());
		sql.addJoin("JOIN flag_data fd ON fd.conid = a.id");
		sql.addJoin("JOIN flag_criteria fc ON fc.id = fd.criteriaid");
		sql.addWhere("fd.flag IN ('Red','Amber')");
		
		ChartDAO db = new ChartDAO();
		List<DataRow> data = db.select(sql.toString());
		MultiSeriesConverter converter = new MultiSeriesConverter();
		converter.setChart(chart);
		converter.addData(data);
		Map<String, DataSet> dataSet = converter.getChart().getDataSets();
		for (DataSet row : dataSet.values()) {
			row.setShowValues(false);
			for(Set set : row.getSets().values()) {
				set.setColor(FlagColor.valueOf(row.getSeriesName()).getHex());
			}
		}

		return chart;
	}
}
