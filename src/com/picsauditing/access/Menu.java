package com.picsauditing.access;

import java.util.List;

import com.picsauditing.actions.PicsActionSupport;
import com.picsauditing.jpa.entities.ContractorAccount;
import com.picsauditing.jpa.entities.Report;

@SuppressWarnings("serial")
public class Menu extends PicsActionSupport {

	// Why is this part of Menu.java?
	private ContractorAccount contractor = null;

	public String execute() throws Exception {
		loadPermissions();

		MenuComponent menu = new MenuComponent();

		if (!permissions.isLoggedIn()) {
			MenuBuilder.addNotLoggedInMenu(menu);
		} else {
			MenuBuilder.addDashboardMenu(menu);

			List<Report> reports = (List<Report>) dao.findWhere(Report.class, "id > 0");
			MenuBuilder.addMyReportsMenu(menu, reports);

			if (permissions.isContractor())
				MenuBuilder.addContractorMenu(menu, permissions);
			if (permissions.isAssessment())
				MenuBuilder.addAssessmentCenterMenu(menu);

			MenuBuilder.addManagementMenu(menu, permissions);
			MenuBuilder.addHelpMenu(menu);
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
