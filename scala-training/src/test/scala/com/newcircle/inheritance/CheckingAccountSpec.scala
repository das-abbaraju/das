package com.newcircle.tests.inheritance

import com.newcircle.classes.BankAccount
import com.newcircle.inheritance.{CheckingAccount, InsufficientFundsException}
import com.newcircle.UnitSpec

class CheckingAccountSpec extends UnitSpec {
  "BankAccount" should "not charge for a deposit" in {
    val account = new BankAccount
    account.deposit(30)
    account.balance should equal (30.0)
  }

  it should "not charge for a withdrawal" in {
    val account = new BankAccount
    account.deposit(30)
    account.balance should equal (30.0)
    account.withdraw(9.0)
    account.balance should equal (21.0)
  }

  "CheckingAccount" should "charge for each deposit" in {
    val account = new CheckingAccount
    account.deposit(30)
    account.balance should equal (29)
  }

  it should "charge for each withdrawal" in {
    val account = new CheckingAccount
    account.deposit(100)
    // Due to charge, the balance is now 99
    account.withdraw(10) should equal (10.0)
    account.balance should equal (88)
  }

  it should "not allow a withdrawal if it can't charge $1" in {
    val account = new CheckingAccount
    account.deposit(100)
    intercept[InsufficientFundsException] {
      account.withdraw(100)
    }
  }

  it should "not allow a deposit if it can't charge $1" in {
    val account = new CheckingAccount
    intercept[InsufficientFundsException] {
      account.deposit(0.99)
    }
  }
}
