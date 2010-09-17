package com.picsauditing.actions.chart;

import java.util.Date;
import java.util.List;

import com.picsauditing.PICS.DateBean;
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
		String threeMonthsAgo = DateBean.toDBFormat(DateBean.addMonths(new Date(), -3));
		String sqlString;
		SelectSQL sql;
		sql = new SelectSQL("contractor_audit ca");
		sql.addJoin("JOIN audit_type t on t.id = ca.auditTypeID");
		sql.addJoin("JOIN contractor_audit_operator cao on cao.auditID = ca.id");
	
		sql.addField("'Completed' as series");
		sql.addField("date_format(cao.statusChangedDate, '%Y%m') as sortBy");
		sql.addField("date_format(cao.statusChangedDate, '%b %Y') as label");
		sql.addField("count(*) as value");
		sql.addWhere("ca.auditorID = " + permissions.getUserId());
		sql.addWhere("cao.status IN ('Submitted','Resubmitted')");
		sql.addWhere("cao.statusChangedDate >= '" + threeMonthsAgo + "'");
		sql.addGroupBy("label");
		sqlString = sql.toString();

		sql = new SelectSQL("contractor_audit ca");
		sql.addJoin("JOIN audit_type t on t.id = ca.auditTypeID");
		sql.addJoin("JOIN contractor_audit_operator cao on cao.auditID = ca.id");
		
		sql.addField("'Closed' as series");
		sql.addField("date_format(cao.statusChangedDate, '%Y%m') as sortBy");
		sql.addField("date_format(cao.statusChangedDate, '%b %Y') as label");
		sql.addField("count(*) as value");
		sql.addWhere("ca.auditorID = " + permissions.getUserId());
		sql.addWhere("cao.status IN ('Complete','Approved')");
		sql.addWhere("cao.statusChangedDate >= '" + threeMonthsAgo + "'");
		sql.addGroupBy("label");
		sqlString += " UNION ";
		sqlString += sql.toString();

		sql = new SelectSQL("contractor_audit ca");
		sql.addJoin("JOIN audit_type t on t.id = ca.auditTypeID");
		sql.addJoin("JOIN contractor_audit_operator cao on cao.auditID = ca.id");
	
		sql.addField("'New' as series");
		sql.addField("date_format(cao.statusChangedDate, '%Y%m') as sortBy");
		sql.addField("date_format(cao.statusChangedDate, '%b %Y') as label");
		sql.addField("count(*) as value");
		sql.addWhere("ca.auditorID = " + permissions.getUserId());
		sql.addWhere("cao.status IN ('Pending')");
		sql.addWhere("cao.statusChangedDate >= '" + threeMonthsAgo + "'");
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
