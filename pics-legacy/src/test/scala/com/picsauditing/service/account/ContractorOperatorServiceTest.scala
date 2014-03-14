package com.picsauditing.service.account

import org.mockito.Mockito._
import org.scalatest.{Matchers, FlatSpec}
import org.scalatest.mock.MockitoSugar
import com.picsauditing.service.account.events.ContractorOperatorEventPublisher
import com.picsauditing.jpa.entities.ContractorOperator
import com.picsauditing.service.account.events.ContractorOperatorEventType._

class ContractorOperatorServiceTest extends FlatSpec with Matchers with MockitoSugar {

  val mockPublisher = mock[ContractorOperatorEventPublisher]
  val testClass = new ContractorOperatorService(mockPublisher)

  "ContractorOperatorService" should "pass along a contractor Registration Request event to its configured publisher." in {
    val co = mock[ContractorOperator]
    val eventGenerate = 55
    
    testClass.publishEvent(co, RegistrationRequest, 55)
    verify(mockPublisher).publishEvent(co, RegistrationRequest, eventGenerate)
  }
}
