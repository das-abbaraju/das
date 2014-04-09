package com.picsauditing.persistence.model

case class AccountData(id: Option[Long], name: String, status: String, autoApproveRelationships: Boolean = true)

trait AccountDAO { this: Profile =>
  import profile.simple._
  val accountTableName = "accounts"

  class AccountSchema(tag: Tag) extends Table[AccountData](tag, accountTableName) {
    def id = column[Long]("id", O.PrimaryKey)
    def name = column[String]("name")
    def status = column[String]("status")

    def autoApproveRelationships = column[Boolean]("autoApproveRelationships")(MappedColumnType.base[Boolean, Int](
      { if (_) 1 else 0 }, { _ == 1 } // Convert TinyInt to Boolean
    ))

    def * = (id.?, name, status, autoApproveRelationships) <> (AccountData.tupled, AccountData.unapply)

  }

  protected[persistence] val accounts = TableQuery[AccountSchema]
}
