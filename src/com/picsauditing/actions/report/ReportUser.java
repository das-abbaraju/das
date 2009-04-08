package com.picsauditing.actions.report;

import java.io.IOException;
import java.util.HashSet;
import java.util.Set;

import org.apache.commons.beanutils.DynaBean;
import org.apache.struts2.ServletActionContext;

import com.opensymphony.xwork2.ActionContext;
import com.picsauditing.access.OpPerms;
import com.picsauditing.jpa.entities.ListType;
import com.picsauditing.mail.WizardSession;
import com.picsauditing.search.SelectFilter;
import com.picsauditing.search.SelectUserUnion;
import com.picsauditing.util.ReportFilterUser;

@SuppressWarnings("serial")
public class ReportUser extends ReportActionSupport {

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

		if (button != null && button.contains("Write Email")) {
			// This condition only occurs when sending results to the mail merge
			// tool
			this.mailMerge = true;
		}

		sql.addField("u.tableType");
		sql.addField("u.columnType");
		sql.addField("u.accountID");
		sql.addField("u.name");
		sql.addField("u.creationDate");
		sql.addField("u.lastLogin");
		sql.addField("u.username");
		sql.addField("u.id");
		sql.addField("u.email");
		sql.addField("u.phone");
		sql.addJoin("JOIN accounts a ON a.id = u.accountID");
		sql.addField("a.name AS companyName");
		sql.addField("a.type AS AcctType");
		sql.addWhere("u.isActive = 'Yes'");
		sql.addWhere("a.active = 'Y'");
		sql.addOrderBy("u.name");

		if (isFiltered())
			this.run(sql);

		return returnResult();
	}

	protected String returnResult() throws IOException {
		if (mailMerge && data != null && data.size() > 0) {
			Set<Integer> ids = new HashSet<Integer>();
			for (DynaBean dynaBean : data) {
				ids.add((Integer) dynaBean.get("id"));
			}
			WizardSession wizardSession = new WizardSession(ActionContext.getContext().getSession());
			wizardSession.setIds(ids);
			wizardSession.setListTypes(ListType.User);
			ServletActionContext.getResponse().sendRedirect("MassMailer.action");
			this.addActionMessage("Redirected to MassMailer");
			return BLANK;
		}
		return SUCCESS;
	}

	private void addFilterToSQL() {
		ReportFilterUser f = getFilter();

		/** **** Filters for Users ********** */
		if (filterOn(f.getStartsWith()))
			report.addFilter(new SelectFilter("name", "u.name LIKE '?%'", f.getStartsWith()));

		if (filterOn(f.getContactName())) {
			report.addFilter(new SelectFilter("ContactName", "u.name LIKE '%?%'", f.getContactName()));
		}

		if (filterOn(f.getPhoneNumber())) {
			report.addFilter(new SelectFilter("PhoneNumber", "u.phone LIKE '%?%'", f.getPhoneNumber()));
		}

		if (filterOn(f.getEmailAddress())) {
			report.addFilter(new SelectFilter("EmailAddress", "u.email LIKE '%?%'", f.getEmailAddress()));
		}

		if (filterOn(f.getUserName())) {
			report.addFilter(new SelectFilter("UserName", "u.username LIKE '%?%'", f.getUserName()));
		}
		
		if (filterOn(f.getCompanyName())) {
			report.addFilter(new SelectFilter("companyName", "a.nameIndex LIKE '%?%'", f.getCompanyName()));
		}
	}

	public ReportFilterUser getFilter() {
		return filter;
	}

}
