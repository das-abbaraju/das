package com.picsauditing.featuretoggle;

import com.opensymphony.xwork2.ActionContext;
import com.picsauditing.access.OpPerms;
import com.picsauditing.access.Permissions;
import org.apache.struts2.ServletActionContext;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.togglz.core.user.FeatureUser;
import org.togglz.servlet.util.HttpServletRequestHolder;

public class ServletFeatureUser implements FeatureUser {
    private final Logger logger = LoggerFactory.getLogger(ServletFeatureUser.class);
    private final static String UNKNOWN_USER_NAME = "Anonymous";
    private final static String PERMISSIONS_ATTRIBUTE_NAME = "permissions";
    private static final String environment = System.getProperty("pics.env");

    private Permissions permissions;

    public ServletFeatureUser() {
        permissions();
    }

    @Override
    public String getName() {
        if (permissions != null) {
            return permissions.getName();
        } else {
            return UNKNOWN_USER_NAME;
        }
    }

    @Override
    public boolean isFeatureAdmin() {
        if (permissions != null) {
            return permissions.has(OpPerms.DevelopmentEnvironment);
        } else {
            return false;
        }
    }

    @Override
    public Object getAttribute(String attribute) {
        permissions();
        switch (attribute) {
            case "userID": return permissions.getUserId();
            case "accountID": return permissions.getAccountId();
            case "groups": return permissions.getAllInheritedGroupIds();
            case "countryCode": return permissions.getCountry();
            case "countrySubdivisionCode": return permissions.getCountrySubdivision();
            case "env": return environment;
            case "environment": return environment;
            default: return null;
        }
    }

    private Permissions permissions() {
        if (permissions == null || permissions.getAccountId() == 0 || permissions.getUserId() == 0) {
            try {
                permissions = (Permissions) ActionContext.getContext().getSession().get(PERMISSIONS_ATTRIBUTE_NAME);
            } catch (Exception e) {
                logger.debug("permissions cannot be loaded by Struts context, trying via request");
                try {
                    permissions = (Permissions) HttpServletRequestHolder.get().getSession().getAttribute(PERMISSIONS_ATTRIBUTE_NAME);
                } catch (Exception e1) {
                    logger.debug("permissions cannot be loaded - if the script depends on it, it'll throw an NPE and the feature toggle will be false");
                }
            }
        }
        return permissions;
    }

}
