package com.picsauditing.persistence.provider

import com.picsauditing.persistence.model.{ContractorOperatorPairAccess, Profile, FacilitiesAccess, AccountAccess}

class FacilityRelationshipDataProvider
  extends AccountAccess
  with FacilitiesAccess
  with ContractorOperatorPairAccess
{ this: Profile =>

  import profile.simple._

  private val activeAutoApprovingFacilitiesFor = Compiled {
    operatorID: Column[Long] =>
      for {
        facility <- facilities if facility.corporateID === operatorID
        account <- accounts where { _.status inSetBind Seq("Active", "Demo") }
        if facility.operatorID === account.id && account.autoApproveRelationships === true
      } yield facility.operatorID
  }

  private val queryForChildrenWorkStatus = Compiled {
    (workStatus: Column[String], operatorID: Column[Long], contractorID: Column[Long]) =>
      for {
        facilityID <- activeAutoApprovingFacilitiesFor.extract(operatorID)
        co <- contractorOperators if co.operatorID === facilityID && co.contractorID === contractorID
        if co.workStatus === workStatus
      } yield co
  }

  private[persistence] def findAutoApprovingFacilityIDsForCorporateOperator(operatorID: Long)(implicit session: Session) =
    activeAutoApprovingFacilitiesFor(operatorID).list.distinct


  def childrenWorkstatusEquals(workStatus: String, corporateID: Long, contractorID: Long)(implicit session: Session): Boolean =
    queryForChildrenWorkStatus(workStatus, corporateID, contractorID).firstOption.isDefined
    // This is a tooling error, because the IDE has a problem with macros. It compiles and runs just fine.

}
