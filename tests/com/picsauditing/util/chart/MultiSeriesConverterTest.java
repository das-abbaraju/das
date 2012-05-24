package com.picsauditing.util.chart;

import static org.junit.Assert.*;
import static org.mockito.Mockito.*;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;

import org.junit.Before;
import org.junit.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;


public class MultiSeriesConverterTest {
	private MultiSeriesConverter multiSeriesConverter;
	private Map<String, Category> categories = new TreeMap<String, Category>();
	private Map<String, DataSet> dataSets = new TreeMap<String, DataSet>();
	private ChartMultiSeries chart;
	
	@Before
	public void setUp() throws Exception {
		MockitoAnnotations.initMocks(this);
	
		chart = new ChartMultiSeries();
		chart.setCategories(categories);
		chart.setDataSets(dataSets);
		
		multiSeriesConverter = new MultiSeriesConverter();
		multiSeriesConverter.setChart(chart);
	}
	
	@Test
	public void testAddData_Categories() throws Exception {
		List<DataRow> data = createTestData_Categories();
		
		multiSeriesConverter.addData(data);
		
		for (DataRow row : data) {
			row.toString();
		}
		
		for (Category category : categories.values()) {
			category.toString();
		}
	}
	
	private List<DataRow> createTestData_Categories() {
		List<DataRow> data = new ArrayList<DataRow>();
		DataRow row = new DataRow();
		row.setLabel("30 Days");
		row.setSeries("Green");
		row.setValue(2.8f);
		data.add(row);
		return data;
	}
}
