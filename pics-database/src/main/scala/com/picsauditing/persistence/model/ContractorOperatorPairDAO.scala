package com.picsauditing.persistence.model

case class ContractorOperatorPairData(id: Option[Long], operatorID: Long, contractorID: Long, workStatus: String)

trait ContractorOperatorPairDAO { this: Profile =>

  import profile.simple._
  val contractorOperatorTableName = "contractor_operator"

  protected[persistence]
  class ContractorOperatorSchema(tag: Tag) extends Table[ContractorOperatorPairData](tag, contractorOperatorTableName) {
    def id = column[Long]("id", O.PrimaryKey, O.AutoInc)
    def contractorID = column[Long]("conID")
    def operatorID = column[Long]("opID")
    def workStatus = column[String]("workStatus")

    def * = (id.?, operatorID, contractorID, workStatus) <> (ContractorOperatorPairData.tupled, ContractorOperatorPairData.unapply)
  }

  protected[persistence] val contractorOperators = TableQuery[ContractorOperatorSchema]
}
