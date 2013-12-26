package com.picsauditing.actions.auditType;

import com.picsauditing.PicsTranslationTest;
import com.picsauditing.dao.AuditOptionValueDAO;
import com.picsauditing.jpa.entities.AuditOptionGroup;
import com.picsauditing.toggle.FeatureToggle;
import org.junit.Before;
import org.junit.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.powermock.reflect.Whitebox;

import static org.junit.Assert.assertFalse;
import static org.mockito.Mockito.when;

public class ManageOptionGroupTest extends PicsTranslationTest {

    private static final String TEST_OPTION_GROUP_NAME = "Test Option";
    private ManageOptionGroup manageOptionGroup;
    
    @Mock
    private AuditOptionGroup group;
    @Mock
    private FeatureToggle featureToggleChecker;
    @Mock
    private AuditOptionValueDAO auditOptionValueDAO;

    @Before
    public void setup() throws Exception {
        MockitoAnnotations.initMocks(this);

        manageOptionGroup = new ManageOptionGroup();
        
        when(group.getName()).thenReturn(TEST_OPTION_GROUP_NAME);
        when(auditOptionValueDAO.save(group)).thenReturn(group);

        Whitebox.setInternalState(manageOptionGroup, "featureToggleChecker", featureToggleChecker);
        Whitebox.setInternalState(manageOptionGroup, "group", group);
        Whitebox.setInternalState(manageOptionGroup, "auditOptionValueDAO", auditOptionValueDAO);
    }

    @Test
    public void testSave_InputValidation_MissingChildRequiredLanguages_TranslationServiceOn() throws Exception {
        when(featureToggleChecker.isFeatureEnabled(FeatureToggle.TOGGLE_USE_TRANSLATION_SERVICE_ADAPTER)).thenReturn(true);

        manageOptionGroup.save();

        assertFalse(manageOptionGroup.hasActionErrors());
    }

    @Test
    public void testSave_InputValidation_MissingChildRequiredLanguages_TranslationServiceOff() throws Exception {
        when(featureToggleChecker.isFeatureEnabled(FeatureToggle.TOGGLE_USE_TRANSLATION_SERVICE_ADAPTER)).thenReturn(false);
        when(group.hasMissingChildRequiredLanguages()).thenReturn(Boolean.TRUE);

        manageOptionGroup.save();

        assertEmitsRequiredLanguageError(manageOptionGroup);
    }

    @Test
    public void testSave_InputValidation_MissingChildRequiredLanguages_TranslationDataSourceOn() throws Exception {
        when(featureToggleChecker.isFeatureEnabled(FeatureToggle.TOGGLE_USE_NEW_TRANSLATIONS_DATASOURCE)).thenReturn(true);

        manageOptionGroup.save();

        assertFalse(manageOptionGroup.hasActionErrors());
    }

    @Test
    public void testSave_InputValidation_MissingChildRequiredLanguages_TranslationDataSourceOff() throws Exception {
        when(featureToggleChecker.isFeatureEnabled(FeatureToggle.TOGGLE_USE_NEW_TRANSLATIONS_DATASOURCE)).thenReturn(false);
        when(group.hasMissingChildRequiredLanguages()).thenReturn(Boolean.TRUE);

        manageOptionGroup.save();

        assertEmitsRequiredLanguageError(manageOptionGroup);
    }
}
