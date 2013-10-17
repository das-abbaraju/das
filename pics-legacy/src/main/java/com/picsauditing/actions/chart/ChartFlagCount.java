package com.picsauditing.actions.chart;

import java.util.List;

import com.picsauditing.access.OpPerms;
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
		sql.addJoin("JOIN contractor_operator co ON a.id = co.conID");
		sql.addWhere("a.status IN ('Active'" + (permissions.getAccountStatus().isDemo() ? ",'Demo'" : "") + ")");
		sql.addWhere("co.flag != 'Clear'");

		if (permissions.isOperatorCorporate()) {
			sql.addWhere("co.opID = " + permissions.getAccountId());
			if (permissions.isGeneralContractor() || !permissions.hasPermission(OpPerms.ViewUnApproved)) {
				sql.addWhere("co.workStatus = 'Y'");
			}

			sql.addField("flag as label");
			sql.addField("count(*) as value");
			sql.addGroupBy("label");
			sql.addOrderBy("label");
		} else if (permissions.hasGroup(User.GROUP_CSR)) {
			sql.addJoin("JOIN contractor_info c ON a.id = c.id");
			sql.addJoin("JOIN accounts oper ON oper.id = co.opID");
            sql.addJoin("JOIN account_user au on au.accountID = a.id and au.role='PICSCustomerServiceRep' and au.startDate < now() and au.endDate > now()");
			sql.addWhere("au.userID = " + permissions.getShadowedUserID());
			sql.addWhere("oper.type = 'Operator'");

			sql.addField("co.flag");
			sql.addGroupBy("co.flag, a.name");

			String derived = sql.toString();
			sql = new SelectSQL("(" + derived + ") t");
			sql.addField("t.flag AS label");
			sql.addField("COUNT(*) AS value");
			sql.addGroupBy("t.flag");
		}

		ChartDAO db = new ChartDAO();
		List<DataRow> data = db.select(sql.toString());
		for (DataRow row : data) {
			Set set = new Set(row);
			set.setColor(FlagColor.valueOf(row.getLabel()).getHex());
			if (permissions.isOperatorCorporate())
				set.setLink("ContractorList.action?filter.flagStatus=" + row.getLabel());
			if (permissions.hasGroup(User.GROUP_CSR))
				set.setLink("ReportContractorOperatorFlag.action?filter.flagStatus=" + row.getLabel());
			set.setLabel(getText("FlagColor." + row.getLabel()));
			chart.addSet(set);
		}
		return chart;
	}
}