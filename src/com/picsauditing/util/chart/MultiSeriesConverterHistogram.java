package com.picsauditing.util.chart;

import java.text.DecimalFormat;
import java.util.List;

public class MultiSeriesConverterHistogram extends MultiSeriesConverter {
	protected float minCategory = 0;
	protected float maxCategory = 100;
	protected float categoryDifference = 10;
	private DecimalFormat df = new DecimalFormat("#0.0");

	@Override
	public void addData(List<DataRow> data) {
		// Create all of the categories
		{
			float categoryValue = minCategory;
			while (categoryValue <= maxCategory) {
				Category category = new Category();
				category.setLabel(df.format(categoryValue));
				chart.addCategory(category);
				categoryValue += categoryDifference;
			}
		}

		// Create all of the series
		for (DataRow row : data) {
			if (!chart.getDataSets().containsKey(row.getIndex())) {
				DataSet dataSet = new DataSet();
				dataSet.setSeriesName(row.getSeries());
				chart.addDataSet(dataSet);
			}
		}

		// Add the values into the series and categories
		for (DataRow row : data) {
			Set set = new Set(row);

			// Convert string values like ".2" to "0.2"
			String label = row.getLabel();
			label = df.format(Float.parseFloat(label));
			set.setIndex(label);
			set.setLabel(label);

			// TODO Handle values outside the range (optional)
			chart.getDataSet(row.getSeries()).addSet(set);
		}
	}

	public float getMinCategory() {
		return minCategory;
	}

	public void setMinCategory(float minCategory) {
		this.minCategory = minCategory;
	}

	public float getMaxCategory() {
		return maxCategory;
	}

	public void setMaxCategory(float maxCategory) {
		this.maxCategory = maxCategory;
	}

	public float getCategoryDifference() {
		return categoryDifference;
	}

	public void setCategoryDifference(float categoryDifference) {
		this.categoryDifference = categoryDifference;
	}

}
