package com.picsauditing.report.models;

import java.util.List;

import com.picsauditing.access.Permissions;
import com.picsauditing.report.Filter;
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
			account.alias = "Account";
			ModelSpec contractor = account.join(AccountTable.Contractor);
			contractor.alias = "Contractor";
			account.join(AccountTable.Contact);
		}
		return spec;
	}

	@Override
	public String getWhereClause(Permissions permissions, List<Filter> filters) {
		return "Invoice.tableType = 'I'";
	}
}
