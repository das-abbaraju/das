package com.picsauditing.actions.chart;

import java.util.List;

import org.apache.commons.beanutils.LazyDynaBean;

import com.picsauditing.jpa.entities.FlagColor;
import com.picsauditing.search.Database;
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
public class ChartFlagCount extends ChartSSAction {
	
	@Override
	public ChartSingleSeries buildChart() throws Exception {
		
		chart.setShowValues(true);

		SelectSQL sql = new SelectSQL("accounts a");
		sql.addField("flag as label");
		sql.addField("count(*) as value");
		sql.addGroupBy("label");
		sql.addOrderBy("label");

		sql.addJoin("JOIN flags f ON f.conID = a.id AND f.opID = " + permissions.getAccountId());

		PermissionQueryBuilder permQuery = new PermissionQueryBuilder(permissions);
		sql.addWhere("1 " +permQuery.toString());

		ChartDAO db = new ChartDAO();
		List<DataRow> data = db.select(sql.toString());
		for (DataRow row : data) {
			Set set = new Set(row);
			set.setColor(FlagColor.valueOf(row.getLabel()).getHex());
			set.setLink("ContractorListOperator.action?flagStatus="+row.getLabel());
			chart.addSet(set);
		}
		return chart;
	}
}
