package com.picsauditing.report;

import java.sql.SQLException;
import java.util.Calendar;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.apache.commons.beanutils.BasicDynaBean;
import org.apache.commons.lang.StringUtils;
import org.jboss.util.Strings;
import org.json.simple.JSONObject;
import org.json.simple.JSONValue;

import com.picsauditing.access.Permissions;
import com.picsauditing.dao.BasicDAO;
import com.picsauditing.jpa.entities.Report;
import com.picsauditing.search.Database;
import com.picsauditing.search.SelectSQL;
import com.picsauditing.util.PermissionQueryBuilder;

/**
 * TODO class refactoring This class is probably going to get quite large. Our intention was never to have all of it in
 * a single class. but until we write this out it was hard to see how to layout the class. In other words, we expect
 * there to be class refactoring later on.
 */
public class QueryRunner {
	private SelectSQL sql = new SelectSQL();
	private Map<String, QueryField> availableFields = new HashMap<String, QueryField>();
	private Map<String, String> joins = new HashMap<String, String>();
	private Permissions permissions;
	private String defaultSort = null;
	private int allRows = 0;
	private List<SortableField> columns;
	private BasicDAO dao;
	private Report report;

	public QueryRunner(Report report, Permissions permissions, BasicDAO dao) {
		this.permissions = permissions;
		this.dao = dao;
		this.report = report;
		
		buildBase(report.getBase());
	}

	public QueryData run() throws SQLException {
		Database db = new Database();
		long queryTime = Calendar.getInstance().getTimeInMillis();
		List<BasicDynaBean> rows = db.select(sql.toString(), true);
		queryTime = Calendar.getInstance().getTimeInMillis() - queryTime;
		allRows = db.getAllRows();
		QueryData data = new QueryData(columns, rows);
		if (queryTime > 1000) {
			System.out.println("Slow Query: " + sql.toString());
			System.out.println("Time to query: " + queryTime + " ms");
		}
		return data;
	}

	public SelectSQL buildQuery(boolean subQuery) {
		QueryCommand command = createCommandFromReportParameters(report.getParameters());
		QueryCommand devCommand = createCommandFromReportParameters(report.getDevParams());
		
		// TODO: merge commands
//		command = mergeCommands(command, devCommand);
		
		prefillColumns(command);
		addGroupBy(command);
		addHaving(command);

		removeObsoleteColumns(command);
		addLeftJoins(command);

		addOrderBy(command);

		if (!subQuery) {
			// We may need to move this to a class field
			sql.setSQL_CALC_FOUND_ROWS(true);
			addLimits(command);
		}

		addRuntimeFilters(command);

		return sql;
	}
	
//	private QueryCommand mergeCommands(QueryCommand command, QueryCommand devCommand) {
//		List<Sortable>command.getColumns()
//	}

	private void prefillColumns(QueryCommand command) {
		if (columns == null || columns.isEmpty())
			columns = command.getColumns();
		if (columns.size() == 0) {
			columns.clear();
			for (String fieldName : availableFields.keySet()) {
				SortableField column = new SortableField();
				column.field = fieldName;
				columns.add(column);
			}
		}
	}

	private void removeObsoleteColumns(QueryCommand command) {
		Iterator<SortableField> iterator = columns.iterator();
		while (iterator.hasNext()) {
			SortableField column = iterator.next();
			if (!availableFields.containsKey(column.field))
				iterator.remove();
		}
	}

	private void addRuntimeFilters(QueryCommand command) {
		if (command.getFilters().size() == 0) {
			return;
		}

		String where = command.getFilterExpression();
		if (where == null || Strings.isEmpty(where)) {
			where = "";
			for (int i = 0; i < command.getFilters().size(); i++) {
				where += "{" + i + "} AND ";
			}
			where = StringUtils.removeEnd(where, " AND ");
		}

		for (int i = 0; i < command.getFilters().size(); i++) {
			QueryFilter queryFilter = command.getFilters().get(i);

			if (queryFilter.getOperator().equals(QueryFilterOperator.InReport)) {
				Report subReport = dao.find(Report.class, Integer.parseInt(queryFilter.getValue()));
				QueryRunner subRunner = new QueryRunner(subReport, permissions, dao);
				SelectSQL subSql = subRunner.buildQuery(true);
				queryFilter.setValue(subSql.toString());
			}

			String filterExp = queryFilter.toExpression(availableFields);
			where = where.replace("{" + i + "}", filterExp);
		}
	}

	private void addLeftJoins(QueryCommand command) {
		Set<String> addedJoins = new HashSet<String>();
		for (SortableField column : columns) {
			if (availableFields.keySet().contains(column.field)) {
				QueryField availableField = availableFields.get(column.field);
				if (availableField.requiresJoin()) {
					if (!addedJoins.contains(availableField.requireJoin)) {
						sql.addJoin(this.joins.get(availableField.requireJoin));
						addedJoins.add(availableField.requireJoin);
					}
				}
				sql.addField(column.toSQL(availableFields) + " AS " + column.field);
				// TODO: Think about case/if (IF function or CASE statement) and
				// calculated fields (2 fields coming out with a result)
			}
		}
	}

	private void addOrderBy(QueryCommand command) {
		if (command.getOrderBy().size() == 0) {
			sql.addOrderBy(defaultSort);
			return;
		}

		for (SortableField field : command.getOrderBy()) {
			String orderBy = field.field;
			if (!columns.contains(field.field))
				orderBy = availableFields.get(field.field).sql;
			if (!field.ascending)
				orderBy += " DESC";
			sql.addOrderBy(orderBy);
		}
	}

	private void addGroupBy(QueryCommand command) {
		if (command.getGroupBy().size() == 0) {
			return;
		}

		for (SortableField field : command.getGroupBy()) {
			String groupBy = field.toSQL(availableFields);
			sql.addGroupBy(groupBy);
		}

		addTotalField();
	}

	private void addHaving(QueryCommand command) {
		if (command.getHaving() == null)
			return;

		String having = command.getHaving().toSQL(availableFields);
		sql.setHavingClause(having);

		addTotalField();
	}

	private void addTotalField() {
		addQueryField("total", null);
		SortableField total = new SortableField();
		total.field = "total";
		total.function = QueryFunction.Count;
		columns.add(total);
	}

	private void addLimits(QueryCommand command) {
		if (command.getPage() > 1)
			sql.setStartRow((command.getPage() - 1) * command.getRowsPerPage());
		sql.setLimit(command.getRowsPerPage());
	}

	private void buildBase(QueryBase base) {
		switch (base) {
		case Operators:
			buildOperatorBase();
			break;
		case Resources:
			buildResourceBase();
			break;
		case Contractors:
			buildContractorBase();
			break;
		case ContractorAudits:
			buildContractorAuditBase();
			break;
		case ContractorAuditData:
			buildContractorAuditDataBase();
			break;
		case ContractorAuditOperators:
			buildContractorAuditOperatorBase();
			break;
		case ContractorAuditOperatorWorkflows:
			buildContractorAuditOperatorWorkflowBase();
			break;
		case ContractorTrades:
			buildContractorTradeBase();
			break;
		case Users:
			buildUserBase();
			break;
		case UserGroups:
			buildUserGroupBase();
			break;
		case UserAssignments:
			buildUserAssignmentBase();
			break;
		case Emails:
			buildEmailBase();
			break;
		case EmailExclusions:
			buildEmailExclusionBase();
			break;
		case EmailSubscriptions:
			buildEmailSubscriptionBase();
			break;
		case Employees:
			buildEmployeeBase();
			break;
		case EmployeeAssessments:
			buildEmployeeAssessmentBase();
			break;
		case ExceptionLogs:
			buildExceptionLogBase();
			break;
		case Invoices:
			buildInvoiceBase();
			break;
		case InvoiceItems:
			buildInvoiceItemBase();
			break;
		case RegistrationRequests:
			buildRegistrationRequestsBase();
			break;
		default:
			buildAccountBase();
			break;
		}
	}

	private QueryField addQueryField(String dataIndex, String sql) {
		QueryField field = new QueryField(dataIndex, sql);
		availableFields.put(dataIndex, field);
		return field;
	}

	private QueryField replaceQueryField(String source, String target) {
		QueryField field = availableFields.remove(source);
		field.dataIndex = target;
		availableFields.put(target, field);
		return field;
	}

	private void buildAccountBase() {
		sql = new SelectSQL();
		sql.setFromTable("accounts a");

		addQueryField("accountID", "a.id");
		addQueryField("accountName", "a.name");
		addQueryField("accountStatus", "a.status");
		addQueryField("accountType", "a.type");
		addQueryField("accountPhone", "a.phone");
		addQueryField("accountFax", "a.fax");
		addQueryField("accountCreationDate", "a.creationDate").type(FieldType.Date);
		addQueryField("accountAddress", "a.address");
		addQueryField("accountCity", "a.city");
		addQueryField("accountState", "a.state");
		addQueryField("accountZip", "a.zip");
		addQueryField("accountCountry", "a.country");
		addQueryField("accountWebsite", "a.web_url").addRenderer("http://{0}\">{0}", new String[] { "accountWebsite" });
		addQueryField("accountDBAName", "a.dbaName");
		addQueryField("accountNameIndex", "a.nameIndex");
		addQueryField("accountReason", "a.reason");
		addQueryField("accountOnsite", "a.onsiteServices");
		addQueryField("accountOffsite", "a.offsiteServices");
		addQueryField("accountTransportation", "a.transportationServices");
		addQueryField("accountMaterialSupplier", "a.materialSupplier");

		leftJoinToUser("accountContact", "a.contactID");

		defaultSort = "a.nameIndex";
	}

	private void buildEmailBase() {
		sql = new SelectSQL();
		sql.setFromTable("emailQueue eq");

		addQueryField("emailID", "eq.emailID");
		addQueryField("emailStatus", "eq.emailStatus");
		addQueryField("emailFromAddress", "eq.emailFromAddress");
		addQueryField("emailToAddresses", "eq.emailToAddresses");
		addQueryField("emailCcAddresses", "eq.emailCcAddresses");
		addQueryField("emailSubject", "eq.subject");
		addQueryField("emailPriority", "eq.priority");
		addQueryField("emailCreationDate", "eq.creationDate");
		addQueryField("emailSentDate", "eq.sentDate");

		leftJoinToAccount("emailContractor", "eq.conID");
		leftJoinToEmailTemplate("et", "emailTemplate");

		defaultSort = "eq.priority DESC, eq.emailID";
	}

	private void buildEmailExclusionBase() {
		sql = new SelectSQL();
		sql.setFromTable("emailExclusions ee");

		addQueryField("emailExcluded", "ee.email");

		defaultSort = "ee.email";
	}

	private void buildExceptionLogBase() {
		sql = new SelectSQL();
		sql.setFromTable("app_error_log as ael");

		addQueryField("errorLogID", "ael.id");
		addQueryField("errorLogCategory", "ael.category");
		addQueryField("errorLogPriority", "ael.priority");
		addQueryField("errorLogStatus", "ael.status");
		addQueryField("errorLogCreatedBy", "ael.createdBy");
		addQueryField("errorLogUpdatedBy", "ael.updatedBy");
		addQueryField("errorLogCreationDate", "ael.creationDate");
		addQueryField("errorLogUpdateDate", "ael.updateDate");
		addQueryField("errorLogMessage", "ael.message");

		defaultSort = "ael.creationDate DESC";
	}

	private void buildRegistrationRequestsBase() {
		sql = new SelectSQL();
		sql.setFromTable("contractor_registration_request crr");

		addQueryField("requestID", "crr.id");

		addQueryField("requestedName", "crr.name").addRenderer("RequestNewContractor.action?newContractor={0}\">{1}",
				new String[] { "requestID", "requestedName" });
		addQueryField("requestedContact", "crr.contact");
		addQueryField("requestedPhone", "crr.phone");
		addQueryField("requestedEmail", "crr.email");
		addQueryField("requestedTaxID", "crr.taxID");
		addQueryField("requestedAddress", "crr.address");
		addQueryField("requestedCity", "crr.city");
		addQueryField("requestedState", "crr.state");
		addQueryField("requestedZip", "crr.zip");
		addQueryField("requestedCountry", "crr.country");
		addQueryField("requestedNotes", "crr.notes");
		addQueryField("requestedDeadline", "crr.deadline").type(FieldType.Date);
		addQueryField("requestedLastContactedByDate", "crr.lastContactDate").type(FieldType.Date);
		addQueryField("requestedContactCount", "crr.contactCount");
		addQueryField("requestedMatchCount", "crr.matchCount");
		addQueryField("requestCreationDate", "crr.creationDate").type(FieldType.Date);
		addQueryField("requestStatus", "crr.status");

		sql.addJoin("JOIN accounts op ON op.id = crr.requestedByID");
		addQueryField("requestedByOperatorID", "op.id");
		addQueryField("requestedByOperatorName", "op.name").addRenderer("FacilitiesEdit.action?operator={0}\">{1}",
				new String[] { "requestedByOperatorID", "requestedByOperatorName" });

		leftJoinToUser("requestedBy", "crr.requestedByUserID");
		leftJoinToUser("contactedBy", "crr.lastContactedBy");

		leftJoinToAccount("requestedExisting", "crr.conID");
	}

	private void buildContractorBase() {
		buildAccountBase();

		availableFields.get("accountName").addRenderer(new Renderer("ContractorView.action?id={0}\">{1}",
				new String[] { "accountID", "accountName" }));

		sql.addJoin("JOIN contractor_info c ON a.id = c.id");
		sql.addWhere("a.type='Contractor'");
		availableFields.remove("accountType");
		replaceQueryField("accountName", "contractorName");
		addQueryField("contractorRiskLevel", "c.riskLevel");
		addQueryField("contractorSafetyRisk", "c.safetyRisk");
		addQueryField("contractorProductRisk", "c.productRisk");
		addQueryField("contractorMainTrade", "c.main_trade");
		addQueryField("contractorTradesSelfPerformed", "c.tradesSelf");
		addQueryField("contractorTradesSubContracted", "c.tradesSub");
		addQueryField("contractorScore", "c.score");
		addQueryField("contractorPaymentExpires", "c.paymentExpires");
		addQueryField("contractorPaymentMethod", "c.paymentMethod");
		addQueryField("contractorCreditCardOnFile", "c.ccOnFile");
		addQueryField("contractorCreditCardExpiration", "c.ccExpiration");
		addQueryField("contractorBalance", "c.balance");
		addQueryField("contractorAccountLevel", "c.accountLevel");
		addQueryField("contractorRenew", "c.renew");
		addQueryField("contractorMustPay", "c.mustPay");
		addQueryField("contractorPayingFacilities", "c.payingFacilities");
		addQueryField("contractorMembershipDate", "c.membershipDate");

		leftJoinToUser("customerService", "c.welcomeAuditor_id");

		PermissionQueryBuilder permQuery = new PermissionQueryBuilder(permissions);
		sql.addWhere("1 " + permQuery.toString());
	}

	private void buildEmailSubscriptionBase() {
		buildAccountBase();
		sql.addJoin("JOIN email_subscription es ON es.userID = u.id");

		addQueryField("emailSubscription", "es.subscription");
		addQueryField("emailSubscriptionTimePeriod", "es.timePeriod");
	}

	private void buildEmployeeBase() {
		buildAccountBase();
		sql.addJoin("JOIN employee e ON a.id = e.accountID");

		addQueryField("employeeID", "e.id");
		addQueryField("employeeFirstName", "e.firstName");
		addQueryField("employeeLastName", "e.lastName");
		addQueryField("employeeTitle", "e.title");
		addQueryField("employeeBirthDate", "e.birthDate");
		addQueryField("employeeHireDate", "e.hireDate");
		addQueryField("employeeEmail", "e.email");
		addQueryField("employeePhone", "e.phone");
		addQueryField("employeeLocation", "e.location");
		addQueryField("employeeSSN", "e.ssn");
		addQueryField("employeeTwicExpiration", "e.twicExpiration");
		QueryField employeeClassification = addQueryField("employeeClassification", "e.classification");
		employeeClassification.translate("EmployeeClassification", "description");
		addQueryField("employeeActive", "e.active");
	}

	private void buildOperatorBase() {
		buildAccountBase();
		sql.addJoin("JOIN operators o ON a.id = o.id");
		sql.addWhere("a.type IN ('Operator','Corporate')");
		replaceQueryField("accountName", "operatorName");
		availableFields.remove("accountType");
	}

	private void buildResourceBase() {
		buildAccountBase();

		sql.addJoin("JOIN operatorforms of ON a.id = of.opID");

		addQueryField("resourceID", "of.id");
		addQueryField("resourceOperatorAccountID", "of.opID");
		addQueryField("resourceName", "of.formName");
		addQueryField("resourceParentResourceID", "of.parentID");
		addQueryField("resourceLocale", "of.locale");

		defaultSort = "o.formName";
	}

	private void buildUserBase() {
		buildAccountBase();
		sql.addJoin("JOIN users u ON u.accountID = a.id");

		addQueryField("userID", "u.id");
		addQueryField("userAccountID", "u.accountID");
		addQueryField("userName", "u.name");
		addQueryField("userScreenName", "u.username");
		addQueryField("userIsActive", "u.isActive");
		addQueryField("userIsGroup", "u.isGroup");
		addQueryField("userLastLogin", "u.lastLogin");
	}

	private void buildUserAssignmentBase() {
		buildUserBase();
		sql.addJoin("JOIN user_assignment ua ON ua.userID = u.id");

		addQueryField("userAssignedCountry", "ua.country");
		addQueryField("userAssignedState", "ua.state");
		addQueryField("userAssignedPostalStart", "ua.postal_start");
		addQueryField("userAssignedPostalEnd", "ua.postal_end");
		addQueryField("userAssignedType", "ua.assignmentType");

		joinToAuditType("userAssignedAuditType", "ua.auditTypeID");
		leftJoinToAccount("contractorAccount", "ua.conID");
	}

	private void buildUserGroupBase() {
		buildUserBase();
		sql.addJoin("JOIN usergroup ug ON u.id = ug.userID");

		leftJoinToUser("grp", "ug.groupID");
	}

	private void buildContractorAuditBase() {
		buildContractorBase();

		sql.addJoin("JOIN contractor_audit ca ON ca.conID = a.id");
		addQueryField("auditID", "ca.id");
		addQueryField("auditCreationDate", "ca.creationDate").type(FieldType.Date);
		addQueryField("auditExpirationDate", "ca.expiresDate").type(FieldType.Date);
		addQueryField("auditScheduledDate", "ca.scheduledDate").type(FieldType.Date);
		addQueryField("auditAssignedDate", "ca.assignedDate").type(FieldType.Date);
		addQueryField("auditLocation", "ca.auditLocation");
		addQueryField("auditFor", "ca.auditFor");
		addQueryField("auditScore", "ca.score");
		addQueryField("auditAuditorID", "ca.auditorID");
		addQueryField("auditClosingAuditorID", "ca.closingAuditorID");
		addQueryField("auditContractorConfirmation", "ca.contractorConfirm");
		addQueryField("auditAuditorConfirmation", "ca.auditorConfirm");

		QueryField auditTypeName = joinToAuditType("auditType", "ca.auditTypeID");
		auditTypeName.addRenderer("Audit.action?auditID={0}\">{1} {2}", new String[] { "auditID", "auditTypeName",
				"auditFor" });

		leftJoinToUser("auditor", "ca.auditorID");
		leftJoinToUser("closingAuditor", "ca.closingAuditorID");
	}

	private void buildContractorTradeBase() {
		buildContractorBase();

		sql.addJoin("contractor_trade parent ON parent.conID = a.id");
		sql.addJoin("ref_trade tParent ON tParent.id = parent.tradeID");
		sql.addJoin("contractor_trade child ON child.conID = a.id");
		sql.addJoin("ref_trade tChild ON tChild.id = child.tradeID");

		QueryField contractorTradeParentID = addQueryField("contractorTradeParentID", "tParent.id");
		contractorTradeParentID.translate("Trade", "name");
		addQueryField("contractorTradeParentIndexStart", "tParent.indexStart");
		addQueryField("contractorTradeParentIndexEnd", "tParent.indexEnd");

		QueryField contractorTradeChildID = addQueryField("contractorTradeChildID", "tChild.id");
		contractorTradeChildID.translate("Trade", "name");
		addQueryField("contractorTradeChildIndexStart", "tChild.indexStart");
		addQueryField("contractorTradeChildIndexEnd", "tChild.indexEnd");
	}

	private void buildInvoiceBase() {
		buildContractorBase();

		sql.addJoin("JOIN invoice i on i.accountID = a.id");

		addQueryField("invoiceID", "i.id");
		addQueryField("invoiceAmountApplied", "i.amountApplied");
		addQueryField("invoiceTotalAmount", "i.totalAmount");
		addQueryField("invoiceDueDate", "i.dueDate").type(FieldType.Date);
		addQueryField("invoiceStatus", "i.status");
		addQueryField("invoiceCreationDate", "i.creationDate").type(FieldType.Date);
		addQueryField("invoiceTableType", "i.tableType");
	}

	private void buildInvoiceItemBase() {
		buildInvoiceBase();

		sql.addJoin("JOIN invoice_item ii on ii.invoiceID = i.id");

		addQueryField("invoiceItemPaymentExpires", "ii.paymentExpires");
	}

	private void buildEmployeeAssessmentBase() {
		buildEmployeeBase();

		sql.addJoin("assessment_result ar ON ar.employeeID = e.id");
		sql.addJoin("assessment_test test ON test.id = ar.assessmentTestID");
		sql.addJoin("accounts center ON center.id = test.assessmentCenterID");

		addQueryField("assessmentResultExpirationDate", "ar.expirationDate");

		addQueryField("assessmentTestDescription", "test.description");
		addQueryField("assessmentTestQualificationType", "test.qualificationType");
		addQueryField("assessmentTestQualificationMethod", "test.qualificationMethod");

		addQueryField("assessmentCenterName", "center.name");
	}

	private void buildContractorAuditOperatorBase() {
		buildContractorAuditBase();

		sql.addJoin("JOIN contractor_audit_operator cao ON cao.auditID = ca.id");
		addQueryField("auditOperatorID", "cao.id");
		addQueryField("auditOperatorStatus", "cao.status");
		addQueryField("auditOperatorStatusChangedDate", "cao.statusChangedDate").type(FieldType.Date);
		addQueryField("auditOperatorVisible", "cao.visible");
		addQueryField("auditOperatorPercentComplete", "cao.percentComplete");

		leftJoinToAccount("caoAccount", "cao.opID");
	}

	private void buildContractorAuditOperatorWorkflowBase() {
		buildContractorAuditOperatorBase();

		sql.addJoin("JOIN contractor_audit_operator_workflow cao ON cao.id = caow.caoID");

		addQueryField("auditOperatorWorkflowStatus", "caow.status");
		addQueryField("auditOperatorWorkflowPreviousStatus", "caow.previousStatus");
		addQueryField("auditOperatorWorkflowCreationDate", "caow.creationDate");
		leftJoinToUser("auditOperatorWorkflowCreatedBy", "caow.createdBy");
	}

	private void buildContractorAuditDataBase() {
		buildContractorAuditOperatorBase();

		sql.addJoin("JOIN pqfdata pd on pd.auditID = ca.id");

		addQueryField("auditDataAnswer", "pd.answer");
		addQueryField("auditDataDateVerified", "pd.dateVerified").type(FieldType.Date);
		addQueryField("auditDataQuestionID", "pd.questionID");
		QueryField auditDataQuestion = addQueryField("auditDataQuestion", "pd.questionID");
		auditDataQuestion.translate("AuditQuestion", "name");
		addQueryField("auditDataUpdateDate", "pd.updateDate");
	}

	private QueryField joinToAuditType(String joinAlias, String foreignKey) {
		sql.addJoin("JOIN audit_type " + joinAlias + " ON " + joinAlias + ".id = " + foreignKey);
		addQueryField(joinAlias + "ID", foreignKey);
		QueryField auditTypeName = addQueryField(joinAlias + "Name", foreignKey);
		auditTypeName.translate("AuditType", "name");

		addQueryField(joinAlias + "ClassType", joinAlias + ".classType");
		addQueryField(joinAlias + "IsScheduled", joinAlias + ".isScheduled");
		addQueryField(joinAlias + "HasAuditor", joinAlias + ".hasAuditor");
		addQueryField(joinAlias + "Scorable", joinAlias + ".scoreable");

		return auditTypeName;
	}

	private void leftJoinToAccount(String joinAlias, String foreignKey) {
		joins.put(joinAlias, "LEFT JOIN accounts " + joinAlias + " ON " + joinAlias + ".id = " + foreignKey);
		addQueryField(joinAlias + "ID", joinAlias + ".id").requireJoin(joinAlias);
		addQueryField(joinAlias + "Name", joinAlias + ".name").requireJoin(joinAlias);
		addQueryField(joinAlias + "Status", joinAlias + ".status").requireJoin(joinAlias);
	}

	private void leftJoinToUser(String joinAlias, String foreignKey) {
		joins.put(joinAlias, "LEFT JOIN users " + joinAlias + " ON " + joinAlias + ".id = " + foreignKey);
		addQueryField(joinAlias + "UserID", joinAlias + ".id").requireJoin(joinAlias);
		addQueryField(joinAlias + "UserName", joinAlias + ".name").requireJoin(joinAlias).addRenderer(
				"UsersManage.action?user={0}\">{1}", new String[] { joinAlias + "UserID", joinAlias + "UserName" });

		addQueryField(joinAlias + "Phone", joinAlias + ".phone").requireJoin(joinAlias);
		addQueryField(joinAlias + "Email", joinAlias + ".email").requireJoin(joinAlias);
	}

	private void leftJoinToEmailTemplate(String joinAlias, String foreignKey) {
		joins.put(joinAlias, "LEFT JOIN email_template " + joinAlias + " ON " + joinAlias + ".id = " + foreignKey);
		addQueryField(joinAlias + "ID", joinAlias + ".id").requireJoin(joinAlias);
		addQueryField(joinAlias + "Name", joinAlias + ".templateName").requireJoin(joinAlias);
	}

	public QueryCommand createCommandFromReportParameters(String parameters) {
		if (StringUtils.isEmpty(parameters))
			return null;
			
		QueryCommand command = new QueryCommand();
		if (parameters != null) {
			JSONObject obj = (JSONObject) JSONValue.parse(parameters);
			if (obj != null) {
				command.fromJSON(obj);
			}
		}
		return command;
	}

	public Map<String, QueryField> getAvailableFields() {
		return availableFields;
	}

	public List<SortableField> getColumns() {
		return columns;
	}

	public int getAllRows() {
		return allRows;
	}

	public String getSQL() {
		return sql.toString().replace("\n", " ").replace("  ", " ");
	}
}