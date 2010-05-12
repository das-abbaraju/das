package com.picsauditing.actions.chart;

import java.util.List;

import com.picsauditing.jpa.entities.FlagColor;
import com.picsauditing.jpa.entities.User;
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
		sql.addWhere("a.status IN ('Active','Demo')");
		
		if(permissions.isOperatorCorporate()) {
			sql.addWhere("gc.genID = " + permissions.getAccountId());
			if (permissions.isApprovesRelationships()) {
				sql.addWhere("gc.workStatus = 'Y'");
			}
		}
		else if(permissions.hasGroup(User.GROUP_CSR)) {
			sql.addJoin("JOIN contractor_info c ON a.id = c.id");
			sql.addWhere("c.welcomeAuditor_id = "+ permissions.getUserId());
			sql.addJoin("JOIN accounts oper ON oper.id = gc.genID");
			sql.addWhere("oper.type = 'Operator'");
		}

		ChartDAO db = new ChartDAO();
		List<DataRow> data = db.select(sql.toString());
		for (DataRow row : data) {
			Set set = new Set(row);
			set.setColor(FlagColor.valueOf(row.getLabel()).getHex());
			if(permissions.isOperatorCorporate())
				set.setLink("ContractorList.action?filter.flagStatus=" + row.getLabel());
			if(permissions.hasGroup(User.GROUP_CSR))
				set.setLink("ReportContractorOperatorFlag.action?filter.flagStatus=" + row.getLabel());
			chart.addSet(set);
		}
		return chart;
	}
}
