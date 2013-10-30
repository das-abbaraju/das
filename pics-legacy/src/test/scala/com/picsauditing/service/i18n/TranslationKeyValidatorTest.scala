package com.picsauditing.service.i18n

import org.scalatest.{BeforeAndAfterAll, FlatSpec}
import org.scalatest.mock.MockitoSugar
import org.junit.runner.RunWith
import org.scalatest.junit.JUnitRunner

@RunWith(classOf[JUnitRunner])
class TranslationKeyValidatorTest extends FlatSpec with BeforeAndAfterAll with MockitoSugar {

  "TranslationKeyValidator" should "return false if the key is null" in new TestSetup {
    expectResult(false) {
      translationKeyValidator validateKey(null)
    }
  }

  it should "return false if the key is an empty string" in new TestSetup {
    expectResult(false) {
      translationKeyValidator validateKey("")
    }
  }

  it should "return false if the key contains spaces" in new TestSetup {
    expectResult(false) {
      translationKeyValidator validateKey("This Key Contains Spaces")
    }
  }

  it should "return true with a single word key" in new TestSetup {
    expectResult(true) {
      translationKeyValidator validateKey("KeyIsGood")
    }
  }

  it should "return true with a dot separated key" in new TestSetup {
    expectResult(true) {
      translationKeyValidator validateKey("This.Key.Is.Well.Formed")
    }
  }

  it should "return false when the key contains <" in new TestSetup {
    expectResult(false) {
      translationKeyValidator validateKey("Thiskeyhas.a<bad.character")
    }
  }

  it should "return false when the key is only <" in new TestSetup {
    expectResult(false) {
      translationKeyValidator validateKey("<")
    }
  }

  it should "return false when the key starts with =" in new TestSetup {
    expectResult(false) {
      translationKeyValidator validateKey("=My.Bad.Key")
    }
  }

  it should "return false when the key ends with =" in new TestSetup {
    expectResult(false) {
      translationKeyValidator validateKey("My.Bad.Key=")
    }
  }

  trait TestSetup {
    val translationKeyValidator = new TranslationKeyValidator
  }
}
