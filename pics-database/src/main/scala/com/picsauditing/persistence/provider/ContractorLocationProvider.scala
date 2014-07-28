package com.picsauditing.persistence.provider

import com.picsauditing.persistence.model.{ContractorLocationAccess, ContractorLocation, Profile}

class ContractorLocationProvider extends ContractorLocationAccess { self: Profile =>

  import profile.simple._

  def insertNew(contractorLocation: ContractorLocation)(implicit sessions: Session) = {
    contractorLocations += contractorLocation
  }

}
