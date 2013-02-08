package com.picsauditing.jpa.entities;

import static org.junit.Assert.*;

import java.util.ArrayList;
import java.util.List;

import org.junit.Before;
import org.junit.Test;


public class ReportTest {

	private Report report;

	private final int REPORT_ID = 123;

	@Before
	public void setUp() {
		report = new Report();
	}

	@Test
	public void testAddColumn_WhenAColumnIsAdded_ItsReportShouldBeSetToTheReportItsBeingAddedTo() {
		Column column = new Column();
		column.setReport(null);
		report.setId(REPORT_ID);

		report.addColumn(column);

		Report columnsReport = report.getColumns().get(0).report;
		assertNotNull(columnsReport);
		assertEquals(REPORT_ID, columnsReport.getId());
	}

	@Test
	public void testAddFilter_WhenAFilterIsAdded_ItsReportShouldBeSetToTheReportItsBeingAddedTo() {
		Filter filter = new Filter();
		filter.setReport(null);
		report.setId(REPORT_ID);

		report.addFilter(filter);

		Report filtersReport = report.getFilters().get(0).report;
		assertNotNull(filtersReport);
		assertEquals(REPORT_ID, filtersReport.getId());
	}

	@Test
	public void testAddSort_WhenASortIsAdded_ItsReportShouldBeSetToTheReportItsBeingAddedTo() {
		Sort sort = new Sort();
		sort.setReport(null);
		report.setId(REPORT_ID);

		report.addSort(sort);

		Report sortsReport = report.getSorts().get(0).report;
		assertNotNull(sortsReport);
		assertEquals(REPORT_ID, sortsReport.getId());
	}

	@Test
	public void testSortColumns_SortsOnSortIndexFirstThenId() {
		Column c1 = new Column();
		Column c2 = new Column();
		Column c3 = new Column();
		Column c4 = new Column();

		// Lower ids come first
		c1.setId(1);
		c2.setId(2);
		c3.setId(3);
		c4.setId(4);

		// Lower sort indices come first
		c1.setSortIndex(20);
		c2.setSortIndex(20);
		c3.setSortIndex(10);
		c4.setSortIndex(10);

		List<Column> unsortedColumns = new ArrayList<Column>();
		unsortedColumns.add(c4);
		unsortedColumns.add(c3);
		unsortedColumns.add(c2);
		unsortedColumns.add(c1);

		report.setColumns(unsortedColumns);
		report.sortColumns();

		List<Column> sortedColumns = report.getColumns();
		assertEquals(c3, sortedColumns.get(0));
		assertEquals(c4, sortedColumns.get(1));
		assertEquals(c1, sortedColumns.get(2));
		assertEquals(c2, sortedColumns.get(3));
	}

}
