package com.picsauditing.persistence.provider

import scala.slick.driver.JdbcDriver.simple.Database
import com.picsauditing.persistence.model.{Profile, UserAccessDAO, UserDAO}


class UserDataProvider(db: Database) extends UserDAO with UserAccessDAO { this: Profile =>
   import profile.simple._

   def findAccountContactByRole(role: String, accountID: Long) = db withSession { implicit session =>
     (for {
         user <- users if user.isActive === "Yes" && user.accountID === accountID
         ua <- userAccess  if user.id === ua.userID  && ua.accessType === role
       } yield user.contactinfo ).list
   }

 }
