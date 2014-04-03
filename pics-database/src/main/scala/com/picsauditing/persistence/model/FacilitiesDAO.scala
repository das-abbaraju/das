package com.picsauditing.persistence.model

case class FacilitiesData(id: Option[Long], corporateID: Long, operatorID: Long, facilityType: String)

trait FacilitiesDAO { this: Profile =>
  import profile.simple._
  val facilitiesTableName = "facilities"

  class FacilitiesSchema(tag: Tag) extends Table[FacilitiesData](tag, facilitiesTableName) {
    def id = column[Long]("id", O.PrimaryKey, O.AutoInc, O.NotNull)
    def corporateID = column[Long]("corporateID", O.NotNull)
    def operatorID = column[Long]("opID", O.NotNull)
    def `type` = column[String]("type")

    def * = (id.?, corporateID, operatorID, `type`) <> (FacilitiesData.tupled, FacilitiesData.unapply)
  }

  val facilities = TableQuery[FacilitiesSchema]
}
