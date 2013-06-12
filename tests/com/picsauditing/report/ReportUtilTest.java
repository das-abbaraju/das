package com.picsauditing.report;

import static com.picsauditing.util.Assert.assertContains;
import static com.picsauditing.util.Assert.assertNotContains;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNull;
import static org.mockito.Matchers.any;
import static org.mockito.Matchers.anyString;
import static org.mockito.Mockito.when;

import java.util.Arrays;
import java.util.Locale;

import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.junit.Before;
import org.junit.Test;
import org.mockito.invocation.InvocationOnMock;
import org.mockito.stubbing.Answer;

import com.picsauditing.EntityFactory;
import com.picsauditing.PicsTranslationTest;
import com.picsauditing.access.Permissions;
import com.picsauditing.jpa.entities.Column;
import com.picsauditing.jpa.entities.Report;
import com.picsauditing.model.i18n.LanguageModel;
import com.picsauditing.report.fields.Field;
import com.picsauditing.report.fields.FieldType;
import com.picsauditing.report.fields.SqlFunction;

public class ReportUtilTest extends PicsTranslationTest {

	@Before
	public void setUp() throws Exception {
		super.resetTranslationService();
		when(translationService.getText(anyString(), any(Locale.class))).then(returnMockTranslation());
	}

	@Test
	public void testTranslateLabel_FieldIsNull() {
		Field field = null;
		String translatedText = ReportUtil.translateLabel(field, Locale.ENGLISH);
		assertNull(translatedText);
	}

	@Test
	public void testRenderEnumFieldAsJson_ifFieldTypeEnumPermissionAware_valueListShouldBeFiltered()
			throws ClassNotFoundException {
		Permissions permissions = EntityFactory.makePermission();

		JSONObject json = ReportUtil.renderEnumFieldAsJson(FieldType.AccountStatus, permissions);
		JSONArray results = (JSONArray) json.get("result");

		assertEquals(7, FieldType.AccountStatus.getEnumClass().getEnumConstants().length);
		assertEquals(5, results.size());
		assertContains("\"key\":\"Active\"}", json.toJSONString());
		assertContains("\"key\":\"Pending\"}", json.toJSONString());
		assertContains("\"key\":\"Requested\"}", json.toJSONString());
		assertContains("\"key\":\"Deactivated\"}", json.toJSONString());
		assertContains("\"key\":\"Declined\"}", json.toJSONString());
		assertNotContains("\"key\":\"Demo\"}", json.toJSONString());
		assertNotContains("\"key\":\"Deleted\"}", json.toJSONString());
	}

	@Test
	public void testRenderEnumFieldAsJson_ifFieldTypeEnumIsTranslatable_valuesShouldBeTranslated() throws Exception {
		Permissions permissions = EntityFactory.makePermission();

		JSONObject json = ReportUtil.renderEnumFieldAsJson(FieldType.AccountStatus, permissions);
		JSONArray results = (JSONArray) json.get("result");

		String format = "{\"value\":\"translation:[AccountStatus.%1$s, " + LanguageModel.ENGLISH.toString() + "]\","
				+ "\"key\":\"%1$s\"}";

		assertEquals(5, results.size());
		assertContains(String.format(format, "Active"), json.toJSONString());
		assertContains(String.format(format, "Pending"), json.toJSONString());
		assertContains(String.format(format, "Requested"), json.toJSONString());
		assertContains(String.format(format, "Deactivated"), json.toJSONString());
		assertContains(String.format(format, "Declined"), json.toJSONString());
	}

	@Test
	public void testRenderEnumFieldAsJson_ifFieldTypeEnumTypeIsOrdinal_keysShouldBeInts() throws ClassNotFoundException {
		Permissions permissions = EntityFactory.makePermission();

		JSONObject json = ReportUtil.renderEnumFieldAsJson(FieldType.LowMedHigh, permissions);
		JSONArray results = (JSONArray) json.get("result");

		assertEquals(4, results.size());
		assertContains("\"key\":0", json.toString());
		assertContains("\"key\":1", json.toString());
		assertContains("\"key\":2", json.toString());
		assertContains("\"key\":3", json.toString());
	}

	@Test
	public void testAddTranslatedLabelsToReportParameters_ColumnWithFunctionTranslation() throws ClassNotFoundException {
		Permissions permissions = EntityFactory.makePermission();

		Report report = new Report();
		Column column = new Column();
		report.addColumn(column);

		Field accountName = new Field("AccountName__Count");
		column.setField(accountName);
		column.setSqlFunction(SqlFunction.Count);
		column.setName("AccountName__Count");

		ReportUtil.addTranslatedLabelsToReport(report, permissions.getLocale());

		String locale = LanguageModel.ENGLISH.toString();

		assertEquals("translation:[Report.Function.Count, " + locale + "]: translation:[Report.AccountName, " + locale
				+ "]", report.getColumns().get(0).getField().getText());
	}

	private Answer<String> returnMockTranslation() {
		return new Answer<String>() {
			@Override
			public String answer(InvocationOnMock invocation) throws Throwable {
				Object[] args = invocation.getArguments();
				return "translation:" + Arrays.toString(args);
			}
		};
	}

}
