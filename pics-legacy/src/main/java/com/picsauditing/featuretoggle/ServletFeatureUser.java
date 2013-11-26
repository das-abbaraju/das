package com.picsauditing.featuretoggle;

import com.picsauditing.access.OpPerms;
import com.picsauditing.access.Permissions;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.togglz.core.user.FeatureUser;
import org.togglz.servlet.util.HttpServletRequestHolder;

public class ServletFeatureUser implements FeatureUser {
    private final Logger logger = LoggerFactory.getLogger(ServletFeatureUser.class);
    private final static String UNKNOWN_USER_NAME = "Anonymous";

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
    public Object getAttribute(String s) {
        return null;
    }

    private Permissions permissions() {
        if (permissions == null || permissions.getAccountId() == 0 || permissions.getUserId() == 0) {
            try {
                permissions = (Permissions) HttpServletRequestHolder.get().getSession().getAttribute("permissions");
            } catch (Exception e) {
                logger.debug("permissions cannot be loaded - if the script depends on it, it'll throw an NPE and the feature toggle will be false");
            }
        }
        return permissions;
    }

}
