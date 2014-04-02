package com.picsauditing.persistence.model

import util.BaseTestSetup
import scala.slick.driver.H2Driver.simple.Session
import scala.slick.jdbc.meta.MTable

class UserDataProvideTest extends BaseTestSetup {

  trait UserDataTest extends DBTest[UserDataProvider] {
    val dbName = "UserDataTest"
    val service = new UserDataProvider(dataSource) with H2TestingProfile

    val richardPrior = new UserData(Some(4L), "Richard Prior", "richard@prior.com", "999-999-9999", "888-888-8888", "No")
    val robinWilliams = new UserData(Some(0L), "Robin Williams", "robin@williams.com", "777-777-7777", "666-666-6666", "Yes")
    val joePeschi = new UserData(Some(1L), "Joe Peschi", "joe@peschi.com", "555-555-5555", "444-444-4444", "Yes")
    val georgeCarlin = new UserData(Some(2L), "George Carlin", "mr_conductor@shiningtimestation.com", "333-333-3333", "222-222-2222", "No")
    val samKinison = new UserData(Some(3L), "Sam Kinison", "aaaaaaah@marriedforthreeyears.com", "111-111-1111", "000-000-0000", "No")

    override def prepareDatabase(implicit session: Session, provider: UserDataProvider) = {
      import service._
      import service.profile.simple._

      val tableList = MTable.getTables.mapResult( _.name.name ).list

      def createTable(tableName: String, table: profile.SchemaDescription) = {
        if (!tableList.contains(tableName)) table.create
      }

      createTable(userTableName, users.ddl)
      createTable(userAccessTableName, userAccess.ddl)


    }
  }

}
