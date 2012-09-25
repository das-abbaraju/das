package com.picsauditing.actions.report;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;

import com.opensymphony.xwork2.ActionContext;
import com.picsauditing.actions.PicsActionSupport;
import com.picsauditing.model.ReportModel;

@SuppressWarnings("serial")
public class ReportBootstrap extends PicsActionSupport {

	@Autowired
	private ReportModel reportModel;

	private String name;
	private int id;

	private static final Logger logger = LoggerFactory.getLogger(ReportBootstrap.class);

	public String execute() {
		String status = SUCCESS;

		if (id == 0) {
			name = "Missing report ID parameter";
			return SUCCESS;
		}

		// No matter what junk we get in the url, redirect
		try {
			if (!reportModel.canUserViewAndCopy(permissions.getUserId(), id)) {
				String errorMessage = "You do not have permissions to view that report.";
				ActionContext.getContext().getSession().put("errorMessage", errorMessage);
			}
		} catch (Exception e) {
			// Probably a null pointer
			logger.error(e.toString());
		}

		return status;
	}

	public String getReportName() {
		return name;
	}

	public void setId(int id) {
		this.id = id;
	}

	@Deprecated
	public void setReport(int id) {
		setId(id);
	}
}
