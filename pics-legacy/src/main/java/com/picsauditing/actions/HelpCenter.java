package com.picsauditing.actions;

public class HelpCenter extends PicsActionSupport {
    private static final String HELPCENTER_DOMAIN = "help.picsorganizer.com";
    private static final String HELPCENTER_LOGIN_URL = "http://" + HELPCENTER_DOMAIN + "/login.action?os_destination=homepage.action&os_cookie=true";
    private static final String CORP_OPERATOR_CREDENTIALS = "&os_username=operator&os_password=oper456ator";
    private static final String CONTRACTOR_CREDENTIALS = "&os_username=contractor&os_password=con123tractor";
    private static final String ADMIN_CREDENTIALS = "&os_username=admin&os_password=ad9870mins";

    // see PICS-11745
    @Override
    public String execute() throws Exception {
        return setUrlForRedirect(helpCenterUrlForUserType());
    }

    private String helpCenterUrlForUserType() {
        if (permissions.isOperatorCorporate()) {
            return HELPCENTER_LOGIN_URL + CORP_OPERATOR_CREDENTIALS;
        } else if (permissions.isContractor()) {
            return HELPCENTER_LOGIN_URL + CONTRACTOR_CREDENTIALS;
        } else {
            // currently "admin" doesn't rally have admin rights - this duplicates what was in main.jsp
            return HELPCENTER_LOGIN_URL + ADMIN_CREDENTIALS;
        }
    }
}
