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
	private int total;

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

			current.addWhere("a.status = 'Requested' AND c.followupDate IS NULL");
			current.addOrderBy("deadline, creationDate");
		} else {
			current = new SelectSQL("contractor_registration_request crr");

			current.addJoin("LEFT JOIN users requestedByUser ON requestedByUser.id = crr.requestedByUserID");
			current.addJoin("JOIN accounts op ON op.id = crr.requestedByID");

			current.addField("crr.id");
			current.addField("crr.name");
			current.addField("crr.deadline");
			current.addField("crr.lastContactDate");
			current.addField("crr.requestedByUser RequestedByUserOther");
			current.addField("requestedByUser.name RequestedUser");
			current.addField("op.id RequestedByID");
			current.addField("op.name RequestedBy");

			current.addWhere("crr.holdDate IS NULL");
			current.addWhere("crr.status = 'Active'");
		}

		if (permissions.isOperator()) {
			current.addWhere("op.id = " + permissions.getAccountId());
		} else if (permissions.isCorporate()) {
			current.addWhere("op.id IN (" + Strings.implode(permissions.getVisibleAccounts()) + ")");
		}

		current.setLimit(10);

		List<BasicDynaBean> requestedContractors = database.select(current.toString(), true);
		total = database.getAllRows();

		return requestedContractors;
	}

	public int getTotal() {
		return total;
	}
}