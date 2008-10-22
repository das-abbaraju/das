package com.picsauditing.actions.report;

import com.picsauditing.access.OpPerms;
import com.picsauditing.search.SelectFilter;
import com.picsauditing.search.SelectUserUnion;
import com.picsauditing.util.ReportFilterUser;

public class ReportUser extends ReportActionSupport {

	protected boolean forwardSingleResults = false;
	protected boolean skipPermissions = false;

	protected SelectUserUnion sql = new SelectUserUnion();
	private ReportFilterUser filter = new ReportFilterUser();

	public SelectUserUnion getSql() {
		return sql;
	}

	public void setSql(SelectUserUnion sql) {
		this.sql = sql;
	}

	public String execute() throws Exception {
		if (!forceLogin())
			return LOGIN;
		permissions.tryPermission(OpPerms.EditUsers);
		if (!skipPermissions)
			sql.setPermissions(permissions);

		getFilter().setShowContact(true);
		getFilter().setShowEmail(true);
		getFilter().setShowPhone(true);
		getFilter().setShowUser(true);
		addFilterToSQL();

		sql.addField("u.tableType");
		sql.addField("u.accountID");
		sql.addField("u.name");
		sql.addField("u.dateCreated");
		sql.addField("u.lastLogin");
		sql.addField("u.username");
		sql.addField("u.id");
		sql.addJoin("JOIN accounts a ON a.id = u.accountID");
		sql.addField("a.name AS companyName");
		sql.addWhere("u.isActive = 'Yes'");
		sql.addOrderBy("u.name");

		if (isFiltered())
			this.run(sql);

		return SUCCESS;
	}

	private void addFilterToSQL() {
		ReportFilterUser f = getFilter();

		/** **** Filters for Users ********** */
		if (filterOn(f.getStartsWith()))
			report.addFilter(new SelectFilter("name", "u.name LIKE '?%'", f.getStartsWith()));

		if (filterOn(f.getContactName(), ReportFilterUser.DEFAULT_NAME)) {
			report.addFilter(new SelectFilter("ContactName", "u.name LIKE '%?%'", f.getContactName()));
		}

		if (filterOn(f.getPhoneNumber(), ReportFilterUser.DEFAULT_PHONE)) {
			report.addFilter(new SelectFilter("PhoneNumber", "u.phone LIKE '%?%'", f.getPhoneNumber()));
		}

		if (filterOn(f.getEmailAddress(), ReportFilterUser.DEFAULT_EMAIL)) {
			report.addFilter(new SelectFilter("EmailAddress", "u.email LIKE '%?%'", f.getEmailAddress()));
		}

		if (filterOn(f.getUserName(), ReportFilterUser.DEFAULT_USERNAME)) {
			report.addFilter(new SelectFilter("UserName", "u.username LIKE '%?%'", f.getUserName()));
		}
	}

	public ReportFilterUser getFilter() {
		return filter;
	}

}
