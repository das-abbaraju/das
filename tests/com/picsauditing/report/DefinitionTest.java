package com.picsauditing.report;

import static com.picsauditing.util.Assert.assertContains;
import static org.junit.Assert.assertEquals;

import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.junit.Test;

@SuppressWarnings("unchecked")
public class DefinitionTest {

	private JSONObject jsonObj = new JSONObject();
	private Definition definition = new Definition("");

	@Test
	public void testColumnParsing() {
		JSONArray list = new JSONArray();
		jsonObj.put("columns", list);

		list.add(new Column("AccountID").toJSON(true));
		list.add(new Column("AccountName").toJSON(true));
		definition = new Definition(jsonObj.toString());

		assertEquals(2, definition.getColumns().size());
		assertEquals("AccountID", definition.getColumns().get(0).getId());

		String definitionJson = definition.toJSON(true).toJSONString();
		assertContains("\"name\":\"AccountID\"", definitionJson);
		assertContains("\"name\":\"AccountName\"", definitionJson);
	}

	@Test
	public void testFilterParsing() {
		JSONArray list = new JSONArray();
		jsonObj.put("filters", list);

		addFilter(list, "AccountID", "3");

		definition.fromJSON(jsonObj);
		assertEquals(1, definition.getFilters().size());
		assertEquals("AccountID", definition.getFilters().get(0).getId());

		String expected = "{\"filters\":[";
		assertContains(expected, definition.toJSON(true).toJSONString());
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

		definition.fromJSON(jsonObj);
		assertEquals(1, definition.getSorts().size());
		assertEquals("AccountID", definition.getSorts().get(0).getId());

		String expected = "{\"sorts\":[";
		assertContains(expected, definition.toJSON(true).toJSONString());
	}
	
	@Test
	public void testFilterExpression() {
		jsonObj.put("filterExpression", "{1}");
		definition.fromJSON(jsonObj);
		
		assertEquals("{1}", definition.getFilterExpression());

		String expected = "{\"filterExpression\":\"{1}\"";
		assertContains(expected, definition.toJSON(true).toJSONString());
	}
}
