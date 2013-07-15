package com.picsauditing.actions;

import java.util.Date;

import com.opensymphony.xwork2.ActionContext;
import org.springframework.beans.factory.annotation.Autowired;

import com.picsauditing.dao.UserDAO;
import com.picsauditing.jpa.entities.User;
import com.picsauditing.toggle.FeatureToggle;

@SuppressWarnings("serial")
public class Reference extends PicsActionSupport {

	@Autowired
	private UserDAO userDAO;
	@Autowired
	private FeatureToggle featureToggle;

    protected String from;

	public String navigationMenu() throws Exception {
		loadPermissions(false);

		// prevent repeated updates to the using dynamic reports date
		if (isFirstTimeVersion7MenuUser()) {
			setUsingVersion7MenuDate();
		}

		return "navigation-menu";
	}

	public String dynamicReport() throws Exception {
		return "dynamic-report";
	}

	public String navigationRestructure() {
		return "navigation-restructure";
	}

	public String reportsManager() throws Exception {
		loadPermissions(false);

        if (from == null) {
            from = ((String[]) ActionContext.getContext().getParameters().get("from"))[0];
        }

        boolean isFromReportsMenu = from != null && from.equals("ReportsMenu");

        if (!isFirstTimeReportsManagerUser() && isFromReportsMenu) {
            setUrlForRedirect("ManageReports!search.action");
            return REDIRECT;
        } else {
			setReportsManagerTutorialDate();
            return "manage-reports";
		}
	}

	/**
	 * Determines whether or not the user logged in is a first-time Dynamic
	 * Report user that needs their user updated so they do not see the Tutorial
	 * page again.
	 *
	 * @return
	 */
	private boolean isFirstTimeVersion7MenuUser() {
		return (permissions.isUsingVersion7Menus() && permissions.getUsingVersion7MenusDate() == null);
	}

	private void setUsingVersion7MenuDate() {
		Date usingVersion7MenuDate = new Date();

		permissions.setUsingVersion7MenusDate(usingVersion7MenuDate);

		User user = userDAO.find(permissions.getUserId());
		user.setUsingVersion7MenusDate(usingVersion7MenuDate);
		if (!featureToggle.isFeatureEnabled(FeatureToggle.TOGGLE_USE_V7_MENU_COLUMN)) {
			user.setusingDynamicReportsDate(usingVersion7MenuDate);
		}

		userDAO.save(user);
	}

	private boolean isFirstTimeReportsManagerUser() {
		return (permissions.getReportsManagerTutorialDate() == null);
	}

	private void setReportsManagerTutorialDate() {
		Date reportsManagerTutorialDate = new Date();
		permissions.setReportsManagerTutorialDate(reportsManagerTutorialDate);

		User user = userDAO.find(permissions.getUserId());
		user.setReportsManagerTutorialDate(reportsManagerTutorialDate);

		userDAO.save(user);
	}

    public String getFrom() {
        return from;
    }

    public void setFrom(String from) {
        this.from = from;
    }
}
