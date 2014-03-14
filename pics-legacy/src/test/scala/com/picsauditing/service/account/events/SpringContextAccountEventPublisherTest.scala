package com.picsauditing.service.account.events

import org.mockito.Mockito._
import org.mockito.Matchers._
import org.scalatest.{Matchers, FlatSpec}
import org.scalatest.mock.MockitoSugar
import org.springframework.context.{ApplicationEvent, ApplicationEventPublisher}
import com.picsauditing.service.account.events.ContractorEventType._
import com.picsauditing.jpa.entities.ContractorAccount
import org.mockito.stubbing.Answer
import org.mockito.invocation.InvocationOnMock


class SpringContextAccountEventPublisherTest extends FlatSpec with Matchers with MockitoSugar {

  val mockPublisher = mock[ApplicationEventPublisher]
  val testClass = new SpringContextAccountEventPublisher
  testClass setApplicationEventPublisher mockPublisher

  "SpringContextAccountEventPublisher" should "create a SpringContractorEvent matching passed parameters." in {
    val contractor = mock[ContractorAccount]

    when(mockPublisher.publishEvent(any(classOf[ApplicationEvent]))).thenAnswer(new Answer[Void]() {
      override def answer(invocation: InvocationOnMock) = {
        val parameter = invocation.getArguments.apply(0).asInstanceOf[ApplicationEvent]
        parameter shouldBe a[SpringContractorEvent]
        val event = parameter.asInstanceOf[SpringContractorEvent]
        event.getContractor shouldEqual contractor
        event.getEvent shouldEqual Registration
        null
    }})

    testClass.publishEvent(contractor, Registration)

    verify(mockPublisher).publishEvent(any(classOf[SpringContractorEvent]))
  }

}
