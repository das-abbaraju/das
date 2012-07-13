package com.picsauditing.actions.flags;

import static org.junit.Assert.assertTrue;
import static org.mockito.Mockito.when;

import org.junit.Before;
import org.junit.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import com.picsauditing.EntityFactory;
import com.picsauditing.PicsTest;
import com.picsauditing.jpa.entities.FlagCriteria;
import com.picsauditing.jpa.entities.TranslatableString;

public class ManageFlagCriteriaTest extends PicsTest {
	ManageFlagCriteria manageFlagCriteria;

	@Mock
	TranslatableString emptyTranslation;

	@Mock
	TranslatableString somethingTranslation;

	@Before
	public void setUp() throws Exception {
		super.setUp();
		MockitoAnnotations.initMocks(this);

		manageFlagCriteria = new ManageFlagCriteria();
		autowireEMInjectedDAOs(manageFlagCriteria);

		when(somethingTranslation.toString()).thenReturn("Test translation");
	}

	@Test
	public void testSave_InputValidation() throws Exception {
		FlagCriteria flagCriteria = new FlagCriteria();

		flagCriteria.setLabel(somethingTranslation);
		flagCriteria.setDescription(somethingTranslation);
		flagCriteria.setCategory("Audits");
		flagCriteria.setRequiredLanguages("[\"en\"]");
		flagCriteria.setDefaultValue("test");
		manageFlagCriteria.setCriteria(flagCriteria);

		// minimum valid criteria
		manageFlagCriteria.save();
		assertTrue(!manageFlagCriteria.hasActionErrors());

		// no category
		flagCriteria.setCategory("");
		manageFlagCriteria.save();
		assertTrue(manageFlagCriteria.hasActionErrors());
		flagCriteria.setCategory("Audits");

		// bad display order
		flagCriteria.setDisplayOrder(-1);
		manageFlagCriteria.save();
		assertTrue(manageFlagCriteria.hasActionErrors());
		flagCriteria.setDisplayOrder(999);

		// bad label
		flagCriteria.setLabel(emptyTranslation);
		manageFlagCriteria.save();
		assertTrue(manageFlagCriteria.hasActionErrors());
		flagCriteria.setLabel(somethingTranslation);

		// bad description
		flagCriteria.setDescription(emptyTranslation);
		manageFlagCriteria.save();
		assertTrue(manageFlagCriteria.hasActionErrors());
		flagCriteria.setDescription(somethingTranslation);

		// bad data type
		flagCriteria.setDataType("");
		manageFlagCriteria.save();
		assertTrue(manageFlagCriteria.hasActionErrors());
		flagCriteria.setDataType("string");

		// bad data type
		flagCriteria.setDataType("boolean");
		manageFlagCriteria.save();
		assertTrue(manageFlagCriteria.hasActionErrors());
		flagCriteria.setDataType("string");

		// bad data type
		flagCriteria.setDataType("number");
		manageFlagCriteria.save();
		assertTrue(manageFlagCriteria.hasActionErrors());
		flagCriteria.setDataType("string");

		// audit and question
		flagCriteria.setAuditType(EntityFactory.makeAuditType(1));
		flagCriteria.setQuestion(EntityFactory.makeAuditQuestion());
		manageFlagCriteria.save();
		assertTrue(manageFlagCriteria.hasActionErrors());
		flagCriteria.setAuditType(null);
		flagCriteria.setQuestion(null);
	}

}
