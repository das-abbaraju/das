package com.picsauditing.interceptors;

import com.opensymphony.xwork2.ActionContext;
import com.opensymphony.xwork2.ActionInvocation;
import com.opensymphony.xwork2.interceptor.AbstractInterceptor;
import com.picsauditing.access.Permissions;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * This Interceptor was built to intercept any request from a first-time Dynamic Report user
 * and redirect them to a Tutorial page, but only the first time they logged-in. This could
 * be re-purposed and used for other similar activities.
 */
public class FirstTimeUserInterceptor extends AbstractInterceptor {

    private static final long serialVersionUID = -8713997040045564789L;

    private static final Logger logger = LoggerFactory.getLogger(FirstTimeUserInterceptor.class);

    @Override
    public String intercept(ActionInvocation invocation) throws Exception {
        try {
            Permissions permissions = (Permissions) ActionContext.getContext().getSession().get("permissions");

            if (redirectUserToTutorial(permissions)) {
                invocation.getStack().set("url", "Tutorial!navigationMenu.action");
                return "redirect";
            }
        } catch (Exception e) {
            logger.info("Error occured when trying to redirect user to Menu Tutorial", e);
        }

        return invocation.invoke();
    }

    private boolean redirectUserToTutorial(Permissions permissions) {
        if (permissions == null) {
            return false;
        }

        return (permissions.isLoggedIn()
                && permissions.getUsingDynamicReportsDate() == null
                && permissions.isUsingDynamicReports());
    }

}
