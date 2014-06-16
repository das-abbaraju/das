package com.picsauditing.angular.controller;

import com.picsauditing.access.Anonymous;
import com.picsauditing.access.LoginController;
import com.picsauditing.access.PageNotFoundException;
import com.picsauditing.actions.PicsActionSupport;
import com.picsauditing.featuretoggle.Features;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;

public class AngularLoader extends PicsActionSupport {

    private static final Logger logger = LoggerFactory.getLogger(AngularLoader.class);

    @Anonymous
    public String load() throws PageNotFoundException, IOException {
        if (mismatchedStrikeIronToggleAndActionCombo()) {
            logger.warn("User navigated to the wrong registration action, perhaps via a bookmark? Redirecting...");
            setUrlForRedirect(LoginController.REGISTRATION_ACTION_NOT_ANGULAR);
            return REDIRECT;
        }
        return BLANK;
    }

    private boolean mismatchedStrikeIronToggleAndActionCombo() {
        return (getRequestURI().equals("/" + LoginController.REGISTRATION_ACTION_ANGULAR) && !Features.USE_STRIKEIRON_ADDRESS_VERIFICATION_SERVICE.isActive());
    }

}
