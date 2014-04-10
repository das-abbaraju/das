package com.picsauditing.persistence.model

import scala.slick.driver.JdbcProfile

trait Profile {
  protected[persistence] val profile: JdbcProfile

  val convertFromTimeStamp = profile.simple.MappedColumnType.base[java.util.Date, java.sql.Timestamp](
    { utilDate => new java.sql.Timestamp(utilDate.getTime) },
    { sqlDate => new java.util.Date(sqlDate.getTime) }
  )

  val convertFromSQLDate = profile.simple.MappedColumnType.base[java.util.Date, java.sql.Date](
    { utilDate => new java.sql.Date(utilDate.getTime)},
    { sqlDate => new java.util.Date(sqlDate.getTime)}
  )

}

trait MySQLProfile extends Profile {
  override lazy val profile = scala.slick.driver.MySQLDriver.profile
}

