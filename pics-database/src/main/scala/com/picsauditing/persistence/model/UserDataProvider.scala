package com.picsauditing.persistence.model

import scala.slick.driver.JdbcDriver.simple.Database


class UserDataProvider(db: Database) extends UserDAO with UserAccessDAO { this: Profile =>
   import profile.simple._

   def findAccountContactByRole(role: String, accountID: Long) = db withSession { implicit session =>
     (for {
         user <- users if user.isActive === "Yes"
         ua <- userAccess  if user.id === ua.userID  && ua.accessType === role
       } yield user.contactinfo ).list
   }

 }
