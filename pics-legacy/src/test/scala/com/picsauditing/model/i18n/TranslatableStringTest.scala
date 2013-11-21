package com.picsauditing.model.i18n

import org.scalatest.{BeforeAndAfterAll, FlatSpec}
import org.scalatest.mock.MockitoSugar
import com.picsauditing.service.i18n.TranslationServiceFactory
import java.util.Locale
import org.mockito.Mockito._
import org.mockito.Matchers._
import org.scalatest.junit.JUnitRunner
import org.junit.runner.RunWith
import com.picsauditing.i18n.service.TranslationService

@RunWith(classOf[JUnitRunner])
class TranslatableStringTest extends FlatSpec with BeforeAndAfterAll with MockitoSugar {
  val testKey = "Test.Key"

  "TranslatableString" should
    "call the logging translation service with the registered locale when no locale specified" in
    new TestSetup {
      ThreadLocalLocale.INSTANCE.set(Locale.ENGLISH)

      translatableString.toTranslatedString()

      verify(translationService).getText(testKey, Locale.ENGLISH)

      ThreadLocalLocale.INSTANCE.set(Locale.FRENCH)

      translatableString.toTranslatedString()

      verify(translationService).getText(testKey, Locale.FRENCH)
    }

  it should "call logging service with the specified locale" in new TestSetup {
    translatableString.toTranslatedString(Locale.GERMAN)

    verify(translationService).getText(testKey, Locale.GERMAN)
  }

  it should "never call the non-logging service" in new TestSetup {
    ThreadLocalLocale.INSTANCE.set(Locale.ENGLISH)
    translatableString.toTranslatedString()
    translatableString.toTranslatedString(Locale.FRENCH)

    verify(nonLoggingTranslationService, never).getText(anyString, any[Locale])

  }

  override protected def afterAll() {
    TranslationServiceFactory.registerTranslationService(null)
    TranslationServiceFactory.registerNonLoggingTranslationService(null)
  }

  trait TestSetup {
    val translationService = mock[TranslationService]
    val nonLoggingTranslationService = mock[TranslationService]
    TranslationServiceFactory.registerTranslationService(translationService)
    TranslationServiceFactory.registerNonLoggingTranslationService(nonLoggingTranslationService)

    val translatableString = new TranslatableString(testKey)
  }

}
