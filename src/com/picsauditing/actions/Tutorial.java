package com.picsauditing.actions;

import com.picsauditing.dao.UserDAO;
import com.picsauditing.jpa.entities.User;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.Date;

@SuppressWarnings("serial")
public class Tutorial extends PicsActionSupport {

    @Autowired
    private UserDAO userDAO;

    @Override
    public String execute() throws Exception {
        loadPermissions(false);

        // prevent repeated updates to the using dynamic reports date
        if (isFirstTimeDynamicReportUser()) {
            setUsingDynamicReportsDate();
        }

        return SUCCESS;
    }

    /**
     * Determines whether or not the user logged in is a first-time Dynamic Report
     * user that needs their user updated so they do not see the Tutorial page again.
     *
     * @return
     */
    private boolean isFirstTimeDynamicReportUser() {
        return (permissions.isUsingDynamicReports()
                && permissions.getUsingDynamicReportsDate() == null);
    }

    private void setUsingDynamicReportsDate() {
        Date usingDynamicReportsDate = new Date();

        permissions.setUsingDynamicReportsDate(usingDynamicReportsDate);

        User user = userDAO.find(permissions.getUserId());
        user.setUsingDynamicReportsDate(usingDynamicReportsDate);
        userDAO.save(user);
    }
}
