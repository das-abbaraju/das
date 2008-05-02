package com.picsauditing.actions.chart;

import java.util.List;

import org.apache.commons.beanutils.BasicDynaBean;

import com.picsauditing.jpa.entities.FlagColor;
import com.picsauditing.search.Database;
import com.picsauditing.search.SelectSQL;
import com.picsauditing.util.PermissionQueryBuilder;
import com.picsauditing.util.chart.Set;

/**
 * Get a count of each flag color for this operator
 * 
 * @author Trevor
 */
public class ChartFlagCount extends ChartAction {

	public String execute() {
		try {
			loadPermissions();
			if (!permissions.isLoggedIn())
				throw new Exception();
		} catch (Exception e) {
			Set set = new Set();
			set.setLabel("Authentication ERROR");
			set.setValue(1);
			chart.getSets().add(set);
			return SUCCESS;
		}

		SelectSQL sql = new SelectSQL("accounts a");
		sql.addField("flag as label");
		sql.addField("count(*) as value");
		sql.addGroupBy("label");
		sql.addOrderBy("label");

		sql.addJoin("JOIN flags f ON f.conID = a.id AND f.opID = " + permissions.getAccountId());

		PermissionQueryBuilder permQuery = new PermissionQueryBuilder(permissions);
		sql.addWhere("1 " +permQuery.toString());

		try {
			Database db = new Database();
			List<BasicDynaBean> data = db.select(sql.toString(), false);
			for (BasicDynaBean row : data) {
				String color = row.get("label").toString();
				Set set = new Set();
				set.setLabel(color);
				set.setValue(Float.parseFloat(row.get("value").toString()));
				set.setColor(FlagColor.valueOf(color).getHex());
				set.setLink("ContractorListOperator.action?flagStatus="+color);
				chart.getSets().add(set);
			}
		} catch (Exception e) {
			Set set = new Set();
			set.setLabel("ERROR");
			set.setValue(1);
			chart.getSets().add(set);
		}

		return super.execute();
	}
}
