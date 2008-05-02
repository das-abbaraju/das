package com.picsauditing.actions.chart;

import java.util.List;

import org.apache.commons.beanutils.BasicDynaBean;

import com.picsauditing.jpa.entities.FlagColor;
import com.picsauditing.search.Database;
import com.picsauditing.search.SelectSQL;
import com.picsauditing.util.chart.Set;

/**
 * Get a count of each flag color for this operator
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
		
		//SelectAccount sql = new SelectAccount();
		//sql.setPermissions(permissions, this.getAccount());
		SelectSQL sql = new SelectSQL("accounts a");
		sql.addField("flag as label");
		sql.addField("count(*) as value");
		sql.addGroupBy("label");
		sql.addOrderBy("label");
		
		sql.addJoin("JOIN generalcontractors gc on gc.subID = a.id");
		sql.addJoin("JOIN flags f on f.opID = gc.genID and f.conID = gc.subID");
		
		sql.addWhere("a.active = 'Y'");
		sql.addWhere("gc.genID = "+permissions.getAccountId());
		
		try {
			Database db = new Database();
			List<BasicDynaBean> data = db.select(sql.toString(), false);
			for(BasicDynaBean row : data) {
				String color = row.get("label").toString();
				Set set = new Set();
				set.setLabel(color);
				set.setValue(Float.parseFloat(row.get("value").toString()));
				set.setColor(FlagColor.valueOf(color).getHex());
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
