package com.picsauditing.access;

import java.util.List;

import com.picsauditing.actions.PicsActionSupport;
import com.picsauditing.jpa.entities.ContractorAccount;
import com.picsauditing.jpa.entities.ReportUserReport;

@SuppressWarnings("serial")
public class Menu extends PicsActionSupport {

	// Why is this part of Menu.java?
	private ContractorAccount contractor = null;

	public String execute() throws Exception {
		loadPermissions();

		// TODO Only add favorites to the menu
		List<ReportUserReport> reports = dao.findWhere(ReportUserReport.class, "userID = " + permissions.getUserId());

		MenuComponent menu = MenuBuilder.buildMenubar(permissions, reports);

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
