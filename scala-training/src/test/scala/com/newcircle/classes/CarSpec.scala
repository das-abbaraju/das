package com.newcircle.tests.classes

import com.newcircle.classes.Car
import com.newcircle.UnitSpec

class CarSpec extends UnitSpec {
  "Car" should "handle just a manufacturer and model" in {
    val car = new Car("Ford", "Focus")
    car.manufacturer should equal ("Ford")
    car.model should equal ("Focus")
    car.licensePlate should equal ("")
    car.year should equal (-1)
  }

  it should "handle manufacturer, model, year" in {
    val car = new Car("Honda", "Civic", 2012)
    car.manufacturer should equal ("Honda")
    car.model should equal ("Civic")
    car.licensePlate should equal ("")
    car.year should equal (2012)
  }

  it should "handle manufacturer, model, year, plate" in {
    val car = new Car("Toyota", "Prius", 2013, "EZ5-1234")
    car.manufacturer should equal ("Toyota")
    car.model should equal ("Prius")
    car.licensePlate should equal ("EZ5-1234")
    car.year should equal (2013)
  }

  it should "allow setting/changing the license plate" in {
    val car = new Car("Tesla", "S", 2014, "ABC-9876")
    car.manufacturer should equal ("Tesla")
    car.model should equal ("S")
    car.year should equal (2014)
    car.licensePlate should equal ("ABC-9876")
    car.licensePlate = "DEF-9999"
    car.licensePlate should equal ("DEF-9999")
  }
}

