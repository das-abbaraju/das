package com.picsauditing.actions.report;

import com.picsauditing.actions.PicsActionSupport;
import com.picsauditing.util.ReportFilter;
import com.picsauditing.util.ReportFilterAccount;
import com.picsauditing.util.ReportFilterAudit;
import com.picsauditing.util.ReportFilterContractor;
import com.picsauditing.util.ReportFilterUser;

public class ReportFilterAjax extends PicsActionSupport {
	private String listType = "";
	private ReportFilter filter = null;
	
	@Override
	public String execute() throws Exception {
		if (!forceLogin())
			return LOGIN;
		if (listType == null || listType == "")
			listType = "Contractor";

		if (listType.equals("Contractor")) {
			ReportFilterContractor filter = new ReportFilterContractor();
			filter.setDestinationAction("ContractorList");
			this.filter = filter;
		}
		if (listType.equals("Audit")) {
			ReportFilterAudit filter = new ReportFilterAudit();
			filter.setDestinationAction("ReportAuditList");
			this.filter = filter;
		}
		if (listType.equals("User")) {
			ReportFilterUser filter = new ReportFilterUser();
			filter.setDestinationAction("UserList");
			this.filter = filter;
		}
		filter.setPermissions(permissions);
		filter.setAjax(true);
		filter.setAllowCollapsed(false);
		filter.setAllowMailMerge(true);
		
		if (listType.equals("User"))
			return "userfilters";
		if (filter == null)
			return BLANK;
		
		return SUCCESS;
	}
	
	public ReportFilter getFilter() {
		return filter;
	}
	public void setListType(String listType) {
		this.listType = listType;
	}
}
