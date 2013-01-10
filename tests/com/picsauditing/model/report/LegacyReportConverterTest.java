package com.picsauditing.model.report;

import static org.junit.Assert.*;

import org.json.simple.JSONObject;
import org.json.simple.JSONValue;
import org.junit.Before;
import org.junit.Test;

import com.picsauditing.access.ReportValidationException;
import com.picsauditing.jpa.entities.Report;
import com.picsauditing.report.models.ModelType;

/**
 * This is a Legacy converter to test taking the JSON in the old format and
 * moving into the new peristence format.
 */
public class LegacyReportConverterTest {

	// TODO: Convert this to use ApprovalTests
	private static final String SAMPLE_JSON = "{\"modelType\":\"Contractors\","
			+ "\"name\":\"Report Title\",\"description\":\"Sub title of report\",\"filterExpression\":\"1 AND (2 OR 3)\","
			+ "\"columns\":[{\"name\":\"AccountName\"},{\"name\":\"AccountStatus\"},{\"name\":\"ContractorPayingFacilities__Count\",\"method\":\"Count\"}],"
			+ "\"sorts\":[{\"direction\":\"DESC\",\"name\":\"AccountCreationDate\"}],"
			+ "\"filters\":[{\"name\":\"AccountStatus\",\"value\":\"Active, Pending\",\"operator\":\"In\"},"
			+ "{\"name\":\"AccountCreationDate\",\"value\":\"-8D\",\"operator\":\"GreaterThan\"}]}";
	
	private JSONObject jsonIn;
	private JSONObject jsonOut;
	private Report report;

	@Before
	public void setup() throws ReportValidationException {
		jsonIn = (JSONObject) JSONValue.parse(SAMPLE_JSON);
		report = new Report();
		report.setParameters(SAMPLE_JSON);
		LegacyReportConverter.fillParameters(report);
		jsonOut = LegacyReportConverter.toJSON(report);
	}

	@Test
	public void verifyFillParameters_CorrectlyPopulatingBasicReportLevelFields() {
		assertEquals(ModelType.Contractors, report.getModelType());
		assertEquals("Report Title", report.getName());
		assertEquals("1 AND (2 OR 3)", report.getFilterExpression());
		assertEquals("Sub title of report", report.getDescription());
	}

	@Test
	public void testFull() {
		assertEquals(jsonIn, jsonOut);
	}

	@Test
	public void testColumnParsing() {
		assertEquals(3, report.getColumns().size());
		assertEquals("AccountName", report.getColumns().get(0).getName());
	}

	@Test
	public void testFilterParsing() {
		assertEquals(2, report.getFilters().size());
		assertEquals("AccountStatus", report.getFilters().get(0).getName());
	}

	@Test
	public void testSortParsing() {
		assertEquals(1, report.getSorts().size());
		assertEquals("AccountCreationDate", report.getSorts().get(0).getName());
	}
}
