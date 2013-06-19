package com.picsauditing;

import org.junit.AfterClass;
import org.junit.BeforeClass;
import org.mockito.Mockito;
import org.powermock.reflect.Whitebox;

import com.picsauditing.service.i18n.TranslationService;
import com.picsauditing.service.i18n.TranslationServiceFactory;

public abstract class PicsTranslationTest {

	protected static TranslationService translationService = Mockito.mock(TranslationService.class);

	@BeforeClass
	public static void setupTranslationServiceForTest() {
		Whitebox.setInternalState(TranslationServiceFactory.class, "translationService", translationService);
	}

	public void resetTranslationService() {
		Mockito.reset(translationService);
	}

	@AfterClass
	public static void tearDownTranslationService() {
		Whitebox.setInternalState(TranslationServiceFactory.class, "translationService", (TranslationService) null);
	}

}
