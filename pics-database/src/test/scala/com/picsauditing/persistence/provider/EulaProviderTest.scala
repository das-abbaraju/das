package com.picsauditing.persistence.provider

import util.BaseTestSetup
import com.picsauditing.persistence.model.{UserData, Eula, H2TestingProfile, DBTest}
import scala.slick.driver.H2Driver.simple.Session

class EulaProviderTest extends BaseTestSetup {


  "EulaProvider" should "return all fields" in new EulaTestSetup {

    queryTest{
      prepareMockDb{
        implicit session => provider =>
          val result = provider.findLoginEulaByCountry("CA")
          result.size shouldBe 1
          result.head.country shouldBe "CA"
      }
    }

  }


  it should "return all fields including the creator id" in new EulaTestSetup {

    queryTest{
      prepareMockDb{
        implicit session => provider =>
          val result = provider.findEulaCreatorsByCountry("CA")
          result.size shouldBe 1
          val (eula, creator) = result.head
          eula.country shouldBe "CA"
          creator.username shouldBe "foo"
      }
    }

  }

  trait EulaTestSetup extends DBTest[EulaProvider] {
    val dbName = "EulaProviderTestDb"
    val service = new EulaProvider with H2TestingProfile

    def prepareMockDb(f: Session => EulaProvider => Unit) = (session: Session, provider: EulaProvider) => {

      implicit val s = session
      val p = provider.asInstanceOf[EulaProvider with H2TestingProfile]

      import p._
      import p.profile.simple._

      createTable(appUserTableName, appUsers.ddl.create)
      createTable(userTableName, users.ddl.create)
      createTable(eulaTableName, eula.ddl.create)

      users += UserData(None, 1, "foo", "bar", "hello@example.com", "555-5555", "444-4444", true, Some(new java.sql.Date(100)), None)
      val insertedUser = users where { _.name === "bar" } first()
      eula += Eula(None, "loginEula", 1, "US", "Hello", insertedUser.id.get, 5, new java.sql.Date(0), new java.sql.Date(0))
      eula += Eula(None, "loginEula", 1, "CA", "Bye", insertedUser.id.get, 5, new java.sql.Date(0), new java.sql.Date(0))

      f(session)(provider)
    }
  }
}

