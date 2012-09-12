package com.picsauditing.actions.contractors;

import java.util.List;

import org.apache.commons.beanutils.BasicDynaBean;

import com.picsauditing.actions.PicsActionSupport;
import com.picsauditing.actions.report.ReportNewRequestedContractor;
import com.picsauditing.search.Database;
import com.picsauditing.search.SelectSQL;
import com.picsauditing.util.Strings;

@SuppressWarnings("serial")
public class RequestedConWidget extends PicsActionSupport {
	private Database database = new Database();

	public String execute() throws Exception {
		if (!permissions.isLoggedIn())
			return LOGIN_AJAX;

		return SUCCESS;
	}

	public List<BasicDynaBean> getRequestedContractors() throws Exception {
		SelectSQL legacy = ReportNewRequestedContractor.buildLegacyQuery();
		SelectSQL current = ReportNewRequestedContractor.buildNewQuery();

		if (permissions.isOperator()) {
			legacy.addWhere("cr.requestedByID = " + permissions.getAccountId());
			current.addWhere("gc.genID = " + permissions.getAccountId());
		} else if (permissions.isCorporate()) {
			legacy.addWhere("cr.requestedByID IN (" + Strings.implode(permissions.getVisibleAccounts()) + ")");
			current.addWhere("gc.genID IN (" + Strings.implode(permissions.getVisibleAccounts()) + ")");
		}

		legacy.addWhere("cr.status = 'Active'");
		current.addWhere("a.status = 'Requested' AND c.followupDate IS NULL");

		String sql = String.format("%s \nUNION\n %s ORDER BY deadline, creationDate LIMIT 10", legacy.toString(),
				current.toString());

		return database.select(sql, false);
	}
}