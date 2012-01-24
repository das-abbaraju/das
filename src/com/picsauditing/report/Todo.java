package com.picsauditing.report;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.picsauditing.access.Permissions;
import com.picsauditing.dao.BasicDAO;
import com.picsauditing.jpa.entities.Report;
import com.picsauditing.report.fields.FilterType;
import com.picsauditing.report.fields.QueryField;
import com.picsauditing.report.fields.SimpleReportField;
import com.picsauditing.report.models.ModelBase;
import com.picsauditing.report.models.QueryAccountContractorAudit;
import com.picsauditing.search.SelectSQL;
import com.picsauditing.util.PermissionQueryBuilder;

public class Todo {
	private ModelBase base;
	private SelectSQL sql = new SelectSQL();
	private Map<String, QueryField> availableFields = new HashMap<String, QueryField>();
	private Map<String, String> joins = new HashMap<String, String>();
	private Permissions permissions;
	private String defaultSort = null;
	private int allRows = 0;
	private List<SimpleReportField> columns;
	private BasicDAO dao;
	private Report report;
	
	private QueryField addQueryField(String dataIndex, String sql, FilterType filterType, String requireJoin) {
		return addQueryField(dataIndex, sql, filterType, requireJoin, false);
	}

	private QueryField addQueryField(String dataIndex, String sql, FilterType filterType, boolean makeDefault) {
		return addQueryField(dataIndex, sql, filterType, null, makeDefault);
	}

	private QueryField addQueryField(String dataIndex, String sql, FilterType filterType) {
		return addQueryField(dataIndex, sql, filterType, null, false);
	}

	private QueryField addQueryField(String dataIndex, String sql, FilterType filterType, String requireJoin,
			boolean makeDefault) {
		QueryField field = new QueryField(dataIndex, sql, filterType, requireJoin, makeDefault);
		availableFields.put(dataIndex, field);
		return field;
	}

	private void buildEmailBase() {
		sql = new SelectSQL();
		sql.setFromTable("email_queue eq");

		addQueryField("emailID", "eq.emailID", FilterType.Number, true);
		addQueryField("emailStatus", "eq.emailStatus", FilterType.Enum);
		addQueryField("emailFromAddress", "eq.emailFromAddress", FilterType.String, true);
		addQueryField("emailToAddresses", "eq.emailToAddresses", FilterType.String, true);
		addQueryField("emailCcAddresses", "eq.emailCcAddresses", FilterType.String);
		addQueryField("emailSubject", "eq.subject", FilterType.String, true);
		addQueryField("emailPriority", "eq.priority", FilterType.Number);
		addQueryField("emailCreationDate", "eq.creationDate", FilterType.Date);
		addQueryField("emailSentDate", "eq.sentDate", FilterType.Date);

		leftJoinToAccount("emailContractor", "eq.conID");
		leftJoinToEmailTemplate("et", "emailTemplate");

		defaultSort = "eq.priority DESC, eq.emailID";
	}

	private void buildEmailExclusionBase() {
		sql = new SelectSQL();
		sql.setFromTable("emailExclusions ee");

		addQueryField("emailExcluded", "ee.email", FilterType.String, true);

		defaultSort = "ee.email";
	}

	private void buildExceptionLogBase() {
		sql = new SelectSQL();
		sql.setFromTable("app_error_log as ael");

		addQueryField("errorLogID", "ael.id", FilterType.Number, true);
		addQueryField("errorLogCategory", "ael.category", FilterType.String, true);
		addQueryField("errorLogPriority", "ael.priority", FilterType.Number, true);
		addQueryField("errorLogStatus", "ael.status", FilterType.Enum);
		addQueryField("errorLogCreatedBy", "ael.createdBy", FilterType.Number);
		addQueryField("errorLogUpdatedBy", "ael.updatedBy", FilterType.Number);
		addQueryField("errorLogCreationDate", "ael.creationDate", FilterType.Date);
		addQueryField("errorLogUpdateDate", "ael.updateDate", FilterType.Date);
		addQueryField("errorLogMessage", "ael.message", FilterType.String);

		defaultSort = "ael.creationDate DESC";
	}

	private void buildRegistrationRequestsBase() {
		sql = new SelectSQL();
		sql.setFromTable("contractor_registration_request crr");

		addQueryField("requestID", "crr.id", FilterType.Number);

		addQueryField("requestedName", "crr.name", FilterType.String, true).addRenderer(
				"RequestNewContractor.action?newContractor={0}\">{1}", new String[] { "requestID", "requestedName" });
		addQueryField("requestedContact", "crr.contact", FilterType.String, true);
		addQueryField("requestedPhone", "crr.phone", FilterType.String, true);
		addQueryField("requestedEmail", "crr.email", FilterType.String);
		addQueryField("requestedTaxID", "crr.taxID", FilterType.String);
		addQueryField("requestedAddress", "crr.address", FilterType.String);
		addQueryField("requestedCity", "crr.city", FilterType.String);
		addQueryField("requestedState", "crr.state", FilterType.String);
		addQueryField("requestedZip", "crr.zip", FilterType.String);
		addQueryField("requestedCountry", "crr.country", FilterType.String);
		addQueryField("requestedNotes", "crr.notes", FilterType.String);
		addQueryField("requestedDeadline", "crr.deadline", FilterType.Date, true);
		addQueryField("requestedLastContactedByDate", "crr.lastContactDate", FilterType.String);
		addQueryField("requestedContactCount", "crr.contactCount", FilterType.Number);
		addQueryField("requestedMatchCount", "crr.matchCount", FilterType.Number);
		addQueryField("requestCreationDate", "crr.creationDate", FilterType.Date);
		addQueryField("requestStatus", "crr.status", FilterType.Enum);

		sql.addJoin("JOIN accounts op ON op.id = crr.requestedByID");
		addQueryField("requestedByOperatorID", "op.id", FilterType.Number);
		addQueryField("requestedByOperatorName", "op.name", FilterType.AccountName).addRenderer(
				"FacilitiesEdit.action?operator={0}\">{1}",
				new String[] { "requestedByOperatorID", "requestedByOperatorName" });

		leftJoinToUser("requestedBy", "crr.requestedByUserID");
		leftJoinToUser("contactedBy", "crr.lastContactedBy");

		leftJoinToAccount("requestedExisting", "crr.conID");
	}

	private void buildEmailSubscriptionBase() {
		// buildAccountBase();
		sql.addJoin("JOIN email_subscription es ON es.userID = u.id");

		addQueryField("emailSubscription", "es.subscription", FilterType.String, true);
		addQueryField("emailSubscriptionTimePeriod", "es.timePeriod", FilterType.String, true);
	}

	private void buildEmployeeBase() {
		// buildAccountBase();
		sql.addJoin("JOIN employee e ON a.id = e.accountID");

		addQueryField("employeeID", "e.id", FilterType.Number, true);
		addQueryField("employeeFirstName", "e.firstName", FilterType.String, true);
		addQueryField("employeeLastName", "e.lastName", FilterType.String, true);
		addQueryField("employeeTitle", "e.title", FilterType.String, true);
		addQueryField("employeeBirthDate", "e.birthDate", FilterType.Date);
		addQueryField("employeeHireDate", "e.hireDate", FilterType.Date);
		addQueryField("employeeEmail", "e.email", FilterType.String);
		addQueryField("employeePhone", "e.phone", FilterType.String);
		addQueryField("employeeLocation", "e.location", FilterType.String);
		addQueryField("employeeSSN", "e.ssn", FilterType.Number);
		addQueryField("employeeTwicExpiration", "e.twicExpiration", FilterType.Date);
		QueryField employeeClassification = addQueryField("employeeClassification", "e.classification", FilterType.Enum);
		employeeClassification.translate("EmployeeClassification", "description");
		addQueryField("employeeActive", "e.active", FilterType.Boolean);
	}

	private void buildOperatorBase() {
		// buildAccountBase();
		sql.addJoin("JOIN operators o ON a.id = o.id");
		sql.addWhere("a.type IN ('Operator','Corporate')");
		// replaceQueryField("accountName", "operatorName");
		availableFields.remove("accountType");

		joinToFacilities("operatorFacility", "opID", "a.id");
		joinToFacilities("corporateFacility", "corporateID", "a.id");
		joinToGeneralContractor("operatorContractor", "genID", "o.id");
	}

	private void buildResourceBase() {
		// buildAccountBase();

		sql.addJoin("JOIN operatorforms of ON a.id = of.opID");

		addQueryField("resourceID", "of.id", FilterType.Number, true);
		addQueryField("resourceOperatorAccountID", "of.opID", FilterType.Number, true);
		addQueryField("resourceName", "of.formName", FilterType.String, true);
		addQueryField("resourceParentResourceID", "of.parentID", FilterType.Number);
		addQueryField("resourceLocale", "of.locale", FilterType.String);

		defaultSort = "o.formName";
	}

	private void buildUserBase() {
		// buildAccountBase();
		sql.addJoin("JOIN users u ON u.accountID = a.id");

		addQueryField("userID", "u.id", FilterType.Number, true);
		addQueryField("userAccountID", "u.accountID", FilterType.Number, true);
		addQueryField("userName", "u.name", FilterType.String, true);
		addQueryField("userScreenName", "u.username", FilterType.String);
		addQueryField("userIsActive", "u.isActive", FilterType.String);
		addQueryField("userIsGroup", "u.isGroup", FilterType.String);
		addQueryField("userLastLogin", "u.lastLogin", FilterType.Date);

		joinToLoginLog("userLoginLog", "u.id");
	}

	private void buildUserAssignmentBase() {
		buildUserBase();
		sql.addJoin("JOIN user_assignment ua ON ua.userID = u.id");

		addQueryField("userAssignedCountry", "ua.country", FilterType.List, true);
		addQueryField("userAssignedState", "ua.state", FilterType.List, true);
		addQueryField("userAssignedPostalStart", "ua.postal_start", FilterType.String, true);
		addQueryField("userAssignedPostalEnd", "ua.postal_end", FilterType.String, true);
		addQueryField("userAssignedType", "ua.assignmentType", FilterType.Enum);

		joinToAuditType("userAssignedAuditType", "ua.auditTypeID");
		leftJoinToAccount("contractorAccount", "ua.conID");
	}

	private void buildUserGroupBase() {
		buildUserBase();
		sql.addJoin("JOIN usergroup ug ON u.id = ug.userID");

		leftJoinToUser("grp", "ug.groupID");
	}

	private void buildContractorAuditBase() {
		base = new QueryAccountContractorAudit();
		// sql = base.getSql();
		PermissionQueryBuilder permQuery = new PermissionQueryBuilder(permissions);
		sql.addWhere("1 " + permQuery.toString());

	}

	private void buildContractorTradeBase() {
		// buildContractorBase();

		sql.addJoin("contractor_trade parent ON parent.conID = a.id");
		sql.addJoin("ref_trade tParent ON tParent.id = parent.tradeID");
		sql.addJoin("contractor_trade child ON child.conID = a.id");
		sql.addJoin("ref_trade tChild ON tChild.id = child.tradeID");

		QueryField contractorTradeParentID = addQueryField("contractorTradeParentID", "tParent.id", FilterType.Number,
				true);
		contractorTradeParentID.translate("Trade", "name");
		addQueryField("contractorTradeParentIndexStart", "tParent.indexStart", FilterType.Number);
		addQueryField("contractorTradeParentIndexEnd", "tParent.indexEnd", FilterType.Number);

		QueryField contractorTradeChildID = addQueryField("contractorTradeChildID", "tChild.id", FilterType.Number,
				true);
		contractorTradeChildID.translate("Trade", "name");
		addQueryField("contractorTradeChildIndexStart", "tChild.indexStart", FilterType.Number);
		addQueryField("contractorTradeChildIndexEnd", "tChild.indexEnd", FilterType.Number);
	}

	private void buildContractorFeeBase() {
		// buildContractorBase();

		sql.addJoin("contractor_fee cf ON cf.conID = a.id");

		addQueryField("contractorFeeCurrentAmount", "cf.currentAmount", FilterType.Number, true);
		addQueryField("contractorFeeNewAmount", "cf.newAmount", FilterType.Number);

		joinToInvoiceFee("contractorFee", "cf.newLevel");
	}

	private void buildInvoiceBase() {
		// buildContractorBase();

		sql.addJoin("JOIN invoice i on i.accountID = a.id");

		addQueryField("invoiceID", "i.id", FilterType.Number, true);
		addQueryField("invoiceAmountApplied", "i.amountApplied", FilterType.Number);
		addQueryField("invoiceTotalAmount", "i.totalAmount", FilterType.Number, true);
		addQueryField("invoiceDueDate", "i.dueDate", FilterType.Date);
		addQueryField("invoiceStatus", "i.status", FilterType.Enum);
		addQueryField("invoiceCreationDate", "i.creationDate", FilterType.Date);
		addQueryField("invoiceTableType", "i.tableType", FilterType.String);
	}

	private void buildInvoiceItemBase() {
		buildInvoiceBase();

		sql.addJoin("JOIN invoice_item ii on ii.invoiceID = i.id");

		addQueryField("invoiceItemPaymentExpires", "ii.paymentExpires", FilterType.Date, true);
		addQueryField("invoiceItemAmount", "ii.amount", FilterType.Number, true);

		joinToInvoiceFee("invoiceItemFee", "ii.feeID");
	}

	private void buildEmployeeAssessmentBase() {
		buildEmployeeBase();

		sql.addJoin("assessment_result ar ON ar.employeeID = e.id");
		sql.addJoin("assessment_test test ON test.id = ar.assessmentTestID");
		sql.addJoin("accounts center ON center.id = test.assessmentCenterID");

		addQueryField("assessmentResultExpirationDate", "ar.expirationDate", FilterType.Date);

		addQueryField("assessmentTestDescription", "test.description", FilterType.String, true);
		addQueryField("assessmentTestQualificationType", "test.qualificationType", FilterType.String, true);
		addQueryField("assessmentTestQualificationMethod", "test.qualificationMethod", FilterType.String);

		addQueryField("assessmentCenterName", "center.name", FilterType.String);
	}

	private void buildContractorAuditOperatorBase() {
		buildContractorAuditBase();

		sql.addJoin("JOIN contractor_audit_operator cao ON cao.auditID = ca.id");
		addQueryField("auditOperatorID", "cao.id", FilterType.Number, true);
		addQueryField("auditOperatorStatus", "cao.status", FilterType.Enum, true);
		addQueryField("auditOperatorStatusChangedDate", "cao.statusChangedDate", FilterType.Date);
		addQueryField("auditOperatorVisible", "cao.visible", FilterType.Boolean);
		addQueryField("auditOperatorPercentComplete", "cao.percentComplete", FilterType.Number, true);

		leftJoinToAccount("caoAccount", "cao.opID");
	}

	private void buildContractorAuditOperatorWorkflowBase() {
		buildContractorAuditOperatorBase();

		sql.addJoin("JOIN contractor_audit_operator_workflow cao ON cao.id = caow.caoID");

		addQueryField("auditOperatorWorkflowStatus", "caow.status", FilterType.Enum, true);
		addQueryField("auditOperatorWorkflowPreviousStatus", "caow.previousStatus", FilterType.Enum, true);
		addQueryField("auditOperatorWorkflowCreationDate", "caow.creationDate", FilterType.Date, true);
		leftJoinToUser("auditOperatorWorkflowCreatedBy", "caow.createdBy");
	}

	private void buildContractorAuditDataBase() {
		buildContractorAuditOperatorBase();

		sql.addJoin("JOIN pqfdata pd on pd.auditID = ca.id");

		addQueryField("auditDataAnswer", "pd.answer", FilterType.String, true);
		addQueryField("auditDataDateVerified", "pd.dateVerified", FilterType.Date);
		addQueryField("auditDataQuestionID", "pd.questionID", FilterType.Number);
		QueryField auditDataQuestion = addQueryField("auditDataQuestion", "pd.questionID", FilterType.String, true);
		auditDataQuestion.translate("AuditQuestion", "name");
		addQueryField("auditDataUpdateDate", "pd.updateDate", FilterType.Date);
	}

	private QueryField joinToAuditType(String joinAlias, String foreignKey) {
		joins.put(joinAlias, "JOIN audit_type " + joinAlias + " ON " + joinAlias + ".id = " + foreignKey);
		addQueryField(joinAlias + "ID", foreignKey, FilterType.Number, joinAlias, true);
		QueryField auditTypeName = addQueryField(joinAlias + "Name", foreignKey, FilterType.String, joinAlias, true);
		auditTypeName.translate("AuditType", "name");

		addQueryField(joinAlias + "ClassType", joinAlias + ".classType", FilterType.Enum, joinAlias);
		addQueryField(joinAlias + "IsScheduled", joinAlias + ".isScheduled", FilterType.Boolean, joinAlias);
		addQueryField(joinAlias + "HasAuditor", joinAlias + ".hasAuditor", FilterType.Boolean, joinAlias);
		addQueryField(joinAlias + "Scorable", joinAlias + ".scoreable", FilterType.Boolean, joinAlias);

		return auditTypeName;
	}

	private void joinToFacilities(String joinAlias, String tableKey, String foreignKey) {
		joins.put(joinAlias, "LEFT JOIN facilities " + joinAlias + " ON " + joinAlias + "." + tableKey + " = "
				+ foreignKey);

		addQueryField(joinAlias + "OperatorID", joinAlias + ".opID", FilterType.Number, joinAlias, true);
		addQueryField(joinAlias + "CorporateID", joinAlias + ".corporateID", FilterType.Number, joinAlias, true);

		leftJoinToAccount("operatorChild", joinAlias + ".opID");
		leftJoinToAccount("corporateParent", joinAlias + ".corporateID");
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
		joins.put(joinAlias, "LEFT JOIN accounts " + joinAlias + " ON " + joinAlias + ".id = " + foreignKey);
		addQueryField(joinAlias + "ID", joinAlias + ".id", FilterType.Number, joinAlias, true);
		addQueryField(joinAlias + "Name", joinAlias + ".name", FilterType.AccountName, joinAlias, true);
		addQueryField(joinAlias + "Status", joinAlias + ".status", FilterType.Enum, joinAlias);
		addQueryField(joinAlias + "Type", joinAlias + ".type", FilterType.Enum, joinAlias);
	}

	private void leftJoinToUser(String joinAlias, String foreignKey) {
		joins.put(joinAlias, "LEFT JOIN users " + joinAlias + " ON " + joinAlias + ".id = " + foreignKey);
		addQueryField(joinAlias + "UserID", joinAlias + ".id", FilterType.Number, joinAlias, true);
		addQueryField(joinAlias + "UserName", joinAlias + ".name", FilterType.String, joinAlias, true).addRenderer(
				"UsersManage.action?user={0}\">{1}", new String[] { joinAlias + "UserID", joinAlias + "UserName" });

		addQueryField(joinAlias + "Phone", joinAlias + ".phone", FilterType.String, joinAlias);
		addQueryField(joinAlias + "Email", joinAlias + ".email", FilterType.String, joinAlias);
	}

	private void leftJoinToEmailTemplate(String joinAlias, String foreignKey) {
		joins.put(joinAlias, "LEFT JOIN email_template " + joinAlias + " ON " + joinAlias + ".id = " + foreignKey);
		addQueryField(joinAlias + "ID", joinAlias + ".id", FilterType.Number, joinAlias, true);
		addQueryField(joinAlias + "Name", joinAlias + ".templateName", FilterType.String, joinAlias, true);
	}


}
