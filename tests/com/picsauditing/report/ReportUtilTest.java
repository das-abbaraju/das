package com.picsauditing.report;

import static com.picsauditing.util.Assert.assertContains;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNull;

import java.util.Locale;

import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.junit.AfterClass;
import org.junit.Before;
import org.junit.Ignore;
import org.junit.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.powermock.reflect.Whitebox;

import com.picsauditing.EntityFactory;
import com.picsauditing.PICS.I18nCache;
import com.picsauditing.access.Permissions;
import com.picsauditing.report.ReportUtil;
import com.picsauditing.report.fields.Field;
import com.picsauditing.report.fields.FieldType;
import com.picsauditing.search.Database;

public class ReportUtilTest {
	@Mock
	private Database databaseForTesting;

	@AfterClass
	public static void classTearDown() {
		Whitebox.setInternalState(I18nCache.class, "databaseForTesting", (Database) null);
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

	@Test
	public void testEnumList_Permissions() throws ClassNotFoundException {
		Permissions permissions = EntityFactory.makePermission();
		JSONObject json = ReportUtil.renderEnumFieldAsJson(FieldType.AccountStatus, permissions);
		JSONArray results = (JSONArray)json.get("result");
		assertEquals(5, results.size());
	}

	@Ignore("TODO: Need to translate AccountStatus.Active")
	@Test
	public void testEnumList_Translated() throws ClassNotFoundException {
		Permissions permissions = EntityFactory.makePermission();
		JSONObject json = ReportUtil.renderEnumFieldAsJson(FieldType.AccountStatus, permissions);
		// TODO Mock the DB since the translation doesn't return anything
		assertContains("{\"value\":\"\",\"key\":\"Active\"}", json.toString());
	}

	@Test
	public void testEnumList_Ordinal() throws ClassNotFoundException {
		Permissions permissions = EntityFactory.makePermission();
		JSONObject json = ReportUtil.renderEnumFieldAsJson(FieldType.LowMedHigh, permissions);
		JSONArray results = (JSONArray)json.get("result");
		assertEquals(4, results.size());
		assertContains("\"key\":1}", json.toString());
	}
}
