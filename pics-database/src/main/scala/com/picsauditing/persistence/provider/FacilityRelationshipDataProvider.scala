package com.picsauditing.persistence.provider

import com.picsauditing.persistence.model.{Profile, FacilitiesDAO, AccountDAO}

class FacilityRelationshipDataProvider extends AccountDAO with FacilitiesDAO { this: Profile =>

  import profile.simple._

  private val facilitiesQuery = Compiled{ operatorID: Column[Long] =>
    for {
      facility <- facilities if facility.corporateID === operatorID
      account <- accounts where { _.status inSetBind Seq("Active", "Demo") }
      if facility.operatorID === account.id && account.autoApproveRelationships === true
    } yield facility.operatorID
  }

  def findFacilityIDsForCorporateOperator(operatorID: Long)(implicit session: Session) = facilitiesQuery(operatorID).list.distinct

}
