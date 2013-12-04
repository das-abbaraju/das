package com.picsauditing;

import com.picsauditing.i18n.service.TranslationService;
import org.junit.*;
import org.mockito.Mockito;

import com.picsauditing.service.i18n.TranslationServiceFactory;

public abstract class PicsTranslationTest {

	protected TranslationService translationService;
	protected TranslationService nonLoggingTranslationService;

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

}
