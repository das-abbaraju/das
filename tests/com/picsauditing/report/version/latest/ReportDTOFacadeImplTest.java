package com.picsauditing.report.version.latest;

import static com.picsauditing.util.Assert.assertContains;
import static org.junit.Assert.assertEquals;

import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.junit.Test;

import com.picsauditing.jpa.entities.Report;
import com.picsauditing.report.Column;
import com.picsauditing.report.Filter;
import com.picsauditing.report.Sort;
import com.picsauditing.report.version.ReportVersionFacadeFactory;

/**
 * Latest Version
 */
@SuppressWarnings("unchecked")
public class ReportDTOFacadeImplTest {
	private JSONObject jsonObj = new JSONObject();
	private Report report;

	@Test
	public void testColumnParsing() {
		JSONArray list = new JSONArray();
		jsonObj.put("columns", list);

		list.add(new Column("AccountID").toJSON(true));
		list.add(new Column("AccountName").toJSON(true));
		report = ReportVersionFacadeFactory.createReport(jsonObj);

		assertEquals(2, report.getColumns().size());
		assertEquals("AccountID", report.getColumns().get(0).getId());

		String definitionJson = report.toJSON(true).toJSONString();
		assertContains("\"name\":\"AccountID\"", definitionJson);
		assertContains("\"name\":\"AccountName\"", definitionJson);
	}

	@Test
	public void testFilterParsing() {
		JSONArray list = new JSONArray();
		jsonObj.put("filters", list);

		addFilter(list, "AccountID", "3");

		report.fromJSON(jsonObj);
		assertEquals(1, report.getFilters().size());
		assertEquals("AccountID", report.getFilters().get(0).getId());

		String expected = "{\"filters\":[";
		assertContains(expected, report.toJSON(true).toJSONString());
	}

	private void addFilter(JSONArray list, String name, String value) {
		Filter filter = new Filter();
		filter.setId(name);
		filter.getValues().add(value);
		list.add(filter.toJSON(true));
	}

	@Test
	public void testSortParsing() {
		JSONArray list = new JSONArray();
		jsonObj.put("sorts", list);

		list.add(new Sort("AccountID").toJSON(true));

		report.fromJSON(jsonObj);
		assertEquals(1, report.getSorts().size());
		assertEquals("AccountID", report.getSorts().get(0).getId());

		String expected = "{\"sorts\":[";
		assertContains(expected, report.toJSON(true).toJSONString());
	}

	@Test
	public void testFilterExpression() {
		jsonObj.put("filterExpression", "{1}");
		report.fromJSON(jsonObj);

		assertEquals("{1}", report.getFilterExpression());

		String expected = "{\"filterExpression\":\"{1}\"";
		assertContains(expected, report.toJSON(true).toJSONString());
	}

}
