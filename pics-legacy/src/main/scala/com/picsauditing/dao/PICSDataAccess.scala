package com.picsauditing.dao

import org.springframework.beans.factory.annotation.Autowired
import javax.sql.DataSource
import scala.slick.session.Database

trait PICSDataAccess {

  @Autowired
  private val dataSource: DataSource = null

  def db = Database.forDataSource(dataSource)

  implicit class ConvertedList[X](list: List[X]) {
    def toJava: java.util.List[X] = {
      val out = new java.util.ArrayList[X](list.size)
      list foreach out.add
      out
    }
  }

}
