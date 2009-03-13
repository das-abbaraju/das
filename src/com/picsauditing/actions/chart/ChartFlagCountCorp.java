package com.picsauditing.actions.chart;

import java.util.List;

import com.picsauditing.access.OpPerms;
import com.picsauditing.jpa.entities.FlagColor;
import com.picsauditing.search.SelectSQL;
import com.picsauditing.util.chart.ChartMultiSeries;
import com.picsauditing.util.chart.DataRow;
import com.picsauditing.util.chart.Set;

@SuppressWarnings("serial")
public class ChartFlagCountCorp extends ChartMSAction {

	public ChartMultiSeries buildChart() {
		if (!permissions.isCorporate() && !permissions.seesAllContractors())
			return chart;
		
		chart.setShowValues(true);

		SelectSQL sql = new SelectSQL("accounts a");
		sql.addField("flag as label");
		sql.addField("o.name as series");
		sql.addField("count(*) as value");
		sql.addGroupBy("series, label");
		sql.addOrderBy("series, label");

		sql.addJoin("JOIN generalcontractors gc ON a.id = gc.subID");
		sql.addJoin("JOIN accounts o ON o.id = gc.genID");
		sql.addJoin("JOIN flags f ON a.id = f.conID AND f.opID = gc.genID");
		sql.addWhere("a.active = 'Y'");
		if (permissions.isCorporate())
			sql.addJoin("JOIN facilities fac ON fac.opID = gc.genID AND fac.corporateID = " + permissions.getAccountId());

		ChartDAO db = new ChartDAO();
		List<DataRow> data = db.select(sql.toString());
		// Get the category set first
		MultiSeriesConverter 
		for (DataRow row : data) {
			Set set = new Set(row);
			set.setColor(FlagColor.valueOf(row.getLabel()).getHex());
			//set.setLink("ContractorList.action?filter.flagStatus=" + row.getLabel());
			chart.addCategory(category)
			chart.addSet(set);
		}
		
		for (DataRow row : data) {
			Set set = new Set(row);
			set.setColor(FlagColor.valueOf(row.getLabel()).getHex());
			//set.setLink("ContractorList.action?filter.flagStatus=" + row.getLabel());
			chart.addCategory(category)
			chart.addSet(set);
		}

		return chart;
	}
}
