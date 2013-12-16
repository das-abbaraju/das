package com.picsauditing.menu;

import com.opensymphony.xwork2.ActionContext;
import com.picsauditing.actions.contractors.ContractorActionSupport;
import com.picsauditing.actions.users.UsersManage;

public class MenuDisplay {

	public static boolean isContractorSubmenuVisible() {
		Object action = ActionContext.getContext().getActionInvocation().getAction();
		if (action instanceof ContractorActionSupport) {
			ContractorActionSupport contractorActionSupport = (ContractorActionSupport) action;

			return contractorActionSupport.isShowContractorSubmenu();
		}

		if (action instanceof UsersManage) {
			UsersManage usersManage = (UsersManage) action;
			return usersManage.getAccount().isContractor();
		}

		return false;
	}
}
