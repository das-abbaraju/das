package com.picsauditing.dao

import com.picsauditing.persistence.provider.FacilityRelationshipDataProvider
import com.picsauditing.persistence.model.MySQLProfile
import com.picsauditing.jpa.entities.{ApprovalStatus, ContractorOperator}

class SlickEnhancedContractorOperatorDAO extends ContractorOperatorDAO with PICSDataAccess {

  lazy val frdProvider = new FacilityRelationshipDataProvider with MySQLProfile

  private def findCorporateChildWithWorkStatusForContractor(co: ContractorOperator, findWorkStatus: String)(notCorporate: ApprovalStatus => Boolean) = {
    if (co.getOperatorAccount.isCorporate)
      frdProvider.childrenWorkstatusEquals(
        workStatus = findWorkStatus,
        corporateID = co.getOperatorAccount.getId.toLong,
        contractorID = co.getContractorAccount.getId.toLong
      )
    else
      notCorporate(co.getWorkStatus)
  }

  def workStatusIsContractor = findCorporateChildWithWorkStatusForContractor(_, "C")( notCorporate = _.isContractor )
  def workStatusIsPending = findCorporateChildWithWorkStatusForContractor(_ , "P")( notCorporate = _.isPending )
  def workStatusIsRejected = findCorporateChildWithWorkStatusForContractor(_, "N")( notCorporate = _.isNo )
  def workStatusIsApproved = findCorporateChildWithWorkStatusForContractor(_, "Y")( notCorporate = _.isYes )


}
