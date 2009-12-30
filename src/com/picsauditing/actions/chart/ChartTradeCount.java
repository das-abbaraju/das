package com.picsauditing.actions.chart;

import java.util.List;

import com.picsauditing.search.SelectSQL;
import com.picsauditing.util.PermissionQueryBuilder;
import com.picsauditing.util.chart.ChartSingleSeries;
import com.picsauditing.util.chart.DataRow;
import com.picsauditing.util.chart.Set;

/**
 * Get a count of each flag color for this operator
 * 
 * @author Trevor
 */
@SuppressWarnings("serial")
public class ChartTradeCount extends ChartSSAction {

	@Override
	public ChartSingleSeries buildChart() throws Exception {
		chart.setRotateLabels(true);

		SelectSQL sql = new SelectSQL("accounts a");
		sql.addJoin("JOIN contractor_info c ON a.id = c.id");
		sql.addField("substring(c.main_trade,1,20) as label");
		sql.addField("count(*) as value");
		sql.addGroupBy("label");
		sql.addOrderBy("value DESC");
		sql.addWhere("c.main_trade > ''");

		PermissionQueryBuilder permQuery = new PermissionQueryBuilder(permissions);
		sql.addWhere("1 " + permQuery.toString());

		ChartDAO db = new ChartDAO();
		List<DataRow> data = db.select(sql.toString());
		int count = 0;
		float sum = 0;
		for (DataRow row : data) {
			count++;
			if (count > 10) {
				sum = sum + row.getValue();
			} else {
				Set set = new Set(row);
				chart.getSets().add(set);
			}
		}

		if (sum > 0) {
			Set set = new Set();
			set.setLabel("All others");
			set.setValue(sum);
			chart.getSets().add(set);
		}
		return chart;
	}
}
