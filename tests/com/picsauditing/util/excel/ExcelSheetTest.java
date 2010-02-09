package com.picsauditing.util.excel;

import java.io.FileInputStream;
import java.io.IOException;
import java.text.ParseException;

import org.apache.poi.hssf.usermodel.HSSFCell;
import org.apache.poi.hssf.usermodel.HSSFRow;
import org.apache.poi.hssf.usermodel.HSSFSheet;
import org.apache.poi.hssf.usermodel.HSSFWorkbook;

import com.picsauditing.jpa.entities.ContractorRegistrationRequest;

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

	public void testImportData() throws ParseException {
		// Example from
		// http://svn.apache.org/repos/asf/poi/trunk/src/examples/src/org/apache/poi/hssf/usermodel/examples/HSSFReadWrite.java
		HSSFWorkbook wb = null;

		try {
			// This is going to fail, of course
			wb = new HSSFWorkbook(new FileInputStream("C:/Users/Lani/Desktop/Test/temp.xls"));

			System.out.println("Data dump:\n");

			for (int k = 0; k < wb.getNumberOfSheets(); k++) {
				HSSFSheet sheet = wb.getSheetAt(k);
				int rows = sheet.getPhysicalNumberOfRows();
				System.out.println("Sheet " + k + " \"" + wb.getSheetName(k) + "\" has " + rows + " row(s).");
				for (int r = 0; r < rows; r++) {
					HSSFRow row = sheet.getRow(r);
					ContractorRegistrationRequest crr = new ContractorRegistrationRequest();
					if (row == null) {
						continue;
					}

					for (int c = 0; c < 15; c++) {
						HSSFCell cell = row.getCell(c);
						String value = null;
						
						if (c == 2 || c == 13)
							value = cell.toString();
						else if (cell != null) {
							switch (cell.getCellType()) {
								case HSSFCell.CELL_TYPE_FORMULA:
									value = cell.getCellFormula();
									break;
								case HSSFCell.CELL_TYPE_NUMERIC:
									value = "" + (int) cell.getNumericCellValue();
									break;
								default: value = cell.getStringCellValue();
							}
						}
						//System.out.println("CELL col=" + cell.getColumnIndex() + " VALUE=" + value);
						System.out.println("[" + c + "]: " + value);
					}
				}
			}
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
}
