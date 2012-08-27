package com.picsauditing.report.access;

import static org.junit.Assert.assertNull;

import java.util.Locale;

import org.junit.AfterClass;
import org.junit.Before;
import org.junit.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.powermock.reflect.Whitebox;

import com.picsauditing.PICS.I18nCache;
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
	public void testTranslateLabel_FieldIsNull() {
		Field field = null;
		String translatedText = ReportUtil.translateLabel(field, Locale.ENGLISH);

		assertNull(translatedText);
	}
}
