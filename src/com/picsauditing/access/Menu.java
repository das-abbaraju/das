package com.picsauditing.access;

import java.util.List;

import com.picsauditing.actions.PicsActionSupport;
import com.picsauditing.jpa.entities.ContractorAccount;
import com.picsauditing.jpa.entities.Report;

@SuppressWarnings("serial")
public class Menu extends PicsActionSupport {

	// This should not be part of Menu.java
	private ContractorAccount contractor = null;

	public String execute() throws Exception {
		loadPermissions();

		MenuBuilder builder = new MenuBuilder();
		MenuComponent menu = null;

		if (!permissions.isLoggedIn()) {
			menu = builder.buildNotLoggedIn();
		} else if (permissions.isContractor()) {
			menu = builder.buildContractorMenu(permissions);
		} else if (permissions.isAssessment()) {
			menu = builder.buildAssessmentCenter();
		} else {
			List<Report> reports = (List<Report>) dao.findWhere(Report.class, "id > 0");
			menu = builder.buildGeneric(permissions, reports, contractor);
		}

		handleSingleChildMenu(menu);

		jsonArray = MenuWriter.exportMenuToExtJS(menu);

		return JSON_ARRAY;
	}

	private void handleSingleChildMenu(MenuComponent menu) {
		if (menu == null)
			return;

		if (menu.getChildren().size() == 1)
			menu = menu.getChildren().get(0);
	}

	public ContractorAccount getContractor() {
		return contractor;
	}

	public void setContractor(ContractorAccount contractor) {
		this.contractor = contractor;
	}
}
