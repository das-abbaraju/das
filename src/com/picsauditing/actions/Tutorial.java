package com.picsauditing.actions;

import java.util.Date;

import org.springframework.beans.factory.annotation.Autowired;

import com.picsauditing.dao.UserDAO;
import com.picsauditing.jpa.entities.User;

@SuppressWarnings("serial")
public class Tutorial extends PicsActionSupport {

	@Autowired
	private UserDAO userDAO;

	public String navigationMenu() throws Exception {
		loadPermissions(false);

		// prevent repeated updates to the using dynamic reports date
		if (isFirstTimeDynamicReportUser()) {
			setUsingDynamicReportsDate();
		}

		return "navigation-menu";
	}

	public String dynamicReport() throws Exception {
		return "dynamic-report";
	}

	/**
	 * Determines whether or not the user logged in is a first-time Dynamic
	 * Report user that needs their user updated so they do not see the Tutorial
	 * page again.
	 *
	 * @return
	 */
	private boolean isFirstTimeDynamicReportUser() {
		return (permissions.isUsingVersion7Menus() && permissions.getUsingVersion7MenusDate() == null);
	}

	private void setUsingDynamicReportsDate() {
		Date usingDynamicReportsDate = new Date();

		permissions.setUsingVersion7MenusDate(usingDynamicReportsDate);

		User user = userDAO.find(permissions.getUserId());
		user.setUsingVersion7MenusDate(usingDynamicReportsDate);
		userDAO.save(user);
	}
}
