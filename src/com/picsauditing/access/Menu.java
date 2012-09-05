package com.picsauditing.access;

import java.util.ArrayList;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;

import com.picsauditing.actions.PicsActionSupport;
import com.picsauditing.actions.report.ManageReports;
import com.picsauditing.dao.ReportUserDAO;
import com.picsauditing.jpa.entities.ContractorAccount;
import com.picsauditing.jpa.entities.ReportUser;

@SuppressWarnings("serial")
public class Menu extends PicsActionSupport {

	@Autowired
	private ReportUserDAO reportUserDao;

	// Why is this part of Menu.java?
	private ContractorAccount contractor = null;

	public String execute() {
		loadPermissions();

		List<ReportUser> favoriteReports = new ArrayList<ReportUser>();

		// Don't break if the database schema changes
		try {
			favoriteReports = reportUserDao.findAllFavorite(permissions.getUserId());

			if (favoriteReports.size() > ManageReports.MAX_REPORTS_IN_MENU) {
				favoriteReports = favoriteReports.subList(0, ManageReports.MAX_REPORTS_IN_MENU);
			}
		} catch (Exception e) {
			// No reports in the menu
		}

		MenuComponent menu = MenuBuilder.buildMenubar(permissions, favoriteReports);

		jsonArray = MenuWriter.convertMenuToJSON(menu);

		return JSON_ARRAY;
	}

	public ContractorAccount getContractor() {
		return contractor;
	}

	public void setContractor(ContractorAccount contractor) {
		this.contractor = contractor;
	}
}
