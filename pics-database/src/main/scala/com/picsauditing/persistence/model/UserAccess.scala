package com.picsauditing.persistence.model

import scala.beans.BeanProperty


case class UserData(
                     id: Option[Long],
                     accountID: Long,
                     username: String,
                     name: String,
                     email: String,
                     phone: String,
                     fax: String,
                     isActive: String,
                     lastLogin: java.util.Date
                   )

case class UserContactInfo(
                            @BeanProperty
                            id: Long,
                            @BeanProperty
                            username: String,
                            @BeanProperty
                            name: String,
                            @BeanProperty
                            email: String,
                            @BeanProperty
                            phone: String,
                            @BeanProperty
                            fax: String)

trait UserAccess { this: Profile =>
  import profile.simple._
  val userTableName = "users"

  class UserSchema(tag: Tag) extends Table[UserData](tag, userTableName) {
    def id = column[Long]("id", O.PrimaryKey, O.AutoInc)
    def accountID = column[Long]("accountID")
    def username = column[String]("username")
    def name = column[String]("name")
    def email = column[String]("email")
    def phone = column[String]("phone")
    def fax = column[String]("fax")
    def isActive = column[String]("isActive")

    def lastLogin = column[java.util.Date]("lastLogin")(convertFromTimeStamp)


    def contactinfo = (id, username, name, email, phone, fax) <> (UserContactInfo.tupled, UserContactInfo.unapply)
    def * = (id.?, accountID, username, name, email, phone, fax, isActive, lastLogin) <> (UserData.tupled, UserData.unapply)
  }

  protected[persistence] val users = TableQuery[UserSchema]
}
