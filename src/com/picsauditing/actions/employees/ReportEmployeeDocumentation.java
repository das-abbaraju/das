package com.picsauditing.actions.employees;

import com.picsauditing.actions.report.ReportActionSupport;
import com.picsauditing.search.SelectSQL;
import com.picsauditing.util.ReportFilterEmployee;
import com.picsauditing.util.Strings;

public class ReportEmployeeDocumentation extends ReportActionSupport {
	private ReportFilterEmployee filter = new ReportFilterEmployee();
	private SelectSQL sql = new SelectSQL("employee_competency ec");

	@Override
	public String execute() throws Exception {
		getFilter().setAjax(false);
		buildQuery();
		run(sql);

		return SUCCESS;
	}

	private void buildQuery() {
		sql.addJoin("JOIN employee e ON e.id = ec.`employeeID`");
		sql.addJoin("JOIN accounts a ON a.id = e.accountID");
		sql.addJoin("JOIN operator_competency oc ON oc.id = ec.competencyID");
		sql.addJoin("JOIN accounts o ON o.id = oc.opID");
		sql.addJoin("LEFT JOIN operator_competency_employee_file ocef ON ocef.competencyID = ec.competencyID AND ocef.employeeID = ec.employeeID");

		sql.addField("e.id employeeID");
		sql.addField("e.firstName, e.lastName");
		sql.addField("a.id");
		sql.addField("a.name");
		sql.addField("o.name opName");
		sql.addField("oc.label");
		sql.addField("oc.description");
		sql.addField("ocef.id fileID");
		sql.addField("ocef.expiration");
		sql.addField("ocef.fileName");

		if (permissions.isOperatorCorporate()) {
			sql.addWhere("o.id IN (" + Strings.implode(permissions.getVisibleAccounts()) + ")");
		} else if (permissions.isContractor()) {
			sql.addWhere("a.id = " + permissions.getAccountId());
		}

		addFilterToSQL();
	}

	private void addFilterToSQL() {
		if (filterOn(filter.getAccountName(), "- Company Name -")) {
			sql.addWhere("a.name like '%" + Strings.escapeQuotes(filter.getAccountName()) + "%'");
			setFiltered(true);
		}

		if (filterOn(filter.getFirstName())) {
			sql.addWhere("e.firstName like '%" + Strings.escapeQuotes(filter.getFirstName()) + "%'");
			setFiltered(true);
		}

		if (filterOn(filter.getLastName())) {
			sql.addWhere("e.lastName like '%" + Strings.escapeQuotes(filter.getLastName()) + "%'");
			setFiltered(true);
		}
	}

	public ReportFilterEmployee getFilter() {
		return filter;
	}

	public void setFilter(ReportFilterEmployee filter) {
		this.filter = filter;
	}
}
