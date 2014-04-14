package com.picsauditing.persistence.model

import scala.beans.BeanProperty


case class UserData(
                     id: Option[Long],
                     accountID: Long,
                     username: Option[String],
                     name: Option[String],
                     email: Option[String],
                     phone: Option[String],
                     fax: Option[String],
                     isActive: Option[String],
                     lastLogin: Option[java.util.Date]
                   )

case class UserContactInfo(
                            @BeanProperty
                            id: Long,
                            @BeanProperty
                            username: String,
                            @BeanProperty
                            name: Option[String],
                            @BeanProperty
                            email: Option[String],
                            @BeanProperty
                            phone: Option[String],
                            @BeanProperty
                            fax: Option[String])

trait UserAccess { this: Profile =>
  import profile.simple._

  val userTableName = "users"
  val appUserTableName = "app_user"

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

    def appUserID = column[Option[Long]]("appUserID")
    def appUser = foreignKey("app_user_fk", appUserID.get, appUsers)(_.id)
    def appUserName = for ( au <- appUser ) yield au.username

    def contactinfo = (id, appUserName, name.?, email.?, phone.?, fax.?) <> (UserContactInfo.tupled, UserContactInfo.unapply)
    def * = (id.?, accountID, username.?, name.?, email.?, phone.?, fax.?, isActive.?, lastLogin.?) <> (UserData.tupled, UserData.unapply)


    implicit val convertFromTimeStamp = MappedColumnType.base[java.util.Date, java.sql.Timestamp](
      { utilDate => new java.sql.Timestamp(utilDate.getTime) },
      { sqlDate => new java.util.Date(sqlDate.getTime) }
    )
  }

  class AppUserSchema(tag: Tag) extends Table[(Long, String)](tag, appUserTableName) {
    def id = column[Long]("id", O.PrimaryKey, O.AutoInc)
    def username = column[String]("username")
    def * = (id, username)
  }

  protected[persistence] val users = TableQuery[UserSchema]
  protected[persistence] val appUsers = TableQuery[AppUserSchema]
}
