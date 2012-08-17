package com.picsauditing.report.fields;

import static org.junit.Assert.*;

import java.util.Set;

import org.json.simple.JSONObject;
import org.junit.Before;
import org.junit.Test;

public class FieldTest {

	private Field field;

	private static final String name = "name";
	private static final String text = "text";
	private static final int width = 10;
	private static final boolean hidden = true;
	private static final String url = "url";

	private static final String value = "value";
	private static final String prefix = "prefix";
	private static final String suffix = "suffix";

	@Before
	public void setUp() {
		field = new Field(null, null, null);
	}

	@Test
	public void testToJsonObject_NullsDontThrow() {
		field.toJSONObject();
	}

	@Test
	public void testToJsonObject_WriteNoFieldsIfNotPresent() {
		JSONObject json = field.toJSONObject();

		assertNull(json.get("name"));
		assertNull(json.get("url"));
		assertNull(json.get("text"));
		assertNull(json.get("type"));
		assertNull(json.get("filterType"));
	}

	@Test
	public void testToJsonObject_DefaultWidthIs200() {
		JSONObject json = field.toJSONObject();

		assertEquals(200, json.get("width"));
	}

	@Test
	public void testToJsonObject_HiddenIfCreatedWithNameEndsInId() {
		field = new Field(name + "id", null, null);

		JSONObject json = field.toJSONObject();

		assertEquals(true, json.get("hidden"));
	}

	@Test
	public void testToJsonObject_PropertiesAlwaysWritten() {
		field.setName(name);
		field.setText(text);

		JSONObject json = field.toJSONObject();

		assertEquals(name, json.get("name"));
		assertEquals(text, json.get("text"), text);
	}

	@Test
	public void testToJsonObject_WriteAllFieldsIfPresent() {
		FilterType filterType = FilterType.Date;
		field = new Field(name, null, filterType);
		field.setText(text);
		field.setWidth(width);
		field.setUrl(url);

		JSONObject json = field.toJSONObject();

		assertEquals(name, json.get("name"));
		assertEquals(text, json.get("text"));
		assertEquals(width, json.get("width"));
		assertEquals(url, json.get("url"));
		assertEquals(text, json.get("text"));
		assertEquals(filterType.getFieldType().toString().toLowerCase(), json.get("type"));
		assertEquals(filterType.toString(), json.get("filterType"));
	}

	@Test
	public void testToJsonObject_NoTypeIfAuto() {
		JSONObject json = field.toJSONObject();

		assertNull(json.get("type"));
	}

	@Test
	public void testToJsonObject_TypeIfNotAuto() {
		field = new Field(null, null, FilterType.Integer);

		JSONObject json = field.toJSONObject();

		assertNotNull(json.get("type"));
	}

	@Test
	public void testToJsonObject_BooleansNotWritten() {
		field.setHidden(false);

		JSONObject json = field.toJSONObject();

		assertNull(json.get("visible"));
		assertNull(json.get("sortable"));
		assertNull(json.get("filterable"));
		assertNull(json.get("hidden"));
	}

	@Test
	public void testToJsonObject_BooleansWritten() {
		field.setHidden(hidden);

		JSONObject json = field.toJSONObject();

		assertEquals(hidden, json.get("hidden"));
	}

	@Test
	public void testGetI18nKey_PrefixNullSuffixNull() {
		field.setTranslationPrefixAndSuffix(null, null);

		String key = field.getI18nKey(value);

		assertEquals(value, key);
	}

	@Test
	public void testGetI18nKey_PrefixNullSuffixSet() {
		field.setTranslationPrefixAndSuffix(null, suffix);

		String key = field.getI18nKey(value);

		assertEquals(value + "." + suffix, key);
	}

	@Test
	public void testGetI18nKey_PrefixSetSuffixNull() {
		field.setTranslationPrefixAndSuffix(prefix, null);

		String key = field.getI18nKey(value);

		assertEquals(prefix + "." + value, key);
	}

	@Test
	public void testGetI18nKey_PrefixSetSuffixSet() {
		field.setTranslationPrefixAndSuffix(prefix, suffix);

		String key = field.getI18nKey(value);

		assertEquals(prefix + "." + value + "." + suffix, key);
	}

	@Test
	public void testGetDependentFields_UrlSingle() {
		field = new Field("contractorName", "a.name", FilterType.AccountName);
		field.setUrl("Test.action?id={accountID}");

		Set<String> dependentFields = field.getDependentFields();

		assertEquals(1, dependentFields.size());
		assertTrue(dependentFields.contains("accountID"));
	}

	@Test
	public void testGetDependentFields_UrlDouble() {
		field = new Field("contractorName", "a.name", FilterType.AccountName);
		field.setUrl("Test.action?id={accountID}&name={reportName}");

		Set<String> dependentFields = field.getDependentFields();

		assertEquals(2, dependentFields.size());
		assertTrue(dependentFields.contains("accountID"));
		assertTrue(dependentFields.contains("reportName"));
	}
}
