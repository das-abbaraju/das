package com.picsauditing.dao

import org.springframework.beans.factory.annotation.Autowired
import javax.sql.DataSource
import scala.slick.session.Database

trait PICSDataAccess {

  @Autowired
  private val dataSource: DataSource = null

  val db = Database.forDataSource(dataSource)

}
