package com.picsauditing.menu.controller;

import com.opensymphony.xwork2.ActionContext;
import com.picsauditing.access.OpPerms;
import com.picsauditing.actions.PicsActionSupport;
import com.picsauditing.actions.report.ManageReports;
import com.picsauditing.dao.ReportUserDAO;
import com.picsauditing.jpa.entities.*;
import com.picsauditing.menu.MenuComponent;
import com.picsauditing.menu.MenuWriter;
import com.picsauditing.menu.builder.ContractorSubmenuBuilder;
import com.picsauditing.menu.builder.MenuBuilder;
import com.picsauditing.report.RecordNotFoundException;
import com.picsauditing.util.generic.GenericPredicate;
import org.apache.commons.collections.CollectionUtils;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

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
		contractor = findContractor();

		if (contractor != null) {
			menu = buildContractorSubmenu(false);
		}

		return "contractor-menu";
	}

	public String operatorSubmenu() throws RecordNotFoundException {
		menu = buildContractorSubmenu(false);
		jsonArray = MenuWriter.convertMenuToJSON(menu);

		return JSON_ARRAY;
	}

	private MenuComponent buildContractorSubmenu(boolean includeSupportMenu) throws RecordNotFoundException {
		loadPermissions();

		MenuComponent contractorSubmenu = contractorSubmenuBuilder.buildMenubar(contractor, permissions, includeSupportMenu);
		MenuComponent contractorCompanyMenu = MenuBuilder.getCompanyMenuFor(contractor, permissions);

		contractorSubmenu.getChildren().add(0, contractorCompanyMenu);

		return contractorSubmenu;
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

	public boolean isShowContractorSubmenu() {
		loadPermissions();

		if (permissions.isContractor() || !permissions.isUsingVersion7Menus() || !visibleToOperator()) {
			return false;
		}

		if (permissions.isOnlyAuditor()) {
			return auditorCanViewAnAudit();
		}

		contractor = findContractor();
		if (contractor == null) {
			return false;
		}

		return true;
	}

	private ContractorAccount findContractor() {
		if (contractor != null && contractor.getId() > 0) {
			return contractor;
		}

		if (account != null && account.isContractor()) {
			return (ContractorAccount) account;
		}

		return null;
	}

	private Map<String, Object> valueStackContext() {
		return ActionContext.getContext().getValueStack().getContext();
	}

	private boolean auditorCanViewAnAudit() {
		for (ContractorAudit audit : getActiveAudits()) {
			if (audit.getAuditor() != null && audit.getAuditor().getId() == permissions.getUserId()) {
				for (ContractorAuditOperator cao : audit.getOperators()) {
					if (cao.getStatus().before(AuditStatus.Complete)) {
						return true;
					}
				}
			}
		}
		return false;
	}

	private List<ContractorAudit> getActiveAudits() {
		List<ContractorAudit> activeAudits = new ArrayList<>(contractor.getAudits());

		CollectionUtils.filter(activeAudits, new GenericPredicate<ContractorAudit>() {
			@Override
			public boolean evaluate(Object object) {
				return super.evaluate(object);    //To change body of overridden methods use File | Settings | File Templates.
			}

			@Override
			public boolean evaluateEntity(ContractorAudit audit) {
				return audit.isVisibleTo(permissions) && !audit.isExpired();
			}
		});

		return activeAudits;
	}

	private boolean visibleToOperator() {
		if (!permissions.hasPermission(OpPerms.ContractorDetails)) {
			return false;
		}

		if (permissions.isOperatorCorporate()) {
			return !contractorIsVisibleToOperator();
		}

		return true;
	}

	private boolean contractorIsVisibleToOperator() {
		for (ContractorOperator contractorOperator : contractor.getOperators()) {
			int operatorId = contractorOperator.getOperatorAccount().getId();

			if (permissions.isOperator() && permissions.getAccountId() == operatorId) {
				return true;
			}

			if (permissions.isCorporate() && permissions.getOperatorChildren().contains(operatorId)) {
				return true;
			}
		}

		return false;
	}

	public MenuComponent getContractorMenu() throws RecordNotFoundException {
		MenuComponent menu = contractorSubmenuBuilder.buildMenubar(contractor, permissions, false);
		MenuComponent companyMenu = MenuBuilder.getCompanyMenuFor(contractor, permissions);

		menu.getChildren().add(0, companyMenu);

		return menu;
	}
}
