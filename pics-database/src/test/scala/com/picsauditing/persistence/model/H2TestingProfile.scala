package com.picsauditing.persistence.model

trait H2TestingProfile extends Profile {
  override lazy val profile = scala.slick.driver.H2Driver.profile
}
