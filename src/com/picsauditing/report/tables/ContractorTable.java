package com.picsauditing.report.tables;

import com.picsauditing.access.Permissions;
import com.picsauditing.report.fields.Field;
import com.picsauditing.report.fields.FilterType;

public class ContractorTable extends ReportTable {

	public ContractorTable(String name) {
		super("contractor_info", name);
		joinToAccount();
		joinToCSR();
		joinToPQF();

		// AccountTable requestedByOperator = new AccountTable(prefix +
		// "RequestedByOperator", alias + ".requestedByID");
		// requestedByOperator.setOverrideCategory(FieldCategory.RequestingClientSite);
		// addLeftJoin(requestedByOperator);
		//
		// ContractorOperatorTable contractorOperator = new
		// ContractorOperatorTable(prefix + "Operator", alias, "myFlag");
		// contractorOperator.includeAllColumns();
		// addJoin(contractorOperator);
	}

	private void joinToAccount() {
		String onClause = symanticName + ".id = " + "account.id";
		ReportJoin join = new ReportJoin(new AccountTable("account"), onClause);
		addJoin(join);
	}

	private void joinToCSR() {
		String onClause = symanticName + ".welcomeAuditor_id = " + symanticName + "CustomerService.id";
		ReportJoin join = new ReportJoin(new UserTable(symanticName + "CustomerService"), onClause);
		join.getTable().setOverrideCategory(FieldCategory.CustomerServiceRepresentatives);
		addLeftJoin(join);
	}

	private void joinToPQF() {
		String onClause = symanticName + ".id = " + symanticName + "PQF.conID AND " + symanticName + "PQF.auditTypeID = 1";
		ReportJoin join = new ReportJoin(new UserTable(symanticName + "CustomerService"), onClause);
		join.getTable().setOverrideCategory(FieldCategory.CustomerServiceRepresentatives);
		addLeftJoin(join);

		// ContractorAuditTable pqf = new ContractorAuditTable(prefix + "PQF",
		// prefix + "PQF", "conID", alias + ".id AND "
		// + prefix + "PQF.auditTypeID = 1");
		// pqf.setOverrideCategory(FieldCategory.PQF);
		// addLeftJoin(pqf);
	}

	public void fill(Permissions permissions) {
		addFields(com.picsauditing.jpa.entities.ContractorAccount.class, FieldImportance.Low);

		Field conID = addPrimaryKey(FilterType.Integer);
		conID.setCategory(FieldCategory.AccountInformation);
		conID.setWidth(80);

		Field contractorName = addField(new Field(symanticName + "Name", symanticName + ".name", FilterType.AccountName));
		contractorName.setCategory(FieldCategory.AccountInformation);
		contractorName.setUrl("ContractorView.action?id={accountID}");
		contractorName.setWidth(300);

		{
			// TODO Remove these fields eventually
			// Field contractorEdit = addField(prefix + "Edit", "'Edit'",
			// FilterType.String);
			// contractorEdit.setUrl("ContractorEdit.action?id={" + prefix +
			// "ID}");
			// contractorEdit.setWidth(100);
			//
			// Field contractorAudits = addField(prefix + "Audits", "'Audits'",
			// FilterType.String);
			// contractorAudits.setUrl("ContractorDocuments.action?id={" +
			// prefix + "ID}");
			// contractorAudits.setWidth(100);
		}
	}
}