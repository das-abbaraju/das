package com.picsauditing.persistence.provider

import scala.slick.driver.JdbcDriver.simple.Database
import com.picsauditing.persistence.model.{Profile, AccountDAO, UserDAO}

class SecurityInformationProvider(db: Database) extends UserDAO with AccountDAO { self: Profile =>
  import profile.simple._

  def findLastAccountLogin(accountID: Long): java.util.Date = db withSession { implicit session =>
    val dates = for {
      user <- users if user.accountID === accountID
    } yield user.lastLogin

    dates.sortBy( _.desc ).first
  }
}
