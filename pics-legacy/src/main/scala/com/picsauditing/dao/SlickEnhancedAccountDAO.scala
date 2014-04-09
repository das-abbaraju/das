package com.picsauditing.dao

import com.picsauditing.persistence.model.MySQLProfile
import java.util.Date
import com.picsauditing.access.OpPerms
import com.picsauditing.persistence.provider.{UserDataProvider, SecurityInformationProvider}

class SlickEnhancedAccountDAO extends AccountDAO with PICSDataAccess {
  lazy val sipProvider = new SecurityInformationProvider with MySQLProfile
  lazy val userDataProvider = new UserDataProvider with MySQLProfile

  override def findAccountContactsByRole(accountID: Int, opPerms: OpPerms) = db withSession {
    implicit session => toJava( userDataProvider findAccountContactByRole(opPerms.toString, accountID) )
  }

  override def findLastAccountLogin(accountID: Int): Date  = db withSession( implicit session => sipProvider findLastAccountLogin accountID.toLong )

}
