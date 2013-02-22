package com.picsauditing.report.models;

import java.util.Map;

import com.picsauditing.access.Permissions;
import com.picsauditing.report.fields.Field;
import com.picsauditing.report.tables.AccountTable;
import com.picsauditing.report.tables.InvoiceItemTable;
import com.picsauditing.report.tables.InvoiceTable;
import com.picsauditing.report.tables.UserAssignmentTable;

public class UserAssignmentModel extends AbstractModel {

	public UserAssignmentModel(Permissions permissions) {
		super(permissions, new UserAssignmentTable());
	}

	public ModelSpec getJoinSpec() {
		ModelSpec userAssign = new ModelSpec(null, "UserAssignment");
		userAssign.join(UserAssignmentTable.User);
		
		ModelSpec account = userAssign.join(UserAssignmentTable.Account);
		account.alias = "Account";
		ModelSpec contractor = account.join(AccountTable.Contractor);
		contractor.alias = "Contractor";
		ModelSpec invoice = account.join(AccountTable.Invoice);
		invoice.alias = "Invoice";
		ModelSpec invoiceItem = invoice.join(InvoiceTable.Item);
		invoiceItem.alias = "InvoiceItem";
		invoiceItem.join(InvoiceItemTable.Fee).alias = "Fee";
		
		return userAssign;
	}

	@Override
	public Map<String, Field> getAvailableFields() {
		Map<String, Field> fields = super.getAvailableFields();
		Field accountName = fields.get("AccountName".toUpperCase());
		accountName.setUrl("ContractorView.action?id={AccountID}");
		return fields;
	}
}
