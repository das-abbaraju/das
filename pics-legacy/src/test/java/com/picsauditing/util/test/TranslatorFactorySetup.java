package com.picsauditing.util.test;

import com.picsauditing.i18n.service.TranslationService;
import com.picsauditing.service.i18n.EchoTranslationService;
import com.picsauditing.service.i18n.TranslationServiceFactory;

public class TranslatorFactorySetup {

	public static void setupTranslatorFactoryForTest() {
		TranslationServiceFactory.registerTranslationService(new EchoTranslationService());
	}

	public static void resetTranslatorFactoryAfterTest() {
		TranslationServiceFactory.registerTranslationService((TranslationService) null);
	}

}
