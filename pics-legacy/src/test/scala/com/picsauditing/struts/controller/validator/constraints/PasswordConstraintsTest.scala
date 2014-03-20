package com.picsauditing.struts.controller.validator.constraints

import org.scalatest.{Matchers, FlatSpec}
import org.scalatest.mock.MockitoSugar
import com.picsauditing.struts.controller.forms.RegistrationForm
import org.mockito.Mockito._
import com.picsauditing.struts.validator.constraints.{PasswordMatch, PasswordUsernameComparison}
import javax.validation.ConstraintValidatorContext

class PasswordConstraintsTest extends FlatSpec with Matchers with MockitoSugar {

  val mockContext = mock[ConstraintValidatorContext]
  val testString = "Some String"
  val testOtherString = "Some Other String"
  val passNameComparitor = new PasswordUsernameComparison
  val passPassComparitor = new PasswordMatch

  "PasswordUsernameComparison" should "return false validation when password and usernames match." in {
    val testObject = mock[RegistrationForm.PasswordPair]
    when(testObject.getUsername) thenReturn testString
    when(testObject.getFirstPassword) thenReturn testString

    passNameComparitor.isValid(testObject, mockContext) shouldBe false
  }

  it should "return a true validation when password and username don't match." in {
    val testObject = mock[RegistrationForm.PasswordPair]
    when(testObject.getUsername) thenReturn testString
    when(testObject.getFirstPassword) thenReturn testOtherString

    passNameComparitor.isValid(testObject, mockContext) shouldBe true
  }

  it should "return a true validation when the username is null. (This will be caught by other validators.)" in {
    val testObject = mock[RegistrationForm.PasswordPair]
    when(testObject.getUsername) thenReturn null.asInstanceOf[String]
    when(testObject.getFirstPassword) thenReturn testOtherString

    passNameComparitor.isValid(testObject, mockContext) shouldBe true
  }

  it should "return a true validation when the first password is null. (This will be caught by other validators.)" in {
    val testObject = mock[RegistrationForm.PasswordPair]
    when(testObject.getUsername) thenReturn testString
    when(testObject.getFirstPassword) thenReturn null.asInstanceOf[String]

    passNameComparitor.isValid(testObject, mockContext) shouldBe true
  }

  "PasswordMatch" should "return false validation when passwords do not match." in {
    val testObject = mock[RegistrationForm.PasswordPair]
    when(testObject.getFirstPassword) thenReturn testString
    when(testObject.getSecondPassword) thenReturn testOtherString

    passPassComparitor isValid(testObject, mockContext) shouldBe false
  }

  it should "return true validation when the passwords do match." in {
    val testObject = mock[RegistrationForm.PasswordPair]
    when(testObject.getFirstPassword) thenReturn testString
    when(testObject.getSecondPassword) thenReturn testString

    passPassComparitor isValid(testObject, mockContext) shouldBe true
  }

  it should "return true validation when the first password is null. (This will be caught by other validators.)" in {
    val testObject = mock[RegistrationForm.PasswordPair]
    when(testObject.getFirstPassword) thenReturn null.asInstanceOf[String]
    when(testObject.getSecondPassword) thenReturn testString

    passPassComparitor isValid(testObject, mockContext) shouldBe true
  }

  it should "return true validation when the second password is null. (This will be caught by other validators.)" in {
    val testObject = mock[RegistrationForm.PasswordPair]
    when(testObject.getFirstPassword) thenReturn testString
    when(testObject.getSecondPassword) thenReturn null.asInstanceOf[String]

    passPassComparitor isValid(testObject, mockContext) shouldBe true
  }

}
