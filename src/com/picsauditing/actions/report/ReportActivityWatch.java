package com.picsauditing.actions.report;

import java.util.ArrayList;
import java.util.List;

import com.picsauditing.access.OpPerms;
import com.picsauditing.access.OpType;
import com.picsauditing.dao.ContractorAccountDAO;
import com.picsauditing.dao.UserDAO;
import com.picsauditing.jpa.entities.ContractorAccount;
import com.picsauditing.jpa.entities.ContractorWatch;
import com.picsauditing.jpa.entities.User;
import com.picsauditing.search.SelectSQL;
import com.picsauditing.util.Strings;

@SuppressWarnings("serial")
public class ReportActivityWatch extends ReportAccount {
	private ContractorAccountDAO conDAO;
	private UserDAO userDAO;
	
	private int conID = 0;
	private int limit = 50;
	private int watchID = 0;

	private boolean auditExpiration = true;
	private boolean auditSubmitted = true;
	private boolean auditActivated = true;
	private boolean flagColorChange = true;
	private boolean login = true;
	private boolean note = true;
	private boolean email = true;
	
	private String conName;
	private List<ContractorWatch> watched;
	
	public ReportActivityWatch(ContractorAccountDAO conDAO, UserDAO userDAO) {
		this.conDAO = conDAO;
		this.userDAO = userDAO;
	}
	
	@Override
	public String execute() throws Exception {
		if (button != null) {
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
				
				return redirect("ReportActivityWatch.action" + (conID > 0 && conID != redirect ? "?conID=" + conID : ""));
			}
			
			if ("Add".equals(button)) {
				if (!Strings.isEmpty(conName)) {
					// Match potentially partially filled in name
					List<ContractorAccount> cons = conDAO.findWhere("a.name LIKE '%" + conName + "%'");
					
					if (cons.size() == 1) {
						boolean exists = false;
						for (ContractorWatch watch : watched) {
							if (watch.getContractor().equals(cons.get(0))) {
								exists = true;
							}
						}
						if (!exists) {
							ContractorWatch watch = new ContractorWatch();
							watch.setAuditColumns(permissions);
							watch.setUser(getUser());
							watch.setContractor(cons.get(0));
							
							userDAO.save(watch);
						} else
							addActionError("Contractor is already on watch list.");
					} else if (cons.size() > 1)
						addActionError("Please type in the full name of the contractor to add to watch list.");
					else
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
		
		String activity = "JOIN (";
		List<String> watchOptions = new ArrayList<String>();
		
		if (!auditExpiration && !auditSubmitted && !auditActivated && !flagColorChange && !login && !note && !email)
			auditExpiration = auditSubmitted = auditActivated = flagColorChange = login = note = email = true;
		
		if (auditExpiration) {
			SelectSQL sql2 = buildWatch("AuditExpiration", "contractor_audit ca", "ca.conID", "ca.expiresDate", "CONCAT(aType.auditName, (CASE WHEN ca.auditFor IS NULL THEN '' ELSE CONCAT(' for ', ca.auditFor) END), ' Expired')", "CONCAT('Audit.action?auditID=', ca.id)");
			sql2.addJoin("JOIN audit_type aType ON ca.auditTypeID = aType.id");
			sql2.addWhere("expiresDate IS NOT NULL AND ca.auditStatus = 'Expired'");
			watchOptions.add("(" + sql2.toString() + ")");
		}
		if (auditSubmitted) {
			SelectSQL sql2 = buildWatch("AuditSubmitted", "contractor_audit ca", "ca.conID", "ca.completedDate", "CONCAT(aType.auditName, (CASE WHEN ca.auditFor IS NULL THEN '' ELSE CONCAT(' for ', ca.auditFor) END), ' Submitted')", "CONCAT('Audit.action?auditID=', ca.id)");
			sql2.addJoin("JOIN audit_type aType ON ca.auditTypeID = aType.id AND aType.hasRequirements = 1");
			sql2.addWhere("completedDate IS NOT NULL AND ca.auditStatus = 'Submitted'");
			watchOptions.add("(" + sql2.toString() + ")");
		}
		if (auditActivated) {
			SelectSQL sql2 = buildWatch("AuditActivated", "contractor_audit ca", "ca.conID", "ca.closedDate", "CONCAT(aType.auditName, (CASE WHEN ca.auditFor IS NULL THEN '' ELSE CONCAT(' for ', ca.auditFor) END), ' Activated')", "CONCAT('Audit.action?auditID=', ca.id)");
			sql2.addJoin("JOIN audit_type aType ON ca.auditTypeID = aType.id");
			sql2.addWhere("closedDate IS NOT NULL AND ca.auditStatus = 'Active'");
			watchOptions.add("(" + sql2.toString() + ")");
		}
		if (flagColorChange) {
			SelectSQL sql2 = buildWatch("FlagColorChange", "generalcontractors gc", "gc.subID", "gc.flagLastUpdated", "CONCAT('Flag Color changed to ', flag)", "CONCAT('ConFlag.action?auditID=', gc.subID)");
			sql2.addWhere("gc.genID = " + permissions.getAccountId());
			watchOptions.add("(" + sql2.toString() + ")");
		}
		if (login) {
			SelectSQL sql2 = buildWatch("Login", "users u", "u.accountID", "u.lastLogin", "CONCAT(u.name, ' logged in')", "''");
			sql2.addWhere("u.lastLogin IS NOT NULL AND u.isGroup = 'No'");
			watchOptions.add("(" + sql2.toString() + ")");
		}
		if (note) {
			SelectSQL sql2 = buildWatch("Note", "note n USE INDEX(creationDate)", "n.accountID", "n.creationDate", "CONCAT(u.name, ' posted a Note')", "CONCAT('ContractorNotes.action?id=', n.accountID)");
			sql2.addJoin("JOIN users u ON n.createdBy = u.id AND u.id > " + User.SYSTEM);
			watchOptions.add("(" + sql2.toString() + ")");
		}
		if (email) {
			SelectSQL sql2 = buildWatch("Email", "email_queue eq", "eq.conID", "eq.sentDate", "CONCAT(et.templateName, ' Email Sent')", "''");
			sql2.addJoin("JOIN email_template et on et.id = eq.templateID");
			sql2.addWhere("eq.status = 'Sent'");
			watchOptions.add("(" + sql2.toString() + ")");
		}
		
		activity += Strings.implode(watchOptions, " UNION ");
		activity += ") ac ON a.id = ac.conID";

		sql.addJoin(activity);
		sql.addField("ac.*");
		orderByDefault = "ac.activityDate DESC";

		report.setLimit(limit);
	}
	
	private SelectSQL buildWatch(String activityType, String from, String accountID, String activityDate, String body, String url) {
		SelectSQL sql = new SelectSQL(from);
		sql.addField(accountID + " conID");
		sql.addField("'" + activityType + "' activityType");
		sql.addField(activityDate + " activityDate");
		sql.addField(body + " body");
		sql.addField(url + " url");
		sql.addOrderBy("activityDate DESC");
		sql.setLimit(limit);
		
		if (conID > 0)
			sql.addWhere(accountID + " = " + conID);
		else
			sql.addJoin("JOIN contractor_watch w ON " + accountID +
					" = w.conID AND w.userID = " + permissions.getUserId());
		
		return sql;
	}
	
	public int getConID() {
		return conID;
	}
	
	public void setConID(int conID) {
		this.conID = conID;
	}
	
	public boolean isAuditExpiration() {
		return auditExpiration;
	}

	public void setAuditExpiration(boolean auditExpiration) {
		this.auditExpiration = auditExpiration;
	}

	public boolean isAuditSubmitted() {
		return auditSubmitted;
	}

	public void setAuditSubmitted(boolean auditSubmitted) {
		this.auditSubmitted = auditSubmitted;
	}

	public boolean isAuditActivated() {
		return auditActivated;
	}

	public void setAuditActivated(boolean auditActivated) {
		this.auditActivated = auditActivated;
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

	public boolean isNote() {
		return note;
	}

	public void setNote(boolean note) {
		this.note = note;
	}
	
	public boolean isEmail() {
		return email;
	}
	
	public void setEmail(boolean email) {
		this.email = email;
	}
	
	public int getWatchID() {
		return watchID;
	}
	
	public void setWatchID(int watchID) {
		this.watchID = watchID;
	}
	
	public String getConName() {
		return conName;
	}
	
	public void setConName(String conName) {
		this.conName = conName;
	}

	public List<ContractorWatch> getWatched() {
		if (watched == null)
			watched = userDAO.findContractorWatch(permissions.getUserId());
		
		return watched;
	}
}
