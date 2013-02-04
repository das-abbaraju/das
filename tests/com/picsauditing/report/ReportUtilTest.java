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
import org.junit.*;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.mockito.invocation.InvocationOnMock;
import org.mockito.stubbing.Answer;
import org.powermock.reflect.Whitebox;

import com.picsauditing.EntityFactory;
import com.picsauditing.PICS.I18nCache;
import com.picsauditing.access.Permissions;
import com.picsauditing.report.fields.Field;
import com.picsauditing.report.fields.FieldType;

public class ReportUtilTest {

	@Mock
	protected I18nCache i18nCache;

	@Before
	public void setUp() throws Exception {
		MockitoAnnotations.initMocks(this);
		Whitebox.setInternalState(I18nCache.class, "INSTANCE", i18nCache);
		when(i18nCache.getText(anyString(), any(Locale.class))).then(returnMockTranslation());
	}

	@After
	public void tearDown() {
		Whitebox.setInternalState(I18nCache.class, "INSTANCE", (I18nCache) null);
	}

	@Test
	public void testTranslateLabel_FieldIsNull() {
		Field field = null;
		String translatedText = ReportUtil.translateLabel(field, Locale.ENGLISH);
		assertNull(translatedText);
	}

	@Test
	public void testRenderEnumFieldAsJson_ifFieldTypeEnumPermissionAware_valueListShouldBeFiltered() throws ClassNotFoundException {
		Permissions permissions = EntityFactory.makePermission();

		JSONObject json = ReportUtil.renderEnumFieldAsJson(FieldType.AccountStatus, permissions);
		JSONArray results = (JSONArray)json.get("result");

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
	public void testRenderEnumFieldAsJson_ifFieldTypeEnumIsTranslatable_valuesShouldBeTranslated() throws ClassNotFoundException {
		Permissions permissions = EntityFactory.makePermission();

		JSONObject json = ReportUtil.renderEnumFieldAsJson(FieldType.AccountStatus, permissions);
		JSONArray results = (JSONArray)json.get("result");

		assertEquals(5, results.size());
		assertContains("{\"value\":\"translation:[AccountStatus.Active, en]\",\"key\":\"Active\"}", json.toJSONString());
		assertContains("{\"value\":\"translation:[AccountStatus.Pending, en]\",\"key\":\"Pending\"}", json.toJSONString());
		assertContains("{\"value\":\"translation:[AccountStatus.Requested, en]\",\"key\":\"Requested\"}", json.toJSONString());
		assertContains("{\"value\":\"translation:[AccountStatus.Deactivated, en]\",\"key\":\"Deactivated\"}", json.toJSONString());
		assertContains("{\"value\":\"translation:[AccountStatus.Declined, en]\",\"key\":\"Declined\"}", json.toJSONString());
	}

	@Test
	public void testRenderEnumFieldAsJson_ifFieldTypeEnumTypeIsOrdinal_keysShouldBeInts() throws ClassNotFoundException {
		Permissions permissions = EntityFactory.makePermission();

		JSONObject json = ReportUtil.renderEnumFieldAsJson(FieldType.LowMedHigh, permissions);
		JSONArray results = (JSONArray)json.get("result");

		assertEquals(4, results.size());
		assertContains("\"key\":0", json.toString());
		assertContains("\"key\":1", json.toString());
		assertContains("\"key\":2", json.toString());
		assertContains("\"key\":3", json.toString());
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
