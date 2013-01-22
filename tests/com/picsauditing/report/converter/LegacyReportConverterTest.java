package com.picsauditing.report.converter;

import static org.junit.Assert.*;

import org.json.simple.JSONObject;
import org.json.simple.JSONValue;
import org.junit.Before;
import org.junit.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.mockito.internal.util.reflection.Whitebox;

import com.picsauditing.jpa.entities.Report;
import com.picsauditing.model.report.ReportService;
import com.picsauditing.report.ReportValidationException;
import com.picsauditing.report.models.ModelType;

/**
 * This is a Legacy converter to test taking the JSON in the old format and
 * moving into the new peristence format.
 */
public class LegacyReportConverterTest {

	@Mock
	private ReportService reportService;
	
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
	private LegacyReportConverter legacyReportConverter;

	@Before
	public void setup() throws ReportValidationException {
		MockitoAnnotations.initMocks(this);
		legacyReportConverter = new LegacyReportConverter();
		
		Whitebox.setInternalState(legacyReportConverter, "reportService", reportService);
		
		jsonIn = (JSONObject) JSONValue.parse(SAMPLE_JSON);
		report = new Report();
		report.setParameters(SAMPLE_JSON);
		legacyReportConverter.convertParametersToEntities(report);
		jsonOut = legacyReportConverter.toJSON(report);
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
