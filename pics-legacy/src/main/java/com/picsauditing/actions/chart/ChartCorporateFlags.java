package com.picsauditing.actions.chart;

import java.util.List;

import com.picsauditing.jpa.entities.FlagColor;
import com.picsauditing.search.SelectSQL;
import com.picsauditing.util.chart.ChartSingleSeries;
import com.picsauditing.util.chart.DataRow;
import com.picsauditing.util.chart.Set;

@SuppressWarnings("serial")
public class ChartCorporateFlags extends ChartSSAction {

	public ChartSingleSeries buildChart() throws Exception {

		chart.setShowValues(true);

		ChartDAO db = new ChartDAO();
		SelectSQL sql = createBase();
		sql.addField("count(*) as value");
		List<DataRow> data = db.select(sql.toString());

		for (DataRow row : data) {
			Set set = new Set(row);
			set.setLabel(row.getLabel());
			set.setColor(FlagColor.valueOf(row.getLabel()).getHex());
			chart.addSet(set);
		}

		return chart;
	}

	/**
	 * SELECT gc.flag as series FROM accounts a<br>
	 * JOIN generalcontractors gc ON a.id = gc.subID<br>
	 * JOIN accounts op ON op.id = gc.genID<br>
	 * WHERE a.status IN ('Active','Demo') AND gc.genID IN (SELECT opID from facilities where corporateID = ?) AND
	 * op.status IN ('Active','Demo')<br>
	 * GROUP BY gc.flag
	 * 
	 * @return
	 */
	private SelectSQL createBase() {
		SelectSQL sql = new SelectSQL("accounts a");
		sql.addWhere("a.status IN ('Active'" + (permissions.getAccountStatus().isDemo() ? ",'Demo'" : "") + ")");
		sql.addJoin("JOIN generalcontractors gc ON a.id = gc.subID");
		sql
				.addWhere("gc.genID IN (SELECT opID from facilities where corporateID = " + permissions.getAccountId()
						+ ")");
		sql.addJoin("JOIN accounts op ON op.id = gc.genID");
		sql.addWhere("op.status IN ('Active'" + (permissions.getAccountStatus().isDemo() ? ",'Demo'" : "") + ")");
		sql.addWhere("gc.flag != 'Clear'");
		sql.addField("gc.flag as label");
		sql.addGroupBy("gc.flag");
		return sql;
	}

}
