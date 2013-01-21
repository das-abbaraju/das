package com.picsauditing.actions.report;

import com.picsauditing.model.report.ReportService;
import org.springframework.beans.factory.annotation.Autowired;

import com.picsauditing.actions.PicsActionSupport;

@SuppressWarnings("serial")
public class ReportBootstrap extends PicsActionSupport {

	private static final String URL_FOR_REDIRECT_FOR_NOT_VIEWABLE = "ManageReports!favoritesList.action";

	private static final String ERROR_MESSAGE_I18N_KEY = "Report.Error.ViewPermissions";
	
	@Autowired
	private ReportService reportService;

	private String name;
	private int report;

	public String execute() throws Exception {
		if (report == 0) {
			name = "Missing report ID parameter";
			return SUCCESS;
		}

		if (!reportService.canUserViewAndCopy(permissions, report)) {
			addActionError(getText(ERROR_MESSAGE_I18N_KEY));
			return setUrlForRedirect(URL_FOR_REDIRECT_FOR_NOT_VIEWABLE);
		}
		
		name = "Loading Report " + report + " ...";

		return SUCCESS;
	}

	public String getReportName() {
		return name;
	}

	@Deprecated
	public void setId(int id) {
	}

	public void setReport(int id) {
		report = id;
	}
}
