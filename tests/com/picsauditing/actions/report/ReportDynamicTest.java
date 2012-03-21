package com.picsauditing.actions.report;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

import org.json.simple.JSONArray;
import org.junit.Test;

import com.picsauditing.jpa.entities.Report;
import com.picsauditing.report.models.ModelType;

public class ReportDynamicTest {

	private ReportDynamic action = new ReportDynamic();
	private Report report = new Report();

	@Test
	public void testExecute() throws Exception {
		action.setReport(report);
		report.setModelType(ModelType.Contractors);
		assertEquals(ReportDynamic.SUCCESS, action.execute());
	}

	@Test
	public void testData() throws Exception {
		action.setReport(report);
		report.setModelType(ModelType.Contractors);
		//assertEquals(ReportDynamic.SUCCESS, action.data());
		//System.out.println(action.getJson());
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

	public void testSaveTranslation()
	{
	
	}

}
