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

class SlickEnhancedAccountDAOTest extends FlatSpec with Matchers with MockitoSugar {

  "Slick Enhanced AccountDAO" should "query the user data provider for a response when finding contacts by role." in {
    val mockUDP = mock[UserDataProvider]
    val mockSIP = mock[SecurityInformationProvider]
    val mockDB = mock[Database]
    val testPerm = mock[OpPerms]
    val PERM_STRING = "some permission string"
    val TEST_ACCOUNT_ID = 5
    val contactInfo1 = UserContactInfo(20L, "some user", "foo", "foo@bar.com", "555-555-5555", "444-444-4444")
    val contactInfo2 = UserContactInfo(20L, "some other user", "bar", "bar@foo.com", "666-666-6666", "333-333-3333")

    when(testPerm.toString) thenReturn PERM_STRING
    when(mockUDP.findAccountContactByRole(anyString, any[Long])) thenReturn List(contactInfo1, contactInfo2)

    trait MockDBConnection extends SlickDatabaseAccessor {
      val db = new Database {
        override def createConnection(): Connection = mock[Connection]
        override def withSession[T](f: (JdbcDriver.Backend#Session) => T): T = f(mock[JdbcDriver.Backend#Session])
      }
    }

    val slickDAO = new SlickEnhancedAccountDAO(mockSIP, mockUDP) with MockDBConnection

    val result = slickDAO.findAccountContactsByRole(TEST_ACCOUNT_ID, testPerm)

  }

}
