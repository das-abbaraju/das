package com.picsauditing.report;

import static org.junit.Assert.*;

import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.junit.Test;

import com.picsauditing.report.fields.QueryFilterOperator;
import com.picsauditing.report.fields.Column;
import com.picsauditing.report.fields.Filter;
import com.picsauditing.report.fields.Sort;

@SuppressWarnings("unchecked")
public class DefinitionTest {
	private Definition definition = new Definition();
	private JSONObject jsonObj = new JSONObject();

	@Test
	public void testEmpty() {
		definition.fromJSON(jsonObj);

		String expected = "{\"rowsPerPage\":10}";
		assertEquals(expected, definition.toJSON(true).toJSONString());
	}

	@Test
	public void testColumns() {
		JSONArray list = new JSONArray();
		list.add(new Column("AccountID").toJSON(true));
		list.add(new Column("AccountName").toJSON(true));
		jsonObj.put("columns", list);
		definition.fromJSON(jsonObj);
		assertEquals(2, definition.getColumns().size());
		assertEquals("AccountID", definition.getColumns().get(0).getName());

		String expected = "{\"rowsPerPage\":10,\"columns\":[{\"name\":\"AccountID\"},{\"name\":\"AccountName\"}]}";
		assertEquals(expected, definition.toJSON(true).toJSONString());
	}

	@Test
	public void testFilters() {
		JSONArray list = new JSONArray();
		jsonObj.put("filters", list);

		Filter filter = new Filter();
		filter.setName("AccountID");
		filter.setOperator(QueryFilterOperator.Equals);
		filter.setValue("123");
		list.add(filter.toJSON(true));
		String notTestingNow = filter.toJSON(true).toJSONString();

		definition.fromJSON(jsonObj);
		assertEquals(1, definition.getFilters().size());
		assertEquals("AccountID", definition.getFilters().get(0).getName());

		String expected = "{\"rowsPerPage\":10,\"filters\":[" + notTestingNow + "]}";
		assertEquals(expected, definition.toJSON(true).toJSONString());
	}

	@Test
	public void testSort() {
		JSONArray list = new JSONArray();
		JSONObject sortJson = new Sort("AccountID").toJSON(true);
		list.add(sortJson);
		jsonObj.put("sorts", list);
		definition.fromJSON(jsonObj);
		assertEquals(1, definition.getOrderBy().size());
		assertEquals("AccountID", definition.getOrderBy().get(0).getName());

		String notTestingNow = sortJson.toJSONString();

		String expected = "{\"rowsPerPage\":10,\"sorts\":[" + notTestingNow + "]}";
		assertEquals(expected, definition.toJSON(true).toJSONString());
	}
}
