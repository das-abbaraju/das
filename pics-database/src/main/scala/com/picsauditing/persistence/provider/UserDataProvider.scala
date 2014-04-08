package com.picsauditing.persistence.provider

import com.picsauditing.persistence.model.{Profile, UserAccessDAO, UserDAO}


class UserDataProvider extends UserDAO with UserAccessDAO { this: Profile =>
  import profile.simple._


  def findAccountContactByRole(role: String, accountID: Long)(implicit session: Session) = contactRoleQuery(role, accountID).list

  val contactRoleQuery = (role: Column[String], accountID: Column[Long]) => {
    for {
      user <- users if user.isActive === "Yes" && user.accountID === accountID
      ua <- userAccess  if user.id === ua.userID  && ua.accessType === role
    } yield user.contactinfo
  }


 }
