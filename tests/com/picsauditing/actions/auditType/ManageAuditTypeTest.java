package com.picsauditing.actions.auditType;

import com.opensymphony.xwork2.interceptor.annotations.After;
import com.picsauditing.PicsActionTest;
import com.picsauditing.jpa.entities.AuditType;
import com.picsauditing.service.i18n.TranslationService;
import com.picsauditing.service.i18n.TranslationServiceFactory;
import org.junit.Before;
import org.junit.Test;
import org.mockito.*;
import org.powermock.reflect.Whitebox;

import java.util.*;

import static org.junit.Assert.assertTrue;
import static org.mockito.Matchers.eq;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

public class ManageAuditTypeTest extends PicsActionTest {
    private ManageAuditType manageAuditType;

    @Mock
    private TranslationService translationService;
    @Mock
    private AuditType auditType;
    @Captor
    private ArgumentCaptor<List<String>> captor;

    @Before
    public void setUp() throws Exception {
        MockitoAnnotations.initMocks(this);
        manageAuditType = new ManageAuditType();
        super.setUp(manageAuditType);

        TranslationServiceFactory.registerTranslationService(translationService);

        manageAuditType.setAuditType(auditType);
    }

    @After
    public void tearDown() throws Exception {
        TranslationServiceFactory.registerTranslationService(null);
    }

    @Test
    public void test_saveRequiredTranslationsForAuditTypeName_verifyClearsCache() throws Exception {
        Whitebox.invokeMethod(manageAuditType, "saveRequiredTranslationsForAuditTypeName");
        verify(translationService).clear();
    }

    @Test
    public void test_saveRequiredTranslationsForAuditTypeName_SavesNameInUsersLocale() throws Exception {
        commonSetupSaveRequiredTranslationsForAuditTypeName();

        Whitebox.invokeMethod(manageAuditType, "saveRequiredTranslationsForAuditTypeName");

        verify(translationService).saveTranslation(eq("KEY"), eq("NAME"), captor.capture());

        assertTrue(captor.getValue().contains(Locale.ENGLISH.getLanguage()));
    }

    @Test
    public void test_saveRequiredTranslationsForAuditTypeName_SavesInAllRequiredLanguages() throws Exception {
        commonSetupSaveRequiredTranslationsForAuditTypeName();

        List<String> requiredLanguages = new ArrayList();
        requiredLanguages.add(Locale.ENGLISH.getLanguage());
        requiredLanguages.add(Locale.FRENCH.getLanguage());
        requiredLanguages.add(Locale.JAPANESE.getLanguage());
        when(auditType.getLanguages()).thenReturn(requiredLanguages);

        Whitebox.invokeMethod(manageAuditType, "saveRequiredTranslationsForAuditTypeName");

        verify(translationService).saveTranslation(eq("KEY"), eq("NAME"), captor.capture());

        // the required plus always the logged in user's locale since they're editing the name in their language
        assertTrue(captor.getValue().size() == 4);
    }

    private void commonSetupSaveRequiredTranslationsForAuditTypeName() {
        when(auditType.getI18nKey("name")).thenReturn("KEY");
        when(auditType.getName()).thenReturn("NAME");
        when(permissions.getLocale()).thenReturn(Locale.ENGLISH);
    }

}
