package com.picsauditing.util.excel;

import static org.junit.Assert.*;

import org.junit.AfterClass;
import org.junit.Before;
import org.junit.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.powermock.reflect.Whitebox;

import com.picsauditing.PICS.I18nCache;
import com.picsauditing.search.Database;

public class ExcelSheetTest {
	private ExcelSheet excelSheet;

	@Mock private Database databaseForTesting;
	
	@AfterClass
	public static void classTearDown() {
		Whitebox.setInternalState(I18nCache.class, "databaseForTesting", (Database)null);
	}

	@Before
	public void setup() {
		MockitoAnnotations.initMocks(this);
		Whitebox.setInternalState(I18nCache.class, "databaseForTesting", databaseForTesting);

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
