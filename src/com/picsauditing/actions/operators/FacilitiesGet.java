package com.picsauditing.actions.operators;

import java.util.ArrayList;
import java.util.List;

import org.apache.commons.beanutils.BasicDynaBean;

import com.opensymphony.xwork2.ActionSupport;
import com.picsauditing.PICS.Facilities;
import com.picsauditing.PICS.Utilities;
import com.picsauditing.dao.OperatorAccountDAO;
import com.picsauditing.jpa.entities.OperatorAccount;
import com.picsauditing.util.SpringUtils;

public class FacilitiesGet extends ActionSupport {
	protected String filter = null;
	protected List<OperatorAccount> facilities = null;
	protected boolean shouldIncludePICS = false;
	private OperatorAccountDAO operatorDao;

	public FacilitiesGet(OperatorAccountDAO operatorDao) {
		this.operatorDao = operatorDao;
	}

	public String execute() throws Exception {

		return SUCCESS;
	}

	public String getFilter() {
		return filter;
	}

	public void setFilter(String filter) {
		this.filter = filter;
	}

	public List<OperatorAccount> getFacilities() {
		String where = null;
		if (filter != null && filter.length() > 3) {
			where = "a IN (SELECT account FROM User WHERE username LIKE '%"
					+ Utilities.escapeQuotes(filter) + "%' OR name LIKE '%"
					+ Utilities.escapeQuotes(filter) + "%' OR email LIKE '%"
					+ Utilities.escapeQuotes(filter) + "%')";
		}
		facilities = new ArrayList<OperatorAccount>();
		facilities.add(new OperatorAccount(OperatorAccount.DEFAULT_NAME));
		facilities.addAll(operatorDao.findWhere(where));
		return facilities;

	}

	public void setFacilities(List<OperatorAccount> facilities) {
		this.facilities = facilities;
	}

	public boolean isShouldIncludePICS() {
		return shouldIncludePICS;
	}

	public void setShouldIncludePICS(boolean shouldIncludePICS) {
		this.shouldIncludePICS = shouldIncludePICS;
	}
}
