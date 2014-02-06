package com.picsauditing.report.tables;

import com.picsauditing.report.fields.Field;
import com.picsauditing.report.fields.FieldType;

public class SingleOperatorContractorTable extends AbstractTable {

	public static final String SingleOperatorContractorsView = "SingleOperatorContractorsView";
	public static final String Account = "Account";

	public static final String logoAccountNameField = "logo_account_name";
	public static final String logoAccountIdField = "logo_account_id";

	public SingleOperatorContractorTable() {
        super("vw_single_operator_contractors");

		Field logoAccountName = addField(new Field(logoAccountNameField, logoAccountNameField, FieldType.String));
		logoAccountName.setImportance(FieldImportance.Required);
		logoAccountName.setVisible(true);

		Field logoAccountId = addField(new Field(logoAccountIdField, logoAccountIdField, FieldType.Integer));
		logoAccountId.setImportance(FieldImportance.Required);
		logoAccountId.setVisible(true);

	}

    protected void addJoins() {

	    ReportForeignKey account = new ReportForeignKey(Account, new AccountTable(), new ReportOnClause("con_id", "id",
			    ReportOnClause.ToAlias + ".type = 'Contractor'"));
	    account.setMinimumImportance(FieldImportance.Low);
	    addRequiredKey(account);
    }
}
