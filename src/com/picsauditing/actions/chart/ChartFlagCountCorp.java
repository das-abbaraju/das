package com.picsauditing.actions.chart;

import java.util.List;
import java.util.Map;

import com.picsauditing.jpa.entities.FlagColor;
import com.picsauditing.search.SelectSQL;
import com.picsauditing.util.chart.ChartMultiSeries;
import com.picsauditing.util.chart.DataRow;
import com.picsauditing.util.chart.DataSet;
import com.picsauditing.util.chart.MultiSeriesConverter;
import com.picsauditing.util.chart.Set;

@SuppressWarnings("serial")
public class ChartFlagCountCorp extends ChartMSAction {

	public ChartMultiSeries buildChart() throws Exception {
		chart.setShowLegend(false);
		chart.setShowValues(false);
		chart.setAnimation(false);
		
		SelectSQL sql = new SelectSQL("accounts a");
		sql.addField("flag as series");
		sql.addField("op.name as label");
		sql.addField("count(*) as value");
		sql.addField("CONCAT('ReportContractorOperatorFlag.action?button=Search&filter.flagStatus=',f.flag,'&filter.operator=',op.id) as link");
		sql.addGroupBy("series, label");
		sql.addOrderBy("series, label");
		sql.addJoin("JOIN generalcontractors gc ON a.id = gc.subID");
		sql.addJoin("JOIN flags f ON a.id = f.conID AND f.opID = gc.genID");
		sql.addJoin("JOIN accounts op ON op.id = gc.genID");
		sql.addWhere("a.status IN ('Active','Demo')");
		sql.addWhere("op.status IN ('Active','Demo')"); 
		sql.addWhere("gc.genID IN (SELECT fac.opID from facilities fac where fac.corporateID = "+ permissions.getAccountId()+ ")");
		
		
		ChartDAO db = new ChartDAO();
		List<DataRow> data = db.select(sql.toString());
		
		MultiSeriesConverter converter = new MultiSeriesConverter();
		converter.setChart(chart);
		converter.addData(data);
		Map<String, DataSet> dataSet = converter.getChart().getDataSets();
		for (DataSet row : dataSet.values()) {
			row.setShowValues(false);
			for(Set set : row.getSets().values()) {
				set.setColor(FlagColor.valueOf(row.getSeriesName()).getHex());
			}
		}
		
		return chart;
	}
}
