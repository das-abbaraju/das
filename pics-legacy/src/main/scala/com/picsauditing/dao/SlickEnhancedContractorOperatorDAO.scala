package com.picsauditing.dao

import com.picsauditing.persistence.provider.FacilityRelationshipDataProvider
import com.picsauditing.jpa.entities.{ApprovalStatus, ContractorOperator}
import com.picsauditing.persistence.model.MySQLProfile

class SpringConfiguredSlickEnhancedContractorOperatorDAO extends SlickEnhancedContractorOperatorDAO with SpringProvidedDataConnection

class SlickEnhancedContractorOperatorDAO (

  frdProvider: FacilityRelationshipDataProvider = new FacilityRelationshipDataProvider with MySQLProfile

) extends ContractorOperatorDAO { this: SlickDatabaseAccessor =>

  private def findCorporateChildWithWorkStatusForContractor(co: ContractorOperator, findWorkStatus: String)(orWorkStatus: ApprovalStatus => Boolean) = {
    if (co.getOperatorAccount.isCorporate) db withSession { implicit session =>
      frdProvider.childrenWorkstatusEquals(
        workStatus = findWorkStatus,
        corporateID = co.getOperatorAccount.getId.toLong,
        contractorID = co.getContractorAccount.getId.toLong
      )
    } else {
      orWorkStatus(co.getWorkStatus)
    }
  }

  def workStatusIsContractor(co: ContractorOperator) = findCorporateChildWithWorkStatusForContractor(co, "C")( orWorkStatus = _.isContractor )
  def workStatusIsPending(co: ContractorOperator) = findCorporateChildWithWorkStatusForContractor(co, "P")( orWorkStatus = _.isPending )
  def workStatusIsRejected(co: ContractorOperator) = findCorporateChildWithWorkStatusForContractor(co, "N")( orWorkStatus = _.isNo )
  def workStatusIsApproved(co: ContractorOperator) = findCorporateChildWithWorkStatusForContractor(co, "Y")( orWorkStatus = _.isYes )


}
