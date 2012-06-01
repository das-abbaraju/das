package com.picsauditing.access;

import java.util.List;

import com.picsauditing.actions.PicsActionSupport;
import com.picsauditing.jpa.entities.ContractorAccount;
import com.picsauditing.jpa.entities.Report;
import com.picsauditing.jpa.entities.ReportUserReport;
import com.picsauditing.jpa.entities.User;

@SuppressWarnings("serial")
public class Menu extends PicsActionSupport {

	// Why is this part of Menu.java?
	private ContractorAccount contractor = null;

	public String execute() throws Exception {
		loadPermissions();

		// TODO Only add favorites to the menu
		List<ReportUserReport> userReports = dao.findWhere(ReportUserReport.class, "userID = " + permissions.getUserId());
		List<Report> baseReports = dao.findWhere(Report.class, "t.createdBy = " + User.SYSTEM);

		MenuComponent menu = MenuBuilder.buildMenubar(permissions, userReports, baseReports);

		jsonArray = MenuWriter.exportMenuToExtJS(menu);

		return JSON_ARRAY;
	}

	public ContractorAccount getContractor() {
		return contractor;
	}

	public void setContractor(ContractorAccount contractor) {
		this.contractor = contractor;
	}
}
