package com.picsauditing.actions.report;

import java.io.IOException;

import com.opensymphony.xwork2.ActionContext;
import com.picsauditing.mail.WizardSession;
import com.picsauditing.search.SelectSQL;

@SuppressWarnings("serial")
public class ReportComplianceByEmployee extends ReportActionSupport {
	protected SelectSQL sql = new SelectSQL();
	
	public ReportComplianceByEmployee() {
		orderByDefault = "a.name, e.lastName";
	}
	
	protected boolean runReport() {
		return true;
	}
	
	protected void buildQuery() {
		sql = new SelectSQL();
		sql.setFromTable("employee_competency ec");
		sql.setSQL_CALC_FOUND_ROWS(true);
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
	
	protected String returnResult() throws IOException {
		return SUCCESS;
	}
}
