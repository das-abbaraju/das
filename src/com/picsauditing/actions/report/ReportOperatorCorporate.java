package com.picsauditing.actions.report;

import java.util.ArrayList;
import java.util.List;

import com.opensymphony.xwork2.ActionContext;
import com.picsauditing.access.NoRightsException;
import com.picsauditing.access.OpPerms;
import com.picsauditing.access.OpType;
import com.picsauditing.jpa.entities.Account;
import com.picsauditing.jpa.entities.ListType;
import com.picsauditing.mail.WizardSession;
import com.picsauditing.search.SelectAccount;
import com.picsauditing.search.SelectFilter;
import com.picsauditing.util.ReportFilterAccount;
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

		if (permissions.hasPermission(OpPerms.ManageOperators)) {
			types.add(Account.OPERATOR_ACCOUNT_TYPE);
			sql.addJoin("LEFT JOIN (SELECT genID, count(*) as opCount FROM generalContractors GROUP BY genID) op ON op.genID = a.id");
			sql.addField("opCount");
			// check to see for any requested contractors
			sql.addJoin("LEFT JOIN (SELECT requestedByID, count(*) as conCount FROM contractor_info GROUP BY requestedByID) requested ON requested.requestedByID = a.id");
			sql.addField("conCount");
		}
		if (permissions.hasPermission(OpPerms.ManageCorporate)) {
			types.add("Corporate");
			sql.addJoin("LEFT JOIN (SELECT corporateID, count(*) as corpCount FROM facilities GROUP BY corporateID) corp ON corp.corporateID = a.id");
			sql.addField("corpCount");
		}
		if (permissions.hasPermission(OpPerms.ManageAssessment)) {
			types.add("Assessment");
		}

		sql.addWhere("a.type IN (" + Strings.implodeForDB(types, ",") + ")");
		sql.setType(null);

		sql.addJoin("left JOIN users contact ON contact.id = a.contactID");
		sql.addField("a.countrySubdivision");
		sql.addField("a.city");
		sql.addField("a.type");
		sql.addField("a.country");
		sql.addOrderBy("a.name");
		addFilterToSQL();
	}

	public String execute() throws Exception {
		if (!permissions.hasPermission(OpPerms.ManageOperators)
				&& !permissions.hasPermission(OpPerms.ManageCorporate)
				&& !permissions.hasPermission(OpPerms.ManageAssessment)) {
			throw new NoRightsException("Administrator");
		}

		buildQuery();
		run(sql);

		WizardSession wizardSession = new WizardSession(ActionContext.getContext().getSession());
		wizardSession.clear();
		wizardSession.setFilter(ListType.ALL, filter);

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

	public boolean isCanEditOp() {
		return permissions.hasPermission(OpPerms.ManageOperators, OpType.Edit);
	}

	public boolean isCanEditAssessment() {
		return permissions.hasPermission(OpPerms.ManageAssessment, OpType.Edit);
	}

	protected void addFilterToSQL() {
		ReportFilterAccount f = getFilter();

		/** **** Filters for Accounts ********** */
		if (filterOn(f.getStartsWith())) {
			report.addFilter(new SelectFilter("startsWith", "a.nameIndex LIKE '?%'", f.getStartsWith()));
		}

		if (filterOn(f.getAccountName(), ReportFilterAccount.getDefaultName())) {
			String accountName = f.getAccountName().trim();
			report.addFilter(new SelectFilter("accountName", "a.nameIndex LIKE '%" + Strings.indexName(accountName)
					+ "%' OR a.name LIKE '%?%' OR a.dbaName LIKE '%" + Strings.escapeQuotes(accountName) + "%' OR a.id = '" +
					Strings.escapeQuotes(accountName) + "'", accountName));

			sql.addField("a.dbaName");
		}

		String statusList = Strings.implodeForDB(f.getStatus(), ",");
		if (filterOn(statusList)) {
			sql.addWhere("a.status IN (" + statusList + ")");
			setFiltered(true);
		}

		if (filterOn(f.getCity(), ReportFilterAccount.getDefaultCity())) {
			report.addFilter(new SelectFilter("city", "a.city LIKE '%?%'", f.getCity()));
		}

		String locationList = Strings.implodeForDB(f.getLocation(), ",");
		if (filterOn(locationList)) {
			sql.addWhere("a.countrySubdivision IN (" + locationList + ") OR a.country IN (" + locationList + ")");
			sql.addOrderBy("CASE WHEN a.country IN (" + locationList + ") THEN 1 ELSE 2 END, a.country");
			sql.addOrderBy("CASE WHEN a.countrySubdivision IN (" + locationList + ") THEN 1 ELSE 2 END, a.countrySubdivision");
			sql.addOrderBy("a.country");
			sql.addOrderBy("a.countrySubdivision");
			setFiltered(true);
		}

		if (filterOn(f.getZip(), ReportFilterAccount.getDefaultZip())) {
			report.addFilter(new SelectFilter("zip", "a.zip LIKE '%?%'", f.getZip()));
		}

		if (f.isPrimaryInformation()) {
			sql.addField("contact.name AS contactname");
			sql.addField("contact.phone AS contactphone");
			sql.addField("contact.email AS contactemail");
			sql.addField("a.address");
			sql.addField("a.city");
			sql.addField("a.countrySubdivision");
			sql.addField("a.zip");
			sql.addField("a.web_URL");
		}

		if (f.isTradeInformation()) {
			sql.addField("c.main_trade");
			sql.addField("c.tradesSelf");
			sql.addField("c.tradesSub");
		}

		if (filterOn(f.getCustomAPI()) && permissions.isAdmin()) {
			sql.addWhere(f.getCustomAPI());
		}

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
