package com.picsauditing.persistence.provider

import util.BaseTestSetup
import com.picsauditing.persistence.model.{UserData, H2TestingProfile, DBTest}
import scala.slick.driver.H2Driver.simple.Session
import scala.slick.jdbc.meta.MTable
import java.util.Date

class SecurityInformationProviderTest extends BaseTestSetup {

  "SecurityInformationProvider" should "return the latest login date for a specified account." in new SecurityDataTest {
    queryTest { prepareDatabase { provider =>
          val foundDate = provider.findLastAccountLogin(TEST_ACCOUNT_ID)
          foundDate shouldEqual LATEST_DATE
          foundDate shouldNot be(IMPOSSIBLE_DATE)
      }
    }
  }

  trait SecurityDataTest extends DBTest[SecurityInformationProvider] {
    val dbName = "SecurityDataTest"
    val service = new SecurityInformationProvider(dataSource) with H2TestingProfile
    val TEST_ACCOUNT_ID = 9L
    val SOME_OTHER_ID = 10L
    val LATEST_DATE = new Date()
    val EARLIER_DATE = new Date(LATEST_DATE.getTime - 10000)
    val IMPOSSIBLE_DATE = new Date(LATEST_DATE.getTime + 10000)

    val findThis = UserData(None, TEST_ACCOUNT_ID, "Nobody Special", "email", "phone", "fax", "Yes", LATEST_DATE)
    val testUser2 = UserData(None, TEST_ACCOUNT_ID, "Still Nobody Special", "email", "phone", "fax", "Yes", EARLIER_DATE)
    val testUser3 = UserData(None, TEST_ACCOUNT_ID, "Still Nobody Special", "email", "phone", "fax", "Yes", EARLIER_DATE)
    val dontFindThis = UserData(None, SOME_OTHER_ID, "Still Nobody Special", "email", "phone", "fax", "Yes", IMPOSSIBLE_DATE)

    def prepareDatabase(testFunction: (SecurityInformationProvider) => Unit) = {
      (session: Session, provider: SecurityInformationProvider) =>
        implicit val s = session
        val p = provider.asInstanceOf[SecurityInformationProvider with H2TestingProfile]
        import p._
        import p.profile.simple._

        if (!MTable.getTables.mapResult(_.name.name).list.contains(userTableName)) users.ddl.create

        users ++= Seq(findThis, testUser2, testUser3, dontFindThis)

        testFunction(provider)
    }
  }

}

