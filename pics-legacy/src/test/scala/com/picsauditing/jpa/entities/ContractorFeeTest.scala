package com.picsauditing.jpa.entities

import org.scalatest.{Matchers, FlatSpec}
import org.junit.runner.RunWith
import org.scalatest.junit.JUnitRunner
import java.math.BigDecimal

@RunWith(classOf[JUnitRunner])
class ContractorFeeTest extends FlatSpec with Matchers {


  "ContractorFeeTest" should "not be an Upgrade if there is an increased price, but not an increased facility count." in {
    val classUnderTest = new ContractorFee
    classUnderTest.setCurrentAmount(BigDecimal.valueOf(20))
    classUnderTest.setNewAmount(BigDecimal.valueOf(40))
    classUnderTest.setCurrentFacilityCount(8)
    classUnderTest.setNewFacilityCount(8)
    classUnderTest.isUpgrade shouldEqual false
  }

  it should "not be an upgrade if there is not an increased price or an increased facility count." in {
    val classUnderTest = new ContractorFee
    classUnderTest.setCurrentAmount(BigDecimal.valueOf(20))
    classUnderTest.setNewAmount(BigDecimal.valueOf(20))
    classUnderTest.setNewFacilityCount(8)
    classUnderTest.setNewFacilityCount(8)
    classUnderTest.isUpgrade shouldEqual false
  }

  it should "not be an upgrade if there is not an increased price and there is an increased facility count." in {
    val classUnderTest = new ContractorFee
    classUnderTest.setCurrentAmount(BigDecimal.valueOf(20))
    classUnderTest.setNewAmount(BigDecimal.valueOf(20))
    classUnderTest.setNewFacilityCount(8)
    classUnderTest.setNewFacilityCount(10)
    classUnderTest.isUpgrade shouldEqual false
  }

  it should "not be an upgrade if there is a decreased price but not an increased facility count." in {
    val classUnderTest = new ContractorFee
    classUnderTest.setCurrentAmount(BigDecimal.valueOf(20))
    classUnderTest.setNewAmount(BigDecimal.valueOf(10))
    classUnderTest.setNewFacilityCount(8)
    classUnderTest.isUpgrade shouldEqual false
  }

  it should "be an upgrade only if there is an increased price and an increased facility count." in {
    val classUnderTest = new ContractorFee
    classUnderTest.setCurrentAmount(BigDecimal.valueOf(20))
    classUnderTest.setNewAmount(BigDecimal.valueOf(40))
    classUnderTest.setNewFacilityCount(8)
    classUnderTest.setNewFacilityCount(10)
    classUnderTest.isUpgrade shouldEqual true
  }
}
