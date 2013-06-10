package com.picsauditing.report.models;

import java.util.List;
import java.util.Map;

import com.picsauditing.access.Permissions;
import com.picsauditing.jpa.entities.Filter;
import com.picsauditing.report.fields.Field;
import com.picsauditing.report.tables.AccountTable;
import com.picsauditing.report.tables.FieldImportance;
import com.picsauditing.report.tables.InvoiceTable;

public class InvoiceModel extends AbstractModel {

	public InvoiceModel(Permissions permissions) {
		super(permissions, new InvoiceTable());
	}

	public ModelSpec getJoinSpec() {
		ModelSpec spec = new ModelSpec(null, "Invoice");

		ModelSpec account = spec.join(InvoiceTable.Account);
		account.alias = "Account";
		account.join(AccountTable.Contact);

		ModelSpec contractor = account.join(AccountTable.Contractor);
		contractor.alias = "Contractor";
		contractor.minimumImportance = FieldImportance.Average;

		return spec;
	}

	@Override
	public String getWhereClause(List<Filter> filters) {
		return "Invoice.tableType = 'I'";
	}

	@Override
	public Map<String, Field> getAvailableFields() {
		Map<String, Field> fields = super.getAvailableFields();

		Field accountName = fields.get("AccountName".toUpperCase());
		accountName.setUrl("ContractorView.action?id={AccountID}");

		return fields;
	}
}
