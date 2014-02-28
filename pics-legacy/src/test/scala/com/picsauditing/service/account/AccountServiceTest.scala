package com.picsauditing.service.account

import org.scalatest.{Matchers, FlatSpec}
import org.scalatest.mock.MockitoSugar
import org.mockito.Mockito._
import com.picsauditing.service.account.events.ContractorEventPublisher
import com.picsauditing.service.account.events.ContractorEventType._
import com.picsauditing.jpa.entities.ContractorAccount

class AccountServiceTest extends FlatSpec with Matchers with MockitoSugar {

  val mockPublisher = mock[ContractorEventPublisher]

  val testClass = new AccountService(mockPublisher)

  "AccountService" should "pass along a contractor registration event to it's configured ContractorEventPublisher" in {
    val contractor = mock[ContractorAccount]
    testClass.publishEvent(contractor, Registration)
    verify(mockPublisher).publishEvent(contractor, Registration)
  }
}
