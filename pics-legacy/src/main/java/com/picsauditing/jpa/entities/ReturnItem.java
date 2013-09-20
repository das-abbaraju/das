package com.picsauditing.jpa.entities;

import javax.persistence.*;
import java.math.BigDecimal;

@Entity
@DiscriminatorValue("C")
public class ReturnItem extends TransactionItem {

    private InvoiceCreditMemo creditMemo;
    private InvoiceItem returnedItem;

    public ReturnItem() {}

    public ReturnItem(InvoiceItem item) {
        returnedItem = item;
        invoiceFee = item.invoiceFee;
        amount = item.getAmount().multiply(BigDecimal.valueOf(-1));
    }

    @ManyToOne
    @JoinColumn(name = "invoiceID")
    public InvoiceCreditMemo getCreditMemo() {
        return creditMemo;
    }

    public void setCreditMemo(InvoiceCreditMemo creditMemo) {
        this.creditMemo = creditMemo;
    }

    @OneToOne
    @JoinColumn(name = "refundFor")
    public InvoiceItem getReturnedItem() {
        return returnedItem;
    }

    public void setReturnedItem(InvoiceItem returnedItem) {
        this.returnedItem = returnedItem;
    }

    public void setTransaction(Transaction transaction) {
        setCreditMemo((InvoiceCreditMemo) transaction);
    }
}
