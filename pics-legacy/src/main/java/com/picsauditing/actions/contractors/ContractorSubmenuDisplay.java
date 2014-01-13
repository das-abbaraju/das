package com.picsauditing.actions.contractors;

import com.opensymphony.xwork2.ActionContext;
import com.picsauditing.access.Permissions;
import com.picsauditing.jpa.entities.Account;
import com.picsauditing.jpa.entities.ContractorAccount;

public class ContractorSubmenuDisplay {
	public static boolean isShowContractorSubmenu() {
		Permissions permissions = permissions = (Permissions) ActionContext.getContext().getSession()
				.get(Permissions.SESSION_PERMISSIONS_COOKIE_KEY);

		if (permissions == null) {
			permissions = new Permissions();
		}

		if (permissions.isContractor()) {
			return false;
		}

		if (!permissions.isUsingVersion7Menus()) {
			return false;
		}

		Object action = ActionContext.getContext().getActionInvocation().getAction();

		if (action instanceof ContractorActionSupport) {
			ContractorActionSupport contractorActionSupport = (ContractorActionSupport) action;
			ContractorAccount contractor = contractorActionSupport.getContractor();
			Account account = contractorActionSupport.getAccount();

			if (contractor == null || (account != null && !account.getType().equals("Contractor"))) {
				return false;
			}

			return contractorActionSupport.isShowHeader();
		}

		return false;
	}
}
