package com.picsauditing.actions.report;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

import org.json.simple.JSONArray;
import org.junit.Before;
import org.junit.Test;

import com.picsauditing.jpa.entities.Report;
import com.picsauditing.report.QueryBase;

public class ReportDynamicTest {

	private ReportDynamic action;
	private Report report = new Report();

	@Before
	public void setUp() throws Exception {
		action = new ReportDynamic();
	}

	@Test
	public void testMissingReport() throws Exception {
		assertEquals(ReportDynamic.BLANK, action.execute());
		assertEquals(1, action.getActionErrors().size());
	}

	@Test
	public void testMissingReportBase() throws Exception {
		action.setReport(report);
		assertEquals(ReportDynamic.BLANK, action.execute());
		assertEquals(1, action.getActionErrors().size());
	}

	@Test
	public void testExecute() throws Exception {
		action.setReport(report);
		report.setBase(QueryBase.Contractors);
		assertEquals(ReportDynamic.SUCCESS, action.execute());
	}

	@Test
	public void testAvailableBases() throws Exception {
		// report.setId(EntityIdGenerator.next());
		assertEquals(ReportDynamic.JSON, action.availableBases());
		assertEquals(1, action.getJson().size());
		JSONArray bases = (JSONArray) action.getJson().get("bases");
		assertTrue(bases.size() > 1);
	}
	

	@Test
	public void testAvailableFields() throws Exception {
		// report.setId(EntityIdGenerator.next());
		assertEquals(ReportDynamic.JSON, action.availableBases());
		assertEquals(1, action.getJson().size());
		JSONArray bases = (JSONArray) action.getJson().get("bases");
		assertTrue(bases.size() > 1);
	}
	
}
