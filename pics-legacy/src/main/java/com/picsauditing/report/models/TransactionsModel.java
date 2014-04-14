package com.picsauditing.report.models;

import com.picsauditing.access.Permissions;
import com.picsauditing.report.fields.Field;
import com.picsauditing.report.fields.FieldType;
import com.picsauditing.report.tables.*;

import java.util.Map;

public class TransactionsModel extends AbstractModel {

	public TransactionsModel(Permissions permissions) {
		super(permissions, new TransactionTable());
	}

	public ModelSpec getJoinSpec() {
		ModelSpec spec = new ModelSpec(null, "Transaction");

		ModelSpec account = spec.join(InvoiceTable.Account);
		account.alias = "Account";

        return spec;
	}

	@Override
	public Map<String, Field> getAvailableFields() {
		Map<String, Field> fields = super.getAvailableFields();

		Field accountName = fields.get("AccountName".toUpperCase());
	    accountName.setUrl("BillingDetail.action?id={AccountID}");

        Field sapSync = new Field("AccountSapSync","Account.sapSync", FieldType.Boolean);
        fields.put(sapSync.getName().toUpperCase(),sapSync);
        Field sapLastSync = new Field("AccountSapLastSync","Account.sapLastSync", FieldType.DateTime);
        fields.put(sapLastSync.getName().toUpperCase(),sapLastSync);

        return fields;
	}
}
