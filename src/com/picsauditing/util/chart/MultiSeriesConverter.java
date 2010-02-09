package com.picsauditing.util.chart;

import java.util.List;

public class MultiSeriesConverter {
	protected ChartMultiSeries chart;

	public void addData(List<DataRow> data) {
		// Create all of the categories
		for (DataRow row : data) {
			if (!chart.getCategories().containsKey(row.getIndex())) {
				Category category = new Category();
				category.setLabel(row.getLabel());
				category.setIndex(row.getIndex());
				chart.addCategory(category);
			}
		}

		// Create all of the series
		for (DataRow row : data) {
			if (!chart.getDataSets().containsKey(row.getSeries())) {
				DataSet dataSet = new DataSet();
				dataSet.setSeriesName(row.getSeries());
				chart.addDataSet(dataSet);
			}
		}

		// Add the values into the series and categories
		for (DataRow row : data) {
			Set set = new Set(row);
			chart.getDataSet(row.getSeries()).addSet(set);
		}
	}

	public ChartMultiSeries getChart() {
		return chart;
	}

	public void setChart(ChartMultiSeries chart) {
		this.chart = chart;
	}
}
