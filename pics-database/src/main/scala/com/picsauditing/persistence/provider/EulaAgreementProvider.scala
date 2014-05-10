package com.picsauditing.persistence.provider

import com.picsauditing.persistence.model._

class EulaAgreementProvider extends EulaAgreementAccess { self: Profile =>

  import profile.simple._

  private val eulaAgreementForUser = Compiled{ userId: Column[Long] =>
    for {
      e <- eulaAgreements if e.userId === userId
    } yield e
  }

  private val eulaAgreementByUserAndEula = Compiled {
    (userId: Column[Long], eulaId: Column[Long]) =>
    for {
      e <- eulaAgreements if e.userId === userId && e.eulaId === eulaId
    } yield e

  }

  def findEulaAgreementByUser(userId: Long)(implicit session: Session): List[EulaAgreement] = {
    eulaAgreementForUser(userId).list
  }

  def findByUserAndEulaId(userId: Long, eulaId: Long)(implicit session: Session): List[EulaAgreement] = {
    eulaAgreementByUserAndEula(userId, eulaId).list
  }

  def insertNew(eula: EulaAgreement)(implicit session: Session) = {
    eulaAgreements += eula
  }

  def update(eula: EulaAgreement, newDate: java.sql.Date, newUser: Long)(implicit session: Session) = {
    eulaAgreements where { _.id === eula.id } map { entry => (entry.updateDate, entry.updatedBy) } update (newDate, newUser)
  }

}
