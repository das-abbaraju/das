package com.picsauditing.actions.report;

import java.io.IOException;

import com.picsauditing.access.OpPerms;
import com.picsauditing.access.OpType;
import com.picsauditing.access.RequiredPermission;
import com.picsauditing.actions.PicsActionSupport;
import com.picsauditing.jpa.entities.Report;

@SuppressWarnings("serial")
public class CreateReport extends PicsActionSupport {
	static private String REPORT_URL = "Report.action?report=";

	private Report report;

	@Override
	@RequiredPermission(value = OpPerms.Report)
	public String execute() throws Exception {
		return SUCCESS;
	}

	@RequiredPermission(value = OpPerms.Report, type = OpType.Edit)
	public String save() throws IOException {
		report.setAuditColumns(permissions);
		dao.save(report);
		return setUrlForRedirect(REPORT_URL + report.getId());
	}

	public Report getReport() {
		return report;
	}

	public void setReport(Report report) {
		this.report = report;
	}

}
