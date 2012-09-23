package com.picsauditing.report.models;

import java.util.HashMap;
import java.util.Map;

import org.junit.Before;

import com.picsauditing.EntityFactory;
import com.picsauditing.access.Permissions;
import com.picsauditing.report.Definition;
import com.picsauditing.report.fields.Field;

abstract public class ModelTest {
	protected Permissions permissions;
	protected Definition definition;
	protected Map<String, Field> availableFields;

	@Before
	protected void setup() {
		permissions = EntityFactory.makePermission();
		definition = new Definition("");
		availableFields = new HashMap<String, Field>();
	}
}
