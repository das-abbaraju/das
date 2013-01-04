package com.picsauditing.report;

import static com.picsauditing.util.Assert.assertContains;
import static org.junit.Assert.assertEquals;

import org.json.simple.JSONObject;
import org.junit.Test;

import com.picsauditing.report.fields.SqlFunction;
import com.picsauditing.report.tables.FieldCategory;
import com.picsauditing.report.version.previous.ReportDTOFacadeImpl;

@SuppressWarnings("unchecked")
public class ColumnTest {

	private Column column = new Column();
	private JSONObject jsonObj = new JSONObject();

	private void fillColumn() {
		column.setId("CompanyStatus__Count");
		column.getField().setCategory(FieldCategory.CompanyStatus);
		column.getField().setText("Company Status");
		column.getField().setHelp("This is the help text for company status.");
		column.getField().setUrl("Contractor.action?id=[AccountID]");
		// Set Type
		// Set Width
	}

	@Test
	public void testId() {
		jsonObj.put("id", "AccountName");
		column = new Column(jsonObj);
		assertEquals("AccountName", column.getId());

		String expected = "\"id\":\"AccountName\"";
		assertContains(expected, convertColumnToJson());
	}

	private String convertColumnToJson() {
		return ReportDTOFacadeImpl.toJSON(column).toString();
	}

	@Test
	public void testMethod() {
		jsonObj.put("id", "AccountName__Count");
		jsonObj.put("method", "UpperCase");
		column = new Column(jsonObj);
		assertEquals(SqlFunction.UpperCase, column.getMethod());

		String expected = "\"sql_function\":\"UpperCase\"";
		assertContains(expected, convertColumnToJson());
	}

	@Test
	public void testBadMethodName() throws Exception {
		try {
			jsonObj.put("id", "AccountName__BadMethodThatDoesNotExist");
			column = new Column(jsonObj);
		} catch (Exception weWantToThrowException) {
			return;
		}
		throw new Exception("There is no QueryMethod called BadMethodThatDoesNotExist");
	}

	@Test
	public void testGetFieldNameWithoutMethod() {
		column.setId("FacilityCount__Count");
		assertEquals("FacilityCount__Count", column.getId());
		assertEquals("FacilityCount", column.getFieldNameWithoutMethod());
		assertEquals(SqlFunction.Count, column.getMethod());
	}

	@Test
	public void testToJsonWithNonAutoMethod() {
		column.setId("FacilityCount__Count");
		JSONObject json = ReportDTOFacadeImpl.toJSON(column);
		Column column2 = ReportDTOFacadeImpl.toColumn(json);
		assertEquals(column.getId(), column2.getId());
	}

}
