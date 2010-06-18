package com.picsauditing.actions.report;

import java.util.ArrayList;
import java.util.List;

import javax.naming.NoPermissionException;

import com.opensymphony.xwork2.ActionContext;
import com.picsauditing.access.NoRightsException;
import com.picsauditing.access.OpPerms;
import com.picsauditing.access.OpType;
import com.picsauditing.dao.AccountDAO;
import com.picsauditing.dao.OperatorAccountDAO;
import com.picsauditing.jpa.entities.Account;
import com.picsauditing.jpa.entities.ListType;
import com.picsauditing.jpa.entities.OperatorAccount;
import com.picsauditing.mail.WizardSession;
import com.picsauditing.search.SelectAccount;
import com.picsauditing.search.SelectFilter;
import com.picsauditing.util.ReportFilterAccount;
import com.picsauditing.util.SpringUtils;
import com.picsauditing.util.Strings;

@SuppressWarnings("serial")
public class ReportOperatorCorporate extends ReportActionSupport {
	protected SelectAccount sql = new SelectAccount();
	protected ReportFilterAccount filter = new ReportFilterAccount();
	protected int accountID;

	public void buildQuery() {
		filter.setPermissions(permissions);
		filter.setShowType(true);
		filter.setShowStatus(true);
		filter.setShowTradeInformation(false);
		filter.setShowPrimaryInformation(true);
		
		List<String> types = new ArrayList<String>();
		
		if (permissions.hasPermission(OpPerms.ManageOperators))
			types.add("Operator");
		if (permissions.hasPermission(OpPerms.ManageCorporate))
			types.add("Corporate");
		if (permissions.hasPermission(OpPerms.ManageAssessment))
			types.add("Assessment");
		
		sql.addWhere("a.type IN (" + Strings.implodeForDB(types, ",") + ")");
		sql.setType(null);

		sql.addJoin("left JOIN users contact ON contact.id = a.contactID");
		sql.addField("a.industry");
		sql.addField("a.state");
		sql.addField("a.city");
		sql.addField("a.type");
		sql.addOrderBy("a.name");
		addFilterToSQL();
	}
	
	public String execute() throws Exception {
		if (!forceLogin())
			return LOGIN;
		
		if (!permissions.hasPermission(OpPerms.ManageOperators) 
				&& !permissions.hasPermission(OpPerms.ManageCorporate)
				&& !permissions.hasPermission(OpPerms.ManageAssessment))
			throw new NoRightsException("Administrator");
		
		buildQuery();
		run(sql);

		WizardSession wizardSession = new WizardSession(ActionContext.getContext().getSession());
		wizardSession.clear();
		wizardSession.setFilter(ListType.ALL, filter);

		if ("Remove".equals(button)) {
			AccountDAO accountDAO = (AccountDAO) SpringUtils.getBean("AccountDAO");
			Account account = accountDAO.find(accountID);

			if (account.getType().equals("Operator") && isCanDeleteOp()) {
				OperatorAccountDAO opDAO = (OperatorAccountDAO) SpringUtils.getBean("OperatorAccountDAO");
				boolean removed = opDAO.removeAllByOpID((OperatorAccount) account, getFtpDir());
				if (!removed)
					addActionError("Cannot Remove this account: " + account.getName());
			}
			else if (account.getType().equals("Corporate") && isCanDeleteCorp()) {
				OperatorAccountDAO opDAO = (OperatorAccountDAO) SpringUtils.getBean("OperatorAccountDAO");
				boolean removed = opDAO.removeAllByOpID((OperatorAccount) account, getFtpDir());
				if (!removed)
					addActionError("Cannot Remove this account: " + account.getName());
			}
			else if (account.getType().equals("Assessment") && isCanDeleteAssessment())
				permissions.tryPermission(OpPerms.ManageAssessment, OpType.Delete);
			else
				throw new NoPermissionException("Delete Account");
		}

		return SUCCESS;
	}

	public int getAccountID() {
		return accountID;
	}

	public void setAccountID(int accountID) {
		this.accountID = accountID;
	}

	public boolean isCanEditCorp() {
		return permissions.hasPermission(OpPerms.ManageCorporate, OpType.Edit);
	}
	
	public boolean isCanDeleteCorp() {
		return permissions.hasPermission(OpPerms.ManageCorporate, OpType.Delete);
	}

	public boolean isCanEditOp() {
		return permissions.hasPermission(OpPerms.ManageOperators, OpType.Edit);
	}

	public boolean isCanDeleteOp() {
		return permissions.hasPermission(OpPerms.ManageOperators, OpType.Delete);
	}
	
	public boolean isCanEditAssessment() {
		return permissions.hasPermission(OpPerms.ManageAssessment, OpType.Edit);
	}
	
	public boolean isCanDeleteAssessment() {
		return permissions.hasPermission(OpPerms.ManageAssessment, OpType.Delete);
	}
	
	protected void addFilterToSQL() {
		ReportFilterAccount f = getFilter();

		/** **** Filters for Accounts ********** */
		if (filterOn(f.getStartsWith()))
			report.addFilter(new SelectFilter("startsWith", "a.nameIndex LIKE '?%'", f.getStartsWith()));

		if (filterOn(f.getAccountName(), ReportFilterAccount.DEFAULT_NAME)) {
			String accountName = f.getAccountName().trim();
			report.addFilter(new SelectFilter("accountName", "a.nameIndex LIKE '%" + Strings.indexName(accountName)
					+ "%' OR a.name LIKE '%?%' OR a.dbaName LIKE '%" + accountName + "%' OR a.id = '" + 
					accountName + "'", accountName));
			sql.addField("a.dbaName");
		}

		String statusList = Strings.implodeForDB(f.getStatus(), ",");
		if (filterOn(statusList)) {
			sql.addWhere("a.status IN (" + statusList + ")");
			setFiltered(true);
		}

		String industryList = Strings.implodeForDB(f.getIndustry(), ",");
		if (filterOn(industryList)) {
			sql.addWhere("a.industry IN (" + industryList + ")");
			setFiltered(true);
		}

		if (filterOn(f.getCity(), ReportFilterAccount.DEFAULT_CITY))
			report.addFilter(new SelectFilter("city", "a.city LIKE '%?%'", f.getCity()));

		String stateList = Strings.implodeForDB(f.getState(), ",");
		if (filterOn(stateList)) {
			sql.addWhere("a.state IN (" + stateList + ")");
			setFiltered(true);
		}

		String countryList = Strings.implodeForDB(f.getCountry(), ",");
		if (filterOn(countryList) && !filterOn(stateList)) {
			sql.addWhere("a.country IN (" + countryList + ")");
			setFiltered(true);
		}

		if (filterOn(f.getZip(), ReportFilterAccount.DEFAULT_ZIP))
			report.addFilter(new SelectFilter("zip", "a.zip LIKE '%?%'", f.getZip()));

		if (f.isPrimaryInformation()) {
			sql.addField("contact.name AS contactname");
			sql.addField("contact.phone AS contactphone");
			sql.addField("contact.email AS contactemail");
			sql.addField("a.address");
			sql.addField("a.city");
			sql.addField("a.state");
			sql.addField("a.zip");
			sql.addField("a.web_URL");
		}

		if (f.isTradeInformation()) {
			sql.addField("c.main_trade");
			sql.addField("a.industry");
			sql.addField("c.tradesSelf");
			sql.addField("c.tradesSub");
		}

		if (filterOn(f.getCustomAPI()) && permissions.isAdmin())
			sql.addWhere(f.getCustomAPI());
		
		String typeList = Strings.implodeForDB(f.getType(), ",");
		if (filterOn(typeList)) {
			sql.addWhere("a.type IN (" + typeList + ")");
			setFiltered(true);
		}
	}
	
	public ReportFilterAccount getFilter() {
		return filter;
	}
}
