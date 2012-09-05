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

		addField(prefix + "ID", alias + ".id", FilterType.Integer, FieldCategory.AccountInformation).setWidth(80);

		Field contractorName = addField(prefix + "Name", parentAlias + ".name", FilterType.AccountName, FieldCategory.AccountInformation);
		contractorName.setUrl("ContractorView.action?id={" + prefix + "ID}");
		contractorName.setWidth(300);

		// TODO Remove eventually
		Field contractorEdit = addField(prefix + "Edit", "'Edit'", FilterType.String);
		contractorEdit.setUrl("ContractorEdit.action?id={" + prefix + "ID}");
		contractorEdit.setWidth(100);

		// TODO Remove eventually
		Field contractorAudits = addField(prefix + "Audits", "'Audits'", FilterType.String);
		contractorAudits.setUrl("ContractorDocuments.action?id={" + prefix + "ID}");
		contractorAudits.setWidth(100);
	}

	public void addJoins() {
		UserTable customerServiceRep = new UserTable(prefix + "CustomerService", alias + ".welcomeAuditor_id");
		customerServiceRep.setOverrideCategory(FieldCategory.CustomerServiceRepresentatives);
		addLeftJoin(customerServiceRep);
		
		AccountTable requestedByOperator = new AccountTable(prefix + "RequestedByOperator", alias + ".requestedByID");
		requestedByOperator.setOverrideCategory(FieldCategory.RequestingClientSite);
		addLeftJoin(requestedByOperator);
		
		ContractorAuditTable pqf = new ContractorAuditTable(prefix + "PQF", prefix + "PQF", "conID", alias + ".id AND " + prefix + "PQF.auditTypeID = 1");
		pqf.setOverrideCategory(FieldCategory.PQF);
		addLeftJoin(pqf);
	}
}