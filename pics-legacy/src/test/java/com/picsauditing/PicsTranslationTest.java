package com.picsauditing;

import org.junit.AfterClass;
import org.junit.BeforeClass;
import org.mockito.Mockito;
import org.powermock.reflect.Whitebox;

import com.picsauditing.service.i18n.TranslationService;
import com.picsauditing.service.i18n.TranslationServiceFactory;

public abstract class PicsTranslationTest {

	protected static TranslationService translationService = Mockito.mock(TranslationService.class);
	protected static TranslationService nonLoggingTranslationService = Mockito.mock(TranslationService.class);

	@BeforeClass
	public static void setupTranslationServiceForTest() {
        TranslationServiceFactory.registerTranslationService(translationService);
        TranslationServiceFactory.registerNonLoggingTranslationService(nonLoggingTranslationService);
	}

	public void resetTranslationService() {
		Mockito.reset(translationService);
		Mockito.reset(nonLoggingTranslationService);
	}

	@AfterClass
	public static void tearDownTranslationService() {
        TranslationServiceFactory.registerTranslationService(null);
        TranslationServiceFactory.registerNonLoggingTranslationService(null);
	}

}
