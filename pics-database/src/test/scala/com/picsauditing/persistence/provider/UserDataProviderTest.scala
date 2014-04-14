package com.picsauditing.persistence.provider

import util.BaseTestSetup
import scala.slick.driver.H2Driver.simple.Session
import com.picsauditing.persistence.model._
import com.picsauditing.persistence.model.UserContactInfo
import scala.Some
import com.picsauditing.persistence.model.UserData

class UserDataProviderTest extends BaseTestSetup {

  "UserDataProvider" should "return a list of User Contact Info objects based on the account ID and role provided." in new UserDataTest {
    queryTest { prepareDatabase { implicit session => service =>
        val expect = evaluateResultsFor(service.findAccountContactByRole(BAR_PERMISSION, samKinison.accountID))
        expect(samKinison, false)
        expect(joePeschi, true)
      }
    }
  }

  it should "return no users that are inactive." in new UserDataTest {
    queryTest { prepareDatabase { implicit session => service =>
        val expect = evaluateResultsFor(service.findAccountContactByRole(BAR_PERMISSION, samKinison.accountID))
        expect(samKinison, false)
        expect(georgeCarlin, false)
        expect(joePeschi, true)
      }
    }
  }

  it should "return users only relevant to the account specified." in new UserDataTest {
    queryTest { prepareDatabase { implicit session => service =>
        val expect = evaluateResultsFor(service.findAccountContactByRole(BAR_PERMISSION, georgeCarlin.accountID))
        expect(joePeschi, true)
        expect(robinWilliams, false)
      }
    }
  }

  it should "return users only with the specified permission type." in new UserDataTest {
    queryTest { prepareDatabase { implicit session => service =>
        val expect = evaluateResultsFor(service.findAccountContactByRole(FOO_PERMISSION, robinWilliams.accountID))
        expect(robinWilliams, false)
      }
    }
  }

  
  def evaluateResultsFor(f: => List[UserContactInfo]) = {
    val result = f
    (ud: UserData, expect: Boolean) => result exists( _.username == ud.username ) shouldBe expect
  }



  trait UserDataTest extends DBTest[UserDataProvider] {
    val dbName = "UserDataTest"
    val service = new UserDataProvider with H2TestingProfile

    val testDate = new java.util.Date()
    val richardPrior = UserData(Some(4L), 9L, Some("rprior"), Some("Richard Prior"), Some("richard@prior.com"), Some("999-999-9999"), Some("888-888-8888"), Some("No"), Some(testDate))
    val robinWilliams = UserData(Some(0L), 9L, Some("rwilliams"), Some("Robin Williams"), Some("robin@williams.com"), Some("777-777-7777"), Some("666-666-6666"), Some("Yes"), Some(testDate))
    val joePeschi = UserData(Some(1L), 5L, Some("jpeschi"), Some("Joe Peschi"), Some("joe@peschi.com"),Some( "555-555-5555"), Some("444-444-4444"), Some("Yes"), Some(testDate))
    val georgeCarlin = UserData(Some(2L), 5L, Some("gcarlin"), Some("George Carlin"), Some("mr_conductor@shiningtimestation.com"), Some("333-333-3333"), Some("222-222-2222"), Some("No"), Some(testDate))
    val samKinison = UserData(Some(3L), 5L, Some("skinison"), Some("Sam Kinison"), Some("aaaaaaah@marriedforthreeyears.com"), Some("111-111-1111"), Some("000-000-0000"), Some("No"), Some(testDate))
    val comedians = Seq(richardPrior, robinWilliams, joePeschi, georgeCarlin, samKinison)
    val FOO_PERMISSION = "Foo"
    val BAR_PERMISSION = "Bar"
    val permissionTypes = Seq(FOO_PERMISSION, BAR_PERMISSION)

    def prepareDatabase(testFunction: Session => (UserDataProvider) => Unit) = {
      (session: Session, provider: UserDataProvider) =>

        implicit val s = session
        val p = provider.asInstanceOf[UserDataProvider with H2TestingProfile]
        import p._
        import p.profile.simple._

        createTable(userTableName, users.ddl.create)
        createTable(userAccessTableName, userAccess.ddl.create)

        users ++= comedians

        for {
          comedian <- comedians
          permission <- permissionTypes
          if comedian.username != robinWilliams.username && permission != FOO_PERMISSION
        } { userAccess += UserAccessInfo(None, comedian.id.get, permission) }

        testFunction(session)(provider)
    }

  }

}
