package com.picsauditing.actions.report;

import java.io.IOException;

import org.apache.struts2.ServletActionContext;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;

import com.opensymphony.xwork2.ActionContext;
import com.picsauditing.actions.PicsActionSupport;
import com.picsauditing.jpa.entities.Report;
import com.picsauditing.model.ReportModel;

@SuppressWarnings("serial")
public class ReportBootstrap extends PicsActionSupport {

	@Autowired
	private ReportModel reportModel;

	private Report report;

	private static final Logger logger = LoggerFactory.getLogger(ReportBootstrap.class);

	public String execute() {
		String status = SUCCESS;

		if (report == null) {
			// No matter what junk we get in the url, redirect
			try {
				status = setUrlForRedirect(ManageReports.LANDING_URL);

				String dirtyReportIdParameter = ServletActionContext.getRequest().getParameter("report");
				// Don't trust user input!
				int reportId = Integer.parseInt(dirtyReportIdParameter);

				if (!reportModel.canUserViewAndCopy(permissions.getUserId(), reportId)) {
					String errorMessage = "You do not have permissions to view that report.";
					ActionContext.getContext().getSession().put("errorMessage", errorMessage);
				}
			} catch (NumberFormatException nfe) {
				// Someone typed junk into the url
				logger.warn(nfe.toString());
			} catch (IOException ioe) {
				// Someone typed junk into the url
				logger.warn("Problem with setUrlForRedirect() for not logged in user.", ioe);
			} catch (Exception e) {
				// Probably a null pointer
				logger.error(e.toString());
			}
		}

		return status;
	}

	public Report getReport() {
		return report;
	}

	public void setReport(Report report) {
		this.report = report;
	}
}
