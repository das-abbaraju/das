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
	private List<SimpleReportField> columns;
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

	public SelectSQL buildQueryWithoutLimits() {
		SimpleReportDefinition definition = createDefinitionFromReportParameters(report.getParameters());
		SimpleReportDefinition devDefinition = createDefinitionFromReportParameters(report.getDevParams());

		definition = mergeDefinition(definition, devDefinition);

		defaultColumns(definition);
		addGroupBy(definition);
		addHaving(definition);

		removeObsoleteColumns();
		addColumns();

		addOrderBy(definition);
		addRuntimeFilters(definition);

		return sql;
	}

	public SelectSQL buildQuery() {
		buildQueryWithoutLimits();

		// We may need to move this to a class field
		sql.setSQL_CALC_FOUND_ROWS(true);
		SimpleReportDefinition definition = createDefinitionFromReportParameters(report.getParameters());
		addLimits(definition);

		return sql;
	}

	private SimpleReportDefinition mergeDefinition(SimpleReportDefinition definition,
			SimpleReportDefinition devDefinition) {
		if (!devDefinition.getColumns().isEmpty())
			definition.getColumns().addAll(devDefinition.getColumns());
		if (!devDefinition.getGroupBy().isEmpty())
			definition.getGroupBy().addAll(devDefinition.getGroupBy());
		if (!devDefinition.getOrderBy().isEmpty())
			definition.getOrderBy().addAll(devDefinition.getOrderBy());
		if (!devDefinition.getHaving().isEmpty())
			definition.getHaving().addAll(devDefinition.getHaving());
		if (!devDefinition.getFilters().isEmpty())
			definition.getFilters().addAll(devDefinition.getFilters());
		if (devDefinition.getFilterExpression() != null)
			definition.setFilterExpression(definition.getFilterExpression() + " AND "
					+ devDefinition.getFilterExpression());

		return definition;
	}

	private void defaultColumns(SimpleReportDefinition definition) {
		if (columns == null || columns.isEmpty()) {
			columns = definition.getColumns();
		}

		if (columns.size() == 0) {
			columns.clear();
			for (String fieldName : availableFields.keySet()) {
				SimpleReportField column = new SimpleReportField();
				column.setQueryField(availableFields.get(fieldName));
				columns.add(column);
			}
		}
	}

	private void removeObsoleteColumns() {
		Iterator<SimpleReportField> iterator = columns.iterator();
		while (iterator.hasNext()) {
			SimpleReportField column = iterator.next();
			if (!availableFields.containsKey(column.field))
				iterator.remove();
		}
	}

	private void addRuntimeFilters(SimpleReportDefinition definition) {
		if (definition.getFilters().size() == 0) {
			return;
		}

		String where = definition.getFilterExpression();
		if (where == null || Strings.isEmpty(where)) {
			where = "";
			for (int i = 0; i < definition.getFilters().size(); i++) {
				where += "{" + i + "} AND ";
			}
			where = StringUtils.removeEnd(where, " AND ");
		}

		for (int i = 0; i < definition.getFilters().size(); i++) {
			SimpleReportFilter queryFilter = definition.getFilters().get(i);

			if (queryFilter.getOperator().equals(QueryFilterOperator.InReport)) {
				Report subReport = dao.find(Report.class, Integer.parseInt(queryFilter.getValue()));
				QueryRunner subRunner = new QueryRunner(subReport, permissions, dao);
				SelectSQL subSql = subRunner.buildQueryWithoutLimits();
				queryFilter.setValue(subSql.toString());
			}

			String filterExp = queryFilter.toExpression(availableFields);
			where = where.replace("{" + i + "}", filterExp);
		}
	}

	private void addColumns() {
		Set<String> addedJoins = new HashSet<String>();
		for (SimpleReportField column : columns) {
			if (availableFields.keySet().contains(column.field)) {
				QueryField availableField = availableFields.get(column.field);
				// TODO figure out if this is the best spot
				column.setQueryField(availableField);

				addLeftJoins(addedJoins, availableField);

				sql.addField(column.toSQL(availableFields) + " AS " + column.field);
				// TODO: Think about case/if (IF function or CASE statement) and
				// calculated fields (2 fields coming out with a result)
			}
		}
	}

	private void addLeftJoins(Set<String> addedJoins, QueryField availableField) {
		if (availableField.requiresJoin()) {
			if (!addedJoins.contains(availableField.requireJoin)) {
				sql.addJoin(this.joins.get(availableField.requireJoin));
				addedJoins.add(availableField.requireJoin);
			}
		}
	}

	private void addOrderBy(SimpleReportDefinition definition) {
		if (definition.getOrderBy().size() == 0) {
			sql.addOrderBy(defaultSort);
			return;
		}

		for (SimpleReportField field : definition.getOrderBy()) {
			String orderBy = field.field;
			if (!columns.contains(field.field))
				orderBy = availableFields.get(field.field).sql;
			if (!field.ascending)
				orderBy += " DESC";
			sql.addOrderBy(orderBy);
		}
	}

	private void addGroupBy(SimpleReportDefinition definition) {
		if (definition.getGroupBy().size() == 0) {
			return;
		}

		for (SimpleReportField field : definition.getGroupBy()) {
			String groupBy = field.toSQL(availableFields);
			sql.addGroupBy(groupBy);
		}

		addTotalField();
	}

	private void addHaving(SimpleReportDefinition definition) {
		if (definition.getHaving().size() == 0) {
			return;
		}

		for (SimpleReportField field : definition.getHaving()) {
			String having = field.toSQL(availableFields);
			sql.addHaving(having);
		}
	}

	private void addTotalField() {
		addQueryField("total", null);
		SimpleReportField total = new SimpleReportField();
		total.field = "total";
		total.function = QueryFunction.Count;
		columns.add(total);
	}

	private void addLimits(SimpleReportDefinition definition) {
		if (definition.getPage() > 1)
			sql.setStartRow((definition.getPage() - 1) * definition.getRowsPerPage());
		sql.setLimit(definition.getRowsPerPage());
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
		case ContractorFees:
			buildContractorFeeBase();
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

		addQueryField("accountID", "a.id").makeDefault();
		addQueryField("accountName", "a.name").makeDefault();
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

		joinToNAICS("naics", "a.naics");

		defaultSort = "a.nameIndex";
	}

	private void buildEmailBase() {
		sql = new SelectSQL();
		sql.setFromTable("emailQueue eq");

		addQueryField("emailID", "eq.emailID").makeDefault();
		addQueryField("emailStatus", "eq.emailStatus");
		addQueryField("emailFromAddress", "eq.emailFromAddress").makeDefault();
		addQueryField("emailToAddresses", "eq.emailToAddresses").makeDefault();
		addQueryField("emailCcAddresses", "eq.emailCcAddresses");
		addQueryField("emailSubject", "eq.subject").makeDefault();
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

		addQueryField("emailExcluded", "ee.email").makeDefault();

		defaultSort = "ee.email";
	}

	private void buildExceptionLogBase() {
		sql = new SelectSQL();
		sql.setFromTable("app_error_log as ael");

		addQueryField("errorLogID", "ael.id").makeDefault();
		addQueryField("errorLogCategory", "ael.category").makeDefault();
		addQueryField("errorLogPriority", "ael.priority").makeDefault();
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
				new String[] { "requestID", "requestedName" }).makeDefault();
		addQueryField("requestedContact", "crr.contact").makeDefault();
		addQueryField("requestedPhone", "crr.phone").makeDefault();
		addQueryField("requestedEmail", "crr.email");
		addQueryField("requestedTaxID", "crr.taxID");
		addQueryField("requestedAddress", "crr.address");
		addQueryField("requestedCity", "crr.city");
		addQueryField("requestedState", "crr.state");
		addQueryField("requestedZip", "crr.zip");
		addQueryField("requestedCountry", "crr.country");
		addQueryField("requestedNotes", "crr.notes");
		addQueryField("requestedDeadline", "crr.deadline").type(FieldType.Date).makeDefault();
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

		sql.addJoin("JOIN contractor_info c ON a.id = c.id");
		sql.addWhere("a.type='Contractor'");
		availableFields.remove("accountType");
		replaceQueryField("accountName", "contractorName").makeDefault();
		availableFields.get("contractorName").addRenderer(
				new Renderer("ContractorView.action?id={0}\">{1}", new String[] { "accountID", "contractorName" }));
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
		addQueryField("contractorLastUpgradeDate", "c.lastUpgradeDate");
		addQueryField("contractorTRIRAverage", "c.trirAverage");

		leftJoinToUser("customerService", "c.welcomeAuditor_id");
		leftJoinToAccount("requestedByOperator", "c.requestedByID");

		joinToFlagCriteriaContractor("contractorFlagCriteria", "c.id");
		leftJoinToEmailQueue("contractorEmail", "c.id");
		joinToGeneralContractor("contractorOperator", "subID", "c.id");
		joinToContractorWatch("contractorWatch", "c.id");

		PermissionQueryBuilder permQuery = new PermissionQueryBuilder(permissions);
		sql.addWhere("1 " + permQuery.toString());
	}

	private void buildEmailSubscriptionBase() {
		buildAccountBase();
		sql.addJoin("JOIN email_subscription es ON es.userID = u.id");

		addQueryField("emailSubscription", "es.subscription").makeDefault();
		addQueryField("emailSubscriptionTimePeriod", "es.timePeriod").makeDefault();
	}

	private void buildEmployeeBase() {
		buildAccountBase();
		sql.addJoin("JOIN employee e ON a.id = e.accountID");

		addQueryField("employeeID", "e.id").makeDefault();
		addQueryField("employeeFirstName", "e.firstName").makeDefault();
		addQueryField("employeeLastName", "e.lastName").makeDefault();
		addQueryField("employeeTitle", "e.title").makeDefault();
		addQueryField("employeeBirthDate", "e.birthDate").type(FieldType.Date);
		addQueryField("employeeHireDate", "e.hireDate").type(FieldType.Date);
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
		replaceQueryField("accountName", "operatorName").makeDefault();
		availableFields.remove("accountType");

		joinToFacilities("operatorFacility", "opID", "a.id");
		joinToFacilities("corporateFacility", "corporateID", "a.id");
		joinToGeneralContractor("operatorContractor", "genID", "o.id");
	}

	private void buildResourceBase() {
		buildAccountBase();

		sql.addJoin("JOIN operatorforms of ON a.id = of.opID");

		addQueryField("resourceID", "of.id").makeDefault();
		addQueryField("resourceOperatorAccountID", "of.opID").makeDefault();
		addQueryField("resourceName", "of.formName").makeDefault();
		addQueryField("resourceParentResourceID", "of.parentID");
		addQueryField("resourceLocale", "of.locale");

		defaultSort = "o.formName";
	}

	private void buildUserBase() {
		buildAccountBase();
		sql.addJoin("JOIN users u ON u.accountID = a.id");

		addQueryField("userID", "u.id").makeDefault();
		addQueryField("userAccountID", "u.accountID").makeDefault();
		addQueryField("userName", "u.name").makeDefault();
		addQueryField("userScreenName", "u.username");
		addQueryField("userIsActive", "u.isActive");
		addQueryField("userIsGroup", "u.isGroup");
		addQueryField("userLastLogin", "u.lastLogin");

		joinToLoginLog("userLoginLog", "u.id");
	}

	private void buildUserAssignmentBase() {
		buildUserBase();
		sql.addJoin("JOIN user_assignment ua ON ua.userID = u.id");

		addQueryField("userAssignedCountry", "ua.country").makeDefault();
		addQueryField("userAssignedState", "ua.state").makeDefault();
		addQueryField("userAssignedPostalStart", "ua.postal_start").makeDefault();
		addQueryField("userAssignedPostalEnd", "ua.postal_end").makeDefault();
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
		addQueryField("auditID", "ca.id").makeDefault();
		addQueryField("auditCreationDate", "ca.creationDate").type(FieldType.Date);
		addQueryField("auditExpirationDate", "ca.expiresDate").type(FieldType.Date);
		addQueryField("auditScheduledDate", "ca.scheduledDate").type(FieldType.Date);
		addQueryField("auditAssignedDate", "ca.assignedDate").type(FieldType.Date);
		addQueryField("auditLocation", "ca.auditLocation");
		addQueryField("auditFor", "ca.auditFor").makeDefault();
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

		joinToOshaAudit("oshaAudit", "ca.id");
	}

	private void buildContractorTradeBase() {
		buildContractorBase();

		sql.addJoin("contractor_trade parent ON parent.conID = a.id");
		sql.addJoin("ref_trade tParent ON tParent.id = parent.tradeID");
		sql.addJoin("contractor_trade child ON child.conID = a.id");
		sql.addJoin("ref_trade tChild ON tChild.id = child.tradeID");

		QueryField contractorTradeParentID = addQueryField("contractorTradeParentID", "tParent.id");
		contractorTradeParentID.translate("Trade", "name").makeDefault();
		addQueryField("contractorTradeParentIndexStart", "tParent.indexStart");
		addQueryField("contractorTradeParentIndexEnd", "tParent.indexEnd");

		QueryField contractorTradeChildID = addQueryField("contractorTradeChildID", "tChild.id");
		contractorTradeChildID.translate("Trade", "name").makeDefault();
		addQueryField("contractorTradeChildIndexStart", "tChild.indexStart");
		addQueryField("contractorTradeChildIndexEnd", "tChild.indexEnd");
	}

	private void buildContractorFeeBase() {
		buildContractorBase();

		sql.addJoin("contractor_fee cf ON cf.conID = a.id");

		addQueryField("contractorFeeCurrentAmount", "cf.currentAmount").makeDefault();
		addQueryField("contractorFeeNewAmount", "cf.newAmount");

		joinToInvoiceFee("contractorFee", "cf.newLevel");
	}

	private void buildInvoiceBase() {
		buildContractorBase();

		sql.addJoin("JOIN invoice i on i.accountID = a.id");

		addQueryField("invoiceID", "i.id").makeDefault();
		addQueryField("invoiceAmountApplied", "i.amountApplied");
		addQueryField("invoiceTotalAmount", "i.totalAmount").makeDefault();
		addQueryField("invoiceDueDate", "i.dueDate").type(FieldType.Date);
		addQueryField("invoiceStatus", "i.status");
		addQueryField("invoiceCreationDate", "i.creationDate").type(FieldType.Date);
		addQueryField("invoiceTableType", "i.tableType");
	}

	private void buildInvoiceItemBase() {
		buildInvoiceBase();

		sql.addJoin("JOIN invoice_item ii on ii.invoiceID = i.id");

		addQueryField("invoiceItemPaymentExpires", "ii.paymentExpires").makeDefault();
		addQueryField("invoiceItemAmount", "ii.amount").makeDefault();

		joinToInvoiceFee("invoiceItemFee", "ii.feeID");
	}

	private void buildEmployeeAssessmentBase() {
		buildEmployeeBase();

		sql.addJoin("assessment_result ar ON ar.employeeID = e.id");
		sql.addJoin("assessment_test test ON test.id = ar.assessmentTestID");
		sql.addJoin("accounts center ON center.id = test.assessmentCenterID");

		addQueryField("assessmentResultExpirationDate", "ar.expirationDate");

		addQueryField("assessmentTestDescription", "test.description").makeDefault();
		addQueryField("assessmentTestQualificationType", "test.qualificationType").makeDefault();
		addQueryField("assessmentTestQualificationMethod", "test.qualificationMethod");

		addQueryField("assessmentCenterName", "center.name");
	}

	private void buildContractorAuditOperatorBase() {
		buildContractorAuditBase();

		sql.addJoin("JOIN contractor_audit_operator cao ON cao.auditID = ca.id");
		addQueryField("auditOperatorID", "cao.id").makeDefault();
		addQueryField("auditOperatorStatus", "cao.status").makeDefault();
		addQueryField("auditOperatorStatusChangedDate", "cao.statusChangedDate").type(FieldType.Date);
		addQueryField("auditOperatorVisible", "cao.visible");
		addQueryField("auditOperatorPercentComplete", "cao.percentComplete").makeDefault();

		leftJoinToAccount("caoAccount", "cao.opID");
	}

	private void buildContractorAuditOperatorWorkflowBase() {
		buildContractorAuditOperatorBase();

		sql.addJoin("JOIN contractor_audit_operator_workflow cao ON cao.id = caow.caoID");

		addQueryField("auditOperatorWorkflowStatus", "caow.status").makeDefault();
		addQueryField("auditOperatorWorkflowPreviousStatus", "caow.previousStatus").makeDefault();
		addQueryField("auditOperatorWorkflowCreationDate", "caow.creationDate").makeDefault();
		leftJoinToUser("auditOperatorWorkflowCreatedBy", "caow.createdBy");
	}

	private void buildContractorAuditDataBase() {
		buildContractorAuditOperatorBase();

		sql.addJoin("JOIN pqfdata pd on pd.auditID = ca.id");

		addQueryField("auditDataAnswer", "pd.answer").makeDefault();
		addQueryField("auditDataDateVerified", "pd.dateVerified").type(FieldType.Date);
		addQueryField("auditDataQuestionID", "pd.questionID");
		QueryField auditDataQuestion = addQueryField("auditDataQuestion", "pd.questionID");
		auditDataQuestion.translate("AuditQuestion", "name").makeDefault();
		addQueryField("auditDataUpdateDate", "pd.updateDate").type(FieldType.Date);
	}

	private QueryField joinToAuditType(String joinAlias, String foreignKey) {
		sql.addJoin("JOIN audit_type " + joinAlias + " ON " + joinAlias + ".id = " + foreignKey);
		addQueryField(joinAlias + "ID", foreignKey).requireJoin(joinAlias).makeDefault();
		QueryField auditTypeName = addQueryField(joinAlias + "Name", foreignKey);
		auditTypeName.translate("AuditType", "name").requireJoin(joinAlias).makeDefault();

		addQueryField(joinAlias + "ClassType", joinAlias + ".classType").requireJoin(joinAlias);
		addQueryField(joinAlias + "IsScheduled", joinAlias + ".isScheduled").requireJoin(joinAlias);
		addQueryField(joinAlias + "HasAuditor", joinAlias + ".hasAuditor").requireJoin(joinAlias);
		addQueryField(joinAlias + "Scorable", joinAlias + ".scoreable").requireJoin(joinAlias);

		return auditTypeName;
	}

	private void joinToContractorWatch(String joinAlias, String foreignKey) {
		sql.addJoin("LEFT JOIN contractor_watch " + joinAlias + " ON " + joinAlias + ".conID = " + foreignKey);

		addQueryField(joinAlias + "ContractorID", foreignKey).requireJoin(joinAlias).makeDefault();
		addQueryField(joinAlias + "UserID", joinAlias + ".userID").requireJoin(joinAlias).makeDefault();
	}

	private void leftJoinToEmailQueue(String joinAlias, String foreignKey) {
		sql.addJoin("JOIN email_queue " + joinAlias + " ON " + joinAlias + ".conID = " + foreignKey);
		addQueryField(joinAlias + "ContractorID", foreignKey).requireJoin(joinAlias).makeDefault();

		addQueryField(joinAlias + "CreationDate", joinAlias + ".creationDate").requireJoin(joinAlias).makeDefault()
				.type(FieldType.Date);
		addQueryField(joinAlias + "SentDate", joinAlias + ".sentDate").requireJoin(joinAlias).makeDefault()
				.type(FieldType.Date);
		addQueryField(joinAlias + "CreatedBy", joinAlias + ".createdBy").requireJoin(joinAlias);
		addQueryField(joinAlias + "ViewableBy", joinAlias + ".viewableBy").requireJoin(joinAlias);
		addQueryField(joinAlias + "Subject", joinAlias + ".subject").requireJoin(joinAlias);
		addQueryField(joinAlias + "TemplateID", joinAlias + ".templateID").requireJoin(joinAlias);
		addQueryField(joinAlias + "Status", joinAlias + ".status").requireJoin(joinAlias);
	}

	private void joinToFacilities(String joinAlias, String tableKey, String foreignKey) {
		sql.addJoin("LEFT JOIN facilities " + joinAlias + " ON " + joinAlias + "." + tableKey + " = " + foreignKey);

		addQueryField(joinAlias + "OperatorID", joinAlias + ".opID").requireJoin(joinAlias).makeDefault();
		addQueryField(joinAlias + "CorporateID", joinAlias + ".corporateID").requireJoin(joinAlias).makeDefault();

		leftJoinToAccount("operatorChild", joinAlias + ".opID");
		leftJoinToAccount("corporateParent", joinAlias + ".corporateID");
	}

	private void joinToFlagCriteriaContractor(String joinAlias, String foreignKey) {
		sql.addJoin("JOIN flag_criteria_contractor " + joinAlias + " ON " + joinAlias + ".conID = " + foreignKey);
		addQueryField(joinAlias + "ContractorID", foreignKey).makeDefault();

		addQueryField(joinAlias + "ID", joinAlias + ".id").requireJoin(joinAlias);
		addQueryField(joinAlias + "CriteriaID", joinAlias + ".criteriaID").requireJoin(joinAlias);
		addQueryField(joinAlias + "Answer", joinAlias + ".answer").requireJoin(joinAlias);
	}

	private void joinToGeneralContractor(String joinAlias, String tableKey, String foreignKey) {
		sql.addJoin("LEFT JOIN generalcontractor " + joinAlias + " ON " + joinAlias + "." + tableKey + " = "
				+ foreignKey);

		addQueryField(joinAlias + "ContractorID", joinAlias + ".subID").requireJoin(joinAlias).makeDefault();
		addQueryField(joinAlias + "OperatorID", joinAlias + ".genID").requireJoin(joinAlias).makeDefault();
		addQueryField(joinAlias + "FlagLastUpdated", joinAlias + ".flagLastUpdated").requireJoin(joinAlias).type(
				FieldType.Date);
		addQueryField(joinAlias + "Flag", joinAlias + ".flag").requireJoin(joinAlias).makeDefault();

		leftJoinToAccount(joinAlias + "Operator", joinAlias + ".genID");
	}

	private void joinToInvoiceFee(String joinAlias, String foreignKey) {
		sql.addJoin("JOIN invoice_fee " + joinAlias + " ON " + joinAlias + ".id = " + foreignKey);

		addQueryField(joinAlias + "ID", joinAlias + ".id").requireJoin(joinAlias).makeDefault();
		addQueryField(joinAlias + "MaxFacilities", joinAlias + ".maxFacilities").requireJoin(joinAlias).makeDefault();
	}

	private void joinToLoginLog(String joinAlias, String foreignKey) {
		sql.addJoin("JOIN loginlog " + joinAlias + " ON " + joinAlias + ".userID = " + foreignKey);
		addQueryField(joinAlias + "UserID", foreignKey).requireJoin(joinAlias).makeDefault();

		addQueryField(joinAlias + "AdminID", joinAlias + ".adminID").requireJoin(joinAlias).makeDefault();
		addQueryField(joinAlias + "LoginDate", joinAlias + ".loginDate").requireJoin(joinAlias);
		addQueryField(joinAlias + "RemoteAccess", joinAlias + ".remoteAccess").requireJoin(joinAlias);
	}

	private void joinToOshaAudit(String joinAlias, String foreignKey) {
		sql.addJoin("JOIN osha_audit " + joinAlias + " ON " + joinAlias + ".auditID = " + foreignKey);
		addQueryField(joinAlias + "ContractorID", foreignKey).requireJoin(joinAlias).makeDefault();

		addQueryField(joinAlias + "ID", joinAlias + ".id").requireJoin(joinAlias);
		addQueryField(joinAlias + "SHAType", joinAlias + ".SHAType").requireJoin(joinAlias);
		addQueryField(joinAlias + "Fatalities", joinAlias + ".fatalities").requireJoin(joinAlias);
		addQueryField(joinAlias + "VerifiedDate", joinAlias + ".verifiedDate").requireJoin(joinAlias).type(
				FieldType.Date);
		addQueryField(joinAlias + "Location", joinAlias + ".location").requireJoin(joinAlias);
		addQueryField(joinAlias + "Description", joinAlias + ".description").requireJoin(joinAlias);
		addQueryField(joinAlias + "RecordableTotal", joinAlias + ".recordableTotal").requireJoin(joinAlias);
		addQueryField(joinAlias + "ManHours", joinAlias + ".manHours").requireJoin(joinAlias);
		addQueryField(joinAlias + "CAD7", joinAlias + ".cad7").requireJoin(joinAlias);
		addQueryField(joinAlias + "NEER", joinAlias + ".neer").requireJoin(joinAlias);
	}

	private void joinToNAICS(String joinAlias, String foreignKey) {
		sql.addJoin("JOIN naics " + joinAlias + " ON " + joinAlias + ".code = " + foreignKey);
		addQueryField(joinAlias + "Code", foreignKey).requireJoin(joinAlias).makeDefault();

		addQueryField(joinAlias + "TRIR", joinAlias + ".trir").requireJoin(joinAlias);
		addQueryField(joinAlias + "LWCR", joinAlias + ".lwcr").requireJoin(joinAlias);
	}

	private void leftJoinToAccount(String joinAlias, String foreignKey) {
		joins.put(joinAlias, "LEFT JOIN accounts " + joinAlias + " ON " + joinAlias + ".id = " + foreignKey);
		addQueryField(joinAlias + "ID", joinAlias + ".id").requireJoin(joinAlias).makeDefault();
		addQueryField(joinAlias + "Name", joinAlias + ".name").requireJoin(joinAlias).makeDefault();
		addQueryField(joinAlias + "Status", joinAlias + ".status").requireJoin(joinAlias);
		addQueryField(joinAlias + "Type", joinAlias + ".type").requireJoin(joinAlias);
	}

	private void leftJoinToUser(String joinAlias, String foreignKey) {
		joins.put(joinAlias, "LEFT JOIN users " + joinAlias + " ON " + joinAlias + ".id = " + foreignKey);
		addQueryField(joinAlias + "UserID", joinAlias + ".id").requireJoin(joinAlias).makeDefault();
		addQueryField(joinAlias + "UserName", joinAlias + ".name")
				.requireJoin(joinAlias)
				.addRenderer("UsersManage.action?user={0}\">{1}",
						new String[] { joinAlias + "UserID", joinAlias + "UserName" }).makeDefault();

		addQueryField(joinAlias + "Phone", joinAlias + ".phone").requireJoin(joinAlias);
		addQueryField(joinAlias + "Email", joinAlias + ".email").requireJoin(joinAlias);
	}

	private void leftJoinToEmailTemplate(String joinAlias, String foreignKey) {
		joins.put(joinAlias, "LEFT JOIN email_template " + joinAlias + " ON " + joinAlias + ".id = " + foreignKey);
		addQueryField(joinAlias + "ID", joinAlias + ".id").requireJoin(joinAlias).makeDefault();
		addQueryField(joinAlias + "Name", joinAlias + ".templateName").requireJoin(joinAlias).makeDefault();
	}

	public SimpleReportDefinition createDefinitionFromReportParameters(String parameters) {
		if (StringUtils.isEmpty(parameters))
			return null;

		SimpleReportDefinition definition = new SimpleReportDefinition();
		if (parameters != null) {
			JSONObject obj = (JSONObject) JSONValue.parse(parameters);
			if (obj != null) {
				definition.fromJSON(obj);
			}
		}
		return definition;
	}

	public Map<String, QueryField> getAvailableFields() {
		return availableFields;
	}

	public List<SimpleReportField> getColumns() {
		return columns;
	}

	public int getAllRows() {
		return allRows;
	}

	public String getSQL() {
		return sql.toString().replace("\n", " ").replace("  ", " ");
	}
}