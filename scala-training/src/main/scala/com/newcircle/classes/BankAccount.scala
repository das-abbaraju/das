package com.newcircle.classes

class BankAccount {

  var actualBalance = BigDecimal("0.0");

  def deposit(amount: BigDecimal): Unit = {
    if(amount.doubleValue() <0){
      throw new IllegalArgumentException("Amount can't be less than zero")
    }

    actualBalance = actualBalance + amount
    // Implement this
  }

  def withdraw(amount: BigDecimal): Unit = {
   if(amount >balance){
     throw new IllegalArgumentException("Can't withdraw more than balance")
   }

   actualBalance = actualBalance - amount
  }

  def balance :BigDecimal= {
   var balance = actualBalance
    return balance
  }
}
