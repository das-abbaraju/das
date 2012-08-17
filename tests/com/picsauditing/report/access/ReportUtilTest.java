package com.picsauditing.report.access;

import static org.junit.Assert.*;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

import org.junit.AfterClass;
import org.junit.Before;
import org.junit.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.powermock.reflect.Whitebox;

import com.picsauditing.PICS.I18nCache;
import com.picsauditing.jpa.entities.ReportUser;
import com.picsauditing.report.Column;
import com.picsauditing.report.fields.Field;
import com.picsauditing.search.Database;

public class ReportUtilTest {
	@Mock private Database databaseForTesting;

	@AfterClass
	public static void classTearDown() {
		Whitebox.setInternalState(I18nCache.class, "databaseForTesting", (Database)null);
	}

	@Before
	public void setUp() throws Exception {
		MockitoAnnotations.initMocks(this);
		Whitebox.setInternalState(I18nCache.class, "databaseForTesting", databaseForTesting);
	}

	@Test
	public void testGetColumnFromFieldName_NullFieldName() {
		String fieldName = null;
		List<Column> columns = new ArrayList<Column>();

		Column column = ReportUtil.getColumnFromFieldName(fieldName, columns);

		assertNull(column);
	}

	@Test
	public void testGetColumnFromFieldName_FieldNameNotFound() {
		String fieldName = "fieldName";
		List<Column> columns = new ArrayList<Column>();
		columns.add(new Column("differentFieldName"));

		Column column = ReportUtil.getColumnFromFieldName(fieldName, columns);

		assertNull(column);
	}

	@Test
	public void testGetColumnFromFieldName_FieldNameFound() {
		String fieldName = "fieldName";
		List<Column> columns = new ArrayList<Column>();
		columns.add(new Column(fieldName));

		Column column = ReportUtil.getColumnFromFieldName(fieldName, columns);

		assertNotNull(column);
		assertEquals(fieldName, column.getFieldName());
	}

	@Test
	public void testTranslateLabel_FieldIsNull() {
		Field field = null;
		String translatedText = ReportUtil.translateLabel(field, Locale.ENGLISH);

		assertNull(translatedText);
	}

	@Test public void testSwapSortOrder() {
		int valOne = 1;
		int valTwo = 2;
		ReportUser userReportOne = new ReportUser();
		ReportUser userReportTwo = new ReportUser();
		userReportOne.setFavoriteSortIndex(valOne);
		userReportTwo.setFavoriteSortIndex(valTwo);

		ReportUtil.swapSortOrder(userReportOne, userReportTwo);

		assertEquals(valOne, userReportTwo.getFavoriteSortIndex());
		assertEquals(valTwo, userReportOne.getFavoriteSortIndex());
	}
}
