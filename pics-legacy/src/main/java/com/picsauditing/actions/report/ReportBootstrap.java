package com.picsauditing.actions.report;

import org.springframework.beans.factory.annotation.Autowired;

import com.picsauditing.access.UnauthorizedException;
import com.picsauditing.actions.PicsActionSupport;
import com.picsauditing.jpa.entities.Report;
import com.picsauditing.jpa.entities.ReportUser;
import com.picsauditing.service.PermissionService;
import com.picsauditing.service.ReportPreferencesService;

@SuppressWarnings("serial")
public class ReportBootstrap extends PicsActionSupport {
	private static final String ERROR_MESSAGE_I18N_KEY = "Report.Error.ViewPermissions";

	@Autowired
	public ReportPreferencesService reportPreferencesService;
	@Autowired
	private PermissionService permissionService;

	private String name;
	private int report;

	@Override
	public String execute() throws UnauthorizedException {
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

		if (!permissionService.canUserViewReport(getUser(), reportObject, permissions)) {
			addActionError(getText(ERROR_MESSAGE_I18N_KEY));

			throw new UnauthorizedException("You do not have access to this report.");
		}

		ReportUser reportUser = reportPreferencesService.loadOrCreateReportUser(permissions.getUserId(), report);
		reportPreferencesService.stampViewed(reportUser, permissions);

		name = getText("Report.execute.loading.title", new Object[] { reportObject.getName() });

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