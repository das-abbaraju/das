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
		Date yesterday = DateBean.addField(new Date(), Calendar.DAY_OF_WEEK, -1);

		String sql = getOperatorFlagHistorySQL(
				DateBean.getFirstofMonthOrClosestSunday(DateBean.addDays(yesterday, -90)),
				getText("OperatorFlagHistoryAjax.90DaysAgo"), operatorID)
				+ " UNION "
				+ getOperatorFlagHistorySQL(DateBean.getFirstofMonthOrClosestSunday(DateBean.addDays(yesterday, -60)),
						getText("OperatorFlagHistoryAjax.60DaysAgo"), operatorID)
				+ " UNION "
				+ getOperatorFlagHistorySQL(DateBean.getFirstofMonthOrClosestSunday(DateBean.addDays(yesterday, -30)),
						getText("OperatorFlagHistoryAjax.30DaysAgo"), operatorID)
				+ " UNION "
				+ getOperatorFlagHistorySQL(yesterday, getText("OperatorFlagHistoryAjax.1DayAgo"), operatorID);

		ChartDAO db = new ChartDAO();
		List<DataRow> data = db.select(sql.toString());

		MultiSeriesConverter converter = new MultiSeriesConverter();
		converter.setChart(chart);
		converter.addData(data);
		Map<String, DataSet> dataSet = converter.getChart().getDataSets();
		for (DataSet row : dataSet.values()) {
			row.setShowValues(false);
			FlagColor color = FlagColor.valueOf(row.getSeriesName());

			for (Set set : row.getSets().values()) {
				set.setColor(color.getHex());
			}

			row.setSeriesName(getText(color.getI18nKey()));
		}

		return chart;
	}

	private String getOperatorFlagHistorySQL(Date date, String label, int operatorID) throws Exception {
		String dbDate = DateBean.toDBFormat(date);
		// String creationDate = DateBean.prettyDate(date);

		return String.format("SELECT '%s' AS label, flag AS series, count(*) AS value, "
				+ "creationDate FROM flag_archive WHERE flag in ('Red','Amber','Green') AND opID = %d "
				+ "AND creationDate = '%s' GROUP BY flag", label, operatorID, dbDate);
	}
}
