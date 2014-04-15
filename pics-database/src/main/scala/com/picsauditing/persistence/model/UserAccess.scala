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
                     lastLogin: Option[java.util.Date],
                     appUserID: Option[Long]
                   )

case class UserContactInfo(
                            @BeanProperty
                            id: Long,
                            @BeanProperty
                            username: String,
                            name: Option[String],
                            email: Option[String],
                            phone: Option[String],
                            fax: Option[String]
                            ) {

  private def nullify[T](input: Option[T]): T = {
    input match {
      case Some(value) => value
      case None => null.asInstanceOf[T]
    }
  }

  def getName = nullify(name)
  def getEmail = nullify(email)
  def getPhone = nullify(phone)
  def getFax = nullify(fax)

}

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

    def contactinfo = (id, username, name.?, email.?, phone.?, fax.?) <> (UserContactInfo.tupled, UserContactInfo.unapply)
    def * = (id.?, accountID, username, name, email, phone, fax, isActive, lastLogin.?, appUserID) <> (UserData.tupled, UserData.unapply)


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

  protected[persistence] val appUsers = TableQuery[AppUserSchema]
  protected[persistence] val users = TableQuery[UserSchema]
}
