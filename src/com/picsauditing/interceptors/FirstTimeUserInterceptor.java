package com.picsauditing.interceptors;

import com.opensymphony.xwork2.ActionContext;
import com.opensymphony.xwork2.ActionInvocation;
import com.opensymphony.xwork2.interceptor.AbstractInterceptor;
import com.picsauditing.access.Permissions;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class FirstTimeUserInterceptor extends AbstractInterceptor {

	private static final long serialVersionUID = -8713997040045564789L;

	private static final Logger logger = LoggerFactory.getLogger(FirstTimeUserInterceptor.class);
	public static final String REFERENCE_NAVIGATION_MENU_ACTION = "Reference!navigationMenu.action";
	public static final String REFERENCE_REPORTS_MANAGER_ACTION = "Reference!reportsManager.action";

	@Override
	public String intercept(ActionInvocation invocation) throws Exception {
		try {
			Permissions permissions = (Permissions) ActionContext.getContext().getSession()
					.get(Permissions.SESSION_PERMISSIONS_COOKIE_KEY);

			if (shouldRedirectToV7NavigationTutorial(permissions)) {
				invocation.getStack().set("url", REFERENCE_NAVIGATION_MENU_ACTION);
				return "redirect";
			}

			if (shouldRedirectToReportsManagerTutorial(permissions)) {
				invocation.getStack().set("url", REFERENCE_REPORTS_MANAGER_ACTION);
				return "redirect";
			}
		} catch (Exception e) {
			logger.info("Error occurred when trying to redirect user to tutorial", e);
		}

		return invocation.invoke();
	}

	private boolean shouldRedirectToV7NavigationTutorial(Permissions permissions) {
		if (permissions == null) {
			return false;
		}

		return (permissions.isLoggedIn() && permissions.getUsingVersion7MenusDate() == null && permissions
				.isUsingVersion7Menus());
	}

	private boolean shouldRedirectToReportsManagerTutorial(Permissions permissions) {
		if (permissions == null) {
			return false;
		}

		return (permissions.isLoggedIn() && permissions.isDynamicReportsUser()
				&& permissions.getReportsManagerTutorialDate() == null );
	}

}
