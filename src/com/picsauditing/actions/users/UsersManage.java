package com.picsauditing.actions.users;

import java.util.ArrayList;
import java.util.List;

import org.apache.commons.beanutils.BasicDynaBean;
import org.apache.struts2.ServletActionContext;

import com.picsauditing.PICS.Utilities;
import com.picsauditing.access.OpPerms;
import com.picsauditing.access.OpType;
import com.picsauditing.actions.PicsActionSupport;
import com.picsauditing.dao.OperatorAccountDAO;
import com.picsauditing.jpa.entities.OperatorAccount;
import com.picsauditing.search.Report;
import com.picsauditing.search.SelectUser;

public class UsersManage extends PicsActionSupport {
	protected String accountId = null;
	protected String filter = null;
	protected List<OperatorAccount> facilities = null;
	private OperatorAccountDAO operatorDao;
	protected Report search = null;
	protected List<BasicDynaBean> searchData = null;

	protected String isGroup = null;
	protected String isActive = null;

	protected boolean hasAllOperators = false;

	public UsersManage(OperatorAccountDAO operatorDao) {
		this.operatorDao = operatorDao;
	}

	public String execute() throws Exception {
		loadPermissions();
		permissions.tryPermission(OpPerms.EditUsers);

		String accountId = permissions.getAccountIdString();
		if (permissions.hasPermission(OpPerms.AllOperators)
				&& this.accountId != null) {
			accountId = Utilities.intToDB(this.accountId);
		}

		this.accountId = accountId;// accountId is on the instance level
		// AND in the execute method...watch out

		SelectUser sql = new SelectUser();
		sql.addField("u.lastLogin");
		sql.addField("u.isGroup");

		search = new Report();
		search.setSql(sql);

		if (isActive == null) {
			isActive = "Yes";
		}

		if ("Yes".equals(isGroup) || "No".equals(isGroup)) {
			sql.addWhere("isGroup = '" + isGroup + "' ");
		}
		if ("Yes".equals(isActive) || "No".equals(isActive)) {
			sql.addWhere("isActive = '" + isActive + "' ");
		}

		sql.addWhere("accountID = " + accountId);
		// Only search for Auditors and Admins
		sql.addOrderBy("u.isGroup, u.name");
		search.setPageByResult(ServletActionContext.getRequest().getParameter(
				"showPage"));

		search.setLimit(25);

		searchData = search.getPage();

		if (permissions.hasPermission(OpPerms.AllOperators)) {
			hasAllOperators = true;
		}

		return SUCCESS;
	}

	public String getAccountId() {
		return accountId;
	}

	public void setAccountId(String accountId) {
		this.accountId = accountId;
	}

	public String getFilter() {
		return filter;
	}

	public void setFilter(String filter) {
		this.filter = filter;
	}

	public String getIsGroup() {
		return isGroup;
	}

	public void setIsGroup(String isGroup) {
		this.isGroup = isGroup;
	}

	public String getIsActive() {
		return isActive;
	}

	public void setIsActive(String isActive) {
		this.isActive = isActive;
	}

	public Report getSearch() {
		return search;
	}

	public void setSearch(Report search) {
		this.search = search;
	}

	public List<BasicDynaBean> getSearchData() {
		return searchData;
	}

	public void setSearchData(List<BasicDynaBean> searchData) {
		this.searchData = searchData;
	}

	public boolean isHasAllOperators() {
		return hasAllOperators;
	}

	public void setHasAllOperators(boolean hasAllOperators) {
		this.hasAllOperators = hasAllOperators;
	}

	public List<OperatorAccount> getFacilities() {
		String where = null;
		if (filter != null && filter.length() > 3) {
			where = "a IN (SELECT account FROM User WHERE username LIKE '%"
					+ Utilities.escapeQuotes(filter) + "%' OR username LIKE '%"
					+ Utilities.escapeQuotes(filter) + "%' OR email LIKE '%"
					+ Utilities.escapeQuotes(filter) + "%')";
		}
		facilities = new ArrayList<OperatorAccount>();
		facilities.add(new OperatorAccount(OperatorAccount.DEFAULT_NAME));
		facilities.addAll(operatorDao.findWhere(where));
		return facilities;

	}

}
