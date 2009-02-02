package com.picsauditing.actions.report;

import com.opensymphony.xwork2.ActionContext;
import com.picsauditing.actions.PicsActionSupport;
import com.picsauditing.jpa.entities.ListType;
import com.picsauditing.mail.WizardSession;
import com.picsauditing.util.ReportFilter;
import com.picsauditing.util.ReportFilterAudit;
import com.picsauditing.util.ReportFilterContractor;
import com.picsauditing.util.ReportFilterUser;

@SuppressWarnings("serial")
public class ReportFilterAjax extends PicsActionSupport {

	private boolean clear;
	private ListType listType;
	private ReportFilter filter = null;

	@Override
	public String execute() throws Exception {
		if (!forceLogin())
			return LOGIN;

		WizardSession wizardSession = new WizardSession(ActionContext.getContext().getSession());
		if (clear)
			wizardSession.clear();
		if (listType != null)
			wizardSession.setListTypes(listType);
		listType = wizardSession.getListType();
		
		if (listType != null) {
			if (listType.equals(ListType.Contractor)) {
				ReportFilterContractor filter = wizardSession.getContractorFilter();
				filter.setDestinationAction("ContractorList");
				filter.setVisible("Y"); // default to only active contractors
				filter.setShowEmailSentDate(true);
				filter.setShowEmailTemplate(true);
				filter.setEmailListType(ListType.Contractor);
				this.filter = filter;
			}
			if (listType.equals(ListType.Audit)) {
				ReportFilterAudit filter = wizardSession.getAuditFilter();
				filter.setDestinationAction("ReportAuditList");
				filter.setVisible("Y"); // default to only active contractors
				filter.setShowEmailSentDate(true);
				filter.setShowEmailTemplate(true);
				filter.setEmailListType(ListType.Audit);
				this.filter = filter;
			}
			if (listType.equals(ListType.User)) {
				ReportFilterUser filter = new ReportFilterUser();
				filter.setDestinationAction("UserList");
				this.filter = filter;
			}
		}
		if (filter == null) {
			filter = new ReportFilter();
		}
		
		filter.setPermissions(permissions);
		filter.setAjax(true);
		filter.setAllowCollapsed(false);
		filter.setAllowMailMerge(true);
		
		if (listType != null && listType.equals(ListType.User))
			return "userfilters";

		return SUCCESS;
	}

	public ReportFilter getFilter() {
		return filter;
	}

	public void setListType(ListType listType) {
		this.listType = listType;
	}

	public void setClear(boolean clear) {
		this.clear = clear;
	}
}
