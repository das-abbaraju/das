package com.picsauditing.actions.report;

import com.picsauditing.jpa.entities.Report;
import org.springframework.beans.factory.annotation.Autowired;

import com.picsauditing.actions.PicsActionSupport;
import com.picsauditing.service.PermissionService;
import com.picsauditing.service.ReportPreferencesService;
import com.picsauditing.jpa.entities.ReportUser;

@SuppressWarnings("serial")
public class ReportBootstrap extends PicsActionSupport {

	private static final String UNAUTHORIZED_ACTION = "ManageReports!unauthorized.action";

	private static final String ERROR_MESSAGE_I18N_KEY = "Report.Error.ViewPermissions";

	@Autowired
	public ReportPreferencesService reportPreferencesService;
	@Autowired
	private PermissionService permissionService;

	private String name;
	private int report;

	@Override
	public String execute() throws Exception {
		if (report == 0) {
			name = "Missing report ID parameter";
			return SUCCESS;
		}

		Report reportObject = null;

		try {
			reportObject = dao.find(Report.class, report);
		} catch (Exception e) {
			// Don't care
		}

		if (!permissionService.canUserViewReport(getUser(), reportObject)) {
			addActionError(getText(ERROR_MESSAGE_I18N_KEY));
			return setUrlForRedirect(UNAUTHORIZED_ACTION + "?referringPage=" + getReferer());
		}

		ReportUser reportUser = reportPreferencesService.loadOrCreateReportUser(permissions.getUserId(), report);
		reportPreferencesService.stampViewed(reportUser, permissions);

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
