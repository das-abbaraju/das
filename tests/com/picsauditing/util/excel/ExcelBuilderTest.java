package com.picsauditing.util.excel;

import java.io.FileOutputStream;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Map;

import junit.framework.Assert;

import org.apache.poi.hssf.usermodel.HSSFSheet;
import org.apache.poi.hssf.usermodel.HSSFWorkbook;
import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.junit.Test;

import com.picsauditing.EntityFactory;
import com.picsauditing.access.OpPerms;
import com.picsauditing.access.Permissions;
import com.picsauditing.jpa.entities.AccountStatus;
import com.picsauditing.model.ReportModel;
import com.picsauditing.report.Column;
import com.picsauditing.report.fields.Field;
import com.picsauditing.report.models.AbstractModel;
import com.picsauditing.report.models.ModelFactory;
import com.picsauditing.report.models.ModelType;

public class ExcelBuilderTest {
	private List<Column> columns;
	private Map<String, Field> availableFields;
	private JSONArray jsonResults;

	private ExcelBuilder builder = new ExcelBuilder();

	@Test
	public void testBuild() throws Exception {
		{
			jsonResults = new JSONArray();
			addDataRow(AccountStatus.Active, "Irvine");
			addDataRow(AccountStatus.Active, "Houston");
			addDataRow(AccountStatus.Pending, "Houston");
			addDataRow(AccountStatus.Deleted, null);

			columns = new ArrayList<Column>();
			AbstractModel table = ModelFactory.build(ModelType.Contractors);
			Permissions permissions = EntityFactory.makePermission();
			EntityFactory.addUserPermission(permissions, OpPerms.Billing);
			availableFields = ReportModel.buildAvailableFields(table.getRootTable(), permissions);
		}
		
		addColumn("accountStatus");
		addColumn("accountID");
		addColumn("accountCity");
		addColumn("contractorScore");
		addColumn("contractorBalance");
		addColumn("contractorPQFExpiresDate");
		
		builder.addColumns(columns);
		HSSFWorkbook workbook = builder.buildWorkbook("Tester", jsonResults);
		HSSFSheet excelSheet = workbook.getSheetAt(0);
		
		// TODO this should probably be broken up into several unit tests that do each assertion
		Assert.assertEquals("Tester", excelSheet.getSheetName());
		Assert.assertEquals(jsonResults.size(), excelSheet.getLastRowNum());
		Assert.assertEquals("accountStatus", excelSheet.getRow(0).getCell(0).getRichStringCellValue().getString());
		Assert.assertEquals("Active", excelSheet.getRow(1).getCell(0).getRichStringCellValue().getString());
		
		Assert.assertEquals("accountStatus should adjust the width", 4522, excelSheet.getColumnWidth(0));
		Assert.assertTrue("accountID column should be hidden", excelSheet.isColumnHidden(1));

		FileOutputStream stream = new FileOutputStream("tests/junitExcelBuilderTest.xls");
		workbook.write(stream);
		stream.close();
	}
	
	private Column addColumn(String fieldName) {
		// It would be great to move this over to another class. It seems like a
		// standard utility like we have in EntityFactory
		Column column = new Column(fieldName);
		Field field = availableFields.get(fieldName.toUpperCase());
		column.setField(field);
		columns.add(column);
		return column;
	}

	@SuppressWarnings("unchecked")
	private void addDataRow(AccountStatus status, String city) {
		JSONObject row = new JSONObject();
		row.put("accountStatus", status.toString());
		row.put("accountCity", city);
		row.put("contractorPQFExpiresDate", new Date());
		row.put("contractorScore", 850);
		row.put("contractorBalance", 123.45);
		jsonResults.add(row);
	}
}
