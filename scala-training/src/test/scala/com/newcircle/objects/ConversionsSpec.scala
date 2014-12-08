package com.newcircle.tests.objects

import com.newcircle.objects.Conversions
import com.newcircle.UnitSpec

class ConversionsSpec extends UnitSpec {
  "inchesToCentimeters" should "handle 1 inch" in {
    Conversions.inchesToCentimeters(1) should equal (2.54)
  }

  it should "handle 0 inches" in {
    Conversions.inchesToCentimeters(0) should equal (0)
  }

  it should "handle inches with a fractional part" in {
    Conversions.inchesToCentimeters(103.25) should equal (262.255)
  }

  it should "handle less than one inch" in {
    Conversions.inchesToCentimeters(0.125) should equal (0.3175)
  }

  "gallonsToLiters" should "handle 1 gallons" in {
    Conversions.gallonsToLiters(1) should equal (3.7854118)
  }

  it should "handle 0 gallons" in {
    Conversions.gallonsToLiters(0) should equal (0)
  }

  it should "handle gallons with a fractional part" in {
    Conversions.gallonsToLiters(1.75) should equal (6.62447065)
  }

  it should "handle less than one gallon" in {
    Conversions.gallonsToLiters(.5) should equal (1.8927059)
  }

  "milesToKilometers" should "handle 1 mile" in {
    Conversions.milesToKilometers(1) should equal (1.609344)
  }

  it should "handle 0 miles" in {
    Conversions.milesToKilometers(0) should equal (0)
  }

  it should "handle miles with a fractional part" in {
    Conversions.milesToKilometers(10.25) should equal (16.495776)
  }

  it should "handle less than one mile" in {
    Conversions.milesToKilometers(.7) should equal (1.1265408)
  }
}
