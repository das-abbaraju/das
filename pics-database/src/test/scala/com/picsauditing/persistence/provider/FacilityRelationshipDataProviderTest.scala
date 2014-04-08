package com.picsauditing.persistence.provider

import util.BaseTestSetup
import com.picsauditing.persistence.model.{AccountData, FacilitiesData, H2TestingProfile, DBTest}
import scala.slick.driver.H2Driver.simple.Session

class FacilityRelationshipDataProviderTest extends BaseTestSetup {

  "FacilityRelationshipDataProvider" should "find active facility IDs for an operator ID." in new TestSetup {
    queryTest{ withDatabase { implicit session =>
      _.findFacilityIDsForCorporateOperator(GOOD_CORP_ID).contains(GOOD_OPERATOR_ID_1) shouldBe true
    }}
  }

  it should "find demo facility IDs for an operator ID." in new TestSetup {
    queryTest { withDatabase { implicit session =>
      _.findFacilityIDsForCorporateOperator(GOOD_CORP_ID).contains(GOOD_OPERATOR_ID_2) shouldBe true
    }}
  }

  it should "not find facilites with any other account status." in new TestSetup {
    queryTest { withDatabase { implicit session =>
          _.findFacilityIDsForCorporateOperator(GOOD_CORP_ID).contains(GOOD_OPERATOR_ID_3) shouldBe false
      }
    }
  }

  it should "find facilities paired with the operator ID provided." in new TestSetup {
    queryTest { withDatabase { implicit session => provider =>
          val results = provider.findFacilityIDsForCorporateOperator(BAD_CORP_ID)
          results.contains(GOOD_OPERATOR_ID_1) shouldBe false
          results.contains(BAD_OPERATOR_ID_1) shouldBe true
      }
    }
  }

  it should "not find facilities that do not 'auto-approve' relationships." in new TestSetup {
    queryTest { withDatabase { implicit session =>
      _.findFacilityIDsForCorporateOperator(BAD_CORP_ID).contains(BAD_OPERATOR_ID_2) shouldBe false
    }}
  }

  it should "not find facilities that are not paired with the operator ID provided." in new TestSetup {
    queryTest { withDatabase { implicit session =>
      _.findFacilityIDsForCorporateOperator(GOOD_CORP_ID).contains(BAD_OPERATOR_ID_2) shouldBe false
    }}
  }

  trait TestSetup extends DBTest[FacilityRelationshipDataProvider] {
    val service = new FacilityRelationshipDataProvider with H2TestingProfile
    val dbName = "FacilityRelationshitDataProvider"

    val GOOD_CORP_ID = 5L
    val BAD_CORP_ID = 10L
    val ACTIVE = "Active"
    val DEMO = "Demo"
    val INACTIVE = "Inactive"
    val TYPE = "Type not relevant for these tests yet."
    val GOOD_OPERATOR_ID_1 = 100L
    val GOOD_OPERATOR_ID_2 = 200L
    val GOOD_OPERATOR_ID_3 = 300L
    val BAD_OPERATOR_ID_1 = 400L
    val BAD_OPERATOR_ID_2 = 500L

    val matchingActiveFacility = FacilitiesData(None, GOOD_CORP_ID, GOOD_OPERATOR_ID_1, TYPE)
    val matchingDemoFacility = FacilitiesData(None, GOOD_CORP_ID, GOOD_OPERATOR_ID_2, TYPE)
    val matchingInactiveFacility = FacilitiesData(None, GOOD_CORP_ID, GOOD_OPERATOR_ID_3, TYPE)
    val badFacility1 = FacilitiesData(None, BAD_CORP_ID, BAD_OPERATOR_ID_1, TYPE)
    val badFacility2 = FacilitiesData(None, BAD_CORP_ID, BAD_OPERATOR_ID_2, TYPE)
    val matchingActiveAccount = AccountData(Some(GOOD_OPERATOR_ID_1), "Matching Active Account", ACTIVE)
    val matchingDemoAccount = AccountData(Some(GOOD_OPERATOR_ID_2), "Matching Demo Account", DEMO)
    val matchingInactiveAccount = AccountData(Some(GOOD_OPERATOR_ID_3), "Matching Inactive Account", INACTIVE)
    val badAccount1 = AccountData(Some(BAD_OPERATOR_ID_1), "Un-matching active account", ACTIVE)
    val badAccount2 = AccountData(Some(BAD_OPERATOR_ID_2), "Un-matching demo account", DEMO, autoApproveRelationships = false)

    def withDatabase(testFunction: Session => FacilityRelationshipDataProvider => Unit) = {
      (session: Session, provider: FacilityRelationshipDataProvider) =>

        implicit val s = session
        val p = provider.asInstanceOf[FacilityRelationshipDataProvider with H2TestingProfile]
        import p._
        import p.profile.simple._

        createTable(accountTableName, accounts.ddl.create)
        createTable(facilitiesTableName, facilities.ddl.create)

        facilities ++= Seq(matchingActiveFacility, matchingDemoFacility, matchingInactiveFacility, badFacility1, badFacility2)
        accounts ++= Seq(matchingActiveAccount, matchingDemoAccount, matchingInactiveAccount, badAccount1, badAccount2)

        testFunction(session)(provider)
    }
  }

}
