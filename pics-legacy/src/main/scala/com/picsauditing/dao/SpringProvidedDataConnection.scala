package com.picsauditing.dao

import org.springframework.beans.factory.annotation.Autowired
import javax.sql.DataSource
import scala.slick.driver.JdbcDriver.simple.Database

trait SlickDatabaseAccessor {
  val db: Database

  implicit class ConvertedList[X](list: List[X]) {
    def toJava: java.util.List[X] = {
      val out = new java.util.ArrayList[X](list.size)
      list foreach out.add
      out
    }
  }
}

trait SpringProvidedDataConnection extends SlickDatabaseAccessor {

  @Autowired
  private val dataSource: DataSource = null

  lazy val db = Database.forDataSource(dataSource)

}
