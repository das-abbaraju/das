package com.picsauditing.persistence.model

case class Eula(
  id: Option[Long],
  name: String,
  versionNumber: Long,
  country: String,
  eulaBody: String,
  createdBy: Long,
  updatedBy: Long,
  creationDate: java.sql.Date,
  updateDate: java.sql.Date
)

object Eula {
  def createFrom(id: Integer, name: String, versionNumber: Long, country: String, eulaBody: String, userId: Long) = {
    val today = new java.sql.Date(new java.util.Date().getTime)
    Eula(
        Some(id.longValue()),
        name,
        versionNumber,
        country,
        eulaBody,
        userId,
        userId,
        today,
        today
    )
  }
}

trait EulaAccess extends UserAccess{this: Profile =>
  import profile.simple._
  val eulaTableName = "eula"


  class eulaSchema (tag: Tag) extends Table[Eula](tag, eulaTableName) {
    def id = column[Long]("id", O.PrimaryKey, O.AutoInc)
    def name = column[String]("name")
    def versionNumber = column[Long]("versionNumber")
    def country = column[String]("isoCode")
    def eulaBody = column[String]("eulaBody")
    def createdBy = column[Long]("createdBy")
    def updatedBy = column[Long]("updatedBy")
    def creationDate = column[java.sql.Date]("creationDate")
    def updateDate = column[java.sql.Date]("updateDate")

    def creator = foreignKey("fk_eula_user", createdBy, users)(_.id)

    def * = (id.?, name, versionNumber, country, eulaBody, createdBy, updatedBy, creationDate, updateDate) <> ((Eula.apply _).tupled, Eula.unapply)

    implicit val convertFromSQLDate = MappedColumnType.base[java.util.Date, java.sql.Date](
      { utilDate => new java.sql.Date(utilDate.getTime)},
      { sqlDate => new java.util.Date(sqlDate.getTime)}
    )

  }


  val eula = TableQuery[eulaSchema]

}
