package com.picsauditing.access;

import java.util.ArrayList;
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

		//List<Report> reports = (List<Report>) dao.findWhere(Report.class, "id > 0");
		// TODO Change this to use the reports_user_reports table
		List<Report> reports = new ArrayList<Report>();

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
