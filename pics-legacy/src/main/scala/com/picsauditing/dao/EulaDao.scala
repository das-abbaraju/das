package com.picsauditing.dao

import com.picsauditing.persistence.provider.EulaProvider
import com.picsauditing.persistence.model.{UserData, Eula, MySQLProfile}

class ProdEulaDao extends EulaDao with SpringProvidedDataConnection

class EulaDao(eulaProvider: EulaProvider = new EulaProvider with MySQLProfile){ self: SlickDatabaseAccessor =>

  def findByCountry(country: String) : Eula = {
    db.withSession( implicit session =>
      eulaProvider.findLoginEulaByCountry(country).headOption match {
        case None => null
        case Some(result) => result
      }
    )
  }

  def findEulaCreatorByCountry(country: String): EulaCreationPairing = db withSession { implicit session =>
    eulaProvider.findEulaCreatorsByCountry(country).headOption match {
      case Some(result) => EulaCreationPairing.tupled(result)
      case None => null
    }
  }

  case class EulaCreationPairing(val eula: Eula, val creator: UserData)


}
