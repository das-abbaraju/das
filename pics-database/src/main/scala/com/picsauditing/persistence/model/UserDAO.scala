package com.picsauditing.persistence.model



case class UserData(id: Option[Long], accountID: Long, username: String, email: String, phone: String, fax: String, isActive: String)

case class UserContactInfo(username: String, email: String, phone: String, fax: String)

trait UserDAO { this: Profile =>
  import profile.simple._
  val userTableName = "users"

  class UserSchema(tag: Tag) extends Table[UserData](tag, userTableName) {
    def id = column[Long]("id", O.PrimaryKey, O.AutoInc)
    def accountID = column[Long]("accountID")
    def username = column[String]("username")
    def email = column[String]("email")
    def phone = column[String]("phone")
    def fax = column[String]("fax")
    def isActive = column[String]("isActive")


    def contactinfo = (username, email, phone, fax) <> (UserContactInfo.tupled, UserContactInfo.unapply)
    def * = (id.?, accountID, username, email, phone, fax, isActive) <> (UserData.tupled, UserData.unapply)
  }

  val users = TableQuery[UserSchema]
}
