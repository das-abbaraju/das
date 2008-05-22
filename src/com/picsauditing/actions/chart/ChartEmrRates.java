package com.picsauditing.actions.chart;

import java.util.List;

import org.apache.commons.beanutils.LazyDynaBean;

import com.picsauditing.jpa.entities.AuditQuestion;
import com.picsauditing.search.Database;
import com.picsauditing.search.SelectSQL;
import com.picsauditing.util.PermissionQueryBuilder;
import com.picsauditing.util.chart.ChartMultiSeries;
import com.picsauditing.util.chart.DataRow;
import com.picsauditing.util.chart.MultiSeriesConverterHistogram;

/**
 * Get a count of each flag color for this operator
 * 
 * @author Trevor
 */
public class ChartEmrRates extends ChartMSAction {

	@Override
	public ChartMultiSeries buildChart() throws Exception {
		SelectSQL sql = new SelectSQL("accounts a");
		sql.addJoin("JOIN pqfdata d ON a.id = d.conID");
		sql.addField("d.questionID");
		sql.addField("floor(d.verifiedAnswer*10)/10 as label");
		sql.addField("count(*) as value");
		sql.addWhere("d.verifiedAnswer > 0");
		sql.addWhere("d.questionID IN ("+AuditQuestion.EMR06+", "+AuditQuestion.EMR07+")");
		sql.addGroupBy("questionID, label");
		sql.addOrderBy("questionID, label");
		
		PermissionQueryBuilder permQuery = new PermissionQueryBuilder(permissions);
		sql.addWhere("1 " +permQuery.toString());

		MultiSeriesConverterHistogram converter = new MultiSeriesConverterHistogram();
		
		converter.setMaxCategory(1.2F);
		converter.setMinCategory(0.4F);
		converter.setCategoryDifference(0.1F);
		converter.setChart(chart);

		ChartDAO db = new ChartDAO();
		List<DataRow> data = db.select(sql.toString());
		
		for (DataRow row : data) {
			int questionID = Integer.parseInt(row.getSeries());
			row.setSeries(AuditQuestion.getEmrYear(questionID) + " EMR");
		}
		
		converter.addData(data);
		
		return chart;
	}
}
