package com.picsauditing.interceptors;

import java.util.Date;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;

import com.opensymphony.xwork2.ActionContext;
import com.opensymphony.xwork2.ActionInvocation;
import com.opensymphony.xwork2.interceptor.AbstractInterceptor;
import com.picsauditing.access.Permissions;
import com.picsauditing.dao.UserDAO;
import com.picsauditing.jpa.entities.User;

public class FirstTimeDynamicReportUser extends AbstractInterceptor {

	@Autowired
	private UserDAO userDAO;

	private static final long serialVersionUID = -8713997040045564789L;

	private static final Logger logger = LoggerFactory.getLogger(FirstTimeDynamicReportUser.class);

	@Override
	public String intercept(ActionInvocation invocation) throws Exception {
		try {
			Permissions permissions = (Permissions) ActionContext.getContext().getSession().get("permissions");

			if (permissions.isLoggedIn()) {
				User user = userDAO.find(permissions.getUserId());
				if (user.getUsingDynamicReportsDate() == null) {
					// TODO: possible put the "usingDynamicReports" into the Permissions object
					user.setUsingDynamicReportsDate(new Date());
					userDAO.save(user);

					invocation.getStack().set("url", "Tutorial.action");
					return "redirect";
				}
			}
		} catch (Exception e) {
			logger.info("Error occured when trying to redirect user to Menu Tutorial", e);
		}

		return invocation.invoke();
	}

}
