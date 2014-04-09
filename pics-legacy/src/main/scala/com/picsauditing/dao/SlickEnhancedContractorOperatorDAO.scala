package com.picsauditing.dao

import com.picsauditing.persistence.provider.FacilityRelationshipDataProvider
import com.picsauditing.persistence.model.MySQLProfile

class SlickEnhancedContractorOperatorDAO extends ContractorOperatorDAO with PICSDataAccess {

  lazy val frdProvider = new FacilityRelationshipDataProvider with MySQLProfile

  def findFacilitiesForCorporateOperatorID(id: Long) = db withSession { implicit session =>
    toJava (frdProvider findFacilityIDsForCorporateOperator id)
  }

}
