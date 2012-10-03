package com.picsauditing.report;

import static com.picsauditing.util.Assert.assertContains;
import static org.junit.Assert.*;

import org.json.simple.JSONObject;
import org.junit.Test;

import com.picsauditing.access.ReportValidationException;
import com.picsauditing.report.fields.Field;
import com.picsauditing.report.fields.FieldType;
import com.picsauditing.report.fields.QueryFilterOperator;

import com.picsauditing.report.Filter;

@SuppressWarnings("unchecked")
public class FilterTest {
	private Filter filter = new Filter();
	private JSONObject jsonObj = new JSONObject();

	@Test
	public void testBlankOperator() {
		jsonObj.put("name", "accountID");
		jsonObj.put("operator", "");

		filter.fromJSON(jsonObj);
		String filterJson = filter.toJSON(true).toJSONString();

		assertContains("\"name\":\"accountID\"", filterJson);
		assertContains("\"operator\":\"Equals\"", filterJson);
	}

	@Test
	public void testValue() {
		jsonObj.put("name", "AccountName");
		jsonObj.put("operator", "BeginsWith");
		jsonObj.put("value", "Trevor's");
		filter.fromJSON(jsonObj);
		assertEquals("AccountName", filter.getFieldName());
		assertEquals(QueryFilterOperator.BeginsWith, filter.getOperator());
		assertEquals("Trevor's", filter.getValues().get(0));

		String jsonString = filter.toJSON(true).toJSONString();

		assertContains("\"name\":\"AccountName\"", jsonString);
		assertContains("\"values\":[\"Trevor's\"]", jsonString);
		assertContains("\"operator\":\"BeginsWith\"", jsonString);
	}

	@Test
	public void testInvalidFilter() throws ReportValidationException {
		assertEquals("true", filter.getSqlForFilter());
	}

	@Test
	public void testFilterEmpty() throws ReportValidationException {
		filter.setFieldName("FieldName");
		filter.setField(new Field(filter.getFieldName(), "fieldName", FieldType.String));
		filter.setOperator(QueryFilterOperator.Empty);

		assertEquals("fieldName IS NULL OR fieldName = ''", filter.getSqlForFilter());
	}

	@Test
	public void testFilterWithValue() throws ReportValidationException {
		filter.setFieldName("FieldName");
		filter.setField(new Field(filter.getFieldName(), "fieldName", FieldType.String));
		filter.getValues().add("Trevor's");

		assertEquals("fieldName = 'Trevor\\'s'", filter.getSqlForFilter());
	}
}
