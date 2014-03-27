package com.picsauditing.jpa.entities.builders;

import com.picsauditing.jpa.entities.InvoiceFee;
import com.picsauditing.jpa.entities.InvoiceItem;

import java.math.BigDecimal;

public class InvoiceItemBuilder {
    private InvoiceItem invoiceItem = new InvoiceItem();

    public InvoiceItem build() {
        return invoiceItem;
    }

    public InvoiceItemBuilder invoiceFee(InvoiceFee invoiceFee) {
        invoiceItem.setInvoiceFee(invoiceFee);
        return this;
    }

    public InvoiceItemBuilder amount(BigDecimal amount) {
        invoiceItem.setAmount(amount);
        return this;
    }

    public InvoiceItemBuilder originalAmount(BigDecimal originalAmount) {
        invoiceItem.setOriginalAmount(originalAmount);
        return this;
    }
}
