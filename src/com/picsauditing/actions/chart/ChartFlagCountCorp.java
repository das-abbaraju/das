package com.picsauditing.actions.chart;

import com.picsauditing.search.SelectSQL;
import com.picsauditing.util.chart.ChartMultiSeries;
import com.picsauditing.util.chart.MultiSeriesConverter;

@SuppressWarnings("serial")
public class ChartFlagCountCorp extends ChartMSAction {

	public ChartMultiSeries buildChart() {

		chart.setShowValues(true);

		SelectSQL sql = new SelectSQL("accounts a");
		sql.addField("flag as label");
		sql.addField("o.name as series");
		sql.addField("count(*) as value");
		sql.addGroupBy("series, label");
		sql.addOrderBy("series, label");

		sql.addJoin("JOIN facilities fac ON fac.opID = a.id AND fac.corporateID = " + permissions.getAccountId());
		sql.addJoin("JOIN generalcontractors gc ON a.id = gc.subID");

		sql.addJoin("JOIN flags f ON a.id = f.conID AND f.opID = gc.genID");

		ChartDAO db = new ChartDAO();
//		List<DataRow> data = db.select(sql.toString());
		// Get the category set first
		MultiSeriesConverter multiSeriesConverter = new MultiSeriesConverter();
		
//		for (DataRow row : data) {
//			Set set = new Set(row);
//			set.setColor(FlagColor.valueOf(row.getLabel()).getHex());
//			//set.setLink("ContractorList.action?filter.flagStatus=" + row.getLabel());
//			chart.addCategory(category)
//			chart.addSet(set);
//		}
//		
//		for (DataRow row : data) {
//			Set set = new Set(row);
//			set.setColor(FlagColor.valueOf(row.getLabel()).getHex());
//			//set.setLink("ContractorList.action?filter.flagStatus=" + row.getLabel());
//			chart.addCategory(category)
//			chart.addSet(set);
//		}

		return chart;
	}
}
