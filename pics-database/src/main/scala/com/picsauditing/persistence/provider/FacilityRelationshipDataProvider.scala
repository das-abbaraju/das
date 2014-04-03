package com.picsauditing.persistence.provider

import scala.slick.driver.JdbcDriver.simple.Database
import com.picsauditing.persistence.model.{Profile, FacilitiesDAO, AccountDAO}

class FacilityRelationshipDataProvider(db: Database) extends AccountDAO with FacilitiesDAO { this: Profile =>

  import profile._
  import profile.simple._

  def findFacilityIDsForCorporateOperator(operatorID: Long) = db withSession { implicit session =>
    (for {
      facility <- facilities if facility.corporateID === operatorID
      account <- accounts where { _.status inSetBind Seq("Active", "Demo") }
      if facility.operatorID === account.id && account.autoApproveRelationships === true
    } yield facility.operatorID).list.distinct
  }

}
