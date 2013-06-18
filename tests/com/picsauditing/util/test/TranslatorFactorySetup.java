package com.picsauditing.util.test;

import com.picsauditing.model.i18n.TranslatorFactory;
import com.picsauditing.service.i18n.EchoTranslationService;
import com.picsauditing.service.i18n.TranslatorServiceImpl;

public class TranslatorFactorySetup {

	public static void setupTranslatorFactoryForTest() {
		TranslatorFactory.registerTranslatorService(new EchoTranslationService());
	}

	public static void resetTranslatorFactoryAfterTest() {
		TranslatorFactory.registerTranslatorService(new TranslatorServiceImpl());
	}

}
