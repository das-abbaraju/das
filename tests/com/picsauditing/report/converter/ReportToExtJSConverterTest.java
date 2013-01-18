package com.picsauditing.report.converter;

import static com.picsauditing.util.Assert.assertContains;

import org.json.simple.JSONObject;
import org.junit.Test;

import com.picsauditing.jpa.entities.Report;
import com.picsauditing.report.converter.ReportToExtJSConverter;
import com.picsauditing.report.models.ModelType;

public class ReportToExtJSConverterTest {

	@Test
	public void testSimpleNameToJson() {
		Report report = new Report();
		report.setName("Test Report");
		report.setModelType(ModelType.Contractors);
		report.setDescription("This is a test report");

		JSONObject json = ReportToExtJSConverter.toJSON(report);
		String jsonString = json.toString();

		assertContains("\"name\":\"Test Report\"", jsonString);
		assertContains("\"type\":\"Contractors\"", jsonString);
		assertContains("\"description\":\"This is a test report\"", jsonString);
	}

}
