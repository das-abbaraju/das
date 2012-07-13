package com.picsauditing.actions.flags;

import static org.junit.Assert.assertTrue;

import org.junit.Before;
import org.junit.Test;
import org.mockito.MockitoAnnotations;
import org.mockito.Mock;

import com.picsauditing.PicsTest;
import com.picsauditing.jpa.entities.FlagCriteria;
import com.picsauditing.jpa.entities.TranslatableString;
import static org.mockito.Mockito.when;

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
}

}
