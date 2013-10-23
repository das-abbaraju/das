package com.picsauditing.actions.chart;

import java.util.List;

import com.picsauditing.jpa.entities.WaitingOn;
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
public class ChartWaitingOnCount extends ChartSSAction {

	@Override
	public ChartSingleSeries buildChart() throws Exception {

		chart.setShowValues(true);

		SelectSQL sql = new SelectSQL("contractor_operator co");
		sql.addJoin("JOIN accounts a ON a.id = co.conID");

		if (permissions.isAdmin()) {
			sql.addJoin("JOIN accounts o ON o.id = co.opID AND o.type = 'Operator' AND o.status IN ('Active')");
			sql.addWhere("a.status IN ('Active')");
		} else {
			sql.addWhere("co.opID = " + permissions.getAccountId());
			if (permissions.getAccountStatus().isDemo())
				sql.addWhere("a.status IN ('Active','Demo')");
			else
				sql.addWhere("a.status IN ('Active')");
		}

		sql.addGroupBy("co.waitingOn");

		sql.addField("co.waitingOn AS label");
		sql.addField("count(*) AS value");
		sql.addOrderBy("value DESC");

		ChartDAO db = new ChartDAO();
		List<DataRow> data = db.select(sql.toString());
		for (DataRow row : data) {
			Set set = new Set(row);
			WaitingOn waitingOn = WaitingOn.valueOf(Integer.parseInt(row.getLabel()));
			String wait = waitingOn.isNone() ? "No One" : waitingOn.toString();
			set.setLabel(wait);
			if (permissions.isOperator())
				set.setLink("ContractorList.action?filter.waitingOn=" + waitingOn.ordinal());
			chart.addSet(set);
		}
		return chart;
	}
}
