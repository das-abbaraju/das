package com.picsauditing.actions.report;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Set;

import org.springframework.beans.factory.annotation.Autowired;

import com.picsauditing.PICS.ContractorWatchlistHelper;
import com.picsauditing.access.NoRightsException;
import com.picsauditing.access.OpPerms;
import com.picsauditing.access.OpType;
import com.picsauditing.access.Permissions;
import com.picsauditing.access.RequiredPermission;
import com.picsauditing.dao.UserDAO;
import com.picsauditing.jpa.entities.Account;
import com.picsauditing.jpa.entities.ContractorAccount;
import com.picsauditing.jpa.entities.ContractorWatch;
import com.picsauditing.jpa.entities.User;
import com.picsauditing.search.SelectSQL;
import com.picsauditing.util.ReportFilterCAO;
import com.picsauditing.util.Strings;

@SuppressWarnings("serial")
public class ReportActivityWatch extends ReportAccount {
	@Autowired
	protected UserDAO userDAO;

	protected ReportFilterCAO filter = new ReportFilterCAO();
	protected ContractorAccount contractor;
	protected ContractorWatch contractorWatch;
	private int limit = 50;

	private boolean audits = true;
	private boolean flagColorChange = true;
	private boolean login = true;
	private boolean notesAndEmail = true;
	private boolean flagCriteria = true;

	private ContractorWatchlistHelper contractorWatchlistHelper;

	@Override
	public String execute() throws Exception {
		checkPermissions();

		if (!login && !notesAndEmail && !audits && !flagColorChange && !flagCriteria ) {
			addActionError(getText("ReportActivityWatch.message.SelectWatchCriteria","Please select a watch criteria"));
			return SUCCESS;
		}

		return super.execute();
	}

	@RequiredPermission(value = OpPerms.ContractorWatch, type = OpType.Edit)
	public String add() throws Exception {
		if (contractor != null) {
			if (getContractorWatchlistHelper().isWatching(contractor))
				addActionError(getText("ReportActivityWatch.message.ContractorAlreadyWatched"));
			else {
				getContractorWatchlistHelper().addContractorToWatchList(contractor);
				addActionMessage(getText("ReportActivityWatch.message.AddedToWatchList",
						new Object[] { contractor.getName() }));
			}
		} else
			addActionError(getText("ReportActivityWatch.message.SelectContractor"));

		if (getActionErrors().size() > 0)
			return SUCCESS;
		
		return super.execute();
	}

	@RequiredPermission(value = OpPerms.ContractorWatch, type = OpType.Edit)
	public String remove() throws Exception {
		if (contractorWatch == null) {
			addActionError(getText("ReportActivityWatch.message.SelectContractorToStopWatching"));
			return SUCCESS;
		}
		getContractorWatchlistHelper().removeContractorFromWatchList(contractorWatch);
		return redirect("ReportActivityWatch.action"
				+ (contractor != null && contractor.getId() != getContractorWatchlistHelper().getRemovedContractorId() ? "?contractor=" + contractor.getId() : ""));
	}

	@Override
	protected void buildQuery() {
		super.buildQuery();


		String activity = "JOIN (";
		List<String> watchOptions = new ArrayList<String>();
		List<String> joins = new ArrayList<String>();

		if (audits) {
			joins.add("JOIN audit_type aType ON ca.auditTypeID = aType.id");
			joins.add("JOIN contractor_audit_operator cao on cao.auditID = ca.id AND cao.visible = 1");
			joins.add("JOIN contractor_audit_operator_workflow caow ON caow.caoID = cao.id");

			if (permissions.isOperatorCorporate()) {
				joins.add("JOIN contractor_audit_operator_permission caop ON caop.caoID = cao.id AND caop.opID IN ("
						+ visibleCaoOpIDsAsCsvList() + ")");
			}
			// Audit Expiration
			SelectSQL sql2 = buildWatch("Audits", "contractor_audit ca", "ca.conID", "caow.creationDate", "aType.id",
					"caow.previousStatus", "caow.status",
					"(CASE WHEN ca.auditFor IS NULL OR ca.auditFor = '' THEN 0 ELSE ca.auditFor END)",
					"CONCAT('Audit.action?auditID=', ca.id)", joins);
			sql2.addWhere("ca.expiresDate IS NOT NULL AND DATE_ADD(ca.expiresDate,INTERVAL 1 day) < NOW()");

			if (filterOn(getFilter().getAuditStatus()))
				sql2.addWhere("caow.status IN (" + Strings.implodeForDB(getFilter().getAuditStatus(), ",") + ")");

			watchOptions.add("(" + sql2.toString() + ")");

			joins.clear();
		}
		if (flagColorChange) {
			joins.add("JOIN accounts oper ON oper.id = gc.genID AND oper.type = 'Operator'");
			SelectSQL sql2 = buildWatch("FlagColorChange", "generalcontractors gc", "gc.subID", "gc.flagLastUpdated",
					"gc.flag", "oper.name", "''", "''",
					"CONCAT('ContractorFlag.action?id=', gc.subID,'&opID=',gc.genID)", joins);

			if (permissions.isOperatorCorporate())
				sql2.addWhere("gc.genID IN (" + Strings.implode(permissions.getVisibleAccounts(), ",") + ")");

			watchOptions.add("(" + sql2.toString() + ")");
			joins.clear();
		}
		if (login) {
			SelectSQL sql2 = buildWatch("Login", "users u", "u.accountID", "u.lastLogin", "u.name", "''", "''", "''",
					"''", joins);
			sql2.addWhere("u.lastLogin IS NOT NULL AND u.isGroup = 'No'");
			watchOptions.add("(" + sql2.toString() + ")");
		}
		if (notesAndEmail) {
			joins.add("JOIN users u ON n.createdBy = u.id");
			SelectSQL sql2 = buildWatch("Note", "note n USE INDEX(creationDate)", "n.accountID", "n.creationDate",
					"u.name", "n.summary", "''", "''", "CONCAT('ContractorNotes.action?id=', n.accountID)", joins);
			String viewableBy = " (n.createdBy = " + permissions.getUserId() + " AND n.viewableBy = " + Account.PRIVATE
					+ ")";
			viewableBy += " OR (n.viewableBy = " + Account.EVERYONE + ")";
			if (permissions.hasPermission(OpPerms.AllOperators))
				viewableBy += " OR (n.viewableBy > 2)";

			if (permissions.isOperatorCorporate())
				viewableBy += " OR (n.viewableBy IN (" + Strings.implode(permissions.getVisibleAccounts(), ",") + "))";
			sql2.addWhere(viewableBy);
			sql2.addWhere("n.createdBy = 1");
			sql2.addWhere("n.status != 0");
			watchOptions.add("(" + sql2.toString() + ")");
			joins.clear();

			sql2 = buildWatch("Email", "email_queue eq", "eq.conID", "eq.sentDate", "eq.subject", "''", "''", "''",
					"''", joins);
			viewableBy = " (eq.createdBy = " + permissions.getUserId() + " AND eq.viewableBy = " + Account.PRIVATE
					+ ")";
			viewableBy += " OR (eq.viewableBy = " + Account.EVERYONE + ")";

			if (permissions.isOperatorCorporate())
				viewableBy += " OR (eq.viewableBy IN (" + Strings.implode(permissions.getVisibleAccounts(), ",") + "))";
			else
				viewableBy += " OR (eq.viewableBy > 2) OR (eq.viewableBy IS NULL)";

			sql2.addWhere(viewableBy);
			sql2.addWhere("eq.status = 'Sent'");
			watchOptions.add("(" + sql2.toString() + ")");
		}
		if (flagCriteria) {
			joins.add("JOIN flag_criteria_operator fco ON fc.id = fco.criteriaID");
			joins.add("JOIN accounts o ON o.id = fco.opID"
					+ (permissions.isOperatorCorporate() ? " AND o.id = " + permissions.getAccountId() : ""));
			joins.add("JOIN flag_data fd ON fd.opID = fco.opID AND fd.criteriaID = fc.id");
			SelectSQL sql2 = buildWatch("FlagCriteria", "flag_criteria fc", "fd.conID", "fco.updateDate", "o.name",
					"CAST(CONCAT('FlagCriteria.', fc.id, '.label') AS CHAR)", "''", "''", "''", joins);
			watchOptions.add("(" + sql2.toString() + ")");
			joins.clear();

			if (contractor != null) {
				joins.add("JOIN (SELECT DISTINCT criteriaID, conID FROM flag_data WHERE conID = " + contractor.getId()
						+ (permissions.isOperatorCorporate() ? " AND opID = " + permissions.getAccountId() : "") + ")"
						+ " fd ON fc.id = fd.criteriaID");
				sql2 = buildWatch("FlagCriteria", "flag_criteria fc", "fd.conID", "fc.updateDate",
						"CAST(CONCAT('FlagCriteria.', fc.id, '.label') AS CHAR)", "''", "''", "''", "''", joins);

				watchOptions.add("(" + sql2.toString() + ")");
			}
		}

		activity += Strings.implode(watchOptions, " UNION ");
		activity += ") ac ON a.id = ac.conID";

		sql.addJoin(activity);
		sql.addField("ac.*");
		orderByDefault = "ac.activityDate DESC";

		report.setLimit(limit);
	}

	private String visibleCaoOpIDsAsCsvList() {
		Set<Integer> visibleCaos = new HashSet<Integer>();

		if (permissions.isOperator()) {
			visibleCaos.add(permissions.getAccountId());
		}
		if (permissions.isCorporate())
			visibleCaos.addAll(permissions.getOperatorChildren());
		return Strings.implode(visibleCaos, ",");
	}

	private SelectSQL buildWatch(String activityType, String from, String accountID, String activityDate, String v1,
			String v2, String v3, String v4, String url, List<String> joins) {
		SelectSQL sql = new SelectSQL(from);
		sql.addField(accountID + " conID");
		sql.addField("'ReportActivityWatch." + activityType + "' activityType");
		sql.addField(activityDate + " activityDate");
		sql.addField(url + " AS url");
		sql.addField(v1 + " AS v1");
		sql.addField(v2 + " AS v2");
		sql.addField(v3 + " AS v3");
		sql.addField(v4 + " AS v4");

		for (String join : joins) {
			sql.addJoin(join);
		}

		if (contractor != null)
			sql.addWhere(accountID + " = " + contractor.getId());
		else
			sql.addJoin("JOIN contractor_watch w ON " + accountID + " = w.conID AND w.userID = "
					+ permissions.getUserId());

		sql.addOrderBy("activityDate DESC");
		sql.setLimit(limit);

		return sql;
	}

	public ContractorAccount getContractor() {
		return contractor;
	}

	public void setContractor(ContractorAccount contractor) {
		this.contractor = contractor;
	}

	public ContractorWatch getContractorWatch() {
		return contractorWatch;
	}

	public void setContractorWatch(ContractorWatch contractorWatch) {
		this.contractorWatch = contractorWatch;
	}

	public boolean isFlagColorChange() {
		return flagColorChange;
	}

	public void setFlagColorChange(boolean flagColorChange) {
		this.flagColorChange = flagColorChange;
	}

	public boolean isLogin() {
		return login;
	}

	public void setLogin(boolean login) {
		this.login = login;
	}

	public boolean isAudits() {
		return audits;
	}

	public void setAudits(boolean audits) {
		this.audits = audits;
	}

	public boolean isNotesAndEmail() {
		return notesAndEmail;
	}

	public void setNotesAndEmail(boolean notesAndEmail) {
		this.notesAndEmail = notesAndEmail;
	}

	public boolean isFlagCriteria() {
		return flagCriteria;
	}

	public void setFlagCriteria(boolean flagCriteria) {
		this.flagCriteria = flagCriteria;
	}

	public List<ContractorWatch> getWatched() {
		return getContractorWatchlistHelper().getWatchedSortedByContractorName();
	}

	@Override
	protected void checkPermissions() throws Exception {
		super.checkPermissions();

		if (!permissions.hasPermission(OpPerms.ContractorWatch) && !permissions.isAdmin())
			throw new NoRightsException(OpPerms.ContractorWatch, OpType.View);
	}

	public ReportFilterCAO getFilter() {
		return filter;
	}

	private ContractorWatchlistHelper getContractorWatchlistHelper() {
		if (contractorWatchlistHelper == null) {
			contractorWatchlistHelper = new ContractorWatchlistHelper(getUser(), permissions);
		}
		return contractorWatchlistHelper;
	}

}