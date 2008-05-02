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
 * @author Trevor
 */
public class ChartTradeCount extends ChartAction {
	
	public String execute() {
		loadPermissions();
		
		chart.setRotateLabels(true);
		
		SelectSQL sql = new SelectSQL("accounts a");
		sql.addJoin("JOIN contractor_info c ON a.id = c.id");
		sql.addField("substring(c.main_trade,1,20) as label");
		sql.addField("count(*) as value");
		sql.addGroupBy("label");
		sql.addOrderBy("value DESC");
		sql.addWhere("c.main_trade > ''");
		
		PermissionQueryBuilder permQuery = new PermissionQueryBuilder(permissions);
		sql.addWhere("1 " +permQuery.toString());
		
		try {
			Database db = new Database();
			List<BasicDynaBean> data = db.select(sql.toString(), false);
			int count = 0;
			float sum = 0;
			for(BasicDynaBean row : data) {
				count++;
				if (count > 10) {
					sum = sum + Float.parseFloat(row.get("value").toString());
				} else {
					Set set = new Set();
					set.setLabel(row.get("label").toString());
					set.setValue(Float.parseFloat(row.get("value").toString()));
					chart.getSets().add(set);
				}
			}
			
			if (sum > 0) {
				Set set = new Set();
				set.setLabel("All others");
				set.setValue(sum);
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
