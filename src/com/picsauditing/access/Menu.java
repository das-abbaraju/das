package com.picsauditing.access;

import java.util.List;

import com.picsauditing.actions.PicsActionSupport;
import com.picsauditing.jpa.entities.ContractorAccount;
import com.picsauditing.jpa.entities.Report;

@SuppressWarnings("serial")
public class Menu extends PicsActionSupport {
	private ContractorAccount contractor = null;
	private MenuBuilder builder = new MenuBuilder();

	public String execute() throws Exception {
		loadPermissions();

		if (!permissions.isLoggedIn()) {
			builder.buildNotLoggedIn();
		} else if (permissions.isContractor()) {
			builder.buildContractorMenu(permissions);
		} else if (permissions.isAssessment()) {
			builder.buildAssessmentCenter();
		} else {
			@SuppressWarnings("unchecked")
			List<Report> reports = (List<Report>) dao.findWhere(Report.class, "id > 0");
			builder.buildNew(permissions, reports, contractor);
		}

		builder.cleanupMenus();

		jsonArray = MenuWriter.exportMenuToExtJS(builder.getMenu());

		return JSON_ARRAY;
	}

	public ContractorAccount getContractor() {
		return contractor;
	}

	public void setContractor(ContractorAccount contractor) {
		this.contractor = contractor;
	}

}
