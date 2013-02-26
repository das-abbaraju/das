package com.picsauditing.report.models;

import static org.junit.Assert.*;
import java.util.Map;

import org.junit.Before;
import org.junit.Test;
import com.picsauditing.report.fields.Field;

public class ContractorFlagDataModelTest extends ModelTest {

	private ContractorFlagDataModel model;

	@Before
	public void setUp() {
		super.setUp();

		model = new ContractorFlagDataModel(permissions);
	}

	@Test
	public void testContractorFlagDataModel_FieldNamesShouldNotChange() {
		// If you change these values, you will break reports in the database because we
		// store the field name and use it to lookup an available field.
		// If you must change these values, change the entities in the database as well.
		assertEquals("FlagCriteriaLabel", ContractorFlagDataModel.FLAG_CRITERIA_LABEL);
		assertEquals("FlagCriteriaDescription", ContractorFlagDataModel.FLAG_CRITERIA_DESCRIPTION);
	}

	@Test
	public void testContractorFlagDataModel_FieldsIndexedByExpectedKeys() {
		Map<String, Field> fields = model.getAvailableFields();

		Field flagCriteriaField = fields.get("FlagCriteriaLabel".toUpperCase());
		assertNotNull(flagCriteriaField);
		assertEquals("FlagCriteriaLabel", flagCriteriaField.getName());

		Field flagCriteriaDescriptionField = fields.get("FlagCriteriaDescription".toUpperCase());
		assertNotNull(flagCriteriaDescriptionField);
		assertEquals("FlagCriteriaDescription", flagCriteriaDescriptionField.getName());
	}
}
