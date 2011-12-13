package com.picsauditing.report;

import java.sql.SQLException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.commons.beanutils.BasicDynaBean;
import org.apache.commons.lang.StringUtils;
import org.jboss.util.Strings;

import com.picsauditing.access.Permissions;
import com.picsauditing.search.Database;
import com.picsauditing.search.SelectSQL;

public class QueryRunner {
	private SelectSQL sql = new SelectSQL();
	private Map<String, QueryField> availableFields = new HashMap<String, QueryField>();
	// private Permissions permissions;
	private String defaultSort = null;
	private int allRows = 0;
	private List<String> columns;

	public QueryRunner(QueryBase base, Permissions permissions) {
		// this.permissions = permissions;

		buildBase(base);
	}

	public SelectSQL buildQuery(QueryCommand command) {
		columns = command.getColumns();
		if (columns.size() == 0) {
			columns.addAll(availableFields.keySet());
		}

		if (command.getPage() > 1)
			sql.setStartRow((command.getPage() - 1) * command.getRowsPerPage());
		sql.setLimit(command.getRowsPerPage());

		if (command.getGroupBy().size() > 0) {
			addQueryField("total", "count(*)");
			columns.add("total");
			for (SortableField field : command.getGroupBy()) {
				String groupBy = field.field;
				if (!columns.contains(field.field))
					groupBy = availableFields.get(field.field).sql;
				if (!field.ascending)
					groupBy += " DESC";
				sql.addGroupBy(groupBy);
			}
		}

		if (command.getOrderBy().size() == 0) {
			sql.addOrderBy(defaultSort);
		} else {
			for (SortableField field : command.getOrderBy()) {
				String orderBy = field.field;
				if (!columns.contains(field.field))
					orderBy = availableFields.get(field.field).sql;
				if (!field.ascending)
					orderBy += " DESC";
				sql.addOrderBy(orderBy);
			}
		}

		for (String column : columns) {
			if (availableFields.keySet().contains(column)) {
				String field = availableFields.get(column).sql;
				sql.addField(field + " AS " + column);

				addLeftJoins(column);
			}
		}

		if (command.getFilters().size() > 0) {
			String where = command.getFilterExpression();
			if (where == null || Strings.isEmpty(where)) {
				for (int i = 0; i > command.getFilters().size(); i++) {
					where = i + " AND ";
				}
				where = StringUtils.removeEnd(where, " AND ");
			}

			for (int i = command.getFilters().size() - 1; i >= 0; i--) {
				QueryFilter queryFilter = command.getFilters().get(i);
				String filterExp = queryFilter.toExpression(availableFields);

				if (queryFilter.getOperator().equals(QueryFilterOperator.InReport))
					// TODO: query the report sql and put down an inner query.
					where = where.replace(i + "", filterExp);
				else
					where = where.replace(i + "", filterExp);
			}
			sql.addWhere(where);
		}

		// We may need to move this to a class field
		sql.setSQL_CALC_FOUND_ROWS(true);

		return sql;
	}

	public QueryData run() throws SQLException {
		Database db = new Database();
		List<BasicDynaBean> rows = db.select(sql.toString(), true);
		allRows = db.getAllRows();
		QueryData data = new QueryData(columns, rows);
		return data;
	}

	private void buildBase(QueryBase base) {
		switch (base) {
		case Operators:
			buildOperatorBase();
			break;
		case Contractors:
			buildContractorBase();
			break;
		case ContractorAudits:
			buildContractorAuditBase();
			break;
		case ContractorAuditOperators:
			buildContractorAuditOperatorBase();
			break;
		case ContractorAuditOperatorWorkflows:
			buildContractorAuditOperatorWorkflowBase();
			break;
		case Invoices:
			buildInvoiceBase();
			break;
		case RegistrationRequests:
			buildRegistrationRequestsBase();
			break;
		default:
			// This really shouldn't happen
			buildAccountBase();
			break;
		}
	}

	private void addLeftJoins(String column) {
		if (StringUtils.endsWithIgnoreCase(column, "UserID")) {
			if (StringUtils.startsWithIgnoreCase(column, "accountContact"))
				sql.addJoin("LEFT JOIN users contact ON contact.id = a.contactUserID");
			else if (StringUtils.startsWithIgnoreCase(column, "customerService"))
				sql.addJoin("LEFT JOIN users auditor ON cs.id = c.welcomeAuditor_id");
			else if (StringUtils.startsWithIgnoreCase(column, "auditor"))
				sql.addJoin("LEFT JOIN users auditor ON auditor.id = ca.auditorUserID");
			else if (StringUtils.startsWithIgnoreCase(column, "closingAuditor"))
				sql.addJoin("LEFT JOIN users closingAuditor ON closingAuditor.id = ca.auditorUserID");
			else if (StringUtils.startsWithIgnoreCase(column, "requestedByOperator"))
				sql.addJoin("LEFT JOIN users u ON u.id = crr.requestedByUserID");
			else if (StringUtils.startsWithIgnoreCase(column, "requestedContactedBy"))
				sql.addJoin("LEFT JOIN users uc ON uc.id = crr.lastContactedBy");
		} else if (StringUtils.startsWithIgnoreCase(column, "AccountID"))
			if (StringUtils.startsWithIgnoreCase(column, "requestedExisting"))
				sql.addJoin("LEFT JOIN accounts con ON con.id = crr.conID");
	}

	private QueryField addQueryField(String dataIndex, String sql) {
		QueryField field = new QueryField(dataIndex, sql);
		availableFields.put(dataIndex, field);
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
		addQueryField("accountWebsite", "a.web_url");
		addQueryField("accountDBAName", "a.dbaName");
		addQueryField("accountReason", "a.reason");

		addQueryField("accountContactUserID", "contact.id");
		addQueryField("accountContactUserAccountID", "contact.id");
		addQueryField("accountContactUserName", "contact.name");
		addQueryField("accountContactUserPhone", "contact.phone");
		addQueryField("accountContactUserEmail", "contact.email");

		defaultSort = "a.nameIndex";
	}

	private void buildOperatorBase() {
		buildAccountBase();
		sql.addJoin("JOIN operators o ON a.id = o.id");
		sql.addWhere("a.type IN ('Operator','Corporate')");
		availableFields.remove("accountType");
	}

	private void buildContractorBase() {
		buildAccountBase();
		sql.addJoin("JOIN contractor_info c ON a.id = c.id");
		sql.addWhere("a.type='Contractor'");
		availableFields.remove("accountType");
		addQueryField("contractorRiskLevel", "c.riskLevel");
		addQueryField("contractorSafetyRisk", "c.safetyRisk");
		addQueryField("contractorProductRisk", "c.productRisk");
		addQueryField("contractorMainTrade", "c.main_trade");
		addQueryField("contractorTradesSelfPerformed", "c.tradesSelf");
		addQueryField("contractorTradesSubContracted", "c.tradesSub");
		addQueryField("contractorScore", "c.score");
		addQueryField("contractorPaymentExpires", "c.paymentExpires");
		addQueryField("contractorCreditCardOnFile", "c.ccOnFile");

		addQueryField("customerServiceUserID", "cs.id");
		addQueryField("customerServiceUserAccountID", "cs.accountID");
		addQueryField("customerServiceUserName", "cs.name");
	}

	private void buildContractorAuditBase() {
		buildContractorBase();

		sql.addJoin("JOIN contractor_audit ca ON ca.conID = a.id");
		sql.addJoin("JOIN audit_type atype ON atype.id = ca.auditTypeID");
		sql.addWhere("atype.classType IN ( 'Audit', 'IM', 'PQF' )");
		sql.setDistinct(true);

		availableFields.get("accountStatus").hide();
		addQueryField("auditID", "ca.id");
		addQueryField("auditTypeID", "ca.auditTypeID");
		QueryField auditTypeName = addQueryField("auditTypeName", "ca.auditTypeID");
		auditTypeName.translate("AuditType", "name");
		addQueryField("auditCreationDate", "ca.creationDate").type(FieldType.Date);
		addQueryField("auditExpirationDate", "ca.expiresDate").type(FieldType.Date);
		addQueryField("auditScheduledDate", "ca.expiresDate").type(FieldType.Date);
		addQueryField("auditAssignedDate", "ca.expiresDate").type(FieldType.Date);
		addQueryField("auditLocation", "ca.auditLocation");
		addQueryField("auditFor", "ca.auditFor");
		addQueryField("auditScore", "ca.score");
		addQueryField("auditContractorConfirmation", "ca.score");
		addQueryField("auditAuditorConfirmation", "ca.score");

		addQueryField("auditTypeIsScheduled", "atype.isScheduled");
		addQueryField("auditTypeHasAuditor", "atype.hasAuditor");
		addQueryField("auditTypeScorable", "atype.scoreable");

		addQueryField("auditorUserID", "auditor.id");
		addQueryField("auditorUserAccountID", "auditor.accountID");
		addQueryField("auditorUserName", "auditor.name");

		addQueryField("closingAuditorUserID", "closingAuditor.id");
		addQueryField("closingAuditorUserAccountID", "closingAuditor.accountID");
		addQueryField("closingAuditorUserName", "closingAuditor.name");
	}

	private void buildContractorAuditOperatorBase() {
		buildContractorAuditBase();

		sql.addJoin("JOIN contractor_audit_operator cao ON cao.auditID = ca.id");
		sql.addJoin("JOIN accounts caoAccount ON cao.opID = caoAccount.id");

		addQueryField("contractorAuditOperatorID", "cao.id");
		addQueryField("contractorAuditOperatorStatus", "cao.status");
		addQueryField("contractorAuditOperatorStatusChangedDate", "cao.statusChangedDate").type(FieldType.Date);

		addQueryField("contractorAuditOperatorAccountID", "caoAccount.id");
		addQueryField("contractorAuditOperatorAccountName", "caoAccount.name");

		defaultSort = "cao.statusChangedDate DESC";
	}

	private void buildContractorAuditOperatorWorkflowBase() {
		buildContractorAuditOperatorBase();

		sql.addJoin("JOIN contractor_audit_operator_workflow cao ON cao.id = caow.caoID");

		addQueryField("contractorAuditOperatorWorkflowStatus", "caow.status");
	}

	private void buildRegistrationRequestsBase() {
		sql = new SelectSQL();
		sql.setFromTable("contractor_registration_request crr");
		sql.addJoin("JOIN accounts op ON op.id = crr.requestedByID");

		addQueryField("requestID", "crr.id");
		addQueryField("requestedName", "crr.name");
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
		addQueryField("requestedByOperatorID", "op.id");
		addQueryField("requestedByOperatorName", "op.name");
		addQueryField("requestedDeadline", "crr.deadline");
		addQueryField("requestedLastContactedByDate", "crr.lastContactDate").type(FieldType.Date);
		addQueryField("requestedContactCount", "crr.contactCount");
		addQueryField("requestedMatchCount", "crr.matchCount");
		addQueryField("requestCreationDate", "crr.creationDate").type(FieldType.Date);

		addQueryField("requestedByOperatorUserID", "u.id");
		addQueryField("requestedByOperatorUserAccountID", "u.accountID");
		addQueryField("requestedByOperatorUserName", "u.name");
		addQueryField("requestedByOperatorUserOther", "crr.requestedByUser");

		addQueryField("requestedContactedByUserID", "uc.id");
		addQueryField("requestedContactedByUserAccountID", "uc.accountID");
		addQueryField("requestedContactedByUserName", "uc.name");

		addQueryField("requestedExistingAccountID", "con.id");
		addQueryField("requestedExistingAccountName", "con.name");
	}

	private void buildInvoiceBase() {
		buildContractorBase();

		sql.addJoin("JOIN invoice i on i.accountID = c.id");
	}

	public Map<String, QueryField> getAvailableFields() {
		return availableFields;
	}

	public List<String> getColumns() {
		return columns;
	}

	public int getAllRows() {
		return allRows;
	}

	public String getSQL() {
		return sql.toString().replace("\n", " ").replace("  ", " ");
	}
}
