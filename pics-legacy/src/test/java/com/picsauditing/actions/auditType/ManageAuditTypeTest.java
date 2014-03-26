package com.picsauditing.actions.auditType;

import com.picsauditing.PicsTranslationTest;
import com.picsauditing.dao.AuditTypeDAO;
import com.picsauditing.dao.WorkFlowDAO;
import com.picsauditing.jpa.entities.AuditType;
import com.picsauditing.toggle.FeatureToggle;
import com.picsauditing.util.SlugService;
import org.junit.Before;
import org.junit.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.powermock.reflect.Whitebox;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;
import static org.mockito.Mockito.when;

public class ManageAuditTypeTest extends PicsTranslationTest {
    private static final String AUDIT_TYPE_NAME = "Audit Type Test";
    private static final int TEST_WORKFLOW_ID = 123;
    private static final int MORE_THAN_ONE = 123;
    private static final int VALID_DAY = 5;
    private static final int VALID_MONTH = 5;

	private ManageAuditType manageAuditType;

    @Mock
    private SlugService slugService;
    @Mock
    private FeatureToggle featureToggleChecker;
    @Mock
    private AuditType auditType;
    @Mock
    private WorkFlowDAO wfDAO;
    @Mock
    private AuditTypeDAO auditTypeDAO;

    @Before
	public void setUp() throws Exception {
		MockitoAnnotations.initMocks(this);

        manageAuditType = new ManageAuditType();

        when(auditType.getName()).thenReturn(AUDIT_TYPE_NAME);
        when(auditType.getMaximumActive()).thenReturn(MORE_THAN_ONE);
        when(auditType.getAnchorDay()).thenReturn(VALID_DAY);
        when(auditType.getAnchorMonth()).thenReturn(VALID_MONTH);
        when(auditType.getId()).thenReturn(MORE_THAN_ONE);

        when(auditTypeDAO.save(auditType)).thenReturn(auditType);

        Whitebox.setInternalState(manageAuditType, "slugService", slugService);
        Whitebox.setInternalState(manageAuditType, "auditType", auditType);
        Whitebox.setInternalState(manageAuditType, "workFlowID", TEST_WORKFLOW_ID);
        Whitebox.setInternalState(manageAuditType, "wfDAO", wfDAO);
        Whitebox.setInternalState(manageAuditType, "featureToggleChecker", featureToggleChecker);
        Whitebox.setInternalState(manageAuditType, "auditTypeDAO", auditTypeDAO);
	}

    @Test
    public void testValidateSlug() {
        when(slugService.slugHasDuplicate(AuditType.class,"manual-audit",0)).thenReturn(true);
        when(slugService.slugIsURICompliant("manual-audit")).thenReturn(true);

        Whitebox.setInternalState(manageAuditType, "slug", "manual-audit");

        String response = manageAuditType.validateSlug();
        assertEquals("json", response);
        assertEquals("{\"isURI\":true,\"isUnique\":false}", manageAuditType.getJson().toString());
    }

    @Test
    public void testGenerateSlug() throws Exception {
        when(slugService.generateSlug(AuditType.class, "Manual Audit", 0)).thenReturn("manual-audit");

        Whitebox.setInternalState(manageAuditType, "stringToSlugify", "Manual Audit");

        String response = manageAuditType.generateSlug();
        assertEquals("json",response);
        assertEquals("{\"slug\":\"manual-audit\"}",manageAuditType.getJson().toString());
    }

    @Test
    public void testSave_NoSlug() throws Exception {
        when(slugService.slugHasDuplicate(AuditType.class,"manual-audit",0)).thenReturn(true);
        when(auditType.getSlug()).thenReturn("manual-audit");

        manageAuditType.save();

        assertTrue(manageAuditType.hasActionErrors());
    }

    @Test
    public void testSave_InputValidation_MissingChildRequiredLanguages_TranslationServiceOn() throws Exception {
        when(featureToggleChecker.isFeatureEnabled(FeatureToggle.TOGGLE_USE_TRANSLATION_SERVICE_ADAPTER)).thenReturn(true);

        manageAuditType.save();

        assertFalse(manageAuditType.hasActionErrors());
    }

    @Test
    public void testSave_InputValidation_MissingChildRequiredLanguages_TranslationServiceOff() throws Exception {
        when(featureToggleChecker.isFeatureEnabled(FeatureToggle.TOGGLE_USE_TRANSLATION_SERVICE_ADAPTER)).thenReturn(false);
        when(auditType.hasMissingChildRequiredLanguages()).thenReturn(Boolean.TRUE);

        manageAuditType.save();

        assertEmitsRequiredLanguageError(manageAuditType);
    }

    @Test
    public void testSave_InputValidation_MissingChildRequiredLanguages_TranslationDataSourceOn() throws Exception {
        when(featureToggleChecker.isFeatureEnabled(FeatureToggle.TOGGLE_USE_NEW_TRANSLATIONS_DATASOURCE)).thenReturn(true);

        manageAuditType.save();

        assertFalse(manageAuditType.hasActionErrors());
    }

    @Test
    public void testSave_InputValidation_MissingChildRequiredLanguages_TranslationDataSourceOff() throws Exception {
        when(featureToggleChecker.isFeatureEnabled(FeatureToggle.TOGGLE_USE_NEW_TRANSLATIONS_DATASOURCE)).thenReturn(false);
        when(auditType.hasMissingChildRequiredLanguages()).thenReturn(Boolean.TRUE);

        manageAuditType.save();

        assertEmitsRequiredLanguageError(manageAuditType);
    }
}
