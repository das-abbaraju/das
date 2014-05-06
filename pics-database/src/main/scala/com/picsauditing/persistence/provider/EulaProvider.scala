package com.picsauditing.persistence.provider

import com.picsauditing.persistence.model.{UserData, Eula, Profile, EulaAccess}

class EulaProvider extends EulaAccess { self: Profile =>

  import profile.simple._

  private val allQuery = Compiled{ country: Column[String] =>
    for{ e <- eula if e.country === country } yield e
  }

  private val eulaWithCreator = Compiled { country: Column[String] =>
      for {
        e <- eula if e.country === country
        c <- e.creator
      } yield (e, c)
  }

  def findLoginEulaByCountry(country: String)(implicit session: Session): List[Eula] = {
    allQuery(country).list
  }

  def findEulaCreatorsByCountry(country: String)(implicit session: Session): List[(Eula, UserData)] = {
    eulaWithCreator(country).list
  }

}
