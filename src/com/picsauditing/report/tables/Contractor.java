package com.picsauditing.report.tables;

import com.picsauditing.report.fields.FilterType;
import com.picsauditing.report.fields.QueryField;

public class Contractor extends BaseTable {

	private String accountAlias = "a";

	public Contractor(String accountAlias) {
		super("contractor_info", "contractor", "c", accountAlias + ".id = c.id AND " + accountAlias
				+ ".type = 'Contractor'");
	}

	public void addFields() {
		addFields(com.picsauditing.jpa.entities.ContractorAccount.class);

		QueryField contractorName;
		contractorName = addField(prefix + "Name", accountAlias + ".name", FilterType.AccountName);
		contractorName.setUrl("ContractorView.action?id={accountID}");
		contractorName.setWidth(300);
	}

	public void addJoins() {
		addLeftJoin(new User(prefix + "CustomerService", alias + ".welcomeAuditor_id"));
		addLeftJoin(new Account(prefix + "RequestedByOperator", alias + ".requestedByID"));
	}
}
