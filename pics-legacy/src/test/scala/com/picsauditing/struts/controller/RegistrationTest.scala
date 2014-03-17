package com.picsauditing.struts.controller

import org.scalatest.{Matchers, FlatSpec}
import org.scalatest.mock.MockitoSugar
import com.picsauditing.dao.UserLoginLogDAO
import com.picsauditing.access.PermissionBuilder
import com.picsauditing.service.registration.{RegistrationService, RegistrationRequestService}
import javax.validation.ValidatorFactory
import com.opensymphony.xwork2.ActionContext
import javax.servlet.http.HttpServletRequest

class RegistrationTest extends FlatSpec with Matchers with MockitoSugar {

  "Registration" should "" in new TestSetup {
    // TODO :(
  }


  trait TestSetup {
    val mockLoginDAO = mock[UserLoginLogDAO]
    val mockPermissionBuilder = mock[PermissionBuilder]
    val mockRegistrationRequestService = mock[RegistrationRequestService]
    val mockRegistrationService = mock[RegistrationService]
    val mockValidatorFactory = mock[ValidatorFactory]
    val mockActionContext = mock[ActionContext]
    val mockRequest = mock[HttpServletRequest]

    val registration = new Registration() {
      override def loggedIn = false
      override def getActionContext = mockActionContext
      override def getRequest = mockRequest
    }
  }
}
