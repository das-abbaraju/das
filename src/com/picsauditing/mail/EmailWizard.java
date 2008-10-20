package com.picsauditing.mail;

import com.picsauditing.actions.PicsActionSupport;
import com.picsauditing.util.ReportFilterAudit;
import com.picsauditing.util.ReportFilterContractor;

public class EmailWizard extends PicsActionSupport {
	private ReportFilterContractor filterContractor = new ReportFilterContractor();
	private ReportFilterAudit filterAudit = new ReportFilterAudit();
	//private ReportFilterUser filterUser;
	
	public String execute() {
		if (!forceLogin())
			return LOGIN;
		filterContractor.setPermissions(permissions);
		filterAudit.setPermissions(permissions);
		
		return SUCCESS;
	}
	
	public ReportFilterContractor getFilter() {
		return filterContractor;
	}

	public ReportFilterContractor getFilterContractor() {
		return filterContractor;
	}

	public void setFilterContractor(ReportFilterContractor filterContractor) {
		this.filterContractor = filterContractor;
	}

	public ReportFilterAudit getFilterAudit() {
		return filterAudit;
	}

	public void setFilterAudit(ReportFilterAudit filterAudit) {
		this.filterAudit = filterAudit;
	}
	
	
}
