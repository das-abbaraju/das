package com.newcircle.inheritance

import com.newcircle.classes.BankAccount

class InsufficientFundsException(message: String) extends Exception(message)

/** Create a CheckingAccount subclass that charges $1 for every deposit or
    withdrawal.
  */
class CheckingAccount extends BankAccount {

 override def deposit(amount: BigDecimal): Unit = {
    if(amount.doubleValue() <1){
      throw new InsufficientFundsException("Amount can't be less than 1 $")
    }
   super.deposit(amount - 1)

  }


  override def withdraw(amount: BigDecimal): Unit = {
    if(amount > balance || amount < 1){
      throw new InsufficientFundsException("Can't withdraw more than balance")
    }
    super.withdraw(amount +1)

  }

}
