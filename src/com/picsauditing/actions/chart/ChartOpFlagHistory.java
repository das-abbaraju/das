package com.picsauditing.actions.chart;

import java.util.Date;
import java.util.List;
import java.util.Map;

import com.picsauditing.PICS.DateBean;
import com.picsauditing.jpa.entities.FlagColor;
import com.picsauditing.search.SelectSQL;
import com.picsauditing.util.chart.ChartMultiSeries;
import com.picsauditing.util.chart.DataRow;
import com.picsauditing.util.chart.DataSet;
import com.picsauditing.util.chart.MultiSeriesConverter;
import com.picsauditing.util.chart.Set;

/**
 * Get a count of each flag color for this operator
 * 
 * @author Trevor
 */
@SuppressWarnings("serial")
public class ChartOpFlagHistory extends ChartMSAction {

	@Override
	public ChartMultiSeries buildChart() throws Exception {
		chart.setRotateLabels(true);

		String sqlString = createSQL(0) + " UNION " + createSQL(30) + " UNION " + createSQL(60) + " UNION "
				+ createSQL(90);

		ChartDAO db = new ChartDAO();
		List<DataRow> data = db.select(sqlString);

		MultiSeriesConverter converter = new MultiSeriesConverter();
		converter.setChart(chart);
		converter.addData(data);

		Map<String, DataSet> dataSet = converter.getChart().getDataSets();
		for (DataSet row : dataSet.values()) {
			row.setShowValues(false);
			for (Set set : row.getSets().values()) {
				set.setColor(FlagColor.valueOf(row.getSeriesName()).getHex());

			}
			chart.addDataSet(row);
		}

		return chart;
	}

	private String createSQL(int daysAgo) {
		SelectSQL sql = new SelectSQL("flag_archive");
		sql.addField("creationDate as label");
		sql.addField("flag as series");
		sql.addField("count(*) as value");
		sql.addGroupBy("series, label");
		sql.addWhere("opID = " + permissions.getAccountId());
		sql.addWhere("flag != 'Clear'");
		Date creationDate = DateBean.addDays(new Date(), daysAgo * -1);
		try {
			sql.addWhere("creationDate = '" + DateBean.toDBFormat(creationDate) + "'");
		} catch (Exception doNotShowAnything) {
			sql.addWhere("creationDate IS NULL");
		}

		return sql.toString();
	}
}
