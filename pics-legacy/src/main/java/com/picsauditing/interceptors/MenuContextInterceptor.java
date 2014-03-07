package com.picsauditing.interceptors;

import com.opensymphony.xwork2.ActionContext;
import com.opensymphony.xwork2.ActionInvocation;
import com.opensymphony.xwork2.interceptor.AbstractInterceptor;
import com.picsauditing.actions.PicsActionSupport;
import com.picsauditing.actions.contractors.ContractorActionSupport;
import com.picsauditing.actions.users.ProfileEdit;
import com.picsauditing.actions.users.UsersManage;

import java.util.Map;

/**
 * This interceptor checks requests to see if the application needs to display a contractor menu.
 * <p/>
 * Given that subsequent requests are made to generate the menu via the s:action tags inside JSPs, the application
 * stores a boolean in the valuestack to display the submenu if the outermost action has a contractor in context.
 */
public class MenuContextInterceptor extends AbstractInterceptor {
	public static final String CONTRACTOR_SUBMENU_DISPLAY = "PICS_MenuContext_ContractorSubmenu";

	@Override
	public String intercept(ActionInvocation invocation) throws Exception {
		Object action = invocation.getAction();

		if (contractorPage(action)) {
			setSubmenuDisplayToTrueOnValuestack();
			addContractorToParameterMap(action);
		}

		return invocation.invoke();
	}

	private void setSubmenuDisplayToTrueOnValuestack() {
		valuestackContext().put(CONTRACTOR_SUBMENU_DISPLAY, true);
	}

	private Map<String, Object> valuestackContext() {
		return ActionContext.getContext().getValueStack().getContext();
	}

	private boolean contractorPage(Object action) {
        boolean actionIsAContractorPage = false;
		if (action instanceof ContractorActionSupport) {
			ContractorActionSupport contractorActionSupport = (ContractorActionSupport) action;

			if (contractorActionSupport.getContractor() != null && contractorActionSupport.getContractor().getId() == 0) {
				actionIsAContractorPage = false;
			} else {
                actionIsAContractorPage = contractorActionSupport.isShowContractorSubmenu();
            }
		} else if (action instanceof UsersManage) {
            // This seems unnecessary since UsersManage is a child of PicsActionSupport and
            // the logic is exactly the same
			UsersManage usersManage = (UsersManage) action;
			if (usersManage.getAccount() != null && usersManage.getAccount().isContractor()) {
                actionIsAContractorPage = true;
			}
		} else if (action instanceof ProfileEdit) {
            actionIsAContractorPage = false;
        } else if (action instanceof PicsActionSupport) {
			// Try to find if there's an account set
			PicsActionSupport picsAction = (PicsActionSupport) action;
			if (picsAction.getAccount() != null && picsAction.getAccount().isContractor()) {
                actionIsAContractorPage = true;
			}
		}

		return actionIsAContractorPage;
	}

	/**
	 * If there is an account set on this page and the account is of type Contractor, then the application neesd to
	 * display the contractor submenu.
	 * <p/>
	 * This is applicable to UsersManage, ManageEmployees and other pages that don't extend ContractorActionSupport but
	 * still have an account object set of type Contractor.
	 * <p/>
	 * The contractor is added to the parameter map for later calls to build the submenu via
	 * {@link com.picsauditing.menu.controller.Menu#contractorSubmenu}
	 *
	 * @param action
	 */
	private void addContractorToParameterMap(Object action) {
		if (action instanceof UsersManage) {
			UsersManage usersManage = (UsersManage) action;
			if (usersManage.getAccount().isContractor()) {
				ActionContext.getContext().getParameters().put("contractor", usersManage.getAccount());
			}
		}

		if (action instanceof PicsActionSupport) {
			PicsActionSupport picsAction = (PicsActionSupport) action;
			if (picsAction.getAccount() != null && picsAction.getAccount().isContractor()) {
				ActionContext.getContext().getParameters().put("contractor", picsAction.getAccount());
			}
		}
	}
}
