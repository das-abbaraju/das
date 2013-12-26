package com.picsauditing.mail;

import com.picsauditing.PicsActionTest;
import com.picsauditing.access.Permissions;
import com.picsauditing.dao.EmailTemplateDAO;
import com.picsauditing.jpa.entities.EmailTemplate;
import com.picsauditing.toggle.FeatureToggle;
import org.junit.Before;
import org.junit.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.powermock.reflect.Whitebox;

import java.util.ArrayList;
import java.util.List;

import static org.junit.Assert.assertFalse;
import static org.mockito.Mockito.when;

public class EmailTemplateSaveTest extends PicsActionTest {

    private static final String TEST_TEMPLATE_NAME = "Test Email Template";
    private static final String TEST_TEMPLATE_SUBJECT = "Test Email Template Subject";
    private static final String TEST_TEMPLATE_BODY = "Test Email Template Body";

    private EmailTemplateSave emailTemplateSave;
    private List<String> validLanguages = new ArrayList<>();

    @Mock
    private FeatureToggle featureToggleChecker;
    @Mock
    private EmailTemplateDAO emailTemplateDAO;
    @Mock
    private EmailTemplate template;
    @Mock
    private Permissions permissions;

    @Before
    public void setup() throws Exception {
        MockitoAnnotations.initMocks(this);

        emailTemplateSave = new EmailTemplateSave();
        super.setUp(emailTemplateSave);

        validLanguages.add("en");

        when(template.getTemplateName()).thenReturn(TEST_TEMPLATE_NAME);
        when(template.getSubject()).thenReturn(TEST_TEMPLATE_SUBJECT);
        when(template.getBody()).thenReturn(TEST_TEMPLATE_BODY);
        when(template.getLanguages()).thenReturn(validLanguages);
        when(template.getId()).thenReturn(123);

        when(emailTemplateDAO.save(template)).thenReturn(template);

        Whitebox.setInternalState(emailTemplateSave, "featureToggleChecker", featureToggleChecker);
        Whitebox.setInternalState(emailTemplateSave, "emailTemplateDAO", emailTemplateDAO);
        Whitebox.setInternalState(emailTemplateSave, "template", template);
        Whitebox.setInternalState(emailTemplateSave, "permissions", permissions);
    }

    @Test
    public void testSave_InputValidation_MissingChildRequiredLanguages_TranslationServiceOn() throws Exception {
        when(featureToggleChecker.isFeatureEnabled(FeatureToggle.TOGGLE_USE_TRANSLATION_SERVICE_ADAPTER)).thenReturn(true);

        emailTemplateSave.save();

        assertFalse(emailTemplateSave.hasActionErrors());
    }

    @Test
    public void testSave_InputValidation_MissingChildRequiredLanguages_TranslationServiceOff() throws Exception {
        when(featureToggleChecker.isFeatureEnabled(FeatureToggle.TOGGLE_USE_TRANSLATION_SERVICE_ADAPTER)).thenReturn(false);
        when(template.hasMissingChildRequiredLanguages()).thenReturn(Boolean.TRUE);

        emailTemplateSave.save();

        assertEmitsRequiredLanguageError(emailTemplateSave);
    }

    @Test
    public void testSave_InputValidation_MissingChildRequiredLanguages_TranslationDataSourceOn() throws Exception {
        when(featureToggleChecker.isFeatureEnabled(FeatureToggle.TOGGLE_USE_NEW_TRANSLATIONS_DATASOURCE)).thenReturn(true);

        emailTemplateSave.save();

        assertFalse(emailTemplateSave.hasActionErrors());
    }

    @Test
    public void testSave_InputValidation_MissingChildRequiredLanguages_TranslationDataSourceOff() throws Exception {
        when(featureToggleChecker.isFeatureEnabled(FeatureToggle.TOGGLE_USE_NEW_TRANSLATIONS_DATASOURCE)).thenReturn(false);
        when(template.hasMissingChildRequiredLanguages()).thenReturn(Boolean.TRUE);

        emailTemplateSave.save();

        assertEmitsRequiredLanguageError(emailTemplateSave);
    }

}
