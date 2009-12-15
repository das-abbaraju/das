package com.picsauditing.actions.chart;

import java.util.List;

import com.picsauditing.jpa.entities.WaitingOn;
import com.picsauditing.search.SelectAccount;
import com.picsauditing.search.SelectSQL;
import com.picsauditing.util.Strings;
import com.picsauditing.util.chart.ChartSingleSeries;
import com.picsauditing.util.chart.DataRow;
import com.picsauditing.util.chart.Set;

/**
 * Get a count of each flag color for this operator
 * 
 * @author Trevor
 */
@SuppressWarnings("serial")
public class ChartWaitingOnCount extends ChartSSAction {

	@Override
	public ChartSingleSeries buildChart() throws Exception {

		chart.setShowValues(true);

		SelectSQL sql = new SelectSQL("generalcontractors gc");
		sql.addJoin("JOIN flags f ON gc.genID = f.opID AND gc.subID = f.conID");
		sql.addJoin("JOIN accounts a ON a.id = f.conID");

		sql.addWhere("a.active = 'Y'");

		sql.addGroupBy("waitingOn");

		if (permissions.isOperator()) {
			sql.addWhere("f.opID = " + permissions.getAccountId());
		}

		sql.addField("waitingOn AS label");
		sql.addField("count(*) AS value");

		ChartDAO db = new ChartDAO();
		List<DataRow> data = db.select(sql.toString());
		for (DataRow row : data) {
			Set set = new Set(row);
			WaitingOn waitingOn = WaitingOn.valueOf(Integer.parseInt(row.getLabel()));
			String wait = waitingOn.isNone() ? "None" : waitingOn.toString();
			set.setLabel(wait);
			if (permissions.isOperator())
				set.setLink("ContractorList.action?filter.waitingOn=" + waitingOn.ordinal());
			chart.addSet(set);
		}
		return chart;
	}
}
