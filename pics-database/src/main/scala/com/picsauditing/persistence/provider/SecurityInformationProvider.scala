package com.picsauditing.persistence.provider

import com.picsauditing.persistence.model.{Profile, AccountDAO, UserDAO}

class SecurityInformationProvider extends UserDAO with AccountDAO { self: Profile =>
  import profile.simple._

  def findLastAccountLogin(accountID: Long)(implicit session: Session): java.util.Date = accountLoginQuery(accountID).first

  private val accountLoginQuery = Compiled{ accountID: Column[Long] =>
    (for { user <- users if user.accountID === accountID } yield user.lastLogin).sortBy( _.desc )
  }

}
