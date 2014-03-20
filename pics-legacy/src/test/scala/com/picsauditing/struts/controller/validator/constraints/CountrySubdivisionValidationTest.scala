package com.picsauditing.struts.controller.validator.constraints

import org.scalatest.{Matchers, FlatSpec}
import org.scalatest.mock.MockitoSugar
import com.picsauditing.struts.validator.constraints.{CountryZipCodeValidator, CountryISOValidation, CountrySubdivisionValidation}
import org.mockito.Mockito._
import org.mockito.Matchers._
import org.mockito.internal.util.reflection.Whitebox
import com.picsauditing.dao.CountryDAO
import com.picsauditing.struts.controller.forms.RegistrationForm
import javax.validation.ConstraintValidatorContext
import com.picsauditing.jpa.entities.{CountrySubdivision, Country}

class CountrySubdivisionValidationTest extends FlatSpec with Matchers with MockitoSugar {

  "CountrySubdivisionValidation" should "return a true validation if the country is null. (This will be handled by other validators.)" in new TestSetup {
    when(mockObject.getCountry) thenReturn null.asInstanceOf[String]
    subdivisionValidator isValid(mockObject, mockContext) shouldBe true
    verify(mockDao, never) findbyISO any[String]
  }

  it should "return a true validation if the country is empty. (This will be handled by other validators.)" in new TestSetup {
    when(mockObject.getCountry) thenReturn ""
    subdivisionValidator isValid(mockObject, mockContext) shouldBe true
    verify(mockDao, never) findbyISO any[String]
  }

  it should "return a true validation if the value object is null. (This will be handled by other validators.)" in new TestSetup {
    subdivisionValidator isValid(null, mockContext) shouldBe true
    verify(mockDao, never) findbyISO any[String]
  }

  it should "return a true validation if the countryISO doesn't reflect an actual ISO. (Handled elsewhere.)" in new TestSetup {
    when(mockDao.findbyISO(any[String])) thenReturn null.asInstanceOf[Country]
    when(mockObject.getCountry) thenReturn "XXX"
    subdivisionValidator isValid(mockObject, mockContext) shouldBe true
  }

  it should "return a true validation if the country doesn't have subdivisions." in new TestSetup {
    when(mockObject getCountry) thenReturn "XXX"
    when(mockCountry isHasCountrySubdivisions) thenReturn false
    subdivisionValidator isValid(mockObject, mockContext) shouldBe true
  }

  it should "return a false validation if the country has subdivisions and the subdivision supplied isn't one of them." in new TestSetup {
    when(mockCountry isHasCountrySubdivisions) thenReturn true
    when(mockObject getSubdivision) thenReturn "Bar"
    when(mockObject getCountry) thenReturn "Foo"

    subdivisionValidator isValid(mockObject, mockContext) shouldBe false
  }

  it should "return a false validation if the country has subdivisions and the subdivision supplied is empty." in new TestSetup {
    when(mockCountry isHasCountrySubdivisions) thenReturn true
    when(mockObject getSubdivision) thenReturn ""
    when(mockObject getCountry) thenReturn "Foo"
    subdivisionValidator isValid(mockObject, mockContext) shouldBe false
  }

  it should "return a false validation if the country has subdivisions and the subdivision supplied is null." in new TestSetup {
    when(mockCountry isHasCountrySubdivisions) thenReturn true
    when(mockObject getSubdivision) thenReturn null.asInstanceOf[String]
    when(mockObject getCountry) thenReturn "Foo"
    subdivisionValidator isValid(mockObject, mockContext) shouldBe false
  }

  it should "return a true validation if the country has subdivisions and the subdivision supplied is one of them." in new TestSetup {
    when(mockCountry isHasCountrySubdivisions) thenReturn true
    when(mockObject getSubdivision) thenReturn "BBB"
    when(mockObject getCountry) thenReturn "Foo"
    subdivisionValidator isValid(mockObject, mockContext) shouldBe true
  }

  "CountryISOValidation" should "return a false validation if the country ISO doesn't reflect an actual ISO." in new TestSetup {
    when(mockDao findbyISO any[String]) thenReturn null.asInstanceOf[Country]
    isoValidator isValid("XXX", mockContext) shouldBe false
  }

  it should "return a true validation if the country ISO does reflect an actual ISO" in new TestSetup {
    when(mockDao findbyISO anyString) thenReturn mockCountry
    isoValidator isValid("XXX", mockContext) shouldBe true
  }

  it should "return a true validation if the countryISO is empty. (Handled elsewhere.)" in new TestSetup {
    isoValidator isValid("", mockContext) shouldBe true
    verify(mockDao, never) findbyISO anyString
  }

  it should "return a true validation if the countryISO is null. (Handled elsewhere.)" in new TestSetup {
    isoValidator isValid(null, mockContext) shouldBe true
    verify(mockDao, never) findbyISO anyString
  }

  "CountryZipCodeValidation" should "return true if the countryISO is null. (Handled elsewhere.)" in new TestSetup {
    when(mockObject getCountry) thenReturn null.asInstanceOf[String]
    zipValidator isValid(mockObject, mockContext) shouldBe true
    verify(mockDao, never) findbyISO anyString
  }

  it should "return true if the countryISO is empty. (Handled elsewhere.)" in new TestSetup {
    when(mockObject getCountry) thenReturn ""
    zipValidator isValid(mockObject, mockContext) shouldBe true
    verify(mockDao, never) findbyISO anyString
  }

  it should "return true if the country for the given ISO doesn't exist. (Handled elsewhere.)" in new TestSetup {
    when(mockObject getCountry) thenReturn "XXX"
    when(mockDao findbyISO anyString) thenReturn null.asInstanceOf[Country]
    zipValidator isValid(mockObject, mockContext) shouldBe true
  }

  it should "return true if the country is found, but it doesn't require zip codes." in new TestSetup {
    when(mockObject getCountry) thenReturn "XXX"
    when(mockCountry isUAE) thenReturn true
    zipValidator isValid(mockObject, mockContext) shouldBe true
  }

  it should "return false if the country requires zip codes, and the zip code provided is empty." in new TestSetup {
    when(mockObject getCountry) thenReturn "XXX"
    when(mockObject getZip) thenReturn ""
    zipValidator isValid(mockObject, mockContext) shouldBe false
  }

  it should "return false if the country requires zip codes, and the zip code provided is null." in new TestSetup {
    when(mockObject getCountry) thenReturn "XXX"
    when(mockObject getZip) thenReturn null.asInstanceOf[String]
    zipValidator isValid(mockObject, mockContext) shouldBe false
  }

  it should "return true if the country is not UK, requires zip codes, and the zip code doesn't have verboten characters." in new TestSetup {
    when(mockObject getCountry) thenReturn "XXX"
    when(mockObject getZip) thenReturn GOOD_ZIP
    when(mockCountry isUK) thenReturn false
    zipValidator isValid(mockObject, mockContext) shouldBe true
  }

  it should "return true if the country is UK, and the zip code is correct." in new TestSetup {
    when(mockObject getCountry) thenReturn "XXX"
    when(mockObject getZip) thenReturn GOOD_UK_ZIP
    when(mockCountry isUK) thenReturn true
    zipValidator isValid(mockObject, mockContext) shouldBe true
  }

  it should "return false if the country is not UK, requires zip codes, and the zip code has verboten characters." in new TestSetup {
    when(mockObject getCountry) thenReturn "XXX"
    when(mockObject getZip) thenReturn BAD_ZIP
    when(mockCountry isUK) thenReturn false
    zipValidator isValid(mockObject, mockContext) shouldBe false
  }

  it should "return false if the country is UK, and the zip code is bad." in new TestSetup {
    when(mockObject getCountry) thenReturn "XXX"
    when(mockObject getZip) thenReturn BAD_UK_ZIP
    when(mockCountry isUK) thenReturn true
    zipValidator isValid(mockObject, mockContext) shouldBe false
  }


  val GOOD_ZIP = "92345"
  val BAD_ZIP = "<<!!<!'"
  val GOOD_UK_ZIP = "W11 2BQ"
  val BAD_UK_ZIP = "W<<! 2BQaaa"

  trait TestSetup {
    val subdivisionValidator = new CountrySubdivisionValidation
    val isoValidator = new CountryISOValidation
    val zipValidator = new CountryZipCodeValidator
    val mockDao = mock[CountryDAO]
    val mockObject = mock[RegistrationForm.CountrySubdivisionPair]
    val mockContext = mock[ConstraintValidatorContext]
    val mockCountry = mock[Country]
    Whitebox.setInternalState(subdivisionValidator, "dao", mockDao)
    Whitebox.setInternalState(isoValidator, "dao", mockDao)
    Whitebox.setInternalState(zipValidator, "dao", mockDao)
    when(mockDao.findbyISO(any[String])) thenReturn mockCountry

    private val subdivisionList = new java.util.ArrayList[CountrySubdivision]
    subdivisionList add new CountrySubdivision("AAA")
    subdivisionList add new CountrySubdivision("BBB")
    subdivisionList add new CountrySubdivision("CCC")
    when(mockCountry getCountrySubdivisions) thenReturn subdivisionList
    when(mockCountry isUAE) thenReturn false
  }



}
