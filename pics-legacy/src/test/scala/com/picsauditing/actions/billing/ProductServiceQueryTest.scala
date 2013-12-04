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
import com.picsauditing.service.i18n.TranslationServiceFactory
import java.math.BigDecimal
import com.picsauditing.jpa.entities.FeeClass._
import com.picsauditing.i18n.service.TranslationService

@RunWith(classOf[JUnitRunner])
class ProductServiceQueryTest extends FlatSpec with MockitoSugar with BeforeAndAfter {

  val EMPLOYEE_GUARD_SUCCESS = "{\"EmployeeGUARD\":true}"
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

  it should "return a positive JSON when the contractor has a non-zero contractor_fee entry for EmployeeGUARD." in new TestSetup {
    when(mockAccount.getFees) thenReturn passingMap
    classUnderTest.employeeGuardQuery should equal (PicsActionSupport.JSON)
    classUnderTest.getJson.toJSONString should equal (EMPLOYEE_GUARD_SUCCESS)
  }

  it should "return a negative JSON when the contractor does not have a contractor_fee entry for EmployeeGUARD." in new TestSetup {
    when(mockAccount.getFees) thenReturn mapWithoutEG
    classUnderTest.employeeGuardQuery should equal (PicsActionSupport.JSON)
    classUnderTest.getJson.toJSONString should equal (EMPLOYEE_GUARD_FAILURE)
  }

  it should "return a negative JSON when the contractor has a zero value for a contractor_fee entry for EmployeeGUARD" in new TestSetup {
    when(mockAccount.getFees) thenReturn failingMap
    classUnderTest.employeeGuardQuery should equal (PicsActionSupport.JSON)
    classUnderTest.getJson.toJSONString should equal (EMPLOYEE_GUARD_FAILURE)
  }



  trait TestSetup {
    val classUnderTest = new ProductServiceQuery
    val mockDao = mock[AccountDAO]
    val mockAccount = mock[ContractorAccount]

    org.mockito.internal.util.reflection.Whitebox.setInternalState(classUnderTest, "dao", mockDao)
    when(mockDao find(anyInt)) thenReturn mockAccount
    when(mockAccount isContractor) thenReturn true

    private[this] val zeroEG = new ContractorFee
    zeroEG.setNewAmount(BigDecimal.ZERO.setScale(2))
    private[this] val nonZeroEG = new ContractorFee
    nonZeroEG.setNewAmount(BigDecimal valueOf 599 setScale 2)
    private[this] val nonEGFee1 = new ContractorFee
    nonEGFee1.setNewAmount(BigDecimal.ZERO.setScale(2))
    private[this] val nonEGFee2 = new ContractorFee
    nonEGFee2.setNewAmount(BigDecimal valueOf 599 setScale 2)

    val passingMap = Map(AuditGUARD -> nonEGFee1, EmployeeGUARD -> nonZeroEG, DocuGUARD -> nonEGFee2)
    val mapWithoutEG = Map(AuditGUARD -> nonEGFee1, DocuGUARD -> nonEGFee2)
    val failingMap = Map(InsureGUARD -> nonEGFee1, EmployeeGUARD -> zeroEG, AuditGUARD -> nonEGFee2)
  }

  before {
    TranslationServiceFactory.registerTranslationService(mock[TranslationService])
  }

  after {
    TranslationServiceFactory.registerTranslationService(null.asInstanceOf[TranslationService])
  }

}
