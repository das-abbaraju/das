package com.picsauditing.report.models;

import com.picsauditing.access.Permissions;
import com.picsauditing.jpa.entities.Filter;
import com.picsauditing.report.fields.Field;
import com.picsauditing.report.tables.*;

import java.util.List;
import java.util.Map;

public class InvoicePaymentLogModel extends AbstractModel {

	public InvoicePaymentLogModel(Permissions permissions) {
		super(permissions, new LogEventTable());
	}

	public ModelSpec getJoinSpec() {
		ModelSpec spec = new ModelSpec(null, "Log");

		ModelSpec invoicePayment = spec.join(LogEventTable.InvoicePayment);

        invoicePayment.join(LogInvoicePaymentTable.Payment);
        ModelSpec invoice = invoicePayment.join(LogInvoicePaymentTable.Invoice);
        invoice.join(InvoiceTable.Account);

        return spec;
	}

	@Override
	public String getWhereClause(List<Filter> filters) {
        return "Log.ddlName = 'invoice_payment'";
	}

	@Override
	public Map<String, Field> getAvailableFields() {
		Map<String, Field> fields = super.getAvailableFields();

		Field accountName = fields.get("LogInvoicePaymentInvoiceAccountName".toUpperCase());
		accountName.setUrl("BillingDetail.action?id={LogInvoicePaymentInvoiceAccountID}");

		return fields;
	}
}
