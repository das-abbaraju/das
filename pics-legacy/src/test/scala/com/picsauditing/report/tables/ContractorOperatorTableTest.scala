package com.picsauditing.report.tables

import org.scalatest.{Matchers, FlatSpec}
import org.powermock.reflect.Whitebox
import com.picsauditing.report.fields.Field
import org.scalatest.junit.JUnitRunner
import org.junit.runner.RunWith

@RunWith(classOf[JUnitRunner])
class ContractorOperatorTableTest extends FlatSpec with Matchers {
  private val TEST_FIELD_NAME = "Foo"
  private val TEST_CON_TYPE = "Bar"
  private val TEST_CON_QUERY = "EXISTS(SELECT * FROM contractor_operator_number con WHERE {TO_ALIAS}.conID = con.conID AND con.opID IN (SELECT f.opID FROM facilities f WHERE f.corporateID = {TO_ALIAS}.opID UNION SELECT {TO_ALIAS}.opID) AND con.type = '" + TEST_CON_TYPE + "')"

  val classUnderTest = new ContractorOperatorTable

  val result = Whitebox.invokeMethod(
      classUnderTest, "fromContractorQuery", TEST_FIELD_NAME, TEST_CON_TYPE
    ).asInstanceOf[Field]

  "Method 'fromContractorQuery'" should
  "return a Field object that has a field name matching the passed param." in {
      result.getName should be(TEST_FIELD_NAME)
  }

  it should "return a Field object that has it's conType inserted into it's database column name." in {
    result.getDatabaseColumnName contains TEST_CON_TYPE shouldEqual true
  }

  it should "return a Field object that has a database column name matching the test query." in {
    result.getDatabaseColumnName should be(TEST_CON_QUERY)
  }

  it should "return a Field object that has an Importance of 'Required'." in {
    result.getImportance shouldEqual FieldImportance.Required
  }
}
