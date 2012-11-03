package com.picsauditing.actions.contractors;

import java.util.List;

import org.apache.commons.beanutils.BasicDynaBean;
import org.springframework.beans.factory.annotation.Autowired;

import com.picsauditing.actions.PicsActionSupport;
import com.picsauditing.actions.report.ReportRegistrationRequests;
import com.picsauditing.search.Database;
import com.picsauditing.search.SelectSQL;
import com.picsauditing.toggle.FeatureToggle;
import com.picsauditing.util.Strings;

@SuppressWarnings("serial")
public class RequestedConWidget extends PicsActionSupport {
	@Autowired
	private FeatureToggle featureToggle;

	private Database database = new Database();

	public String execute() throws Exception {
		if (!permissions.isLoggedIn()) {
			return LOGIN_AJAX;
		}

		return SUCCESS;
	}

	public List<BasicDynaBean> getRequestedContractors() throws Exception {
		SelectSQL current;

		if (featureToggle.isFeatureEnabled(FeatureToggle.TOGGLE_REQUESTNEWCONTRACTORACCOUNT)) {
			current = ReportRegistrationRequests.buildAccountQuery();
		} else {
			current = new SelectSQL("contractor_registration_request crr");
		}

		if (permissions.isOperator()) {
			current.addWhere("gc.genID = " + permissions.getAccountId());
		} else if (permissions.isCorporate()) {
			current.addWhere("gc.genID IN (" + Strings.implode(permissions.getVisibleAccounts()) + ")");
		}

		current.addWhere("a.status = 'Requested' AND c.followupDate IS NULL");
		current.addOrderBy("deadline, creationDate");
		current.setLimit(10);

		return database.select(current.toString(), false);
	}
}