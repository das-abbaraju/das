package com.newcircle.tests.classes

import com.newcircle.classes.BankAccount
import com.newcircle.UnitSpec

class BankAccountSpec extends UnitSpec {
  "BankAccount" should "permit deposits of non-negative amounts" in {
    val account = new BankAccount
    account.deposit(30)
    account.balance should equal (30.0)
  }

  it should "reject deposits of non-negative amounts" in {
    val account = new BankAccount
    intercept[IllegalArgumentException] { account.deposit(-30) }
  }

  it should "allow withdrawal of amount less than balance" in {
    val account = new BankAccount
    account.deposit(123.22)
    account.withdraw(100) should equal (100.0)
    account.balance should equal (23.22)
  }

  it should "handle withdrawal of amount more than balance" in {
    val account = new BankAccount
    account.deposit(99.12)
    account.withdraw(100) should equal (99.12)
    account.balance should equal (0)
  }

  it should "handle withdrawal when balance is 0" in {
    val account = new BankAccount
    account.withdraw(100) should equal (0)
    account.balance should equal (0)
  }

  it should "handle withdrawal of 0" in {
    val account = new BankAccount
    account.deposit(100)
    account.withdraw(0) should equal (0)
    account.balance should equal (100)
  }
}
