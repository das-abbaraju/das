package com.picsauditing.persistence.model

import scala.slick.driver.JdbcProfile

trait Profile {
  protected[persistence] val profile: JdbcProfile
  protected[persistence] val simple = profile.simple

}

trait MySQLProfile extends Profile {
  override lazy val profile = scala.slick.driver.MySQLDriver.profile
}

