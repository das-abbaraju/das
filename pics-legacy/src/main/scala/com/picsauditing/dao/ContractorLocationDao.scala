package com.picsauditing.dao

import com.picsauditing.persistence.provider.ContractorLocationProvider
import com.picsauditing.persistence.model.{MySQLProfile, ContractorLocation}

class ProdContractorLocationDao extends ContractorLocationDao with SpringProvidedDataConnection

class ContractorLocationDao(contractorLocationProvider: ContractorLocationProvider = new ContractorLocationProvider with MySQLProfile) {
  self: SlickDatabaseAccessor=>

  def insertContractorLocation(contractorLocation: ContractorLocation) = db.withSession { implicit  session =>
    contractorLocationProvider.insertNew(contractorLocation)
  }

}
