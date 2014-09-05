package com.picsauditing.companyfinder.dao


import com.picsauditing.companyfinder.model.ContractorGeoLocation
import com.picsauditing.dao.{SlickDatabaseAccessor, SpringProvidedDataConnection}
import com.picsauditing.persistence.model.MySQLProfile

class ProdContractorLocationDao extends ContractorLocationDao2 with SpringProvidedDataConnection

class ContractorLocationDao2(contractorLocationProvider: ContractorLocationProvider = new ContractorLocationProvider with MySQLProfile) {
  self: SlickDatabaseAccessor=>

  def insertContractorLocation(contractorLocation: ContractorGeoLocation) = db.withSession { implicit  session =>
    contractorLocationProvider.insertNew(contractorLocation)
  }



}
