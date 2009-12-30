package com.picsauditing.actions.chart;

import java.util.List;

import com.picsauditing.search.SelectSQL;
import com.picsauditing.util.chart.ChartMultiSeries;
import com.picsauditing.util.chart.DataRow;
import com.picsauditing.util.chart.MultiSeriesConverter;

/**
 * Get a count of each flag color for this operator
 * 
 * @author Trevor
 */
@SuppressWarnings("serial")
public class ChartAuditsClosed extends ChartMSAction {

	@Override
	public ChartMultiSeries buildChart() throws Exception {
		String threeMonthsAgo = "2008-03-01";
		String sqlString;
		SelectSQL sql;
		sql = new SelectSQL("contractor_audit ca");
		sql.addJoin("JOIN audit_type t on t.id = ca.auditTypeID");

		sql.addField("'Completed' as series");
		sql.addField("date_format(completedDate, '%Y%m') as sortBy");
		sql.addField("date_format(completedDate, '%b %Y') as label");
		sql.addField("count(*) as value");
		sql.addWhere("ca.auditorID = " + permissions.getUserId());
		sql.addWhere("auditStatus IN ('Submitted','Active')");
		sql.addWhere("completedDate >= '" + threeMonthsAgo + "'");
		sql.addGroupBy("label");
		sqlString = sql.toString();

		sql = new SelectSQL("contractor_audit ca");
		sql.addJoin("JOIN audit_type t on t.id = ca.auditTypeID");

		sql.addField("'Closed' as series");
		sql.addField("date_format(closedDate, '%Y%m') as sortBy");
		sql.addField("date_format(closedDate, '%b %Y') as label");
		sql.addField("count(*) as value");
		sql.addWhere("ca.auditorID = " + permissions.getUserId());
		sql.addWhere("auditStatus IN ('Submitted','Active')");
		sql.addWhere("closedDate >= '" + threeMonthsAgo + "'");
		sql.addGroupBy("label");
		sqlString += " UNION ";
		sqlString += sql.toString();

		sql = new SelectSQL("contractor_audit ca");
		sql.addJoin("JOIN audit_type t on t.id = ca.auditTypeID");

		sql.addField("'New' as series");
		sql.addField("date_format(creationDate, '%Y%m') as sortBy");
		sql.addField("date_format(creationDate, '%b %Y') as label");
		sql.addField("count(*) as value");
		sql.addWhere("ca.auditorID = " + permissions.getUserId());
		sql.addWhere("creationDate >= '" + threeMonthsAgo + "'");
		sql.addGroupBy("label");
		sql.addOrderBy("series, sortBy");
		sqlString += " UNION ";
		sqlString += sql.toString();

		ChartDAO db = new ChartDAO();
		List<DataRow> data = db.select(sqlString);

		MultiSeriesConverter converter = new MultiSeriesConverter();
		converter.setChart(chart);
		converter.addData(data);
		return chart;
	}
}
