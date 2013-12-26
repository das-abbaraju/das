package com.picsauditing.report.tables;

import com.picsauditing.jpa.entities.PaymentApplied;
import com.picsauditing.jpa.entities.PaymentAppliedToInvoice;
import com.picsauditing.jpa.entities.TransactionApplied;
import com.picsauditing.report.fields.Field;
import com.picsauditing.report.fields.FieldType;

public class LogEventTable extends AbstractTable {

    public static final String InvoicePayment = "InvoicePayment";

    public LogEventTable() {
		super("log_event");

        Field loggedAction = new Field("LoggedAction","dmlType", FieldType.String);
        addField(loggedAction);

        Field logDate = new Field("LogDate","logStart", FieldType.String);
        addField(logDate);
    }

    protected void addJoins() {
        addRequiredKey(new ReportForeignKey(InvoicePayment, new LogInvoicePaymentTable(), new ReportOnClause("id","logID")));
    }
}