package com.picsauditing.persistence.provider

import com.picsauditing.persistence.model.{UserContactInfo, Profile, UserAccessAccess, UserAccess}


class UserDataProvider extends UserAccess with UserAccessAccess { this: Profile =>
  import profile.simple._


  def findAccountContactByRole(role: String, accountID: Long)(implicit session: Session): List[UserContactInfo] =
    contactRoleQuery(role, accountID).list
    // The error above is a tooling error. It compiles and runs.

  private val contactRoleQuery = Compiled { (role: Column[String], accountID: Column[Long]) => {
    for {
      user <- users if user.isActive === true && user.accountID === accountID
      ua <- userAccess if user.id === ua.userID && ua.accessType === role
    } yield user.contactinfo
  }}

}
