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
		sql.addJoin("JOIN contractor_audit ca ON a.id = ca.conID and ca.auditTypeID = 1");
		sql.addJoin("JOIN pqfdata d ON d.auditID = ca.id AND d.answer LIKE '%C%'");
		sql.addJoin("JOIN audit_question q ON q.questionType = 'Service' AND d.questionID = q.id");
		
		sql.addField("substring(q.name,1,20) as label");
		sql.addField("count(*) as value");
		sql.addGroupBy("q.name");
		sql.addOrderBy("value DESC");

		PermissionQueryBuilder permQuery = new PermissionQueryBuilder(permissions);
		sql.addWhere("1 " + permQuery.toString());

		ChartDAO db = new ChartDAO();
		List<DataRow> data = db.select(sql.toString());
		int count = 0;
		for (DataRow row : data) {
			if (count < 20) {
				Set set = new Set(row);
				chart.getSets().add(set);
			}
			count++;
		}

		return chart;
	}
}
