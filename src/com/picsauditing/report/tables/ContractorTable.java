package com.picsauditing.report.tables;

import com.picsauditing.report.fields.Field;
import com.picsauditing.report.fields.FilterType;

public class ContractorTable extends BaseTable {

	public ContractorTable(String prefix, String alias) {
		super("contractor_info", "contractor", "c", alias + ".id = c.id AND " + alias
				+ ".type = 'Contractor'");
		this.prefix = prefix;
		this.alias = alias;
	}

	public void addFields() {
		addFields(com.picsauditing.jpa.entities.ContractorAccount.class);

		Field contractorName;
		contractorName = addField(prefix + "Name", alias + ".name", FilterType.AccountName);
		contractorName.setUrl("ContractorView.action?id={" + prefix + "ID}");
		contractorName.setWidth(300);

		Field contractorEdit;
		contractorEdit = addField(prefix + "Edit", "'Edit'", FilterType.String);
		contractorEdit.setUrl("ContractorEdit.action?id={" + prefix + "ID}");
		contractorEdit.setWidth(300);

		Field contractorAudits;
		contractorAudits = addField(prefix + "Audits", "'Audits'", FilterType.String);
		contractorAudits.setUrl("ContractorDocuments.action?id={" + prefix + "ID}");
		contractorAudits.setWidth(300);
	}

	public void addJoins() {
		addLeftJoin(new UserTable(prefix + "CustomerService", alias + ".welcomeAuditor_id"));
		addLeftJoin(new AccountTable(prefix + "RequestedByOperator", alias + ".requestedByID"));
		addLeftJoin(new ContractorAuditTable(prefix + "PQF", prefix + "PQF", "conID", alias + ".id AND " + prefix + "PQF.auditTypeID = 1"));
	}
}