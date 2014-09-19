package com.picsauditing.validator;

import org.scalatest.{BeforeAndAfterAll, FlatSpec}
import org.scalatest.mock.MockitoSugar
import org.junit.runner.RunWith
import org.scalatest.junit.JUnitRunner
import com.sun.jersey.api.client.{ClientHandlerException, WebResource, Client}
import org.mockito.Mockito._
import org.mockito.Matchers._
import org.scalatest.matchers.ShouldMatchers
import org.mockito.stubbing.Answer
import org.mockito.invocation.InvocationOnMock

@RunWith(classOf[JUnitRunner])
class VATWebValidatorTest extends FlatSpec with BeforeAndAfterAll with MockitoSugar with ShouldMatchers {
  "VATWebValidator" should "return true when enter valid VAT id" in new TestValidate {
    val result = validator1.execute()
    result should be(java.lang.Boolean.TRUE)
  }
  it should "return false when the enter invalid VAT id (1)" in new TestValidate {
    val result = validator2.execute()
    result should be(java.lang.Boolean.FALSE)
  }
  it should "return false when the enter invalid VAT id (2)" in new TestValidate {
    val result = validator3.execute()
    result should be(java.lang.Boolean.FALSE)
  }
  it should "return false when the enter invalid VAT id (3)" in new TestValidate {
    val result = validator4.execute()
    result should be(java.lang.Boolean.FALSE)
  }
  trait TestValidate {
    val validVAT = "ESA79187423"
    val invalidVAT1 = "ESA791X7423"
    val invalidVAT2 = "A79187423"
    val invalidVAT3 = "79187423"
    val validator1 = new VATWebValidator(validVAT)
    val validator2 = new VATWebValidator(invalidVAT1)
    val validator3 = new VATWebValidator(invalidVAT2)
    val validator4 = new VATWebValidator(invalidVAT3)
  }
//
//  "VATWebValidator" should "return true when validation succeeds" in new TestSetup {
//    when(webResource.get(classOf[String])).thenReturn("true")
//
//    val result = validator.execute()
//
//    result should be (java.lang.Boolean.TRUE)
//    validator.isResponseFromFallback should be (java.lang.Boolean.FALSE)
//  }
//
//  it should "return false when the validation fails" in new TestSetup {
//    when(webResource.get(classOf[String])).thenReturn("false")
//
//    val result = validator.execute()
//
//    result should be (java.lang.Boolean.FALSE)
//    validator.isResponseFromFallback should be (java.lang.Boolean.FALSE)
//  }
//
//  it should "return true from fallback when the thread times out" in new TestSetup {
//    doAnswer(new SleepingAnswer).when(webResource).get(classOf[String])
//
//    val result = validator.execute()
//
//    validator.isResponseFromFallback should be (java.lang.Boolean.TRUE)
//    validator.isResponseTimedOut should be (java.lang.Boolean.TRUE)
//    result should be (java.lang.Boolean.TRUE)
//  }
//
//  it should "return true from fallback when the resource throws" in new TestSetup {
//    doThrow(new ClientHandlerException()).when(webResource).get(classOf[String])
//
//    val result = validator.execute()
//
//    validator.isResponseFromFallback should be (java.lang.Boolean.TRUE)
//    result should be (java.lang.Boolean.TRUE)
//  }
//
//  it should "return false for an unparsable vat code without calling the web resource" in new TestSetup {
//    val validator2 = new VATWebValidator(unparsableVatcode)
//
//    val result = validator2.execute()
//
//    result should be (java.lang.Boolean.FALSE)
//    validator.isResponseFromFallback should be (java.lang.Boolean.FALSE)
//    verify(webResource, never).get(classOf[String])
//  }
//
//  override protected def afterAll() {
//    VATWebValidator.registerWebClient(Client.create())
//  }
//
//  trait TestSetup {
//    val vatcode = "GB802311782"
//    val unparsableVatcode = "X"
//    val client = mock[Client]
//    val webResource = mock[WebResource]
//    when(webResource.path(anyString)).thenReturn(webResource)
//    when(client.resource(anyString())).thenReturn(webResource)
//    VATWebValidator.registerWebClient(client)
//    val validator = new VATWebValidator(vatcode)
//  }
//
//}
//
//class SleepingAnswer extends Answer[Any] {
//  def answer(invocation: InvocationOnMock ): Any = {
//    Thread.sleep(VATWebValidator.THREAD_TIMEOUT_MS + 100)
//    null
//  }
}