package com.picsauditing.validator;

import org.scalatest.{BeforeAndAfterAll, FlatSpec}
import org.scalatest.mock.MockitoSugar
import org.junit.runner.RunWith
import org.scalatest.junit.JUnitRunner
import com.sun.jersey.api.client.{WebResource, Client}
import org.mockito.Mockito._
import org.mockito.Matchers._
import org.scalatest.matchers.ShouldMatchers

@RunWith(classOf[JUnitRunner])
class VATWebValidatorTest extends FlatSpec with BeforeAndAfterAll with MockitoSugar with ShouldMatchers {

  "VATWebValidator" should "not suck" in new TestSetup {
    val result = validator.execute()
    result should be (java.lang.Boolean.FALSE)
    // verify(client).resource("http://isvat.appspot.com/GB/802311782/")
  }

  it should "return true when the thread times out"

  override protected def afterAll() {
    VATWebValidator.registerWebClient(Client.create())
  }

  trait TestSetup {
    val vatcode = "GB802311782"
    val unparsableVatcode = "XX311782"
    val client = mock[Client]
    val webResource = mock[WebResource]
    when(client.resource(anyString())).thenReturn(webResource)
    VATWebValidator.registerWebClient(client)
    val validator = new VATWebValidator(vatcode)
  }

}
