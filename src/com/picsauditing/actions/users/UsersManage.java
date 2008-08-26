package com.picsauditing.actions.users;

import java.util.ArrayList;
import java.util.List;

import org.apache.commons.beanutils.BasicDynaBean;
import org.apache.struts2.ServletActionContext;

import com.opensymphony.xwork2.Preparable;
import com.picsauditing.PICS.Utilities;
import com.picsauditing.access.OpPerms;
import com.picsauditing.access.OpType;
import com.picsauditing.actions.PicsActionSupport;
import com.picsauditing.dao.OperatorAccountDAO;
import com.picsauditing.dao.UserDAO;
import com.picsauditing.jpa.entities.OperatorAccount;
import com.picsauditing.jpa.entities.User;
import com.picsauditing.search.Report;
import com.picsauditing.search.SelectUser;

public class UsersManage extends PicsActionSupport implements Preparable {
	private static final long serialVersionUID = -167727120482502678L;
	
	protected int accountId = 0;
	protected User user;

	protected String filter = null;
	protected List<OperatorAccount> facilities = null;
	protected Report search = null;
	protected List<BasicDynaBean> searchData = null;

	protected boolean filtered = false;
	
	protected String isGroup = null;
	protected String isActive = null;

	protected boolean hasAllOperators = false;

	protected OperatorAccountDAO operatorDao;
	protected UserDAO userDAO;

	public UsersManage(OperatorAccountDAO operatorDao, UserDAO userDAO) {
		this.operatorDao = operatorDao;
		this.userDAO = userDAO;
	}

	public String execute() throws Exception {
		if (!forceLogin())
			return LOGIN;
		permissions.tryPermission(OpPerms.EditUsers);

		if (accountId == 0)
			accountId = permissions.getAccountId();
		
		if (permissions.getAccountId() != accountId)
			permissions.tryPermission(OpPerms.AllOperators);

		SelectUser sql = new SelectUser();
		sql.addField("u.lastLogin");
		sql.addField("u.isGroup");
		sql.addField("u.isActive");
		search = new Report();
		search.setSql(sql);

		if (isActive == null) {
			isActive = "Yes";
		} else
			filtered = true;


		if ("Yes".equals(isGroup) || "No".equals(isGroup)) {
			filtered = true;
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

	public int getAccountId() {
		return accountId;
	}

	public void setAccountId(int accountId) {
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
					+ Utilities.escapeQuotes(filter) + "%' OR name LIKE '%"
					+ Utilities.escapeQuotes(filter) + "%' OR email LIKE '%"
					+ Utilities.escapeQuotes(filter) + "%')";
		}
		facilities = new ArrayList<OperatorAccount>();
		facilities.add(new OperatorAccount(OperatorAccount.DEFAULT_NAME));
		facilities.addAll(operatorDao.findWhere(true, where));
		return facilities;

	}

	public boolean isFiltered() {
		return filtered;
	}

	public User getUser() {
		return user;
	}

	public void setUser(User user) {
		this.user = user;
	}

	@Override
	public void prepare() throws Exception {
		int id = getParameter("user.id");
		user = userDAO.find(id);
	}
}
