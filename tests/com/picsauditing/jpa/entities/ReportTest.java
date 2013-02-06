package com.picsauditing.jpa.entities;

import static org.junit.Assert.*;

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

}
