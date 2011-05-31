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
		sql.addJoin("JOIN contractor_info ci ON a.id = ci.id");
		sql.addJoin("JOIN contractor_audit ca ON a.id = ca.conID AND ca.auditTypeID = 1");
		sql.addJoin("JOIN contractor_trade ct ON ca.conID = ct.conID AND ct.selfPerformed = 1");
		sql.addJoin("JOIN app_translation at ON CONCAT('Trade.', ct.tradeID, '.name') = at.msgKey");
		
		sql.addField("at.msgValue as label");
		sql.addField("count(*) as value");
		sql.addGroupBy("label");
		sql.addOrderBy("value DESC, label");
		
		PermissionQueryBuilder permQuery = new PermissionQueryBuilder(permissions);
		sql.addWhere("1 " + permQuery.toString());

		ChartDAO db = new ChartDAO();
		List<DataRow> data = db.select(sql.toString());
		int count = 0;
		for (DataRow row : data) {
			if (count < 20) {
				Set set = new Set(row);
				// set.setLabel(getText("AuditQuestion." + row.getLabel() + ".name"));
				chart.getSets().add(set);
			}
			count++;
		}

		return chart;
	}
}
