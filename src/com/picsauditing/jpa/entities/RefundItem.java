package com.picsauditing.jpa.entities;

import javax.persistence.*;
import java.math.BigDecimal;

@Entity
@DiscriminatorValue("C")
public abstract class RefundItem extends TransactionItem {

    private InvoiceCreditMemo creditMemo;
    private InvoiceItem refundedItem;

    public RefundItem() {}

    public RefundItem(InvoiceItem item) {
        refundedItem = item;
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
    public InvoiceItem getRefundedItem() {
        return refundedItem;
    }

    public void setRefundedItem(InvoiceItem refundedItem) {
        this.refundedItem = refundedItem;
    }
}
