package com.picsauditing.actions.chart;

import java.util.List;

import com.picsauditing.search.SelectSQL;
import com.picsauditing.util.PermissionQueryBuilder;
import com.picsauditing.util.chart.ChartMultiSeries;
import com.picsauditing.util.chart.ChartSingleSeries;
import com.picsauditing.util.chart.DataRow;
import com.picsauditing.util.chart.MultiSeriesConverter;
import com.picsauditing.util.chart.Set;

/**
 * Get a count of each flag color for this operator
 * 
 * @author Trevor
 */
public class ChartOpFlagHistory extends ChartMSAction {

	@Override
	public ChartMultiSeries buildChart() throws Exception {
		chart.setRotateLabels(true);

		String sqlString ="";
		SelectSQL sql = new SelectSQL("accounts a");
		sql.addJoin("JOIN contractor_info c ON a.id = c.id");
		sql.addField("substring(c.main_trade,1,20) as label");
		sql.addField("count(*) as value");
		sql.addGroupBy("label");
		sql.addOrderBy("value DESC");
		sql.addWhere("c.main_trade > ''");
		
		sql.addGroupBy("flag");
		
		
/*
 * select creationDate, flag, count(*) from flag_archive where opID = 2475 and creationDate = '2009-04-29' group by flag
Union
select creationDate, flag, count(*) from flag_archive where opID = 2475 and creationDate = '2009-03-29' group by flag
Union
select creationDate, flag, count(*) from flag_archive where opID = 2475 and creationDate = '2009-02-28' group by flag
Union
select creationDate, flag, count(*) from flag_archive where opID = 2475 and creationDate = '2009-01-29' group by flag		
 */

		ChartDAO db = new ChartDAO();
		List<DataRow> data = db.select(sqlString);

		MultiSeriesConverter converter = new MultiSeriesConverter();
		converter.setChart(chart);
		converter.addData(data);
		return chart;
	}
}
