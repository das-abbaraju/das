package com.newcircle.tests.packages

import com.newcircle.packages._
import com.newcircle.UnitSpec

class ConversionsSpec extends UnitSpec {
  "inchesToCentimeters" should "handle 1 inch" in {
    inchesToCentimeters(1) should equal (2.54)
  }

  it should "handle 0 inches" in {
    inchesToCentimeters(0) should equal (0)
  }

  it should "handle inches with a fractional part" in {
    inchesToCentimeters(103.25) should equal (262.255)
  }

  it should "handle less than one inch" in {
    inchesToCentimeters(0.125) should equal (0.3175)
  }

  "gallonsToLiters" should "handle 1 gallons" in {
    gallonsToLiters(1) should equal (3.7854118)
  }

  it should "handle 0 gallons" in {
    gallonsToLiters(0) should equal (0)
  }

  it should "handle gallons with a fractional part" in {
    gallonsToLiters(1.75) should equal (6.62447065)
  }

  it should "handle less than one gallon" in {
    gallonsToLiters(.5) should equal (1.8927059)
  }

  "milesToKilometers" should "handle 1 mile" in {
    milesToKilometers(1) should equal (1.609344)
  }

  it should "handle 0 miles" in {
    milesToKilometers(0) should equal (0)
  }

  it should "handle miles with a fractional part" in {
    milesToKilometers(10.25) should equal (16.495776)
  }

  it should "handle less than one mile" in {
    milesToKilometers(.7) should equal (1.1265408)
  }
}
