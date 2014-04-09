package com.picsauditing.service.account

import org.scalatest.{Matchers, FlatSpec}
import org.scalatest.mock.MockitoSugar
import org.mockito.Mockito._
import com.picsauditing.service.account.events.ContractorEventPublisher
import com.picsauditing.service.account.events.ContractorEventType._
import com.picsauditing.jpa.entities.ContractorAccount
import com.picsauditing.dao.ContractorAccountDAO

class AccountServiceTest extends FlatSpec with Matchers with MockitoSugar {

  val mockPublisher = mock[ContractorEventPublisher]
  val mockDao = mock[ContractorAccountDAO]

  val testClass = new AccountService(mockPublisher, mockDao)

  "AccountService" should "pass along a contractor registration event to it's configured ContractorEventPublisher" in {
    val contractor = mock[ContractorAccount]
    testClass.publishEvent(contractor, Registration)
    verify(mockPublisher).publishEvent(contractor, Registration)
  }

  "AccountService" should "refresh the contractor so that the Country object is hydrated" in {
    val contractor = mock[ContractorAccount]
    testClass.persist(contractor)
    verify(mockDao).refresh(contractor)
  }
}
