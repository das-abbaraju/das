package com.picsauditing.actions.report;

import org.springframework.beans.factory.annotation.Autowired;

import com.opensymphony.xwork2.ActionContext;
import com.picsauditing.actions.PicsActionSupport;
import com.picsauditing.model.ReportModel;

@SuppressWarnings("serial")
public class ReportBootstrap extends PicsActionSupport {

	@Autowired
	private ReportModel reportModel;

	private String name;
	private int report;

	public String execute() {
		if (report == 0) {
			name = "Missing report ID parameter";
			return SUCCESS;
		}

		if (!reportModel.canUserViewAndCopy(permissions, report)) {
			String errorMessage = "You do not have permissions to view that report.";
			ActionContext.getContext().getSession().put("errorMessage", errorMessage);
		}
		name = "Loading Report " + report + " ...";

		return SUCCESS;
	}

	public String getReportName() {
		return name;
	}

	public void setId(int id) {
	}

	public void setReport(int id) {
		report = id;
	}
}
