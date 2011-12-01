package com.picsauditing.report;

import java.sql.SQLException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.commons.beanutils.BasicDynaBean;

import com.picsauditing.access.Permissions;
import com.picsauditing.search.Database;
import com.picsauditing.search.SelectSQL;

public class QueryRunner {
	private SelectSQL sql = new SelectSQL();
	private Map<String, String> availableFields = new HashMap<String, String>();
	// private Permissions permissions;
	private String defaultSort = null;
	private int allRows = 0;

	public QueryRunner(QueryBase base, Permissions permissions) {
		// this.permissions = permissions;

		buildBase(base);
	}

	public QueryData run(QueryCommand command, Database db) throws SQLException {

		List<String> columns = command.getColumns();
		if (columns.size() == 0) {
			columns.addAll(availableFields.keySet());
		}

		for (String alias : availableFields.keySet()) {
			if (columns.contains(alias)) {
				String field = availableFields.get(alias);
				sql.addField(field + " AS " + alias);
			}
		}

		if (command.getPage() > 1)
			sql.setStartRow((command.getPage() - 1) * command.getRowsPerPage());
		sql.setLimit(command.getRowsPerPage());

		if (command.getOrderBy().size() == 0) {
			sql.addOrderBy(defaultSort);
		} else {
			for (SortableField field : command.getOrderBy()) {
				sql.addOrderBy(field.toString());
			}
		}

		// We may need to move this to a class field
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

		default:
			// This really shouldn't happen
			buildAccountBase();
			break;
		}
	}

	private void buildAccountBase() {
		sql = new SelectSQL();
		sql.setFromTable("accounts a");
		availableFields.put("accountID", "a.id");
		availableFields.put("accountName", "a.name");
		availableFields.put("accountStatus", "a.status");
		availableFields.put("accountType", "a.type");
		defaultSort = "a.name";
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
	}

	private void buildContractorAuditBase() {
		buildContractorBase();
		sql.addJoin("JOIN contractor_audit ca ON ca.conID = a.id");
		sql.addJoin("JOIN audit_type atype ON atype.id = ca.auditTypeID");

		availableFields.put("auditID", "ca.id");
		availableFields.put("auditTypeID", "ca.auditTypeID");
		availableFields.put("auditID", "ca.id");
		// sql.addField("CONCAT('AuditType.',atype.id,'.name') `atype.name`");

		availableFields.put("auditCreationDate", "ca.creationDate");
		availableFields.put("auditExpiresDate", "ca.expiresDate");
		availableFields.put("auditFor", "ca.auditFor");

		defaultSort = "ca.creationDate DESC";
	}

	public Map<String, String> getAvailableFields() {
		return availableFields;
	}

	public int getAllRows() {
		return allRows;
	}
}
