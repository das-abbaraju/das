package com.picsauditing.actions.chart;

import java.util.List;

import com.picsauditing.access.OpPerms;
import com.picsauditing.jpa.entities.FlagColor;
import com.picsauditing.search.SelectSQL;
import com.picsauditing.util.chart.ChartSingleSeries;
import com.picsauditing.util.chart.DataRow;
import com.picsauditing.util.chart.Set;

/**
 * Get a count of each flag color for this operator
 * 
 * @author Trevor
 */
@SuppressWarnings("serial")
public class ChartFlagCount extends ChartSSAction {

	@Override
	public ChartSingleSeries buildChart() throws Exception {

		chart.setShowValues(true);

		SelectSQL sql = new SelectSQL("accounts a");
		sql.addField("flag as label");
		sql.addField("count(*) as value");
		sql.addGroupBy("label");
		sql.addOrderBy("label");

		sql.addJoin("JOIN generalcontractors gc ON a.id = gc.subID");
		sql.addJoin("JOIN flags f ON a.id = f.conID AND f.opID = gc.genID");
		sql.addWhere("a.active = 'Y'");

		sql.addWhere("gc.genID = " + permissions.getAccountId());
		if (permissions.isApprovesRelationships()) {
			sql.addWhere("gc.workStatus = 'Y'");
		}

		ChartDAO db = new ChartDAO();
		List<DataRow> data = db.select(sql.toString());
		for (DataRow row : data) {
			Set set = new Set(row);
			set.setColor(FlagColor.valueOf(row.getLabel()).getHex());
			set.setLink("ContractorList.action?filter.flagStatus=" + row.getLabel());
			chart.addSet(set);
		}
		return chart;
	}
}
