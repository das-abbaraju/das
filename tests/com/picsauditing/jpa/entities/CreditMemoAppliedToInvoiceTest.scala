package com.picsauditing.jpa.entities

import org.scalatest.FlatSpec
import org.junit.runner.RunWith
import org.scalatest.junit.JUnitRunner
import java.math.BigDecimal
import org.scalatest.matchers.ShouldMatchers

@RunWith(classOf[JUnitRunner])
class CreditMemoAppliedToInvoiceTest extends FlatSpec with ShouldMatchers {

  val TWO = BigDecimal valueOf 2 setScale 2
  val FIVE = BigDecimal valueOf 5 setScale 2
  val SIX = BigDecimal valueOf 6 setScale 2
  val NINE = BigDecimal valueOf 9 setScale 2
  val FIFTEEN = BigDecimal valueOf 15 setScale 2

  val invoice = new Invoice
  val creditMemoApplied = CreditMemoAppliedToInvoice from invoice
  val creditMemo = creditMemoApplied.getCreditMemo
  val invoiceFee = new InvoiceFee
  invoiceFee.setDisplayOrder(1)
  val (invoiceItem1, invoiceItem2, invoiceItem3) = (new InvoiceItem, new InvoiceItem, new InvoiceItem)
  val (refundItem1, refundItem2, refundItem3) = (new RefundItem, new RefundItem, new RefundItem)

  List(invoiceItem1, invoiceItem2, invoiceItem3) foreach { item =>
    item.setInvoiceFee(invoiceFee)
    invoice.getItems.add(item)
    item.setInvoice(invoice)
    item.setAmount(FIVE)
  }

  List(refundItem1, refundItem2, refundItem3) foreach { item =>
    item.setInvoiceFee(invoiceFee)
    creditMemo.getItems.add(item)
    item.setCreditMemo(creditMemo)
    item.setAmount(TWO)
  }

  creditMemoApplied.updateAmountApplied()

  "CreditMemoAppliedToInvoice" should "update the values for the relevant invoice when updateAmountApplied is called." in {
    creditMemoApplied.getInvoice.getTotalAmount should equal(FIFTEEN)
    creditMemoApplied.getInvoice.getAmountApplied should equal(SIX)
  }

  it should "update the values for the relevant credit memo when updateAmountApplied is called." in {
    creditMemoApplied.getCreditMemo.getTotalAmount should equal(SIX)
    creditMemoApplied.getInvoice.getAmountApplied should equal(SIX)
  }

  it should "calculate it's own value based on the underlying credit memo when updateAmountApplied is called." in {
    creditMemoApplied.getAmount should equal(creditMemoApplied.getCreditMemo.getTotalAmount)
    creditMemoApplied.getAmount should equal(SIX)
  }
}
