package com.picsauditing.persistence.provider

import util.BaseTestSetup
import com.picsauditing.persistence.model._

import com.picsauditing.persistence.model.UserData
import com.picsauditing.persistence.model.EulaAgreement
import scala.Some
import scala.slick.driver.H2Driver.simple.Session

class EulaAgreementProviderTest extends BaseTestSetup {

  "EulaAgreementProvider" should "return all fields given a user id" in new EulaAgreementTestSetup {

    queryTest{
      prepareMockDb{
        implicit session => provider =>
          val result = provider.findEulaAgreementByUser(2)
          result.size shouldBe 1
      }
    }

  }


  trait EulaAgreementTestSetup extends DBTest[EulaAgreementProvider] {
    val dbName = "EulaAgreementProviderTestDb"
    val service = new EulaAgreementProvider with H2TestingProfile

    def prepareMockDb(f: Session => EulaAgreementProvider => Unit) = (session: Session, provider: EulaAgreementProvider) => {

      implicit val s = session
      val p = provider.asInstanceOf[EulaAgreementProvider with H2TestingProfile]

      import p._
      import p.profile.simple._

      createTable(appUserTableName, appUsers.ddl.create)
      createTable(userTableName, users.ddl.create)
      createTable(eulaTableName, eula.ddl.create)
      createTable(eulaAgreementTableName, eulaAgreements.ddl.create)


      users += UserData(None, 1, "foo", "bar", "hello@example.com", "555-5555", "444-4444", true, Some(new java.sql.Date(100)), None)
      val insertedUser = users where { _.name === "bar" } first()

      eula  += Eula(None, "loginEula", 1, "US", "Hello", insertedUser.id.get, 5, new java.sql.Date(0), new java.sql.Date(0))
      val insertedEula = eula where {_.country === "US"} first()

      eulaAgreements += EulaAgreement(None, insertedUser.id.get, insertedEula.id.get, insertedUser.id.get, 5, new java.sql.Date(0), new java.sql.Date(0))

      f(session)(provider)
    }

  }
}
