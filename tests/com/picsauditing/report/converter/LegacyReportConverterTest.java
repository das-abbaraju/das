package com.picsauditing.report.converter;

import static org.junit.Assert.*;

import org.json.simple.JSONObject;
import org.json.simple.JSONValue;
import org.junit.Before;
import org.junit.Test;
import org.mockito.MockitoAnnotations;
import com.picsauditing.jpa.entities.Report;
import com.picsauditing.report.ReportValidationException;
import com.picsauditing.report.models.ModelType;

/**
 * This is a Legacy converter to test taking the JSON in the old format and
 * moving into the new persistence format.
 */
public class LegacyReportConverterTest {

	private LegacyReportConverter legacyReportConverter;

	// TODO: Convert this to use ApprovalTests
	private static final String SAMPLE_JSON = "{\"modelType\":\"Contractors\","
			+ "\"name\":\"Report Title\",\"description\":\"Sub title of report\",\"filterExpression\":\"1 AND (2 OR 3)\","
			+ "\"columns\":[{\"name\":\"AccountName\"},{\"name\":\"AccountStatus\"},{\"name\":\"ContractorPayingFacilities__Count\",\"method\":\"Count\"}],"
			+ "\"sorts\":[{\"direction\":\"DESC\",\"name\":\"AccountCreationDate\"}],"
			+ "\"filters\":[{\"name\":\"AccountStatus\",\"value\":\"Active, Pending\",\"operator\":\"In\"},"
			+ "{\"name\":\"AccountCreationDate\",\"value\":\"-8D\",\"operator\":\"GreaterThan\"}]}";

	@Before
	public void setUp() throws ReportValidationException {
		MockitoAnnotations.initMocks(this);
		legacyReportConverter = new LegacyReportConverter();
	}

	// TODO break this into two separate tests for JSON => Report and Report => JSON
	@Test
	public void testSetReportPropertiesFromJsonParameters_RoundTrip() throws ReportValidationException {
		JSONObject jsonIn = (JSONObject) JSONValue.parse(SAMPLE_JSON);
		Report report = new Report();
		report.setParameters(SAMPLE_JSON);

		legacyReportConverter.setReportPropertiesFromJsonParameters(report);
		JSONObject jsonOut = legacyReportConverter.toJSON(report);

		// JSON => Report test
		assertEquals(ModelType.Contractors, report.getModelType());
		assertEquals("Report Title", report.getName());
		assertEquals("1 AND (2 OR 3)", report.getFilterExpression());
		assertEquals("Sub title of report", report.getDescription());

		// Report => JSON test
		assertEquals(jsonIn, jsonOut);
		// column parsing
		assertEquals(3, report.getColumns().size());
		assertEquals("AccountName", report.getColumns().get(0).getName());
		// filter parsing
		assertEquals(2, report.getFilters().size());
		assertEquals("AccountStatus", report.getFilters().get(0).getName());
		// sort parsing
		assertEquals(1, report.getSorts().size());
		assertEquals("AccountCreationDate", report.getSorts().get(0).getName());
	}
}
