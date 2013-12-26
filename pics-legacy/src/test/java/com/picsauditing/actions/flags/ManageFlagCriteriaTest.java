package com.picsauditing.actions.flags;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;
import static org.mockito.Mockito.when;

import com.picsauditing.jpa.entities.*;
import com.picsauditing.toggle.FeatureToggle;
import org.junit.*;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import com.picsauditing.EntityFactory;
import com.picsauditing.PicsTest;
import com.picsauditing.PicsTranslationTest;
import com.picsauditing.util.Strings;
import com.picsauditing.util.test.TranslatorFactorySetup;
import org.powermock.reflect.Whitebox;

public class ManageFlagCriteriaTest extends PicsTest {
	ManageFlagCriteria manageFlagCriteria;

	private static final String TEST_TRANSLATION = "Test translations";
	private static final String EMPTY_TRANSLATION = Strings.EMPTY_STRING;

    private FlagCriteria flagCriteria;

    @Mock
    private FeatureToggle featureToggleChecker;

	@AfterClass
	public static void classTearDown() {
		TranslatorFactorySetup.resetTranslatorFactoryAfterTest();
	}

	@Before
	public void setUp() throws Exception {
		TranslatorFactorySetup.setupTranslatorFactoryForTest();

        flagCriteria = new FlagCriteria();

		super.setUp();
		MockitoAnnotations.initMocks(this);

		manageFlagCriteria = new ManageFlagCriteria();
		autowireEMInjectedDAOs(manageFlagCriteria);

        when(featureToggleChecker.isFeatureEnabled(FeatureToggle.TOGGLE_USE_TRANSLATION_SERVICE_ADAPTER)).thenReturn(false);
        when(featureToggleChecker.isFeatureEnabled(FeatureToggle.TOGGLE_USE_NEW_TRANSLATIONS_DATASOURCE)).thenReturn(false);
        Whitebox.setInternalState(manageFlagCriteria, "featureToggleChecker", featureToggleChecker);

        flagCriteria.setLabel(TEST_TRANSLATION);
        flagCriteria.setDescription(TEST_TRANSLATION);
        flagCriteria.setCategory(FlagCriteriaCategory.Audits);
        flagCriteria.setRequiredLanguages("[\"en\"]");
        flagCriteria.setDefaultValue("test");
        manageFlagCriteria.setCriteria(flagCriteria);

        flagCriteria.setAuditType(null);
        flagCriteria.setQuestion(null);
        flagCriteria.setRequiredStatus(null);
    }

	@Test
	public void testSave_InputValidation_MinimumValidCriteria() throws Exception {
		manageFlagCriteria.save();

		assertTrue(!manageFlagCriteria.hasActionErrors());
    }

    @Test
    public void testSave_InputValidation_NoCategory() throws Exception {
		flagCriteria.setCategory(null);

		manageFlagCriteria.save();

		assertTrue(manageFlagCriteria.hasActionErrors());
    }

    @Test
    public void testSave_InputValidation_BadDisplayOrder() throws Exception {
        flagCriteria.setCategory(FlagCriteriaCategory.Audits);
		flagCriteria.setDisplayOrder(-1);

		manageFlagCriteria.save();

		assertTrue(manageFlagCriteria.hasActionErrors());
    }

    @Test
    public void testSave_InputValidation_BadLabel() throws Exception {
        flagCriteria.setDisplayOrder(999);
		flagCriteria.setLabel(EMPTY_TRANSLATION);

		manageFlagCriteria.save();

		assertTrue(manageFlagCriteria.hasActionErrors());
    }

    @Test
    public void testSave_InputValidation_BadDescription() throws Exception {
        flagCriteria.setLabel(TEST_TRANSLATION);
		flagCriteria.setDescription(EMPTY_TRANSLATION);

		manageFlagCriteria.save();

		assertTrue(manageFlagCriteria.hasActionErrors());
    }

    @Test
    public void testSave_InputValidation_BadDataTypeBoolean() throws Exception {
        flagCriteria.setDescription(TEST_TRANSLATION);
		flagCriteria.setDataType("boolean");

		manageFlagCriteria.save();

		assertTrue(manageFlagCriteria.hasActionErrors());
    }

    @Test
    public void testSave_InputValidation_BadDataTypeNumber() throws Exception {
        flagCriteria.setDataType("string");
		flagCriteria.setDataType("number");

		manageFlagCriteria.save();

		assertTrue(manageFlagCriteria.hasActionErrors());
    }

    @Test
    public void testSave_InputValidation_AuditTypeAndQuestion() throws Exception {
        flagCriteria.setDataType("string");
		flagCriteria.setAuditType(EntityFactory.makeAuditType(1));
		flagCriteria.setQuestion(EntityFactory.makeAuditQuestion());

		manageFlagCriteria.save();

		assertTrue(manageFlagCriteria.hasActionErrors());
    }

    @Ignore("The code that enforces this is currently commented out")
    @Test
    public void testSave_InputValidation_NoAuditTypeSuppliedForRequiredStatus() throws Exception {
		flagCriteria.setRequiredStatusComparison("<");
		flagCriteria.setRequiredStatus(AuditStatus.Complete);

		manageFlagCriteria.save();

        assertTrue(manageFlagCriteria.hasActionErrors());
	}

    @Test
    public void testSave_InputValidation_MissingChildRequiredLanguages_TranslationServiceOn() throws Exception {
        when(featureToggleChecker.isFeatureEnabled(FeatureToggle.TOGGLE_USE_TRANSLATION_SERVICE_ADAPTER)).thenReturn(true);

        manageFlagCriteria.save();

        assertFalse(manageFlagCriteria.hasActionErrors());
    }

    @Test
    public void testSave_InputValidation_MissingChildRequiredLanguages_TranslationServiceOff() throws Exception {
        when(featureToggleChecker.isFeatureEnabled(FeatureToggle.TOGGLE_USE_TRANSLATION_SERVICE_ADAPTER)).thenReturn(false);
        flagCriteria.setRequiredLanguages(null);

        manageFlagCriteria.save();

        assertEmitsRequiredLanguageError(manageFlagCriteria);
    }

    @Test
    public void testSave_InputValidation_MissingChildRequiredLanguages_TranslationDataSourceOn() throws Exception {
        when(featureToggleChecker.isFeatureEnabled(FeatureToggle.TOGGLE_USE_NEW_TRANSLATIONS_DATASOURCE)).thenReturn(true);

        manageFlagCriteria.save();

        assertFalse(manageFlagCriteria.hasActionErrors());
    }

    @Test
    public void testSave_InputValidation_MissingChildRequiredLanguages_TranslationDataSourceOff() throws Exception {
        when(featureToggleChecker.isFeatureEnabled(FeatureToggle.TOGGLE_USE_NEW_TRANSLATIONS_DATASOURCE)).thenReturn(false);
        flagCriteria.setRequiredLanguages(null);

        manageFlagCriteria.save();

        assertEmitsRequiredLanguageError(manageFlagCriteria);
    }

}
