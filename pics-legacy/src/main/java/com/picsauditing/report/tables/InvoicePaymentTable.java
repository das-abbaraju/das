package com.picsauditing.report.tables;

import com.picsauditing.jpa.entities.*;

public class InvoicePaymentTable extends AbstractTable {

    public static final String Invoice = "Invoice";
    public static final String Payment = "Payment";

    public InvoicePaymentTable() {
		super("invoice_payment");
		addFields(TransactionApplied.class);
        addFields(PaymentApplied.class);
        addFields(PaymentAppliedToInvoice.class);

        addCreationDate();
        addUpdateDate();
    }

    protected void addJoins() {
        ReportForeignKey invoice = new ReportForeignKey(Invoice, new InvoiceTable(), new ReportOnClause("invoiceID"));
        addRequiredKey(invoice);

        ReportForeignKey payment = new ReportForeignKey(Payment, new PaymentTable(), new ReportOnClause("paymentID"));
        addRequiredKey(payment);
    }
}