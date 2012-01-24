package com.picsauditing.report.models;

import com.picsauditing.report.fieldtypes.FilterType;
import com.picsauditing.report.tables.Contractor;
import com.picsauditing.util.PermissionQueryBuilder;

public class QueryAccountContractor extends QueryAccount {
	public QueryAccountContractor() {
		super();
		from.getFields().remove("accountName");
		from.getFields().remove("accountType");

		Contractor contractor = new Contractor();
		from.addJoin(contractor);
		contractor.addFields();
		contractor.addJoins();
		
		// PermissionQueryBuilder permQuery = new PermissionQueryBuilder(permissions);
		// sql.addWhere("1 " + permQuery.toString());
	}

	private void joinToContractorWatch(String joinAlias, String foreignKey) {
		joins.put(joinAlias, "LEFT JOIN contractor_watch " + joinAlias + " ON " + joinAlias + ".conID = " + foreignKey);

		addQueryField(joinAlias + "ContractorID", foreignKey, FilterType.Number, joinAlias, true);
		addQueryField(joinAlias + "UserID", joinAlias + ".userID", FilterType.Number, joinAlias, true);
	}

	private void leftJoinToEmailQueue(String joinAlias, String foreignKey) {
		joins.put(joinAlias, "JOIN email_queue " + joinAlias + " ON " + joinAlias + ".conID = " + foreignKey);
		addQueryField(joinAlias + "ContractorID", foreignKey, FilterType.Number, joinAlias, true);

		addQueryField(joinAlias + "CreationDate", joinAlias + ".creationDate", FilterType.Date, joinAlias, true);
		addQueryField(joinAlias + "SentDate", joinAlias + ".sentDate", FilterType.Date, joinAlias, true);
		addQueryField(joinAlias + "CreatedBy", joinAlias + ".createdBy", FilterType.Number, joinAlias);
		addQueryField(joinAlias + "ViewableBy", joinAlias + ".viewableBy", FilterType.Number, joinAlias);
		addQueryField(joinAlias + "Subject", joinAlias + ".subject", FilterType.String, joinAlias);
		addQueryField(joinAlias + "TemplateID", joinAlias + ".templateID", FilterType.Number, joinAlias);
		addQueryField(joinAlias + "Status", joinAlias + ".status", FilterType.Enum, joinAlias);
	}

	private void joinToFacilities(String joinAlias, String tableKey, String foreignKey) {
		joins.put(joinAlias, "LEFT JOIN facilities " + joinAlias + " ON " + joinAlias + "." + tableKey + " = "
				+ foreignKey);

		addQueryField(joinAlias + "OperatorID", joinAlias + ".opID", FilterType.Number, joinAlias, true);
		addQueryField(joinAlias + "CorporateID", joinAlias + ".corporateID", FilterType.Number, joinAlias, true);

		leftJoinToAccount("operatorChild", joinAlias + ".opID");
		leftJoinToAccount("corporateParent", joinAlias + ".corporateID");
	}

	private void joinToFlagCriteriaContractor(String joinAlias, String foreignKey) {
		joins.put(joinAlias, "JOIN flag_criteria_contractor " + joinAlias + " ON " + joinAlias + ".conID = "
				+ foreignKey);
		addQueryField(joinAlias + "ContractorID", foreignKey, FilterType.Number, true);

		addQueryField(joinAlias + "ID", joinAlias + ".id", FilterType.Number, joinAlias);
		addQueryField(joinAlias + "CriteriaID", joinAlias + ".criteriaID", FilterType.Number, joinAlias);
		addQueryField(joinAlias + "Answer", joinAlias + ".answer", FilterType.String, joinAlias);
	}

	private void joinToGeneralContractor(String joinAlias, String tableKey, String foreignKey) {
		joins.put(joinAlias, "LEFT JOIN generalcontractors " + joinAlias + " ON " + joinAlias + "." + tableKey + " = "
				+ foreignKey);

		addQueryField(joinAlias + "ContractorID", joinAlias + ".subID", FilterType.Number, joinAlias, true);
		addQueryField(joinAlias + "OperatorID", joinAlias + ".genID", FilterType.Number, joinAlias, true);
		addQueryField(joinAlias + "FlagLastUpdated", joinAlias + ".flagLastUpdated", FilterType.Date, joinAlias);
		addQueryField(joinAlias + "Flag", joinAlias + ".flag", FilterType.String, joinAlias, true);

		leftJoinToAccount(joinAlias + "Operator", joinAlias + ".genID");
	}

	private void joinToInvoiceFee(String joinAlias, String foreignKey) {
		joins.put(joinAlias, "JOIN invoice_fee " + joinAlias + " ON " + joinAlias + ".id = " + foreignKey);

		addQueryField(joinAlias + "ID", joinAlias + ".id", FilterType.Number, joinAlias, true);
		addQueryField(joinAlias + "MaxFacilities", joinAlias + ".maxFacilities", FilterType.Number, joinAlias, true);
	}

	private void joinToLoginLog(String joinAlias, String foreignKey) {
		joins.put(joinAlias, "JOIN loginlog " + joinAlias + " ON " + joinAlias + ".userID = " + foreignKey);
		addQueryField(joinAlias + "UserID", foreignKey, FilterType.Number, joinAlias, true);

		addQueryField(joinAlias + "AdminID", joinAlias + ".adminID", FilterType.Number, joinAlias, true);
		addQueryField(joinAlias + "LoginDate", joinAlias + ".loginDate", FilterType.Date, joinAlias);
		addQueryField(joinAlias + "RemoteAccess", joinAlias + ".remoteAccess", FilterType.String, joinAlias);
	}

	private void leftJoinToAccount(String joinAlias, String foreignKey) {
		// Use JoinAccount instead
	}

	private void leftJoinToEmailTemplate(String joinAlias, String foreignKey) {
		joins.put(joinAlias, "LEFT JOIN email_template " + joinAlias + " ON " + joinAlias + ".id = " + foreignKey);
		addQueryField(joinAlias + "ID", joinAlias + ".id", FilterType.Number, joinAlias, true);
		addQueryField(joinAlias + "Name", joinAlias + ".templateName", FilterType.String, joinAlias, true);
	}

}
