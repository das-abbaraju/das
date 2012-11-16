package com.picsauditing.actions.report;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;
import static org.mockito.Matchers.anyInt;
import static org.mockito.Mockito.when;

import java.io.File;
import java.util.Date;

import javax.persistence.EntityManager;

import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.RichTextString;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Workbook;
import org.junit.AfterClass;
import org.junit.Before;
import org.junit.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.powermock.reflect.Whitebox;

import com.picsauditing.PicsTestUtil;
import com.picsauditing.PICS.I18nCache;
import com.picsauditing.access.Permissions;
import com.picsauditing.actions.PicsActionSupport;
import com.picsauditing.actions.report.ReportNewReqConImport.RegistrationRequestColumn;
import com.picsauditing.jpa.entities.Country;
import com.picsauditing.jpa.entities.CountrySubdivision;
import com.picsauditing.jpa.entities.OperatorAccount;
import com.picsauditing.jpa.entities.User;
import com.picsauditing.search.Database;
import com.picsauditing.toggle.FeatureToggle;

public class ReportNewReqConImportTest {
	private ReportNewReqConImport reportNewReqConImport;

	@Mock
	private Cell cell;
	@Mock
	private Country country;
	@Mock
	private CountrySubdivision countrySubdivision;
	@Mock
	private Database database;
	@Mock
	private EntityManager entityManager;
	@Mock
	private FeatureToggle featureToggle;
	@Mock
	private File file;
	@Mock
	private OperatorAccount operator;
	@Mock
	private Permissions permissions;
	@Mock
	private RichTextString richText;
	@Mock
	private Row row;
	@Mock
	private User user;
	@Mock
	private Workbook workbook;

	@Before
	public void setUp() throws Exception {
		MockitoAnnotations.initMocks(this);
		Whitebox.setInternalState(I18nCache.class, "databaseForTesting", database);

		reportNewReqConImport = new ReportNewReqConImport();
		PicsTestUtil picsTestUtil = new PicsTestUtil();
		picsTestUtil.autowireEMInjectedDAOs(reportNewReqConImport, entityManager);

		when(file.length()).thenReturn(Long.MAX_VALUE);

		Whitebox.setInternalState(reportNewReqConImport, "featureToggle", featureToggle);
		Whitebox.setInternalState(reportNewReqConImport, "permissions", permissions);
		Whitebox.setInternalState(reportNewReqConImport, "workbook", workbook);
	}

	@AfterClass
	public static void classTearDown() {
		Whitebox.setInternalState(I18nCache.class, "databaseForTesting", (Database) null);
	}

	@Test
	public void testSave_FileMissing() throws Exception {
		assertEquals(PicsActionSupport.SUCCESS, reportNewReqConImport.save());
		assertTrue(reportNewReqConImport.hasActionErrors());
	}

	@Test
	public void testSave_NotExcelFile() throws Exception {
		reportNewReqConImport.setFile(file);
		reportNewReqConImport.setFileFileName("test.file");

		assertEquals(PicsActionSupport.SUCCESS, reportNewReqConImport.save());
		assertTrue(reportNewReqConImport.hasActionErrors());
	}

	@Test
	public void testSave_OldExcelFileExtension() throws Exception {
		reportNewReqConImport.setFile(file);
		reportNewReqConImport.setFileFileName("test.xls");

		assertEquals(PicsActionSupport.SUCCESS, reportNewReqConImport.save());
		assertFalse(reportNewReqConImport.hasActionErrors());
		assertTrue(reportNewReqConImport.hasActionMessages());
	}

	@Test
	public void testSave_NewExcelFileExtension() throws Exception {
		reportNewReqConImport.setFile(file);
		reportNewReqConImport.setFileFileName("test.xlsx");

		assertEquals(PicsActionSupport.SUCCESS, reportNewReqConImport.save());
		assertFalse(reportNewReqConImport.hasActionErrors());
		assertTrue(reportNewReqConImport.hasActionMessages());
	}

	@Test
	public void testSkipHeaderRow_CellNull() throws Exception {
		Boolean skipHeaderRow = Whitebox.invokeMethod(reportNewReqConImport, "skipHeaderRow", row);
		assertFalse(skipHeaderRow);
	}

	@Test
	public void testSkipHeaderRow_HeaderRow() throws Exception {
		when(cell.getRichStringCellValue()).thenReturn(richText);
		when(richText.getString()).thenReturn("Account");
		when(row.getCell(anyInt())).thenReturn(cell);

		Boolean skipHeaderRow = Whitebox.invokeMethod(reportNewReqConImport, "skipHeaderRow", row);
		assertTrue(skipHeaderRow);
	}

	@Test
	public void testGetValue_Numeric() throws Exception {
		double numericCellValue = 1.2;

		when(cell.getCellType()).thenReturn(Cell.CELL_TYPE_NUMERIC);
		when(cell.getNumericCellValue()).thenReturn(numericCellValue);
		when(row.getCell(anyInt())).thenReturn(cell);

		assertEquals(numericCellValue,
				Whitebox.invokeMethod(reportNewReqConImport, "getValue", row, RegistrationRequestColumn.Phone));
	}

	@Test
	public void testGetValue_CountrySubdivision() throws Exception {
		when(cell.getCellType()).thenReturn(Cell.CELL_TYPE_STRING);
		when(cell.getRichStringCellValue()).thenReturn(richText);
		when(richText.getString()).thenReturn("Country-Subdivision");
		when(richText.toString()).thenReturn("Country-Subdivision");
		when(row.getCell(anyInt())).thenReturn(cell);

		assertEquals("Country-Subdivision", Whitebox.invokeMethod(reportNewReqConImport, "getValue", row,
				RegistrationRequestColumn.CountrySubdivision));
	}

	@Test
	public void testGetValue_Country() throws Exception {
		when(cell.getCellType()).thenReturn(Cell.CELL_TYPE_STRING);
		when(cell.getRichStringCellValue()).thenReturn(richText);
		when(entityManager.find(Country.class, "Country")).thenReturn(country);
		when(richText.getString()).thenReturn("Country");
		when(richText.toString()).thenReturn("Country");
		when(row.getCell(anyInt())).thenReturn(cell);

		assertEquals(country,
				Whitebox.invokeMethod(reportNewReqConImport, "getValue", row, RegistrationRequestColumn.Country));
	}

	@Test
	public void testGetValue_RequestedBy() throws Exception {
		when(cell.getCellType()).thenReturn(Cell.CELL_TYPE_NUMERIC);
		when(cell.getNumericCellValue()).thenReturn(1.0);
		when(entityManager.find(OperatorAccount.class, 1)).thenReturn(operator);
		when(row.getCell(anyInt())).thenReturn(cell);

		assertEquals(operator,
				Whitebox.invokeMethod(reportNewReqConImport, "getValue", row, RegistrationRequestColumn.RequestedBy));
	}

	@Test
	public void testGetValue_RequestedByUser() throws Exception {
		when(cell.getCellType()).thenReturn(Cell.CELL_TYPE_NUMERIC);
		when(cell.getNumericCellValue()).thenReturn(1.0);
		when(entityManager.find(User.class, 1)).thenReturn(user);
		when(row.getCell(anyInt())).thenReturn(cell);

		assertEquals(user, Whitebox.invokeMethod(reportNewReqConImport, "getValue", row,
				RegistrationRequestColumn.RequestedByUser));
	}

	@Test
	public void testGetValue_Deadline() throws Exception {
		Date now = new Date();

		when(cell.getCellType()).thenReturn(Cell.CELL_TYPE_STRING);
		when(cell.getDateCellValue()).thenReturn(now);
		when(cell.getRichStringCellValue()).thenReturn(richText);
		when(richText.getString()).thenReturn(now.toString());
		when(row.getCell(anyInt())).thenReturn(cell);

		assertEquals(now,
				Whitebox.invokeMethod(reportNewReqConImport, "getValue", row, RegistrationRequestColumn.Deadline));
	}
}