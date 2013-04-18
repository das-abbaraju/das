package com.picsauditing.actions.report;

import java.io.IOException;

import com.picsauditing.access.OpPerms;
import com.picsauditing.access.OpType;
import com.picsauditing.access.RequiredPermission;
import com.picsauditing.actions.PicsActionSupport;
import com.picsauditing.jpa.entities.Report;
import com.picsauditing.jpa.entities.ReportPermissionUser;
import com.picsauditing.service.PermissionService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;

@SuppressWarnings("serial")
public class CreateReport extends PicsActionSupport {
	static private String REPORT_URL = "Report.action?report=";

	@Autowired
	private PermissionService permissionService;

	private Report report;

	private static final Logger logger = LoggerFactory.getLogger(CreateReport.class);

	@Override
	@RequiredPermission(value = OpPerms.Report)
	public String execute() throws Exception {
		return SUCCESS;
	}

	@RequiredPermission(value = OpPerms.Report, type = OpType.Edit)
	public String save() throws IOException {
		report.setAuditColumns(permissions);
		report.setOwner(getUser());
		dao.save(report);

		try {
			int userId = getUser().getId();
			ReportPermissionUser newOwner = permissionService.grantUserEditPermission(userId, userId, report.getId());
			newOwner.setAuditColumns(permissions);
			dao.save(newOwner);
		} catch (Exception e) {
			logger.error("Could not add report_permission_user row for user " + permissions.getUserId() + " and report " + report.getId());
			addActionError("Could not add report_permission_user row for user " + permissions.getUserId() + " and report " + report.getId());
			return SUCCESS;
		}

		return setUrlForRedirect(REPORT_URL + report.getId());
	}

	public Report getReport() {
		return report;
	}

	public void setReport(Report report) {
		this.report = report;
	}

}
