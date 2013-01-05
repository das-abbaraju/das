package com.picsauditing.model.report;

import static org.junit.Assert.*;

import org.json.simple.JSONObject;
import org.json.simple.JSONValue;
import org.junit.Before;
import org.junit.Test;

import com.picsauditing.jpa.entities.Report;
import com.picsauditing.model.report.ReportParameterConverter;
import com.picsauditing.report.models.ModelType;

/**
 * Previous Version
 */
public class ReportParameterConverterTest {

	private static final String SAMPLE_JSON = "{\"modelType\":\"Contractors\","
			+ "\"name\":\"Report Title\",\"description\":\"Sub title of report\",\"filterExpression\":\"1 AND (2 OR 3)\","
			+ "\"columns\":[{\"name\":\"AccountName\"},{\"name\":\"AccountStatus\"},{\"name\":\"ContractorPayingFacilities__Count\",\"method\":\"Count\"}],"
			+ "\"sorts\":[{\"direction\":\"DESC\",\"name\":\"AccountCreationDate\"}],"
			+ "\"filters\":[{\"name\":\"AccountStatus\",\"value\":\"Active, Pending\",\"operator\":\"In\"},"
			+ "{\"name\":\"AccountCreationDate\",\"value\":\"-8D\",\"operator\":\"GreaterThan\"}],"
			+ "\"version\":\"6.29\"}";

	private JSONObject jsonIn;
	private JSONObject jsonOut;
	private Report report;

	@Before
	public void setup() {
		jsonIn = (JSONObject) JSONValue.parse(SAMPLE_JSON);
		report = new Report();
		report.setParameters(SAMPLE_JSON);
		ReportParameterConverter.fillParameters(report);
		jsonOut = ReportParameterConverter.toJSON(report);
	}

	@Test
	public void testBasicReportLevelFields() {
		// TODO Should we remove the ID from the JSON?
		// assertEquals(123, report.getId());
		assertEquals(ModelType.Contractors, report.getModelType());
		assertEquals("Report Title", report.getName());
		assertEquals("1 AND (2 OR 3)", report.getFilterExpression());
		assertEquals("Sub title of report", report.getDescription());
	}

	@Test
	public void testFull() {
		assertEquals(jsonIn, jsonOut);
		// assertTrue(jsonIn.equals(jsonOut));
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
