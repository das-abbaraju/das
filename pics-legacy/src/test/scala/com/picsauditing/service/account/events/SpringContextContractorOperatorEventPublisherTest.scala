package com.picsauditing.service.account.events

import org.scalatest.{Matchers, FlatSpec}
import org.scalatest.mock.MockitoSugar
import org.springframework.context.{ApplicationEvent, ApplicationEventPublisher}
import com.picsauditing.service.account.events.ContractorOperatorEventType._
import com.picsauditing.jpa.entities.ContractorOperator
import org.mockito.stubbing.Answer
import org.mockito.invocation.InvocationOnMock
import org.mockito.Matchers._
import org.mockito.Mockito._

class SpringContextContractorOperatorEventPublisherTest extends FlatSpec with Matchers with MockitoSugar {

  val mockPublisher = mock[ApplicationEventPublisher]
  val testClass = new SpringContextContractorOperatorEventPublisher
  testClass setApplicationEventPublisher mockPublisher

  "SpringContextContractorOperatorEventPublisherTest" should "create a SpringContractorOperatorEvent matching passed parameters" in {

    val contractorOperator = mock[ContractorOperator]
    val testID = 55

    when(mockPublisher.publishEvent(any(classOf[ApplicationEvent]))).thenAnswer( new Answer[Void]() {
      override def answer(invocation: InvocationOnMock) = {
        val parameter = invocation.getArguments.apply(0).asInstanceOf[ApplicationEvent]

        parameter shouldBe a[SpringContractorOperatorEvent]
        val co = parameter.asInstanceOf[SpringContractorOperatorEvent]

        co.getContractorOperator shouldEqual contractorOperator
        co.getEvent shouldEqual RegistrationRequest
        co.getGeneratingEventUserID shouldEqual testID

        null
      }
    })

    testClass.publishEvent(contractorOperator, RegistrationRequest, 55)

    verify(mockPublisher).publishEvent(any(classOf[SpringContractorOperatorEvent]))
  }
}
