package com.picsauditing.report;

import static org.junit.Assert.*;
import org.json.simple.JSONArray;
import org.junit.Before;
import org.junit.Ignore;
import org.junit.Test;

import com.picsauditing.jpa.entities.Dashboard;
import com.picsauditing.jpa.entities.DashboardWidget;
import com.picsauditing.jpa.entities.Report;
import com.picsauditing.jpa.entities.WidgetType;
import com.picsauditing.report.models.ModelType;

public class DashboardBuilderTest {
	private Dashboard dashboard;
	
	@Before
	public void setUp() throws Exception {
		dashboard = new Dashboard();
	}

	@Test
	public void testUrl() {
		DashboardWidget widget = dashboard.addWidget(WidgetType.Html, 1);
		widget.setUrl("test.html");

		JSONArray result = DashboardBuilder.build(dashboard);
		assertEquals(1, result.size());
		assertEquals("[{\"panels\":[{\"type\":\"Html\",\"url\":\"test.html\"}]}]", result.toString());
	}

	@Test
	public void testReport() {
		DashboardWidget widget = dashboard.addWidget(WidgetType.Report, 1);
		Report report = new Report();
		widget.setId(1);
		report.setId(1);
		report.setModelType(ModelType.Accounts);
		report.setName("Account Status");
		widget.setReport(report);

		JSONArray result = DashboardBuilder.build(dashboard);
		assertEquals(1, result.size());
		String resultStr = result.toString();
		String expected = "[{\"panels\":[{\"id\":1,\"name\":\"Account Status\",\"type\":\"Report\"}]}]";
		assertEquals(expected, resultStr);
	}
}
