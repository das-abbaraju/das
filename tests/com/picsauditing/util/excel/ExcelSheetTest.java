package com.picsauditing.util.excel;

import junit.framework.TestCase;

public class ExcelSheetTest extends TestCase {

	public void testDisplayOrder() {

		ExcelSheet excelSheet = new ExcelSheet();
		excelSheet.addColumn(new ExcelColumn("first")); // order 10
		excelSheet.addColumn(new ExcelColumn("fourth"), 100);
		excelSheet.addColumn(new ExcelColumn("second"), 20);
		excelSheet.addColumn(new ExcelColumn("third")); // should be 30

		assertNotNull(excelSheet.getColumns().get(30));
		assertEquals("third", excelSheet.getColumns().get(30).getName());
	}
}
