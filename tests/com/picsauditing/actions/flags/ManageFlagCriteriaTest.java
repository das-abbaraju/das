package com.picsauditing.actions.flags;

import static org.junit.Assert.assertTrue;

import com.picsauditing.jpa.entities.*;
import org.junit.AfterClass;
import org.junit.Before;
import org.junit.Test;
import org.mockito.MockitoAnnotations;

import com.picsauditing.EntityFactory;
import com.picsauditing.PicsTest;
import com.picsauditing.PicsTranslationTest;
import com.picsauditing.util.Strings;
import com.picsauditing.util.test.TranslatorFactorySetup;

public class ManageFlagCriteriaTest extends PicsTest {
	ManageFlagCriteria manageFlagCriteria;

	private static final String TEST_TRANSLATION = "Test translations";
	private static final String EMPTY_TRANSLATION = Strings.EMPTY_STRING;

	@AfterClass
	public static void classTearDown() {
		PicsTranslationTest.tearDownTranslationService();
		TranslatorFactorySetup.resetTranslatorFactoryAfterTest();
	}

	@Before
	public void setUp() throws Exception {
		TranslatorFactorySetup.setupTranslatorFactoryForTest();

		super.setUp();
		MockitoAnnotations.initMocks(this);

		manageFlagCriteria = new ManageFlagCriteria();
		autowireEMInjectedDAOs(manageFlagCriteria);
	}

	@Test
	public void testSave_InputValidation() throws Exception {
		FlagCriteria flagCriteria = new FlagCriteria();

		flagCriteria.setLabel(TEST_TRANSLATION);
		flagCriteria.setDescription(TEST_TRANSLATION);
		flagCriteria.setCategory(FlagCriteriaCategory.Audits);
		flagCriteria.setRequiredLanguages("[\"en\"]");
		flagCriteria.setDefaultValue("test");
		manageFlagCriteria.setCriteria(flagCriteria);

		// minimum valid criteria
		manageFlagCriteria.save();
		assertTrue(!manageFlagCriteria.hasActionErrors());

		// no category
		flagCriteria.setCategory(null);
		manageFlagCriteria.save();
		assertTrue(manageFlagCriteria.hasActionErrors());
		flagCriteria.setCategory(FlagCriteriaCategory.Audits);

		// bad display order
		flagCriteria.setDisplayOrder(-1);
		manageFlagCriteria.save();
		assertTrue(manageFlagCriteria.hasActionErrors());
		flagCriteria.setDisplayOrder(999);

		// bad label
		flagCriteria.setLabel(EMPTY_TRANSLATION);
		manageFlagCriteria.save();
		assertTrue(manageFlagCriteria.hasActionErrors());
		flagCriteria.setLabel(TEST_TRANSLATION);

		// bad description
		flagCriteria.setDescription(EMPTY_TRANSLATION);
		manageFlagCriteria.save();
		assertTrue(manageFlagCriteria.hasActionErrors());
		flagCriteria.setDescription(TEST_TRANSLATION);

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

		// no audit type supplied for required status
		flagCriteria.setRequiredStatusComparison("<");
		flagCriteria.setRequiredStatus(AuditStatus.Complete);
		manageFlagCriteria.save();
		assertTrue(manageFlagCriteria.hasActionErrors());
		flagCriteria.setRequiredStatus(null);
	}
}
