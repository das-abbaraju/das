package com.picsauditing.util.excel;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;

import org.junit.Before;
import org.junit.Test;
import org.mockito.MockitoAnnotations;

import com.picsauditing.PicsTranslationTest;

public class ExcelSheetTest extends PicsTranslationTest {

	private ExcelSheet excelSheet;

	@Before
	public void setup() {
		MockitoAnnotations.initMocks(this);
		super.resetTranslationService();

		excelSheet = new ExcelSheet();
	}

	@Test
	public void testDisplayOrder() {
		excelSheet.addColumn(new ExcelColumn("first")); // order 10
		excelSheet.addColumn(new ExcelColumn("fourth"), 100);
		excelSheet.addColumn(new ExcelColumn("second"), 20);
		excelSheet.addColumn(new ExcelColumn("third")); // should be 30

		assertNotNull(excelSheet.getColumns().get(30));
		assertEquals("third", excelSheet.getColumns().get(30).getName());
	}
}
