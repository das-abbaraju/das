package com.picsauditing.persistence.model

import java.sql.Date

case class EulaAgreement (
  id: Option[Long],
  userId: Long,
  eulaId: Long,
  createdBy: Long,
  updatedBy: Long,
  creationDate: Date,
  updateDate: Date
)


object EulaAgreement {
  val ACCEPTED = "accepted"
  val NOT_ACCEPTED = "not accepted"

  def createFrom(userId: Int, eula: Eula) = {
    val today = new java.sql.Date(new java.util.Date().getTime)
    EulaAgreement(
      None,
      userId,
      eula.id.get,
      userId,
      userId,
      today,
      today
    )
}

  def createFrom(userId: Int, eulaId: Int) = {
    val today = new java.sql.Date(new java.util.Date().getTime)
    EulaAgreement(
      None,
      userId,
      eulaId,
      userId,
      userId,
      today,
      today
    )
  }

  def createFrom(userId: Int, eulaId: Int, date: java.util.Date) = {
    val convertedDate = new java.sql.Date(date.getTime())
    EulaAgreement(
      None,
      userId,
      eulaId,
      userId,
      userId,
      convertedDate,
      convertedDate
    )
  }
}

trait EulaAgreementAccess extends EulaAccess with UserAccess{this: Profile =>
  import profile.simple._
  val eulaAgreementTableName = "eula_agreement"

  class eulaAgreementSchema (tag: Tag) extends Table[EulaAgreement](tag, eulaAgreementTableName) {
    def id = column[Long]("id", O.PrimaryKey, O.AutoInc)
    def userId = column[Long]("userId")
    def eulaId = column[Long]("eulaId")
    def createdBy = column[Long]("createdBy")
    def updatedBy = column[Long]("updatedBy")
    def creationDate = column[Date]("creationDate")
    def updateDate = column[Date]("updateDate")

    def userFk = foreignKey("fk_eula_agreement_user", userId, users)(_.id)
    def eulaFk = foreignKey("fk_eula_agreement_eula", eulaId, eula)(_.id)

    def * = (id.?, userId, eulaId, createdBy, updatedBy, creationDate, updateDate) <> ((EulaAgreement.apply _).tupled, EulaAgreement.unapply)
  }

  val eulaAgreements = TableQuery[eulaAgreementSchema]
}
