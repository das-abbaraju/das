package com.picsauditing.actions.report;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import com.picsauditing.access.NoRightsException;
import com.picsauditing.access.OpPerms;
import com.picsauditing.access.OpType;
import com.picsauditing.dao.ContractorAccountDAO;
import com.picsauditing.dao.UserDAO;
import com.picsauditing.jpa.entities.Account;
import com.picsauditing.jpa.entities.ContractorAccount;
import com.picsauditing.jpa.entities.ContractorWatch;
import com.picsauditing.search.SelectSQL;
import com.picsauditing.util.ReportFilterCAO;
import com.picsauditing.util.Strings;

@SuppressWarnings("serial")
public class ReportActivityWatch extends ReportAccount {
	private ContractorAccountDAO conDAO;
	private UserDAO userDAO;

	private ReportFilterCAO filter = new ReportFilterCAO();

	private int conID = 0;
	private int limit = 50;
	private int watchID = 0;

	private boolean audits = true;
	private boolean flagColorChange = true;
	private boolean login = true;
	private boolean notesAndEmail = true;
	private boolean flagCriteria = true;

	private List<ContractorWatch> watched;
	private Set<Integer> visibleCaos = new HashSet<Integer>();

	public ReportActivityWatch(ContractorAccountDAO conDAO, UserDAO userDAO) {
		this.conDAO = conDAO;
		this.userDAO = userDAO;
	}

	@Override
	public String execute() throws Exception {
		if (!forceLogin())
			return LOGIN;
		checkPermissions();

		if (login == false && notesAndEmail == false && audits == false && flagColorChange == false
				&& flagCriteria == false) {
			addActionError("Please select a watch criteria");
			return SUCCESS;
		}

		if (button != null) {
			if (!"Search".equals(button))
				tryPermissions(OpPerms.ContractorWatch, OpType.Edit);

			List<ContractorWatch> watched = userDAO.findContractorWatch(permissions.getUserId());
			if ("Remove".equals(button)) {
				int redirect = 0;
				if (watchID > 0) {
					for (ContractorWatch watch : watched) {
						if (watch.getId() == watchID) {
							userDAO.remove(watch);
							redirect = watch.getContractor().getId();
							break;
						}
					}
				} else {
					addActionError("Please select a contractor to stop watching.");
					return SUCCESS;
				}

				return redirect("ReportActivityWatch.action"
						+ (conID > 0 && conID != redirect ? "?conID=" + conID : ""));
			}

			if ("Add".equals(button)) {
				if (conID > 0) {
					ContractorAccount con = conDAO.find(conID);
					boolean exists = false;

					if (con != null) {
						for (ContractorWatch watch : getWatched()) {
							if (watch.getContractor().getId() == conID)
								exists = true;
						}

						if (!exists) {
							ContractorWatch watch = new ContractorWatch();
							watch.setAuditColumns(permissions);
							watch.setContractor(con);
							watch.setUser(getUser());

							userDAO.save(watch);
						} else
							addActionError("Contractor is already on watch list.");
					} else
						addActionError("Contractor not found.");
				} else
					addActionError("Please select a contractor to start watching.");
			}

			if (getActionErrors().size() > 0)
				return SUCCESS;
		}

		return super.execute();
	}

	@Override
	protected void buildQuery() {
		super.buildQuery();

		if (permissions.isOperator()) {
			visibleCaos.add(permissions.getAccountId());
		}
		if (permissions.isCorporate())
			visibleCaos.addAll(permissions.getOperatorChildren());

		String activity = "JOIN (";
		List<String> watchOptions = new ArrayList<String>();
		List<String> joins = new ArrayList<String>();

		if (audits) {
			// Audit Expiration
			SelectSQL sql2 = buildWatch(
					"Audits",
					"contractor_audit ca",
					"ca.conID",
					"caow.creationDate",
					"CONCAT(aType.auditName, (CASE WHEN ca.auditFor IS NULL THEN '' ELSE CONCAT(' for ', ca.auditFor) END), ' changed from ', caow.previousStatus , ' to ', caow.status)",
					"CONCAT('Audit.action?auditID=', ca.id)", joins);
			sql2.addWhere("ca.expiresDate IS NOT NULL AND ca.expiresDate < NOW()");
			watchOptions.add("(" + sql2.toString() + ")");
		}
		if (flagColorChange) {
			joins.add("JOIN accounts oper ON oper.id = gc.genID AND oper.type = 'Operator'");
			SelectSQL sql2 = buildWatch("FlagColorChange", "generalcontractors gc", "gc.subID", "gc.flagLastUpdated",
					"CONCAT('Flag Color changed to ', gc.flag,' for ', oper.name)",
					"CONCAT('ContractorFlag.action?id=', gc.subID,'&opID=',gc.genID)", joins);

			if (permissions.isOperatorCorporate())
				sql2.addWhere("gc.genID IN (" + Strings.implode(permissions.getVisibleAccounts(), ",") + ")");

			watchOptions.add("(" + sql2.toString() + ")");
			joins.clear();
		}
		if (login) {
			SelectSQL sql2 = buildWatch("Login", "users u", "u.accountID", "u.lastLogin",
					"CONCAT(u.name, ' logged in')", "''", joins);
			sql2.addWhere("u.lastLogin IS NOT NULL AND u.isGroup = 'No'");
			watchOptions.add("(" + sql2.toString() + ")");
		}
		if (notesAndEmail) {
			joins.add("JOIN users u ON n.createdBy = u.id");
			SelectSQL sql2 = buildWatch("Note", "note n USE INDEX(creationDate)", "n.accountID", "n.creationDate",
					"CONCAT(u.name, ' posted - ', n.summary)", "CONCAT('ContractorNotes.action?id=', n.accountID)",
					joins);
			String viewableBy = " (n.createdBy = " + permissions.getUserId() + " AND n.viewableBy = " + Account.PRIVATE
					+ ")";
			viewableBy += " OR (n.viewableBy = " + Account.EVERYONE + ")";
			if (permissions.hasPermission(OpPerms.AllOperators))
				viewableBy += " OR (n.viewableBy > 2)";

			if (permissions.isOperatorCorporate())
				viewableBy += " OR (n.viewableBy IN (" + Strings.implode(permissions.getVisibleAccounts(), ",") + "))";
			sql2.addWhere(viewableBy);
			sql2.addWhere("n.status != 0");
			watchOptions.add("(" + sql2.toString() + ")");
			joins.clear();

			sql2 = buildWatch("Email", "email_queue eq", "eq.conID", "eq.sentDate",
					"CONCAT(eq.subject, ' Email Sent')", "''", joins);
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
			SelectSQL sql2 = buildWatch("FlagCriteriaOperator", "flag_criteria fc", "fd.conID", "fco.updateDate",
					"CONCAT(o.name, ' flag criteria for ', fc.label, ' was changed or added')", "''", joins);
			watchOptions.add("(" + sql2.toString() + ")");
			joins.clear();

			if (conID > 0) {
				joins.add("JOIN (SELECT DISTINCT criteriaID, conID FROM flag_data WHERE conID = " + conID
						+ (permissions.isOperatorCorporate() ? " AND opID = " + permissions.getAccountId() : "") + ")"
						+ " fd ON fc.id = fd.criteriaID");
				sql2 = buildWatch("FlagCriteria", "flag_criteria fc", "fd.conID", "fc.updateDate",
						" CONCAT('Flag criteria for ', fc.label, ' was changed or added')", "''", joins);

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

	private SelectSQL buildWatch(String activityType, String from, String accountID, String activityDate, String body,
			String url, List<String> joins) {
		SelectSQL sql = new SelectSQL(from);
		sql.addField(accountID + " conID");
		sql.addField("'" + activityType + "' activityType");
		sql.addField(activityDate + " activityDate");
		sql.addField(body + " body");
		sql.addField(url + " url");

		if (activityType.equals("Audits")) {
			sql.addJoin("JOIN audit_type aType ON ca.auditTypeID = aType.id");
			sql.addJoin("JOIN contractor_audit_operator cao on cao.auditID = ca.id AND cao.visible = 1");
			sql.addJoin("JOIN contractor_audit_operator_workflow caow ON caow.caoID = cao.id");

			if (permissions.isOperatorCorporate())
				sql.addJoin("JOIN contractor_audit_operator_permission caop ON caop.caoID = cao.id AND caop.opID IN ("
						+ Strings.implode(visibleCaos, ",") + ")");
			if (filterOn(getFilter().getAuditStatus()))
				sql.addWhere("caow.status IN (" + Strings.implodeForDB(getFilter().getAuditStatus(), ",") + ")");
		}

		for (String join : joins) {
			sql.addJoin(join);
		}

		if (conID > 0)
			sql.addWhere(accountID + " = " + conID);
		else
			sql.addJoin("JOIN contractor_watch w ON " + accountID + " = w.conID AND w.userID = "
					+ permissions.getUserId());

		sql.addOrderBy("activityDate DESC");
		sql.setLimit(limit);

		return sql;
	}

	public int getConID() {
		return conID;
	}

	public void setConID(int conID) {
		this.conID = conID;
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

	public int getWatchID() {
		return watchID;
	}

	public void setWatchID(int watchID) {
		this.watchID = watchID;
	}

	public List<ContractorWatch> getWatched() {
		if (watched == null)
			watched = userDAO.findContractorWatch(permissions.getUserId());

		return watched;
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
}