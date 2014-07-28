package com.picsauditing.dao

import com.picsauditing.persistence.provider.EulaAgreementProvider
import com.picsauditing.persistence.model.MySQLProfile
import com.picsauditing.persistence.model.EulaAgreement
import com.picsauditing.jpa.entities.User
import java.sql.Date

class ProdEulaAgreementDao extends EulaAgreementDao with SpringProvidedDataConnection

class EulaAgreementDao(eulaAgreementProvider: EulaAgreementProvider = new EulaAgreementProvider with MySQLProfile) {
  self: SlickDatabaseAccessor=>

  def insertEulaAgreement(eulaAgreement: EulaAgreement) = db.withSession { implicit session =>
    eulaAgreementProvider.insertNew(eulaAgreement)

  }

  def findByUserAndEulaId(userId: Long, eulaId: Long): EulaAgreement = db.withSession { implicit session =>
    eulaAgreementProvider.findByUserAndEulaId(userId, eulaId).headOption match {
      case None => null
      case Some(result) => result
    }
  }

  def updateEulaAgreement(eulaAgreement: EulaAgreement, newDate: java.util.Date, newUser: User) = db.withSession { implicit  session =>
    eulaAgreementProvider.update(eulaAgreement, new Date(newDate.getTime), newUser.getId())
  }

}
