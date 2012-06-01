package com.picsauditing.report;

/**
 * Leftover code by Trevor and Michael. This needs to be refactored into
 * entities or wherever else it needs to go. Otherwise it should be thrown away.
 */
public class Todo {
	// private SelectSQL sql = new SelectSQL();
	// private Map<String, String> joins = new HashMap<String, String>();
	// private String defaultSort = null;
	//
	// private Field addQueryField(String dataIndex, String sql, FilterType
	// filterType, String requireJoin) {
	// return addQueryField(dataIndex, sql, filterType, false);
	// }
	//
	// private Field addQueryField(String dataIndex, String sql, FilterType
	// filterType, boolean makeDefault) {
	// Field field = new Field(dataIndex, sql, filterType, makeDefault);
	// return field;
	// }
	//
	// private Field addQueryField(String dataIndex, String sql, FilterType
	// filterType) {
	// return addQueryField(dataIndex, sql, filterType, false);
	// }
	//	
	// private Field addQueryField(String dataIndex, String sql, FilterType
	// filterType, String requireJoin,
	// boolean makeDefault) {
	// return addQueryField(dataIndex, sql, filterType, false);
	// }
	//
	// private void buildEmailBase() {
	// sql = new SelectSQL();
	// sql.setFromTable("email_queue eq");
	//
	// addQueryField("emailID", "eq.emailID", FilterType.Integer, true);
	// addQueryField("emailStatus", "eq.emailStatus", FilterType.Enum);
	// addQueryField("emailFromAddress", "eq.emailFromAddress",
	// FilterType.String, true);
	// addQueryField("emailToAddresses", "eq.emailToAddresses",
	// FilterType.String, true);
	// addQueryField("emailCcAddresses", "eq.emailCcAddresses",
	// FilterType.String);
	// addQueryField("emailSubject", "eq.subject", FilterType.String, true);
	// addQueryField("emailPriority", "eq.priority", FilterType.Integer);
	// addQueryField("emailCreationDate", "eq.creationDate", FilterType.Date);
	// addQueryField("emailSentDate", "eq.sentDate", FilterType.Date);
	//
	// // leftJoinToAccount("emailContractor", "eq.conID");
	// leftJoinToEmailTemplate("et", "emailTemplate");
	//
	// defaultSort = "eq.priority DESC, eq.emailID";
	// }
	//
	// private void buildEmailExclusionBase() {
	// sql = new SelectSQL();
	// sql.setFromTable("emailExclusions ee");
	//
	// addQueryField("emailExcluded", "ee.email", FilterType.String, true);
	//
	// defaultSort = "ee.email";
	// }
	//
	// private void buildExceptionLogBase() {
	// sql = new SelectSQL();
	// sql.setFromTable("app_error_log as ael");
	//
	// addQueryField("errorLogID", "ael.id", FilterType.Integer, true);
	// addQueryField("errorLogCategory", "ael.category", FilterType.String,
	// true);
	// addQueryField("errorLogPriority", "ael.priority", FilterType.Integer,
	// true);
	// addQueryField("errorLogStatus", "ael.status", FilterType.Enum);
	// addQueryField("errorLogCreatedBy", "ael.createdBy", FilterType.Integer);
	// addQueryField("errorLogUpdatedBy", "ael.updatedBy", FilterType.Integer);
	// addQueryField("errorLogCreationDate", "ael.creationDate",
	// FilterType.Date);
	// addQueryField("errorLogUpdateDate", "ael.updateDate", FilterType.Date);
	// addQueryField("errorLogMessage", "ael.message", FilterType.String);
	//
	// defaultSort = "ael.creationDate DESC";
	// }
	//
	// private void buildEmailSubscriptionBase() {
	// // buildAccountBase();
	// sql.addJoin("JOIN email_subscription es ON es.userID = u.id");
	//
	// addQueryField("emailSubscription", "es.subscription", FilterType.String,
	// true);
	// addQueryField("emailSubscriptionTimePeriod", "es.timePeriod",
	// FilterType.String, true);
	// }
	//
	// private void buildEmployeeBase() {
	// // buildAccountBase();
	// sql.addJoin("JOIN employee e ON a.id = e.accountID");
	//
	// addQueryField("employeeID", "e.id", FilterType.Integer, true);
	// addQueryField("employeeFirstName", "e.firstName", FilterType.String,
	// true);
	// addQueryField("employeeLastName", "e.lastName", FilterType.String, true);
	// addQueryField("employeeTitle", "e.title", FilterType.String, true);
	// addQueryField("employeeBirthDate", "e.birthDate", FilterType.Date);
	// addQueryField("employeeHireDate", "e.hireDate", FilterType.Date);
	// addQueryField("employeeEmail", "e.email", FilterType.String);
	// addQueryField("employeePhone", "e.phone", FilterType.String);
	// addQueryField("employeeLocation", "e.location", FilterType.String);
	// addQueryField("employeeSSN", "e.ssn", FilterType.Integer);
	// addQueryField("employeeTwicExpiration", "e.twicExpiration",
	// FilterType.Date);
	// Field employeeClassification = addQueryField("employeeClassification",
	// "e.classification", FilterType.Enum);
	// employeeClassification.translate("EmployeeClassification",
	// "description");
	// addQueryField("employeeActive", "e.active", FilterType.Boolean);
	// }
	//
	// private void buildOperatorBase() {
	// // buildAccountBase();
	// sql.addJoin("JOIN operators o ON a.id = o.id");
	// sql.addWhere("a.type IN ('Operator','Corporate')");
	// // replaceQueryField("accountName", "operatorName");
	// // availableFields.remove("accountType");
	//
	// joinToFacilities("operatorFacility", "opID", "a.id");
	// joinToFacilities("corporateFacility", "corporateID", "a.id");
	// joinToGeneralContractor("operatorContractor", "genID", "o.id");
	// }
	//
	// private void buildResourceBase() {
	// // buildAccountBase();
	//
	// sql.addJoin("JOIN operatorforms of ON a.id = of.opID");
	//
	// addQueryField("resourceID", "of.id", FilterType.Integer, true);
	// addQueryField("resourceOperatorAccountID", "of.opID", FilterType.Integer,
	// true);
	// addQueryField("resourceName", "of.formName", FilterType.String, true);
	// addQueryField("resourceParentResourceID", "of.parentID",
	// FilterType.Integer);
	// addQueryField("resourceLocale", "of.locale", FilterType.String);
	//
	// defaultSort = "o.formName";
	// }
	//
	// private void buildUserBase() {
	// // buildAccountBase();
	// sql.addJoin("JOIN users u ON u.accountID = a.id");
	//
	// addQueryField("userID", "u.id", FilterType.Integer, true);
	// addQueryField("userAccountID", "u.accountID", FilterType.Integer, true);
	// addQueryField("userName", "u.name", FilterType.String, true);
	// addQueryField("userScreenName", "u.username", FilterType.String);
	// addQueryField("userIsActive", "u.isActive", FilterType.String);
	// addQueryField("userIsGroup", "u.isGroup", FilterType.String);
	// addQueryField("userLastLogin", "u.lastLogin", FilterType.Date);
	//
	// joinToLoginLog("userLoginLog", "u.id");
	// }
	//
	// private void buildUserAssignmentBase() {
	// buildUserBase();
	// sql.addJoin("JOIN user_assignment ua ON ua.userID = u.id");
	//
	// addQueryField("userAssignedCountry", "ua.country", FilterType.List,
	// true);
	// addQueryField("userAssignedState", "ua.state", FilterType.List, true);
	// addQueryField("userAssignedPostalStart", "ua.postal_start",
	// FilterType.String, true);
	// addQueryField("userAssignedPostalEnd", "ua.postal_end",
	// FilterType.String, true);
	// addQueryField("userAssignedType", "ua.assignmentType", FilterType.Enum);
	//
	// // joinToAuditType("userAssignedAuditType", "ua.auditTypeID");
	// // leftJoinToAccount("contractorAccount", "ua.conID");
	// }
	//
	// private void buildUserGroupBase() {
	// buildUserBase();
	// sql.addJoin("JOIN usergroup ug ON u.id = ug.userID");
	//
	// // leftJoinToUser("grp", "ug.groupID");
	// }
	//
	// private void buildContractorTradeBase() {
	// // buildContractorBase();
	//
	// sql.addJoin("contractor_trade parent ON parent.conID = a.id");
	// sql.addJoin("ref_trade tParent ON tParent.id = parent.tradeID");
	// sql.addJoin("contractor_trade child ON child.conID = a.id");
	// sql.addJoin("ref_trade tChild ON tChild.id = child.tradeID");
	//
	// Field contractorTradeParentID = addQueryField("contractorTradeParentID",
	// "tParent.id", FilterType.Integer,
	// true);
	// contractorTradeParentID.translate("Trade", "name");
	// addQueryField("contractorTradeParentIndexStart", "tParent.indexStart",
	// FilterType.Integer);
	// addQueryField("contractorTradeParentIndexEnd", "tParent.indexEnd",
	// FilterType.Integer);
	//
	// Field contractorTradeChildID = addQueryField("contractorTradeChildID",
	// "tChild.id", FilterType.Integer,
	// true);
	// contractorTradeChildID.translate("Trade", "name");
	// addQueryField("contractorTradeChildIndexStart", "tChild.indexStart",
	// FilterType.Integer);
	// addQueryField("contractorTradeChildIndexEnd", "tChild.indexEnd",
	// FilterType.Integer);
	// }
	//
	// private void buildContractorFeeBase() {
	// // buildContractorBase();
	//
	// sql.addJoin("contractor_fee cf ON cf.conID = a.id");
	//
	// addQueryField("contractorFeeCurrentAmount", "cf.currentAmount",
	// FilterType.Integer, true);
	// addQueryField("contractorFeeNewAmount", "cf.newAmount",
	// FilterType.Integer);
	//
	// joinToInvoiceFee("contractorFee", "cf.newLevel");
	// }
	//
	// private void buildInvoiceBase() {
	// // buildContractorBase();
	//
	// sql.addJoin("JOIN invoice i on i.accountID = a.id");
	//
	// addQueryField("invoiceID", "i.id", FilterType.Integer, true);
	// addQueryField("invoiceAmountApplied", "i.amountApplied",
	// FilterType.Integer);
	// addQueryField("invoiceTotalAmount", "i.totalAmount", FilterType.Integer,
	// true);
	// addQueryField("invoiceDueDate", "i.dueDate", FilterType.Date);
	// addQueryField("invoiceStatus", "i.status", FilterType.Enum);
	// addQueryField("invoiceCreationDate", "i.creationDate", FilterType.Date);
	// addQueryField("invoiceTableType", "i.tableType", FilterType.String);
	// }
	//
	// private void buildInvoiceItemBase() {
	// buildInvoiceBase();
	//
	// sql.addJoin("JOIN invoice_item ii on ii.invoiceID = i.id");
	//
	// addQueryField("invoiceItemPaymentExpires", "ii.paymentExpires",
	// FilterType.Date, true);
	// addQueryField("invoiceItemAmount", "ii.amount", FilterType.Integer,
	// true);
	//
	// joinToInvoiceFee("invoiceItemFee", "ii.feeID");
	// }
	//
	// private void buildEmployeeAssessmentBase() {
	// buildEmployeeBase();
	//
	// sql.addJoin("assessment_result ar ON ar.employeeID = e.id");
	// sql.addJoin("assessment_test test ON test.id = ar.assessmentTestID");
	// sql.addJoin("accounts center ON center.id = test.assessmentCenterID");
	//
	// addQueryField("assessmentResultExpirationDate", "ar.expirationDate",
	// FilterType.Date);
	//
	// addQueryField("assessmentTestDescription", "test.description",
	// FilterType.String, true);
	// addQueryField("assessmentTestQualificationType",
	// "test.qualificationType", FilterType.String, true);
	// addQueryField("assessmentTestQualificationMethod",
	// "test.qualificationMethod", FilterType.String);
	//
	// addQueryField("assessmentCenterName", "center.name", FilterType.String);
	// }
	//
	// private void buildContractorAuditOperatorWorkflowBase() {
	// // buildContractorAuditOperatorBase();
	//
	// sql.addJoin("JOIN contractor_audit_operator_workflow cao ON cao.id = caow.caoID");
	//
	// addQueryField("auditOperatorWorkflowStatus", "caow.status",
	// FilterType.Enum, true);
	// addQueryField("auditOperatorWorkflowPreviousStatus",
	// "caow.previousStatus", FilterType.Enum, true);
	// addQueryField("auditOperatorWorkflowCreationDate", "caow.creationDate",
	// FilterType.Date, true);
	// // leftJoinToUser("auditOperatorWorkflowCreatedBy", "caow.createdBy");
	// }
	//
	// private void joinToFacilities(String joinAlias, String tableKey, String
	// foreignKey) {
	// joins.put(joinAlias, "LEFT JOIN facilities " + joinAlias + " ON " +
	// joinAlias + "." + tableKey + " = "
	// + foreignKey);
	//
	// addQueryField(joinAlias + "OperatorID", joinAlias + ".opID",
	// FilterType.Integer, joinAlias, true);
	// addQueryField(joinAlias + "CorporateID", joinAlias + ".corporateID",
	// FilterType.Integer, joinAlias, true);
	//
	// // leftJoinToAccount("operatorChild", joinAlias + ".opID");
	// // leftJoinToAccount("corporateParent", joinAlias + ".corporateID");
	// }
	//
	// private void joinToGeneralContractor(String joinAlias, String tableKey,
	// String foreignKey) {
	// joins.put(joinAlias, "LEFT JOIN generalcontractors " + joinAlias + " ON "
	// + joinAlias + "." + tableKey + " = "
	// + foreignKey);
	//
	// addQueryField(joinAlias + "ContractorID", joinAlias + ".subID",
	// FilterType.Integer, joinAlias, true);
	// addQueryField(joinAlias + "OperatorID", joinAlias + ".genID",
	// FilterType.Integer, joinAlias, true);
	// addQueryField(joinAlias + "FlagLastUpdated", joinAlias +
	// ".flagLastUpdated", FilterType.Date, joinAlias);
	// addQueryField(joinAlias + "Flag", joinAlias + ".flag", FilterType.String,
	// joinAlias, true);
	//
	// // leftJoinToAccount(joinAlias + "Operator", joinAlias + ".genID");
	// }
	//
	// private void joinToInvoiceFee(String joinAlias, String foreignKey) {
	// joins.put(joinAlias, "JOIN invoice_fee " + joinAlias + " ON " + joinAlias
	// + ".id = " + foreignKey);
	//
	// addQueryField(joinAlias + "ID", joinAlias + ".id", FilterType.Integer,
	// joinAlias, true);
	// addQueryField(joinAlias + "MaxFacilities", joinAlias + ".maxFacilities",
	// FilterType.Integer, joinAlias, true);
	// }
	//
	// private void joinToLoginLog(String joinAlias, String foreignKey) {
	// joins.put(joinAlias, "JOIN loginlog " + joinAlias + " ON " + joinAlias +
	// ".userID = " + foreignKey);
	// addQueryField(joinAlias + "UserID", foreignKey, FilterType.Integer,
	// joinAlias, true);
	//
	// addQueryField(joinAlias + "AdminID", joinAlias + ".adminID",
	// FilterType.Integer, joinAlias, true);
	// addQueryField(joinAlias + "LoginDate", joinAlias + ".loginDate",
	// FilterType.Date, joinAlias);
	// addQueryField(joinAlias + "RemoteAccess", joinAlias + ".remoteAccess",
	// FilterType.String, joinAlias);
	// }
	//
	// private void leftJoinToEmailTemplate(String joinAlias, String foreignKey)
	// {
	// joins.put(joinAlias, "LEFT JOIN email_template " + joinAlias + " ON " +
	// joinAlias + ".id = " + foreignKey);
	// addQueryField(joinAlias + "ID", joinAlias + ".id", FilterType.Integer,
	// joinAlias, true);
	// addQueryField(joinAlias + "Name", joinAlias + ".templateName",
	// FilterType.String, joinAlias, true);
	// }
	//
	// private void joinToContractorWatch(String joinAlias, String foreignKey) {
	// joins.put(joinAlias, "LEFT JOIN contractor_watch " + joinAlias + " ON " +
	// joinAlias + ".conID = " + foreignKey);
	//
	// addQueryField(joinAlias + "ContractorID", foreignKey, FilterType.Integer,
	// joinAlias, true);
	// addQueryField(joinAlias + "UserID", joinAlias + ".userID",
	// FilterType.Integer, joinAlias, true);
	// }
	//
	// private void leftJoinToEmailQueue(String joinAlias, String foreignKey) {
	// joins.put(joinAlias, "JOIN email_queue " + joinAlias + " ON " + joinAlias
	// + ".conID = " + foreignKey);
	// addQueryField(joinAlias + "ContractorID", foreignKey, FilterType.Integer,
	// joinAlias, true);
	//
	// addQueryField(joinAlias + "CreationDate", joinAlias + ".creationDate",
	// FilterType.Date, joinAlias, true);
	// addQueryField(joinAlias + "SentDate", joinAlias + ".sentDate",
	// FilterType.Date, joinAlias, true);
	// addQueryField(joinAlias + "CreatedBy", joinAlias + ".createdBy",
	// FilterType.Integer, joinAlias);
	// addQueryField(joinAlias + "ViewableBy", joinAlias + ".viewableBy",
	// FilterType.Integer, joinAlias);
	// addQueryField(joinAlias + "Subject", joinAlias + ".subject",
	// FilterType.String, joinAlias);
	// addQueryField(joinAlias + "TemplateID", joinAlias + ".templateID",
	// FilterType.Integer, joinAlias);
	// addQueryField(joinAlias + "Status", joinAlias + ".status",
	// FilterType.Enum, joinAlias);
	// }
	//
	// private void joinToFlagCriteriaContractor(String joinAlias, String
	// foreignKey) {
	// joins.put(joinAlias, "JOIN flag_criteria_contractor " + joinAlias +
	// " ON " + joinAlias + ".conID = "
	// + foreignKey);
	// // addQueryField(joinAlias + "ContractorID", foreignKey,
	// FilterType.Number, true);
	//
	// addQueryField(joinAlias + "ID", joinAlias + ".id", FilterType.Integer,
	// joinAlias);
	// addQueryField(joinAlias + "CriteriaID", joinAlias + ".criteriaID",
	// FilterType.Integer, joinAlias);
	// addQueryField(joinAlias + "Answer", joinAlias + ".answer",
	// FilterType.String, joinAlias);
	// }
}