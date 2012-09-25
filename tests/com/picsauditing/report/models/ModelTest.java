package com.picsauditing.report.models;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

import org.junit.Before;

import com.picsauditing.EntityFactory;
import com.picsauditing.access.Permissions;
import com.picsauditing.report.Definition;
import com.picsauditing.report.fields.Field;

abstract public class ModelTest {
	protected Permissions permissions;
	protected Definition definition;
	protected Map<String, Field> availableFields;
	protected Set<String> fieldsThatShouldBeIncluded;
	protected Set<String> fieldsThatShouldNotBeIncluded;

	@Before
	protected void setup() {
		permissions = EntityFactory.makePermission();
		definition = new Definition("");
		availableFields = new HashMap<String, Field>();
		fieldsThatShouldBeIncluded = new HashSet<String>();
		fieldsThatShouldNotBeIncluded = new HashSet<String>();
	}

	protected void checkFields() {
		for (Field field : availableFields.values()) {
			System.out.println(field.getName());
		}
		
		for (String fieldName : fieldsThatShouldBeIncluded) {
			assertTrue(fieldName + " was missing from availableFields",
					availableFields.containsKey(fieldName.toUpperCase()));
		}
		for (String fieldName : fieldsThatShouldNotBeIncluded) {
			assertFalse(fieldName + " was present in availableFields",
					availableFields.containsKey(fieldName.toUpperCase()));
		}
	}
}
