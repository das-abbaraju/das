package com.picsauditing.report.fields;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertTrue;

import java.util.Set;

import org.json.simple.JSONObject;
import org.junit.Before;
import org.junit.Test;

import com.picsauditing.EntityFactory;
import com.picsauditing.access.OpPerms;
import com.picsauditing.access.Permissions;

public class FieldTest {

	private Field field;

	private static final String name = "name";
	private static final String text = "text";
	private static final int width = 10;
	private static final String url = "url";

	private static final String value = "value";
	private static final String prefix = "prefix";
	private static final String suffix = "suffix";

	@Before
	public void setUp() {
		field = new Field("");
	}

	@Test
	public void testToJsonObject_NullsDontThrow() {
		field.toJSONObject();
	}

	@Test
	public void testToJsonObject_WriteNoFieldsIfNotPresent() {
		JSONObject json = field.toJSONObject();

		assertNull(json.get("url"));
		assertNull(json.get("text"));
	}

	@Test
	public void testToJsonObject_DefaultWidthIs200() {
		JSONObject json = field.toJSONObject();

		assertEquals(200, json.get("width"));
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
		FieldType type = FieldType.Date;
		field = new Field(name, null, type);
		field.setText(text);
		field.setWidth(width);
		field.setUrl(url);

		JSONObject json = field.toJSONObject();

		assertEquals(name, json.get("name"));
		assertEquals(text, json.get("text"));
		assertEquals(width, json.get("width"));
		assertEquals(url, json.get("url"));
		assertEquals(text, json.get("text"));
		assertEquals(type.getDisplayType().toString().toLowerCase(), json.get("type"));
		assertEquals(type.getFilterType().toString(), json.get("filterType"));
	}

	@Test
	public void testToJsonObject_TypeIfNotAuto() {
		field = new Field("IntegerField", null, FieldType.Integer);

		JSONObject json = field.toJSONObject();
		assertEquals("integer", json.get("type"));
	}

	@Test
	public void testToJsonObject_Booleans() {
		JSONObject json = field.toJSONObject();

		assertTrue((Boolean) json.get("visible"));
		assertTrue((Boolean) json.get("sortable"));
		assertTrue((Boolean) json.get("filterable"));
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
		field = new Field("contractorName", "a.name", FieldType.String);
		field.setUrl("Test.action?id={accountID}");

		Set<String> dependentFields = field.getDependentFields();

		assertEquals(1, dependentFields.size());
		assertTrue(dependentFields.contains("accountID"));
	}

	@Test
	public void testGetDependentFields_UrlDouble() {
		field = new Field("contractorName", "a.name", FieldType.String);
		field.setUrl("Test.action?id={accountID}&name={reportName}");

		Set<String> dependentFields = field.getDependentFields();

		assertEquals(2, dependentFields.size());
		assertTrue(dependentFields.contains("accountID"));
		assertTrue(dependentFields.contains("reportName"));
	}
	

	@Test
	public void testGetAllFields_NoPermissionsTrue() {
		Permissions permissions = EntityFactory.makePermission();
		assertTrue(field.canUserSeeQueryField(permissions));
	}
	
	@Test
	public void testGetAllFields_BillingPermissionsFalse() {
		field.requirePermission(OpPerms.Billing);
		Permissions permissions = EntityFactory.makePermission();
		
		assertFalse(field.canUserSeeQueryField(permissions));
	}
	
	@Test
	public void testGetAllFields_BillingPermissionsTrue() {
		field.requirePermission(OpPerms.Billing);
		Permissions permissions = EntityFactory.makePermission();
		
		EntityFactory.addUserPermission(permissions, OpPerms.Billing);
		assertTrue(field.canUserSeeQueryField(permissions));
	}
	
}
