package com.picsauditing.dao

import com.picsauditing.persistence.provider.FacilityRelationshipDataProvider
import com.picsauditing.jpa.entities.{ApprovalStatus, ContractorOperator}
import com.picsauditing.persistence.model.MySQLProfile

class SlickEnhancedContractorOperatorDAO (

  frdProvider: FacilityRelationshipDataProvider = new FacilityRelationshipDataProvider with MySQLProfile

) extends ContractorOperatorDAO with PICSDataAccess {

  private def findCorporateChildWithWorkStatusForContractor(co: ContractorOperator, findWorkStatus: String)(orWorkStatus: ApprovalStatus => Boolean) = {
    if (co.getOperatorAccount.isCorporate) db withSession {
      frdProvider.childrenWorkstatusEquals(
        workStatus = findWorkStatus,
        corporateID = co.getOperatorAccount.getId.toLong,
        contractorID = co.getContractorAccount.getId.toLong
      )
    } else {
      orWorkStatus(co.getWorkStatus)
    }
  }

  def workStatusIsContractor = findCorporateChildWithWorkStatusForContractor(_, "C")( orWorkStatus = _.isContractor )
  def workStatusIsPending = findCorporateChildWithWorkStatusForContractor(_ , "P")( orWorkStatus = _.isPending )
  def workStatusIsRejected = findCorporateChildWithWorkStatusForContractor(_, "N")( orWorkStatus = _.isNo )
  def workStatusIsApproved = findCorporateChildWithWorkStatusForContractor(_, "Y")( orWorkStatus = _.isYes )


}
