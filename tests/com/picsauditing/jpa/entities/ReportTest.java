package com.picsauditing.jpa.entities;

import static com.picsauditing.util.Assert.assertContains;

import org.junit.Before;
import org.junit.Test;

import com.picsauditing.report.Column;
import com.picsauditing.report.Definition;
import com.picsauditing.report.models.ModelType;

public class ReportTest {

	private Report report;

	@Before
	public void setUp() throws Exception {
		report = new Report();
		report.setName("Test Report");
		report.setModelType(ModelType.Contractors);
		report.setDescription("This is a test report");
	}

	@Test
	public void testSimpleNameToJson() {
		String jsonString = report.toJSON(true).toJSONString();

		assertContains("\"name\":\"Test Report\"", jsonString);
		assertContains("\"modelType\":\"Contractors\"", jsonString);
		assertContains("\"description\":\"This is a test report\"", jsonString);
	}

	@Test
	public void testSimpleReportToJson() {
		Definition definition = new Definition("");
		definition.getColumns().add(new Column("accountName"));
		report.setDefinition(definition);

		String jsonString = report.toJSON(true).toJSONString();
		assertContains("\"columns\":[{", jsonString);
	}
}
