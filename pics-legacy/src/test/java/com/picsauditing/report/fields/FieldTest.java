package com.picsauditing.report.fields;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

import java.util.Set;

import org.junit.Test;

import com.picsauditing.EntityFactory;
import com.picsauditing.access.OpPerms;
import com.picsauditing.access.Permissions;

public class FieldTest {

	private Field field = new Field("");

	private static final String value = "value";
	private static final String prefix = "prefix";
	private static final String suffix = "suffix";

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
