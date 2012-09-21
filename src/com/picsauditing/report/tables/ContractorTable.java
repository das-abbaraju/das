package com.picsauditing.report.tables;

import com.picsauditing.access.Permissions;
import com.picsauditing.report.fields.Field;
import com.picsauditing.report.fields.FilterType;

public class ContractorTable extends ReportTable {

	ContractorTable() {
		super("contractor_info");
		addKey(new ReportForeignKey("Account", new AccountTable(), new ReportOnClause("id")));
		addKey(new ReportForeignKey("CustomerService", new AccountTable(), new ReportOnClause("welcomeAuditor_id")));
		addKey(new ReportForeignKey("PQF", new AccountTable(), new ReportOnClause("id", "conID", "PQF.auditTypeID = 1")));
		// FieldCategory.CustomerServiceRepresentatives

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

	public void fill(Permissions permissions) {
//		addFields(com.picsauditing.jpa.entities.ContractorAccount.class, FieldImportance.Low);
//
//		Field conID = addPrimaryKey(FilterType.Integer);
//		conID.setCategory(FieldCategory.AccountInformation);
//		conID.setWidth(80);

//		Field contractorName = addField(new Field(symanticName + "Name", symanticName + ".name", FilterType.AccountName));
//		contractorName.setCategory(FieldCategory.AccountInformation);
//		contractorName.setUrl("ContractorView.action?id={accountID}");
//		contractorName.setWidth(300);

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