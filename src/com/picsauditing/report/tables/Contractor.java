package com.picsauditing.report.tables;

import com.picsauditing.report.fields.FilterType;
import com.picsauditing.report.fields.QueryField;

public class Contractor extends BaseReportTable {

	public Contractor(String parentPrefix, String parentAlias) {
		super("contractor_info", "contractor", "c", parentAlias + ".id = c.id AND " + parentAlias
				+ ".type = 'Contractor'");
		this.parentPrefix = parentPrefix;
		this.parentAlias = parentAlias;
	}

	public void addFields() {
		addFields(com.picsauditing.jpa.entities.ContractorAccount.class);

		QueryField contractorName;
		contractorName = addField(prefix + "Name", parentAlias + ".name", FilterType.AccountName);
		contractorName.setUrl("ContractorView.action?id={" + parentPrefix + "ID}");
		contractorName.setWidth(300);

		QueryField contractorEdit;
		contractorEdit = addField(prefix + "Edit", "'Edit'", FilterType.String);
		contractorEdit.setUrl("ContractorEdit.action?id={" + parentPrefix + "ID}");
		contractorEdit.setWidth(300);

		QueryField contractorAudits;
		contractorAudits = addField(prefix + "Audits", "'Audits'", FilterType.String);
		contractorAudits.setUrl("ContractorDocuments.action?id={" + parentPrefix + "ID}");
		contractorAudits.setWidth(300);
	}

	public void addJoins() {
		addLeftJoin(new User(prefix + "CustomerService", alias + ".welcomeAuditor_id"));
		addLeftJoin(new Account(prefix + "RequestedByOperator", alias + ".requestedByID"));
		addLeftJoin(new ContractorAudit(prefix + "PQF", prefix + "PQF", "conID", alias + ".id AND " + prefix + "PQF.auditTypeID = 1"));
	}
}