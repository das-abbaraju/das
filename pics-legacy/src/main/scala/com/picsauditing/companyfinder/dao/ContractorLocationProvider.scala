package com.picsauditing.companyfinder.dao

import com.picsauditing.companyfinder.model.{ContractorGeoLocation, ContractorLocationAccess}
import com.picsauditing.persistence.model.Profile

class ContractorLocationProvider extends ContractorLocationAccess { self: Profile =>

  import profile.simple._

  def insertNew(contractorLocation: ContractorGeoLocation)(implicit sessions: Session) = {
    contractorLocations += contractorLocation
  }

}
