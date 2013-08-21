package com.picsauditing.actions.billing

import org.scalatest.{BeforeAndAfter, FlatSpec}
import org.scalatest.mock.MockitoSugar
import org.junit.runner.RunWith
import org.scalatest.junit.JUnitRunner
import com.picsauditing.dao.AccountDAO
import com.picsauditing.jpa.entities._
import org.mockito.Mockito._
import org.mockito.Matchers._
import com.opensymphony.xwork2.Action._
import org.scalatest.matchers.ShouldMatchers._
import scala.collection.JavaConversions._
import com.picsauditing.actions.PicsActionSupport
import com.picsauditing.service.i18n.{TranslationServiceFactory, TranslationService}

@RunWith(classOf[JUnitRunner])
class ProductServiceQueryTest extends FlatSpec with MockitoSugar with BeforeAndAfter {

/*  val EMPLOYEE_GUARD_SUCCESS = "{\"EmployeeGUARD\":true}"
    val EMPLOYEE_GUARD_FAILURE = "{\"EmployeeGUARD\":false}"

  "ProductServiceQueryTest" should "return an error when a non-contractor account ID is querried." in new TestSetup {
    when(mockAccount isContractor) thenReturn false
    evaluating { classUnderTest employeeGuardQuery } should produce [NotImplementedError]
  }

  it should "return a simple success message when the controller's basic 'execute' method is called." in new TestSetup {
    (classUnderTest.execute) should equal (SUCCESS)
  }

  it should "return an error on a non-existent ID" in new TestSetup {
    when(mockDao find(anyInt)) thenReturn null.asInstanceOf[Account]
    (classUnderTest employeeGuardQuery) should equal (ERROR)
  }

  it should "return a positive JSON when the contractor has an invoice containing EmployeeGuard less than 1 year old." in new TestSetup {
    when(mockAccount.getSortedInvoices) thenReturn List(failingInvoiceWithoutEG, passingInvoice)
    classUnderTest.employeeGuardQuery should equal (PicsActionSupport.JSON)
    classUnderTest.getJson.toJSONString should equal (EMPLOYEE_GUARD_SUCCESS)
  }

  it should "return a negative JSON when the contractor has an invoice containing EmployeeGuard more than 1 year old, but not one less than 1 year old." in new TestSetup {
    when(mockAccount getSortedInvoices) thenReturn List(failingInvoiceWithEG, failingInvoiceWithoutEG)
    classUnderTest.employeeGuardQuery should equal (PicsActionSupport.JSON)
    classUnderTest.getJson.toJSONString should equal (EMPLOYEE_GUARD_FAILURE)
  }

  it should "return a negative JSON when the contractor has no invoice containing EmployeeGuard." in new TestSetup {
    when(mockAccount getSortedInvoices) thenReturn List(failingInvoiceWithoutEG)
    classUnderTest.employeeGuardQuery should equal (PicsActionSupport.JSON)
    classUnderTest.getJson.toJSONString should equal (EMPLOYEE_GUARD_FAILURE)
  }



  trait TestSetup {
    val classUnderTest = new ProductServiceQuery
    val mockDao = mock[AccountDAO]
    val mockAccount = mock[ContractorAccount]
    val (passingInvoice, failingInvoiceWithEG, failingInvoiceWithoutEG) = (mock[Invoice], mock[Invoice], mock[Invoice])
    private[this] val EGLineItem = mock[InvoiceItem]
    private[this] val NonEGLineItem = mock[InvoiceItem]
    private[this] val EGInvoiceFee = mock[InvoiceFee]
    private[this] val NonEGInvoiceFee = mock[InvoiceFee]

    org.mockito.internal.util.reflection.Whitebox.setInternalState(classUnderTest, "dao", mockDao)
    when(mockDao find(anyInt)) thenReturn mockAccount

    when(EGInvoiceFee getFeeClass) thenReturn FeeClass.EmployeeGUARD
    when(NonEGInvoiceFee getFeeClass) thenReturn FeeClass.AuditGUARD

    when(EGLineItem getInvoiceFee) thenReturn EGInvoiceFee
    when(NonEGLineItem getInvoiceFee) thenReturn NonEGInvoiceFee

    when(passingInvoice.getItems) thenReturn List(EGLineItem, NonEGLineItem)
    when(passingInvoice getCreationDate) thenReturn {
      val cal = java.util.Calendar.getInstance
      cal.add(java.util.Calendar.MONTH, -3)
      cal.getTime
    }
    when(failingInvoiceWithEG getItems) thenReturn List(EGLineItem, NonEGLineItem)
    when(failingInvoiceWithEG getCreationDate) thenReturn {
      val cal = java.util.Calendar.getInstance
      cal.add(java.util.Calendar.YEAR, -2)
      cal.getTime
    }
    when(failingInvoiceWithoutEG getItems) thenReturn List(NonEGLineItem)
    when(failingInvoiceWithoutEG getCreationDate) thenReturn(new java.util.Date)

    when(mockAccount isContractor) thenReturn true;

  }

  before {
    TranslationServiceFactory.registerTranslationService(mock[TranslationService])
  }

  after {
    TranslationServiceFactory.registerTranslationService(null.asInstanceOf[TranslationService])
  }
*/
}
