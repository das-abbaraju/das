package com.picsauditing.report.converter;

import static com.picsauditing.report.ReportJson.*;
import static com.picsauditing.util.Assert.*;
import static org.mockito.Mockito.*;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Locale;
import java.util.Map;

import org.json.simple.JSONArray;
import org.junit.AfterClass;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.powermock.reflect.Whitebox;

import com.picsauditing.PICS.I18nCache;
import com.picsauditing.access.Permissions;
import com.picsauditing.jpa.entities.Account;
import com.picsauditing.report.ReportUtil;
import com.picsauditing.report.converter.JsonReportElementsBuilder;
import com.picsauditing.report.fields.Field;
import com.picsauditing.report.fields.FieldType;
import com.picsauditing.report.models.ReportModel;
import com.picsauditing.report.tables.FieldCategory;
import com.picsauditing.search.Database;

public class JsonReportElementsBuilderTest {

	@Mock
	private Permissions permissions;
	@Mock
	protected I18nCache i18nCache;
	@Mock
	private ReportModel reportModel;

	private final int USER_ID = 123;
	private final int ACCOUNT_ID = Account.PicsID;
	private static final String TRANSLATION_PREFIX = "translation:";

	@BeforeClass
	public static void setUpClass() {
		Whitebox.setInternalState(I18nCache.class, "databaseForTesting", mock(Database.class));
	}

	@Before
	public void setUp() throws Exception {
		MockitoAnnotations.initMocks(this);
		Whitebox.setInternalState(ReportUtil.class, "i18nCache", i18nCache);

		when(permissions.getAccountIdString()).thenReturn("" + ACCOUNT_ID);
		when(permissions.getVisibleAccounts()).thenReturn(new HashSet<Integer>());
		when(permissions.getUserIdString()).thenReturn("" + USER_ID);
		when(permissions.getUserId()).thenReturn(USER_ID);
		when(permissions.getLocale()).thenReturn(Locale.ENGLISH);
	}

	@AfterClass
	public static void classTearDown() {
		Whitebox.setInternalState(I18nCache.class, "databaseForTesting", (Database) null);
		Whitebox.setInternalState(ReportUtil.class, "i18nCache", (I18nCache) null);
	}

	@Test
	public void testBuildColumns_WhenMembersAreSet_ThenTheyreWrittenToJson() {
		// ReportElement common properties
		String fieldName = "Field Name";
		String categoryTranslation = "Account Information";
		String description = "This is a column";

		// TODO Avoid constructor with side effects
		Field field = new Field("");
		field.setName(fieldName);
		FieldCategory fieldCategory = FieldCategory.AccountInformation;
		field.setCategory(fieldCategory);
		// We're not calling field.setCategoryTranslation() because that's set by ReportUtil
		// We're not calling field.setHelp() because that's set by ReportUtil

		// Column specific properties that live in the field
		FieldType fieldType = FieldType.AccountID;
		String url = "www.picsauditing.com";
		boolean sortable = true;
		int width = 123;

		field.setType(fieldType);
		field.setUrl(url);
		field.setSortable(sortable);
		field.setWidth(width);

		Map<String, Field> fieldsMap = new HashMap<String, Field>();
		fieldsMap.put("DONT_CARE", field);
		when(reportModel.getAvailableFields()).thenReturn(fieldsMap);

		// This is super brittle, but testing exactly what's actually happening
		when(i18nCache.getText(eq(ReportUtil.REPORT_CATEGORY_KEY_PREFIX + fieldCategory.toString()), any(Locale.class))).thenReturn(TRANSLATION_PREFIX + categoryTranslation);
		when(i18nCache.getText(eq(ReportUtil.REPORT_KEY_PREFIX + fieldName), any(Locale.class))).thenReturn(TRANSLATION_PREFIX + fieldName);
		when(i18nCache.getText(eq(ReportUtil.REPORT_KEY_PREFIX + fieldName + ReportUtil.HELP_KEY_SUFFIX), any(Locale.class))).thenReturn(TRANSLATION_PREFIX + description);

		JSONArray jsonArray = JsonReportElementsBuilder.buildColumns(reportModel, permissions);
		String jsonString = jsonArray.toString();

		assertJson(REPORT_ELEMENT_FIELD_ID, fieldName, jsonString);
		assertJson(REPORT_ELEMENT_CATEGORY, TRANSLATION_PREFIX + categoryTranslation, jsonString);
		assertJson(REPORT_ELEMENT_NAME, TRANSLATION_PREFIX + fieldName, jsonString);
		assertJson(REPORT_ELEMENT_DESCRIPTION, TRANSLATION_PREFIX + description, jsonString);

		assertJson(COLUMN_TYPE, field.getDisplayType().name(), jsonString);
		assertJson(COLUMN_URL, url, jsonString);
		assertJsonNoQuotes(COLUMN_SQL_FUNCTION, null, jsonString);
		assertJsonNoQuotes(COLUMN_WIDTH, width, jsonString);
		assertJsonNoQuotes(COLUMN_SORTABLE, sortable, jsonString);
	}

	@Test
	public void testBuildFilters_WhenMembersAreSet_ThenTheyreWrittenToJson() {
		// ReportElement common properties
		String fieldName = "Field Name";
		String categoryTranslation = "Account Information";
		String description = "This is a column";

		// TODO why does this require an argument?
		Field field = new Field("");
		field.setName(fieldName);
		FieldCategory fieldCategory = FieldCategory.AccountInformation;
		field.setCategory(fieldCategory);
		// We're not calling field.setCategoryTranslation() because that's set by ReportUtil
		// We're not calling field.setHelp() because that's set by ReportUtil

		// Column specific properties that live in the field
		FieldType fieldType = FieldType.AccountID;

		field.setType(fieldType);

		Map<String, Field> fieldsMap = new HashMap<String, Field>();
		fieldsMap.put("DONT_CARE", field);
		when(reportModel.getAvailableFields()).thenReturn(fieldsMap);

		// This is super brittle, but testing exactly what's actually happening
		when(i18nCache.getText(eq(ReportUtil.REPORT_CATEGORY_KEY_PREFIX + fieldCategory.toString()), any(Locale.class))).thenReturn(TRANSLATION_PREFIX + categoryTranslation);
		when(i18nCache.getText(eq(ReportUtil.REPORT_KEY_PREFIX + fieldName), any(Locale.class))).thenReturn(TRANSLATION_PREFIX + fieldName);
		when(i18nCache.getText(eq(ReportUtil.REPORT_KEY_PREFIX + fieldName + ReportUtil.HELP_KEY_SUFFIX), any(Locale.class))).thenReturn(TRANSLATION_PREFIX + description);

		JSONArray jsonArray = JsonReportElementsBuilder.buildFilters(reportModel, permissions);
		String jsonString = jsonArray.toString();

		assertJson(REPORT_ELEMENT_FIELD_ID, fieldName, jsonString);
		assertJson(REPORT_ELEMENT_CATEGORY, TRANSLATION_PREFIX + categoryTranslation, jsonString);
		assertJson(REPORT_ELEMENT_NAME, TRANSLATION_PREFIX + fieldName, jsonString);
		assertJson(REPORT_ELEMENT_DESCRIPTION, TRANSLATION_PREFIX + description, jsonString);

		assertJson(FILTER_TYPE, fieldType.getFilterType(), jsonString);
		assertJsonNoQuotes(FILTER_OPERATOR, fieldType.getFilterType().defaultOperator, jsonString);
		assertJsonNoQuotes(FILTER_VALUE, "", jsonString);
		assertJsonNoQuotes(FILTER_COLUMN_COMPARE, null, jsonString);
	}

}
