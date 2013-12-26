package com.picsauditing;

import com.opensymphony.xwork2.ActionSupport;
import com.picsauditing.i18n.service.TranslationService;
import org.junit.*;
import org.mockito.Mockito;

import com.picsauditing.service.i18n.TranslationServiceFactory;

import static org.junit.Assert.assertTrue;

public abstract class PicsTranslationTest {

	protected TranslationService translationService;
	protected TranslationService nonLoggingTranslationService;

    static final String REQUIRED_LANGUAGE_ACTION_ERROR_STARTS_WITH = "Changes to required languages";

	@Before
	public void setupTranslationServiceForTest() {
        translationService = Mockito.mock(TranslationService.class);
        nonLoggingTranslationService = Mockito.mock(TranslationService.class);

        TranslationServiceFactory.registerTranslationService(translationService);
        TranslationServiceFactory.registerNonLoggingTranslationService(nonLoggingTranslationService);
	}

	@After
	public void tearDownTranslationService() {
        TranslationServiceFactory.registerTranslationService(null);
        TranslationServiceFactory.registerNonLoggingTranslationService(null);
	}

    public void assertEmitsRequiredLanguageError(ActionSupport action) {
        boolean foundError = false;
        for (String error : action.getActionErrors()) {
            if (error.startsWith(REQUIRED_LANGUAGE_ACTION_ERROR_STARTS_WITH)) {
                foundError = true;
                break;
            }
        }
        assertTrue(foundError);
    }


}
