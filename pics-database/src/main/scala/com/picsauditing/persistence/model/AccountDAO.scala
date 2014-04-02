package com.picsauditing.persistence.model

case class AccountData(id: Option[Long], name: String, status: String)

trait AccountDAO { this: Profile =>
  import profile.simple._
  val accountTableName = "accounts"

  class AccountSchema(tag: Tag) extends Table[AccountData](tag, accountTableName) {
    def id = column[Long]("id", O.PrimaryKey)
    def name = column[String]("name")
    def status = column[String]("status")

    def * = (id.?, name, status) <> (AccountData.tupled, AccountData.unapply)

  }

  val accounts = TableQuery[AccountSchema]
}
