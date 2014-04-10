package com.picsauditing.dao

import java.util.Date
import com.picsauditing.access.OpPerms
import com.picsauditing.persistence.provider.{UserDataProvider, SecurityInformationProvider}
import com.picsauditing.persistence.model.MySQLProfile

class SpringConfiguredSlickEnhancedAccountDAO extends SlickEnhancedAccountDAO with PICSDataAccess

class SlickEnhancedAccountDAO (

  sipProvider: SecurityInformationProvider = new SecurityInformationProvider with MySQLProfile,
  userDataProvider: UserDataProvider = new UserDataProvider with MySQLProfile

) extends AccountDAO { this: SlickDatabaseAccessor =>

  override def findAccountContactsByRole(accountID: Int, opPerms: OpPerms) = db withSession {
    implicit session => userDataProvider.findAccountContactByRole(opPerms.toString, accountID).toJava
  }

  override def findLastAccountLogin(accountID: Int): Date  = db withSession {
    implicit session => sipProvider findLastAccountLogin accountID.toLong
  }

}
