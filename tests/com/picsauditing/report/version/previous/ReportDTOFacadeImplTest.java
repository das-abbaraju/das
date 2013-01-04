package com.picsauditing.report.version.previous;

import static com.picsauditing.util.Assert.assertContains;
import static org.junit.Assert.assertEquals;

import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.junit.Before;
import org.junit.Test;

import com.picsauditing.jpa.entities.Report;
import com.picsauditing.report.Column;
import com.picsauditing.report.Filter;
import com.picsauditing.report.Sort;
import com.picsauditing.report.version.ReportDTOFacade;
import com.picsauditing.report.version.ReportVersionFacadeFactory;

/**
 * Previous Version
 */
@SuppressWarnings("unchecked")
public class ReportDTOFacadeImplTest {
	private JSONObject jsonObj = new JSONObject();
	private Report report;
	private ReportDTOFacade facade;

	@Before
	public void setup() {
		jsonObj.put("version", "6.29");
		facade = ReportVersionFacadeFactory.getFacade(jsonObj);
	}

	@Test
	public void testColumnParsing() {
		JSONArray list = new JSONArray();
		jsonObj.put("columns", list);

		list.add(ReportDTOFacadeImpl.toJSON(new Column("AccountID")));
		list.add(ReportDTOFacadeImpl.toJSON(new Column("AccountName")));
		report = ReportVersionFacadeFactory.createReport(jsonObj);

		assertEquals(2, report.getColumns().size());
		assertEquals("AccountID", report.getColumns().get(0).getId());

		jsonObj = facade.toJSON(report);
		String definitionJson = jsonObj.toString();
		assertContains("\"name\":\"AccountID\"", definitionJson);
		assertContains("\"name\":\"AccountName\"", definitionJson);
	}

	@Test
	public void testFilterParsing() {
		JSONArray list = new JSONArray();
		jsonObj.put("filters", list);

		addFilter(list, "AccountID", "3");

		report = ReportVersionFacadeFactory.createReport(jsonObj);

		assertEquals(1, report.getFilters().size());
		assertEquals("AccountID", report.getFilters().get(0).getId());

		jsonObj = facade.toJSON(report);
		assertContains("\"filters\":[", jsonObj.toString());
	}

	private void addFilter(JSONArray list, String name, String value) {
		Filter filter = new Filter();
		filter.setId(name);
		filter.getValues().add(value);
		list.add(ReportDTOFacadeImpl.toJSON(filter));
	}

	@Test
	public void testSortParsing() {
		JSONArray list = new JSONArray();
		jsonObj.put("sorts", list);

		list.add(ReportDTOFacadeImpl.toJSON(new Sort("AccountID")));

		report = ReportVersionFacadeFactory.createReport(jsonObj);
		assertEquals(1, report.getSorts().size());
		assertEquals("AccountID", report.getSorts().get(0).getId());

		jsonObj = facade.toJSON(report);
		assertContains("\"sorts\":[", jsonObj.toString());
	}

	@Test
	public void testFilterExpression() {
		jsonObj.put("filterExpression", "{1}");
		report = ReportVersionFacadeFactory.createReport(jsonObj);

		assertEquals("{1}", report.getFilterExpression());

		jsonObj = facade.toJSON(report);
		assertContains("\"filterExpression\":\"{1}\"", jsonObj.toString());
	}

}
