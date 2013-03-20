package com.picsauditing.report.models;


import static org.junit.Assert.*;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

import org.junit.Before;

import com.picsauditing.EntityFactory;
import com.picsauditing.access.Permissions;
import com.picsauditing.jpa.entities.Report;
import com.picsauditing.report.fields.Field;

abstract public class ModelTest {

	protected Permissions permissions;
	protected Report report = new Report();
	protected Map<String, Field> availableFields;
	protected Set<String> includedFields;
	protected Set<String> excludedFields;

	@Before
	protected void setUp() {
		permissions = EntityFactory.makePermission();
		availableFields = new HashMap<String, Field>();
		includedFields = new HashSet<String>();
		excludedFields = new HashSet<String>();
	}

	protected void checkFields() {
		for (String fieldName : includedFields) {
			assertTrue(fieldName + " was missing from availableFields",
					availableFields.containsKey(fieldName.toUpperCase()));
		}
		for (String fieldName : excludedFields) {
			assertFalse(fieldName + " was present in availableFields",
					availableFields.containsKey(fieldName.toUpperCase()));
		}
	}
}
