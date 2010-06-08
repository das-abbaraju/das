package com.picsauditing.actions.chart;

import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Map;

import com.picsauditing.PICS.DateBean;
import com.picsauditing.jpa.entities.FlagColor;
import com.picsauditing.util.chart.ChartMultiSeries;
import com.picsauditing.util.chart.DataRow;
import com.picsauditing.util.chart.DataSet;
import com.picsauditing.util.chart.MultiSeriesConverter;
import com.picsauditing.util.chart.Set;

@SuppressWarnings("serial")
public class OperatorFlagHistory extends ChartMSAction {

	public ChartMultiSeries buildChart() throws Exception {
		chart.setShowLegend(false);
		chart.setShowValues(false);
		chart.setAnimation(false);
		int operatorID = permissions.getAccountId();
		Date yesterday =  DateBean.addField(new Date(), Calendar.DAY_OF_WEEK, -1);

		String sql = getOperatorFlagHistorySQL(yesterday, "0 days", operatorID)
			+ " UNION " +	
			getOperatorFlagHistorySQL(DateBean.getFirstofMonthOrClosestSunday(DateBean.addDays(yesterday, -30)), "30 days", operatorID)
			+ " UNION " +
			getOperatorFlagHistorySQL(DateBean.getFirstofMonthOrClosestSunday(DateBean.addDays(yesterday, -60)), "60 days", operatorID)
			+ " UNION " +
			getOperatorFlagHistorySQL(DateBean.getFirstofMonthOrClosestSunday(DateBean.addDays(yesterday, -90)), "90 days", operatorID)
			+ " ORDER BY creationDate DESC";

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
	
	private String getOperatorFlagHistorySQL(Date date, String label, int operatorID) throws Exception {
		String dbDate = DateBean.toDBFormat(date);
		//String creationDate = DateBean.prettyDate(date);
		String sql = "SELECT '"+ label +"' AS label, flag AS series, count(*) AS value, creationDate FROM flag_archive WHERE flag in ('Red','Amber','Green') AND opID = "+ operatorID + " AND creationDate = '" + dbDate +"' GROUP BY flag";	
		return sql;
	}
}
