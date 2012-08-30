package com.picsauditing.report;

import static com.picsauditing.util.Assert.assertContains;
import static org.junit.Assert.*;
import static org.mockito.Mockito.*;

import java.util.Arrays;

import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.junit.Before;
import org.junit.Test;
import org.mockito.MockitoAnnotations;
import org.mockito.Spy;

import com.picsauditing.report.fields.QueryFilterOperator;

public class DefinitionTest {
	
	Definition definition = new Definition();
	
	@Spy 
	Definition spiedDefinition = new Definition();
	
	@Spy 
	Column column = new Column();
	@Spy 
	Filter filter = new Filter();
	@Spy 
	Sort sort= new Sort();		
	
	private JSONObject jsonObj = new JSONObject();

	@Before
	public void setUp() throws Exception {
		MockitoAnnotations.initMocks(this);
	}
	
	@Test
	public void testMerge_NullDefinition() {
		assertNull(definition.merge(null));
	}
	
	@Test
	public void testMerge_NonNullDefinitionWithFilterExpression() {
		commonMergeTestSetup();
		
		definition.merge(spiedDefinition);
		
		commonMergeTestVerification();
		assertNull(definition.getFilterExpression());
	}
	
	@Test
	public void testMerge_NonNullDefinitionWithFilterExpressionAndExistingDefinitionHasNonNullExpression() {
		commonMergeTestSetup();		
		when(spiedDefinition.getFilterExpression()).thenReturn("Expression");
		
		definition.setFilterExpression("Another Expression");
		definition.merge(spiedDefinition);
		
		commonMergeTestVerification();
		assertEquals("Another Expression AND Expression", definition.getFilterExpression());
	}
	
	@Test
	public void testMerge_NonNullDefinitionWithOutFilterExpression() {
		commonMergeTestSetup();		
		when(spiedDefinition.getFilterExpression()).thenReturn("Expression");
		
		definition.merge(spiedDefinition);
		
		commonMergeTestVerification();
		assertEquals("Expression", definition.getFilterExpression());
	}
	
	@SuppressWarnings("unchecked")
	@Test
	public void testColumnParsing() {
		JSONArray list = new JSONArray();
		list.add(new Column("AccountID").toJSON(true));
		list.add(new Column("AccountName").toJSON(true));
		jsonObj.put("columns", list);
		definition.fromJSON(jsonObj);
		assertEquals(2, definition.getColumns().size());
		assertEquals("AccountID", definition.getColumns().get(0).getFieldName());

		assertContains("\"name\":\"AccountID\"", definition.toJSON(true).toJSONString());
		assertContains("\"name\":\"AccountName\"", definition.toJSON(true).toJSONString());
	}

	@SuppressWarnings("unchecked")
	@Test
	public void testFilterParsing() {
		JSONArray list = new JSONArray();
		jsonObj.put("filters", list);

		Filter filter = new Filter();
		filter.setFieldName("AccountID");
		filter.setOperator(QueryFilterOperator.Equals);
		filter.values.add("123");
		list.add(filter.toJSON(true));
		String notTestingNow = filter.toJSON(true).toJSONString();

		definition.fromJSON(jsonObj);
		assertEquals(1, definition.getFilters().size());
		assertEquals("AccountID", definition.getFilters().get(0).getFieldName());

		String expected = "{\"filters\":[" + notTestingNow + "]}";
		assertEquals(expected, definition.toJSON(true).toJSONString());
	}

	@SuppressWarnings("unchecked")
	@Test
	public void testSortParsing() {
		JSONArray list = new JSONArray();
		JSONObject sortJson = new Sort("AccountID").toJSON(true);
		list.add(sortJson);
		jsonObj.put("sorts", list);
		definition.fromJSON(jsonObj);
		assertEquals(1, definition.getSorts().size());
		assertEquals("AccountID", definition.getSorts().get(0).getFieldName());

		String notTestingNow = sortJson.toJSONString();

		String expected = "{\"sorts\":[" + notTestingNow + "]}";
		assertEquals(expected, definition.toJSON(true).toJSONString());
	}
	
	private void commonMergeTestSetup() {
		when(column.getFieldName()).thenReturn("Column");
		when(sort.getFieldName()).thenReturn("Sort FieldName");
		
		spiedDefinition.setColumns(Arrays.asList(column));
		spiedDefinition.setFilters(Arrays.asList(filter));
		spiedDefinition.setSorts(Arrays.asList(sort));
	}
	
	private void commonMergeTestVerification() {
		assertEquals("Column", definition.getColumns().get(0).getFieldName());
		assertEquals("Sort FieldName", definition.getSorts().get(0).getFieldName());
	}
}
