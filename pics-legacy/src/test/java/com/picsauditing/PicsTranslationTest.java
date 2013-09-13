package com.picsauditing;

import org.junit.*;
import org.mockito.Mockito;
import org.powermock.reflect.Whitebox;

import com.picsauditing.service.i18n.TranslationService;
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
