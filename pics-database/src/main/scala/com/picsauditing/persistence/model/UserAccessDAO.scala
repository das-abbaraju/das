package com.picsauditing.persistence.model


case class UserAccessInfo(id: Option[Long], userID: Long, accessType: String)

trait UserAccessDAO { this: Profile =>
   import profile.simple._
   val userAccessTableName = "useraccess"

   class UserAccessSchema(tag: Tag) extends Table[UserAccessInfo](tag, userAccessTableName) {
     def id = column[Long]("id", O.PrimaryKey)
     def userID = column[Long]("userID")
     def accessType = column[String]("accessType")

     def * = (id.?, userID, accessType) <> (UserAccessInfo.tupled, UserAccessInfo.unapply)
   }

   val userAccess = TableQuery[UserAccessSchema]
 }
