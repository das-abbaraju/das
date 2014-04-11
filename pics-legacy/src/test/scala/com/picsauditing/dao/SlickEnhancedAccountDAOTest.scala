package com.picsauditing.dao

import org.scalatest.{Matchers, FlatSpec}
import org.scalatest.mock.MockitoSugar
import com.picsauditing.persistence.provider.{SecurityInformationProvider, UserDataProvider}
import com.picsauditing.access.OpPerms
import org.mockito.Mockito._
import org.mockito.Matchers._
import com.picsauditing.persistence.model.UserContactInfo
import scala.slick.driver.JdbcDriver.simple.Database
import java.sql.Connection
import scala.slick.driver.JdbcDriver
import org.mockito.stubbing.Answer
import org.mockito.invocation.InvocationOnMock

class SlickEnhancedAccountDAOTest extends FlatSpec with Matchers with MockitoSugar {

// TODO: Figure out how to complete this test.
//  "Slick Enhanced AccountDAO" should "query the user data provider for a response when finding contacts by role." in {
//    val mockUDP = mock[UserDataProvider]
//    val mockSIP = mock[SecurityInformationProvider]
//    val testPerm = mock[OpPerms]
//    val PERM_STRING = "some permission string"
//    val TEST_ACCOUNT_ID = 5
//    val contactInfo1 = UserContactInfo(20L, "some user", "foo", "foo@bar.com", "555-555-5555", "444-444-4444")
//    val contactInfo2 = UserContactInfo(20L, "some other user", "bar", "bar@foo.com", "666-666-6666", "333-333-3333")
//
//    when(testPerm.toString) thenReturn PERM_STRING
//    when(mockUDP.findAccountContactByRole(anyString, any[Long])(any[slick.jdbc.JdbcBackend.Session])) thenReturn List(contactInfo1, contactInfo2)
//
//    trait MockDBConnection extends SlickDatabaseAccessor {
//      val mockDB = mock[Database]
//      val mockSession = mock[slick.jdbc.JdbcBackend.Session]
//      val db = mockDB
//      when(mockDB.withSession(any(classOf[(JdbcDriver.Backend#Session) => _]))) thenAnswer(new Answer[_] {
//        override def answer(p1: InvocationOnMock) = {
//          val method = p1.getArguments()(0)
//          method.asInstanceOf[]
//        }
//      })
//    }
//
//    val slickDAO = new SlickEnhancedAccountDAO(mockSIP, mockUDP) with MockDBConnection
//
//    val result = slickDAO.findAccountContactsByRole(TEST_ACCOUNT_ID, testPerm)
//
//  }

}
