package com.picsauditing.companyfinder.model

import com.picsauditing.persistence.model.Profile


case class ContractorGeoLocation (
                                id: Option[Long],
                                conId: Long,
                                latitude: Float,
                                longitude: Float,
                                createdBy: Long,
                                updatedBy: Long,
                                creationDate: java.sql.Date,
                                updateDate: java.sql.Date
                                )

object ContractorGeoLocation {
  def createFrom(conId: Integer, latitude: Float, longitude: Float, userId: Int) = {
    val today = new java.sql.Date(new java.util.Date().getTime)
    ContractorGeoLocation(
      None,
      conId.longValue(),
      latitude,
      longitude,
      userId,
      userId,
      today,
      today
    )
  }
}

trait ContractorLocationAccess {
  this: Profile =>

  import profile.simple._

  val contractorLocationName = "contractor_location"

  class ContractorLocationSchema (tag: Tag) extends Table[ContractorGeoLocation](tag, contractorLocationName) {
    def id = column[Long]("id", O.PrimaryKey, O.AutoInc)
    def conId = column[Long]("conId")
    def latitude = column[Float]("latitude")
    def longitude = column[Float]("longitude")
    def createdBy = column[Long]("createdBy")
    def updatedBy = column[Long]("updatedBy")
    def creationDate = column[java.sql.Date]("creationDate")
    def updateDate = column[java.sql.Date]("updateDate")

    def * = (id.?, conId, latitude, longitude, createdBy, updatedBy, creationDate, updateDate) <> ((ContractorGeoLocation.apply _).tupled, ContractorGeoLocation.unapply)
  }

  val contractorLocations = TableQuery[ContractorLocationSchema]
}
