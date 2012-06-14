package com.picsauditing.report.tables;

import com.picsauditing.report.fields.Field;
import com.picsauditing.report.fields.FilterType;

public class ContractorTable extends AbstractTable {

	public ContractorTable(String parentPrefix, String parentAlias) {
		super("contractor_info", "contractor", "c", parentAlias + ".id = c.id AND " + parentAlias
				+ ".type = 'Contractor'");
		this.parentPrefix = parentPrefix;
		this.parentAlias = parentAlias;
	}

	public void addFields() {
		addFields(com.picsauditing.jpa.entities.ContractorAccount.class);

		addField(prefix + "ID", alias + ".id", FilterType.Integer).setWidth(80);

		Field contractorName;
		contractorName = addField(prefix + "Name", parentAlias + ".name", FilterType.AccountName);
		contractorName.setUrl("ContractorView.action?id={" + prefix + "ID}");
		contractorName.setWidth(300);

		Field contractorEdit;
		contractorEdit = addField(prefix + "Edit", "'Edit'", FilterType.String);
		contractorEdit.setUrl("ContractorEdit.action?id={" + prefix + "ID}");
		contractorEdit.setWidth(100);

		Field contractorAudits;
		contractorAudits = addField(prefix + "Audits", "'Audits'", FilterType.String);
		contractorAudits.setUrl("ContractorDocuments.action?id={" + prefix + "ID}");
		contractorAudits.setWidth(100);
	}

	public void addJoins() {
		addLeftJoin(new UserTable(prefix + "CustomerService", alias + ".welcomeAuditor_id"));
		addLeftJoin(new AccountTable(prefix + "RequestedByOperator", alias + ".requestedByID"));
		addLeftJoin(new ContractorAuditTable(prefix + "PQF", prefix + "PQF", "conID", alias + ".id AND " + prefix + "PQF.auditTypeID = 1"));
	}
}