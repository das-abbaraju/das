package com.picsauditing.report.models;

import com.picsauditing.access.Permissions;
import com.picsauditing.report.tables.AccountTable;
import com.picsauditing.report.tables.InvoiceCommissionTable;
import com.picsauditing.report.tables.InvoiceTable;
import com.picsauditing.report.tables.PaymentCommissionTable;

public class PaymentCommissionModel extends AbstractModel {
	public PaymentCommissionModel(Permissions permissions) {
		super(permissions, new PaymentCommissionTable());
	}

	public ModelSpec getJoinSpec() {
		ModelSpec spec = new ModelSpec(null, "PaymentCommission");

		{
			ModelSpec invoiceCommission = spec.join(PaymentCommissionTable.Commission);
			invoiceCommission.alias = "InvoiceCommission";
			{
				ModelSpec invoice = invoiceCommission.join(InvoiceCommissionTable.Invoice);
				invoice.alias = "Invoice";
				{
					ModelSpec account = invoice.join(InvoiceTable.Account);
					account.alias = "Account";
					account.join(AccountTable.Contact);
					// ModelSpec contractor =
					// account.join(AccountTable.Contractor);
					// contractor.alias = "Contractor";
					// contractor.minimumImportance = FieldImportance.Average;
				}
			}
			ModelSpec recipientUser = invoiceCommission.join(InvoiceCommissionTable.User);
			recipientUser.alias = "InvoiceCommissionRecipientUser";
		}
		ModelSpec payment = spec.join(PaymentCommissionTable.Payment);
		payment.alias = "PaymentCommissionPayment";

		return spec;
	}
}