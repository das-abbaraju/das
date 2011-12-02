package com.picsauditing.report;

import java.sql.SQLException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.commons.beanutils.BasicDynaBean;
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
				sql.addGroupBy(field.toString());
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

		for (String alias : availableFields.keySet()) {
			if (columns.contains(alias)) {
				String field = availableFields.get(alias).sql;
				sql.addField(field + " AS " + alias);
			}
		}

		if (command.getFilters().size() > 0) {
			String where = command.getFilterExpression();
			if (where == null || Strings.isEmpty(where))
				where = "0";
			for (int i = command.getFilters().size()-1; i >= 0; i--) {
				String filter = command.getFilters().get(i).toExpression(availableFields);
				where = where.replace(i + "", filter);
			}
			sql.addWhere(where);
		}

		// We may need to move this to a class field
		sql.setSQL_CALC_FOUND_ROWS(true);
		
		return sql;
	}

	public QueryData run(Database db) throws SQLException {
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
	
	private QueryField addQueryField(String dataIndex, String sql) {
		QueryField field = new QueryField();
		field.sql = sql;
		field.dataIndex = dataIndex;
		availableFields.put(dataIndex, field);
		return field;
	}

	private void buildAccountBase() {
		sql = new SelectSQL();
		sql.setFromTable("accounts a");
		addQueryField("accountID", "a.id").hide();
		QueryField accountName = addQueryField("accountName", "a.name");
		// accountName.flex = 1;
		accountName.width = 200;
		accountName.renderer = new JavaScript("function(value, metaData, record) {return Ext.String.format('<a href=\"ContractorView.action?id={0}\">{1}</a>',record.data.accountID,record.data.accountName);}");
		addQueryField("accountStatus", "a.status");
		addQueryField("accountType", "a.type");
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

		addQueryField("auditID", "ca.id");
		addQueryField("auditTypeID", "ca.auditTypeID");
		addQueryField("auditID", "ca.id");
		// sql.addField("CONCAT('AuditType.',atype.id,'.name') `atype.name`");

		addQueryField("auditCreationDate", "ca.creationDate");
		addQueryField("auditExpiresDate", "ca.expiresDate");
		addQueryField("auditFor", "ca.auditFor");

		defaultSort = "ca.creationDate DESC";
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
