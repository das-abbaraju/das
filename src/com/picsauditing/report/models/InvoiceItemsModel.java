package com.picsauditing.report.models;

import com.picsauditing.access.Permissions;
import com.picsauditing.jpa.entities.Filter;
import com.picsauditing.report.fields.Field;
import com.picsauditing.report.tables.AccountTable;
import com.picsauditing.report.tables.FieldImportance;
import com.picsauditing.report.tables.InvoiceItemTable;
import com.picsauditing.report.tables.InvoiceTable;

import java.util.List;
import java.util.Map;

public class InvoiceItemsModel extends AbstractModel {

	public InvoiceItemsModel(Permissions permissions) {
		super(permissions, new InvoiceItemTable());
	}

	public ModelSpec getJoinSpec() {
		ModelSpec spec = new ModelSpec(null, "InvoiceItem");
        spec.join(InvoiceItemTable.Fee);

        ModelSpec invoice = spec.join(InvoiceItemTable.Invoice);
        invoice.alias = "Invoice";

        ModelSpec account = invoice.join(InvoiceTable.Account);
		account.alias = "Account";

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
