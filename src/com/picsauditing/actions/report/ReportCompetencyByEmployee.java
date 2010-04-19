package com.picsauditing.actions.report;

import com.opensymphony.xwork2.ActionContext;
import com.picsauditing.mail.WizardSession;
import com.picsauditing.search.SelectSQL;

@SuppressWarnings("serial")
public class ReportCompetencyByEmployee extends ReportActionSupport {

	protected SelectSQL sql = new SelectSQL();

	public ReportCompetencyByEmployee() {
		orderByDefault = "a.name, e.lastName, e.firstName";
	}

	protected void buildQuery() {
		sql = new SelectSQL("employee e");

		sql.addJoin("JOIN accounts a on a.id = e.accountID");
		sql.addJoin("JOIN (SELECT DISTINCT er.employeeID, jc.competencyID FROM employee_role er"
				+ " JOIN job_competency jc ON jc.jobRoleID = er.jobRoleID) jc ON jc.employeeID = e.id");
		sql.addJoin("LEFT JOIN employee_competency ec ON ec.competencyID = jc.competencyID AND e.id = ec.employeeID");
		sql.addGroupBy("e.id");

		if (permissions.isContractor())
			sql.addWhere("a.id = " + permissions.getAccountId());

		// sql.addField("e.id");
		sql.addField("e.firstName");
		sql.addField("e.lastName");
		sql.addField("a.id AS accountID");
		sql.addField("a.name");
		sql.addField("COUNT(jc.competencyID) AS required");
		sql.addField("SUM(IFNULL(ec.skilled,0)) AS skilled");
	}

	@SuppressWarnings("unchecked")
	public String execute() throws Exception {
		if (!forceLogin())
			return LOGIN;

		if (runReport()) {
			buildQuery();
			run(sql);

			WizardSession wizardSession = new WizardSession(ActionContext.getContext().getSession());
			wizardSession.clear();

			return returnResult();
		}
		return SUCCESS;
	}

	private String returnResult() {
		return SUCCESS;
	}

	private boolean runReport() {
		return true;
	}

}
