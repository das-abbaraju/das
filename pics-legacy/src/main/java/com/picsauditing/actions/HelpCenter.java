package com.picsauditing.actions;

import org.apache.commons.lang3.CharEncoding;
import org.apache.commons.lang3.StringUtils;

import java.net.URLEncoder;

public class HelpCenter extends PicsActionSupport {
    private static final String HELPCENTER_DOMAIN = "help.picsorganizer.com";
    private static final String HELPCENTER_LOGIN_URL = "http://" + HELPCENTER_DOMAIN + "/login.action?os_cookie=true&os_destination=";

    private static final String CORP_OPERATOR_CREDENTIALS = "&os_username=operator&os_password=oper456ator";
    private static final String CONTRACTOR_CREDENTIALS = "&os_username=contractor&os_password=con123tractor";
    private static final String ADMIN_CREDENTIALS = "&os_username=admin&os_password=ad9870mins";

	private static final String DEFAULT_REDIRECTION = "homepage.action";
	private static final String DEFAULT_SPACE_KEY = "contractors";

	private volatile String space = null;
	private volatile String page = null;

	/**
	 * Confluent space-key.
	 */
	public String getSpace() {
		return StringUtils.isEmpty(space) ? DEFAULT_SPACE_KEY : space;
	}

	public void setSpace(String space) {
		this.space = space;
	}

	/**
	 * Confluent page-name in a space.
	 */
	public String getPage() {
		return page;
	}

	public void setPage(String page) {
		this.page = page;
	}

    // see PICS-11745
    @Override
    public String execute() throws Exception {
        return setUrlForRedirect(helpCenterUrlForUserType());
    }

    private String helpCenterUrlForUserType() throws Exception {
        String helpUrl = HELPCENTER_LOGIN_URL + URLEncoder.encode(getRedirectionLocate(), CharEncoding.UTF_8);
        if (permissions.isOperatorCorporate()) {
            return helpUrl + CORP_OPERATOR_CREDENTIALS;
        } else if (permissions.isContractor()) {
            return helpUrl + CONTRACTOR_CREDENTIALS;
        } else {
            // currently "admin" doesn't rally have admin rights - this duplicates what was in main.jsp
            return helpUrl + ADMIN_CREDENTIALS;
        }
    }

	private String getRedirectionLocate() {
		return StringUtils.isEmpty(getPage()) ? DEFAULT_REDIRECTION
				: ("/display/" + getSpace().replaceAll(" ", "+") + "/" + getPage().replaceAll(" ", "+"));
	}
}
