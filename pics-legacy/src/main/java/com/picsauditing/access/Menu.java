package com.picsauditing.access;

import java.util.ArrayList;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;

import com.picsauditing.actions.PicsActionSupport;
import com.picsauditing.actions.report.ManageReports;
import com.picsauditing.dao.ReportUserDAO;
import com.picsauditing.jpa.entities.ContractorAccount;
import com.picsauditing.jpa.entities.ReportUser;
import com.picsauditing.report.RecordNotFoundException;

@SuppressWarnings("serial")
public class Menu extends PicsActionSupport {

	@Autowired
	private ReportUserDAO reportUserDao;

	private ContractorAccount contractor = null;

	private MenuComponent menu;

	@Autowired
	private ContractorSubmenuBuilder contractorSubmenuBuilder;

	@Override
	public String execute() {
		menu = buildMainMenu();
		jsonArray = MenuWriter.convertMenuToJSON(menu);

		return JSON_ARRAY;
	}

	public String menu() {
		menu = buildMainMenu();
		return SUCCESS;
	}

	public String bootstrap3Menu() {
		menu = buildMainMenu();
		return "bootstrap3";
	}

	public String mobileMenu() {
		menu = buildMainMenu();
		return "mobile-menu";
	}

	private MenuComponent buildMainMenu() {
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

		return MenuBuilder.buildMenubar(permissions, favoriteReports);
	}

	public String contractorSubmenu() throws RecordNotFoundException {
		menu = buildContractorSubmenu(true);

		return "contractor-menu";
	}

	public String operatorSubmenu() throws RecordNotFoundException {
		menu = buildContractorSubmenu(false);
		jsonArray = MenuWriter.convertMenuToJSON(menu);

		return JSON_ARRAY;
	}

	private MenuComponent buildContractorSubmenu(boolean includeSupportMenu) throws RecordNotFoundException {
		loadPermissions();

		return contractorSubmenuBuilder.buildMenubar(contractor, permissions,
				includeSupportMenu);
	}

	public ContractorAccount getContractor() {
		return contractor;
	}

	public void setContractor(ContractorAccount contractor) {
		this.contractor = contractor;
	}

	public MenuComponent getMenu() {
		return menu;
	}
}
