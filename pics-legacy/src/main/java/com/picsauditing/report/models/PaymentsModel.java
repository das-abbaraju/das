package com.picsauditing.report.models;

import com.picsauditing.access.Permissions;
import com.picsauditing.jpa.entities.Filter;
import com.picsauditing.report.fields.Field;
import com.picsauditing.report.tables.*;

import java.util.List;
import java.util.Map;

public class PaymentsModel extends AbstractModel {

	public PaymentsModel(Permissions permissions) {
		super(permissions, new PaymentTable());
	}

	public ModelSpec getJoinSpec() {
		ModelSpec spec = new ModelSpec(null, "Payment");

		ModelSpec account = spec.join(PaymentTable.Account);
		account.alias = "Account";
		account.join(AccountTable.Contact);

        ModelSpec invoicePayment = spec.join(PaymentTable.InvoicePayment);
        invoicePayment.join(InvoicePaymentTable.Invoice);

        ModelSpec contractor = account.join(AccountTable.Contractor);
		contractor.alias = "Contractor";

        ModelSpec insideSales = contractor.join(ContractorTable.InsideSales);
        insideSales.minimumImportance = FieldImportance.None;
        insideSales.join(AccountUserTable.User);

		return spec;
	}

	@Override
	public String getWhereClause(List<Filter> filters) {
		return "Payment.tableType = 'P'";
	}

	@Override
	public Map<String, Field> getAvailableFields() {
		Map<String, Field> fields = super.getAvailableFields();

		Field accountName = fields.get("AccountName".toUpperCase());
		accountName.setUrl("ContractorView.action?id={AccountID}");

		return fields;
	}
}
