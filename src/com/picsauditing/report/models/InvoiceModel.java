package com.picsauditing.report.models;

import com.picsauditing.access.Permissions;
import com.picsauditing.report.tables.AccountTable;
import com.picsauditing.report.tables.InvoiceTable;

public class InvoiceModel extends AbstractModel {
	public InvoiceModel(Permissions permissions) {
		super(permissions, new InvoiceTable());
	}

	public ModelSpec getJoinSpec() {
		ModelSpec spec = new ModelSpec(null, "Invoice");
		{
			ModelSpec account = spec.join(InvoiceTable.Account);
			account.join(AccountTable.Contractor);
			account.join(AccountTable.Contact);
		}
		return spec;
	}
}
