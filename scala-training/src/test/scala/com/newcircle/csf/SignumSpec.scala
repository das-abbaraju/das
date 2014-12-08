package com.newcircle.tests.csf

import com.newcircle.csf.Signum
import com.newcircle.UnitSpec
import org.scalatest._

class SignumSpec extends UnitSpec {
  val signum = new Signum

  "signum" should "return 1 for a positive number" in {
    signum.signum(100) should equal (1)
    signum.signum(1) should equal (1)
    signum.signum(1000000) should equal (1)
  }

  it should "return -1 for a negative number" in {
    signum.signum(-100) should equal (-1)
    signum.signum(-1) should equal (-1)
    signum.signum(-1000000) should equal (-1)
  }

  it should "return 0 for 0" in {
    signum.signum(0) should equal (0)
  }
}

    
