package com.picsauditing.jpa.entities.builders;

import com.picsauditing.jpa.entities.InvoiceFee;
import com.picsauditing.jpa.entities.InvoiceItem;

public class InvoiceItemBuilder {
    private InvoiceItem invoiceItem = new InvoiceItem();

    public InvoiceItem build() {
        return invoiceItem;
    }

    public InvoiceItemBuilder invoiceFee(InvoiceFee invoiceFee) {
        invoiceItem.setInvoiceFee(invoiceFee);
        return this;
    }
}
